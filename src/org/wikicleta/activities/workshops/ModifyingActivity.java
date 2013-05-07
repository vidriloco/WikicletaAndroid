package org.wikicleta.activities.workshops;

import java.util.HashMap;
import org.wikicleta.R;
import org.wikicleta.activities.common.LocationAwareMapWithControlsActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.common.FieldValidators;
import org.wikicleta.helpers.SlidingMenuAndActionBarHelper;
import org.wikicleta.models.Tip;
import org.wikicleta.models.Workshop;
import org.wikicleta.routing.Others.Cruds;
import org.wikicleta.routing.Workshops;
import org.wikicleta.views.PinOverlay;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.maps.GeoPoint;

public class ModifyingActivity extends LocationAwareMapWithControlsActivity {

	protected PinOverlay pinOverlay;
	public AlertDialog  formView;
	
	protected EditText name;
	protected EditText details;
	protected CheckBox isStore;
	protected CheckBox anyoneCanEditCheckbox;
	protected EditText horary;
	protected EditText twitter;
	protected EditText phone;
	protected EditText cellPhone;
	protected EditText webpage;

	public Workshop workshop;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.select_poi_on_map);
		setTheme(R.style.Theme_wikicleta);
		
		AppBase.currentActivity = this;
    	assignToggleActionsForAutomapCenter();

    	workshop = (Workshop) getIntent().getSerializableExtra("workshop");
		if(workshop != null && workshop.existsOnRemoteServer()) {
			// We are on editing mode
			turnOffLocation();
			this.mapView.getController().animateTo(new GeoPoint(workshop.latitude, workshop.longitude));
			SlidingMenuAndActionBarHelper.setDefaultFontForActionBarWithTitle(this, R.string.workshops_edit_title);
		} else {
			workshop = new Workshop();
	    	// TODO: Move to cancelable alert
	    	showToastMessage();
			SlidingMenuAndActionBarHelper.setDefaultFontForActionBarWithTitle(this, R.string.workshops_new_title);
		}
    	
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
				workshop.latitude = pinOverlay.getLocation().getLatitudeE6();
				workshop.longitude = pinOverlay.getLocation().getLongitudeE6();
				displaySaveForm();
			}
    		
    	});
    	
	}
	
	/**
	 * Methods for saving 
	 */
	
	public void attemptCommit() {
		String workshopDetails = details.getText().toString();
		workshop.details = workshopDetails;
		
		String workshopName = name.getText().toString();
		workshop.name = workshopName;
		
		String twitterValue = twitter.getText().toString();
		workshop.twitter = twitterValue;
		
		if(phone.getText().toString().length() > 0) 
			workshop.phone = Integer.valueOf(phone.getText().toString());
		
		if(cellPhone.getText().toString().length() > 0) 
			workshop.cellPhone = Integer.valueOf(cellPhone.getText().toString());
		
		workshop.horary = horary.getText().toString();
		workshop.webpage = webpage.getText().toString();
		
		workshop.isStore = isStore.isChecked();
		workshop.anyoneCanEdit = anyoneCanEditCheckbox.isChecked();
		
		if(FieldValidators.isFieldEmpty(workshopName)) {
			name.setError(getResources().getString(R.string.workshops_input_empty_name));
			return;
		}
		
		if(FieldValidators.isFieldLongerThan(workshopName, 30)) {
			name.setError(getResources().getString(R.string.workshops_input_length_max_error_name));
			return;
		}
		
		if(FieldValidators.isFieldShorterThan(workshopName, 8)) {
			name.setError(getResources().getString(R.string.workshops_input_length_min_error_name));
			return;
		}
		
		if(FieldValidators.isFieldEmpty(workshopDetails)) {
			details.setError(getResources().getString(R.string.workshops_input_empty));
			return;
		}
		
		if(FieldValidators.isFieldLongerThan(workshopDetails, Constants.CHARACTERS_LENGTH_MAX)) {
			details.setError(getResources().getString(R.string.workshops_input_length_max_error));
			return;
		}
		
		if(FieldValidators.isFieldShorterThan(workshopDetails, Constants.CHARACTERS_LENGTH_MIN)) {
			details.setError(getResources().getString(R.string.workshops_input_length_min_error));
			return;
		}
		
		if(!FieldValidators.isFieldEmpty(twitterValue) && twitterValue.contains("@")) {
			twitter.setError(getResources().getString(R.string.workshops_bad_input_twitter_format));
			return;
		}
		
		Workshops.PostOrPut poster = new Workshops().new PostOrPut();
		poster.activity = this;
		if(workshop.existsOnRemoteServer())
			poster.mode = Cruds.MODIFY;
		poster.execute(workshop);
	}
	
	protected void displaySaveForm() {
		AlertDialog.Builder toggleBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        
        final View view = inflater.inflate(R.layout.workshops_activity_saving, null);
        
        TextView title = (TextView) view.findViewById(R.id.dialog_menu_title);
        title.setTypeface(AppBase.getTypefaceStrong());
        
        view.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				formView.dismiss();
			}
        	
        });
        
        TextView extraFieldsInstruction = (TextView) view.findViewById(R.id.workshops_hint_extra_fields);
        extraFieldsInstruction.setTypeface(AppBase.getTypefaceStrong());
        
        extraFieldsInstruction.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(view.findViewById(R.id.extra_fields_container).getVisibility() == View.GONE)
					view.findViewById(R.id.extra_fields_container).setVisibility(View.VISIBLE);
				else
					view.findViewById(R.id.extra_fields_container).setVisibility(View.GONE);
			}
        	
        });
        
        name = (EditText) view.findViewById(R.id.workshops_name);   
        name.setTypeface(AppBase.getTypefaceLight());
        name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				name.setError(null);
			}
    		
    	});
        
        details = (EditText) view.findViewById(R.id.workshops_details);   
        details.setTypeface(AppBase.getTypefaceLight());
        details.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				details.setError(null);
			}
    		
    	});
        
        horary = (EditText) view.findViewById(R.id.workshops_horary);   
        horary.setTypeface(AppBase.getTypefaceLight());
        
        twitter = (EditText) view.findViewById(R.id.workshops_twitter);   
        twitter.setTypeface(AppBase.getTypefaceLight());

        cellPhone = (EditText) view.findViewById(R.id.workshops_cellphone);   
        cellPhone.setTypeface(AppBase.getTypefaceLight());

        phone = (EditText) view.findViewById(R.id.workshops_phone);   
        phone.setTypeface(AppBase.getTypefaceLight());

        webpage = (EditText) view.findViewById(R.id.workshops_webpage);   
        webpage.setTypeface(AppBase.getTypefaceLight());

    	isStore = (CheckBox) view.findViewById(R.id.workshops_isStore);
    	anyoneCanEditCheckbox = (CheckBox) view.findViewById(R.id.workshops_anyoneCanEdit);
    	
    	// On editing mode if we are modifying the tip
    	if(workshop.existsOnRemoteServer()) {
    		details.setText(workshop.details);
    		name.setText(workshop.name);
    		horary.setText(workshop.horary);
    		twitter.setText(workshop.twitter);
    		if(workshop.cellPhone != 0)
    			cellPhone.setText(String.valueOf(workshop.cellPhone));
    		if(workshop.phone != 0)
    			phone.setText(String.valueOf(workshop.phone));
    		webpage.setText(workshop.webpage);
    		title.setText(this.getResources().getString(R.string.actions_update));
    		isStore.setChecked(workshop.isStore);
    		anyoneCanEditCheckbox.setEnabled(workshop.isOwnedByCurrentUser());
    	}
    	
    	
    	Button saveButton = (Button) view.findViewById(R.id.save_workshop);
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
	
	public class TipsCategoriesArrayAdapter extends ArrayAdapter<String>{
        
        public TipsCategoriesArrayAdapter(Context context, int textViewResourceId,   String[] objects) {
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
        	HashMap<Integer, String> valuesTipCategories = Tip.getCategories();
            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.poi_category_row, parent, false);
            
            TextView label=(TextView)row.findViewById(R.id.category);
            
            String categoryCode = valuesTipCategories.get((Integer) position+1);
            String categoryText = getResources().getString(
					getResources().getIdentifier("tips.categories.".concat(categoryCode), "string", getPackageName()));
            label.setText(categoryText);
            label.setTypeface(AppBase.getTypefaceLight());

            ImageView icon=(ImageView) row.findViewById(R.id.icon);
            icon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(
            		"tip_".concat(categoryCode).concat("_icon"), "drawable", getPackageName())));
            return row;
        }
    }
	
}