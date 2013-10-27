package org.wikicleta.activities.parkings;

import org.wikicleta.R;
import org.wikicleta.activities.common.ModifyingOnMapBaseActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.common.FieldValidators;
import org.wikicleta.models.Parking;
import org.wikicleta.routing.Parkings;
import org.wikicleta.routing.Others.Cruds;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ModifyingActivity extends ModifyingOnMapBaseActivity {
	
	public Dialog dialog;
	protected Marker marker;
	protected Spinner kindSelector;
	protected EditText details;
	protected CheckBox hasRoofCheckbox;
	public Parking parking;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		attemptCenterOnLocationAtStart = false;
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_wikicleta);
		
		AppBase.currentActivity = this;

    	// Parking from a draft store on local db
    	if(getIntent().getSerializableExtra("id") != null)
    		parking = Parking.load(Parking.class, Long.valueOf(getIntent().getSerializableExtra("id").toString()));
    	
		if(parking != null) {
			// We are on editing mode
			turnOffLocation();
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(parking.latitude, parking.longitude), 18));
		} else {
			turnOnLocation();
			parking = new Parking();
		}
	}

	protected void presentSaveForm() {
		LatLng center = map.getCameraPosition().target;
		// Setting parking coordinates
		parking.latitude = center.latitude;
		parking.longitude = center.longitude;
		displaySaveForm();
	}
	
	/**
	 * Methods for saving 
	 */
	
	public void attemptCommit() {
		String parkingsContents = details.getText().toString();
		parking.details = parkingsContents;
		parking.hasRoof = hasRoofCheckbox.isChecked(); 
		
		if(FieldValidators.isFieldEmpty(parkingsContents)) {
			details.setError(getResources().getString(R.string.parkings_input_empty));
			return;
		}
		
		if(FieldValidators.isFieldLongerThan(parkingsContents, Constants.CHARACTERS_LENGTH_MAX)) {
			details.setError(getResources().getString(R.string.parkings_input_length_max_error));
			return;
		}
		
		if(FieldValidators.isFieldShorterThan(parkingsContents, Constants.CHARACTERS_LENGTH_MIN)) {
			details.setError(getResources().getString(R.string.parkings_input_length_min_error));
			return;
		}
		
		Parkings.PostOrPut poster = new Parkings().new PostOrPut();
		poster.activity = this;
		if(parking.existsOnRemoteServer())
			poster.mode = Cruds.MODIFY;
		poster.execute(parking);
	}
	
	protected void displaySaveForm() {
		dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        LayoutInflater inflater = this.getLayoutInflater();
        
        View view = inflater.inflate(R.layout.dialog_parking_save, null);
        
        TextView title = (TextView) view.findViewById(R.id.dialog_menu_title);
        title.setTypeface(AppBase.getTypefaceStrong());
        
        TextView instructionsForKind = (TextView) view.findViewById(R.id.parking_kind_instructions);
        instructionsForKind.setTypeface(AppBase.getTypefaceStrong());
        
        view.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
        	
        });
        
        // Form Fields setup
    	kindSelector = (Spinner) view.findViewById(R.id.parking_kind_selector);
    	ParkingsCategoriesArrayAdapter adapter = new ParkingsCategoriesArrayAdapter(this, 0, Parking.getKindsValues());
    	kindSelector.setAdapter(adapter);
    	kindSelector.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				// Setting tip category
				parking.kind = pos+1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
    		
    	});
    	
    	details = (EditText) view.findViewById(R.id.parkings_details);   
    	details.setTypeface(AppBase.getTypefaceLight());
    	details.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				details.setError(null);
			}
    		
    	});
    	
    	hasRoofCheckbox = (CheckBox) view.findViewById(R.id.parkings_hasRoof);

    	// On editing mode if we are modifying the parking
    	if(parking != null) {
    		details.setText(parking.details);
    		kindSelector.setSelection(parking.kind-1);
    		title.setText(this.getResources().getString(R.string.actions_update));
    		hasRoofCheckbox.setChecked(parking.hasRoof);
    	}
    	
    	Button saveButton = (Button) view.findViewById(R.id.save_parking);
    	saveButton.setTypeface(AppBase.getTypefaceStrong());
    	saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				attemptCommit();
			}
    		
    	});
       
    	dialog.setContentView(view);
    	
    	dialog.setCanceledOnTouchOutside(false);
    	dialog.show();
	}
	
	public class ParkingsCategoriesArrayAdapter extends ArrayAdapter<String>{
        
        public ParkingsCategoriesArrayAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.poi_category_row, parent, false);
            
            TextView label=(TextView)row.findViewById(R.id.category);
            
            String categoryCode = Parking.getKinds().get((Integer) position+1);
            String categoryText = getResources().getString(
					getResources().getIdentifier("parkings.kinds.".concat(categoryCode), "string", getPackageName()));
            label.setText(categoryText);
            label.setTypeface(AppBase.getTypefaceLight());

            ImageView icon=(ImageView) row.findViewById(R.id.icon);
            icon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(
            		"parking_".concat(categoryCode).concat("_icon"), "drawable", getPackageName())));
            return row;
        }
    }
}
