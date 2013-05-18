package org.wikicleta.activities.parkings;

import org.wikicleta.R;
import org.wikicleta.activities.common.LocationAwareMapWithControlsActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.common.FieldValidators;
import org.wikicleta.helpers.SlidingMenuAndActionBarHelper;
import org.wikicleta.models.Parking;
import org.wikicleta.models.Workshop;
import org.wikicleta.routing.Parkings;
import org.wikicleta.routing.Others.Cruds;
import org.wikicleta.views.PinOverlay;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.google.android.maps.GeoPoint;

public class ModifyingActivity extends LocationAwareMapWithControlsActivity {

	protected PinOverlay pinOverlay;
	public AlertDialog  formView;
	
	protected Spinner kindSelector;
	protected EditText details;
	protected CheckBox hasRoofCheckbox;
	protected CheckBox anyoneCanEditCheckbox;
	public Parking parking;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.select_poi_on_map);
		setTheme(R.style.Theme_wikicleta);
		
		AppBase.currentActivity = this;
    	assignToggleActionsForAutomapCenter();
    	
    	// Parking from remote server which can be edit by me
    	parking = (Parking) getIntent().getSerializableExtra("parking");
    	
    	// Parking from a draft store on local db
    	if(getIntent().getSerializableExtra("id") != null)
    		parking = Parking.load(Parking.class, Long.valueOf(getIntent().getSerializableExtra("id").toString()));
    	
		if(parking != null) {
			// We are on editing mode
			turnOffLocation();
			this.mapView.getController().animateTo(new GeoPoint(parking.latitude, parking.longitude));
	    	SlidingMenuAndActionBarHelper.setDefaultFontForActionBarWithTitle(this, R.string.parkings_edit_title);
		} else {
			turnOnLocation();
			parking = new Parking();
	    	// TODO: Move to cancelable alert
	    	showToastMessage();
	    	SlidingMenuAndActionBarHelper.setDefaultFontForActionBarWithTitle(this, R.string.parkings_new_title);
		}

    	((TextView) this.findViewById(R.id.poi_save_text)).setTypeface(AppBase.getTypefaceStrong());
    	((TextView) this.findViewById(R.id.poi_back_text)).setTypeface(AppBase.getTypefaceStrong());

    	this.findViewById(R.id.poi_back_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
    		
    	});
    	
    	pinOverlay = new PinOverlay(this);
    	
    	this.mapView.getOverlays().add(pinOverlay);
    	this.toolbar = (LinearLayout) this.findViewById(R.id.poi_toolbar);

    	this.findViewById(R.id.poi_finish_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Setting tip coordinates
				parking.latitude = pinOverlay.getLocation().getLatitudeE6();
				parking.longitude = pinOverlay.getLocation().getLongitudeE6();
				displaySaveForm();
			}
    		
    	});
    	
	}

	/**
	 * Methods for saving 
	 */
	
	public void attemptCommit() {
		String parkingsContents = details.getText().toString();
		parking.details = parkingsContents;
		parking.anyoneCanEdit = anyoneCanEditCheckbox.isChecked(); 
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
		AlertDialog.Builder toggleBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        
        View view = inflater.inflate(R.layout.parkings_activity_saving, null);
        
        TextView title = (TextView) view.findViewById(R.id.dialog_menu_title);
        title.setTypeface(AppBase.getTypefaceStrong());
        
        TextView instructionsForKind = (TextView) view.findViewById(R.id.parking_kind_instructions);
        instructionsForKind.setTypeface(AppBase.getTypefaceStrong());
        
        view.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				formView.dismiss();
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
    	anyoneCanEditCheckbox = (CheckBox) view.findViewById(R.id.parkings_anyoneCanEdit);

    	// On editing mode if we are modifying the parking
    	if(parking != null) {
    		details.setText(parking.details);
    		kindSelector.setSelection(parking.kind-1);
    		title.setText(this.getResources().getString(R.string.actions_update));
    		anyoneCanEditCheckbox.setChecked(parking.anyoneCanEdit);
    		hasRoofCheckbox.setChecked(parking.hasRoof);
    		anyoneCanEditCheckbox.setEnabled(parking.isOwnedByCurrentUser());
    	}
    	
    	Button saveButton = (Button) view.findViewById(R.id.save_parking);
    	saveButton.setTypeface(AppBase.getTypefaceStrong());
    	saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				attemptCommit();
			}
    		
    	});
       
    	toggleBuilder.setView(view);
    	formView = toggleBuilder.create();
    	formView.setCanceledOnTouchOutside(false);
    	formView.show();
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
