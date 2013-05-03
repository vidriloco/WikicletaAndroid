package org.wikicleta.tips.activities;

import java.util.HashMap;
import org.wikicleta.R;
import org.wikicleta.activities.LocationAwareMapActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.common.FieldValidators;
import org.wikicleta.helpers.SlidingMenuAndActionBarHelper;
import org.wikicleta.models.Tip;
import org.wikicleta.models.User;
import org.wikicleta.routing.Tips;
import org.wikicleta.routing.Others.Cruds;
import org.wikicleta.views.PinOverlay;
import com.google.android.maps.GeoPoint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class ModifyingTipActivity extends LocationAwareMapActivity {
	protected ImageView centerOnMapOn;
	protected ImageView centerOnMapOff;
	protected LinearLayout toolbar;
	protected PinOverlay pinOverlay;
	public AlertDialog  formView;
	
	protected Spinner categorySelector;
	protected EditText content;
	
	public Tip tip;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.tips_activity_new);
		setTheme(R.style.Theme_wikicleta);
		
		AppBase.currentActivity = this;
    	assignToggleActionsForAutomapCenter();

    	tip = (Tip) getIntent().getSerializableExtra("tip");
		if(tip != null && tip.existsOnRemoteServer()) {
			// We are on editing mode
			turnOffLocation();
			this.mapView.getController().animateTo(new GeoPoint(tip.latitude, tip.longitude));
	    	SlidingMenuAndActionBarHelper.loadWithActionBarTitle(this, this.getResources().getString(R.string.tips_edit_title));
		} else {
			tip = new Tip();
	    	// TODO: Move to cancelable alert
	    	showToastMessage();
	    	SlidingMenuAndActionBarHelper.setDefaultFontForActionBar(this);
		}
		
    	
    	
    	this.findViewById(R.id.tips_back_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
    		
    	});
    	
    	pinOverlay = new PinOverlay(this);
    	
    	this.mapView.getOverlays().add(pinOverlay);
    	this.toolbar = (LinearLayout) this.findViewById(R.id.tips_toolbar);

    	this.findViewById(R.id.tips_finish_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Setting tip coordinates
				tip.latitude = pinOverlay.getLocation().getLatitudeE6();
				tip.longitude = pinOverlay.getLocation().getLongitudeE6();
				displaySaveForm();
			}
    		
    	});
    	
	}
	
	protected boolean shouldEnableMyLocationOnResume() {
		return this.centerOnMapOn.getVisibility() == View.VISIBLE;
	}
	
	protected void showToastMessage() {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.message, (ViewGroup) findViewById(R.id.toast_layout_root));
		
		TextView text = (TextView) layout.findViewById(R.id.message_text);
		text.setTypeface(AppBase.getTypefaceLight());
		text.setText(R.string.select_location_on_map);
		Toast toast = new Toast(getApplicationContext());
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.setView(layout);
		toast.show();
	}
	
	protected void assignToggleActionsForAutomapCenter() {
		centerOnMapOff = (ImageView) findViewById(R.id.centermap_search_button);
    	centerOnMapOn = (ImageView) findViewById(R.id.centermap_search_button_enabled);

    	centerOnMapOff.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				turnOnLocation();
			}
		});
    	
    	centerOnMapOn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				turnOffLocation();
			}
		});
	}
	
	protected void turnOnLocation() {
		locationOverlay.enableMyLocation();
		centerOnMapOff.setVisibility(View.GONE);
		centerOnMapOn.setVisibility(View.VISIBLE);
	}
	
	protected void turnOffLocation() {
		locationOverlay.disableMyLocation();
		centerOnMapOff.setVisibility(View.VISIBLE);
		centerOnMapOn.setVisibility(View.GONE);
	}
	
	
	/**
	 * Methods for saving 
	 */
	
	public void attemptCommit() {
		String tipContents = content.getText().toString();
		tip.userId = User.id();
		tip.content = tipContents;
		if(FieldValidators.isFieldEmpty(tipContents)) {
			content.setError(getResources().getString(R.string.tips_input_empty));
			return;
		}
		
		if(FieldValidators.isFieldLongerThan(tipContents, Constants.CHARACTERS_LENGTH_MAX)) {
			content.setError(getResources().getString(R.string.tips_input_length_max_error));
			return;
		}
		
		if(FieldValidators.isFieldShorterThan(tipContents, Constants.CHARACTERS_LENGTH_MIN)) {
			content.setError(getResources().getString(R.string.tips_input_length_min_error));
			return;
		}
		
		Tips.PostOrPut poster = new Tips().new PostOrPut();
		poster.activity = this;
		if(tip.existsOnRemoteServer())
			poster.mode = Cruds.MODIFY;
		poster.execute(tip);
	}
	
	protected void displaySaveForm() {
		AlertDialog.Builder toggleBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        
        View view = inflater.inflate(R.layout.tips_activity_saving, null);
        
        TextView title = (TextView) view.findViewById(R.id.dialog_menu_title);
        title.setTypeface(AppBase.getTypefaceStrong());
        
        TextView instructionsForCategory = (TextView) view.findViewById(R.id.tip_category_instructions);
        instructionsForCategory.setTypeface(AppBase.getTypefaceStrong());
        
        view.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				formView.dismiss();
			}
        	
        });
        
        // Form Fields setup
    	categorySelector = (Spinner) view.findViewById(R.id.tipCategorySelector);
    	String[] strings = {"danger","alert","sightseeing"};
    	TipsCategoriesArrayAdapter adapter = new TipsCategoriesArrayAdapter(this, R.id.tip_category, strings);
    	categorySelector.setAdapter(adapter);
    	categorySelector.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				// Setting tip category
				tip.category = pos+1;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
    		
    	});
    	
    	content = (EditText) view.findViewById(R.id.tips_content);   
    	content.setTypeface(AppBase.getTypefaceLight());
    	content.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				content.setError(null);
			}
    		
    	});
    	
    	// On editing mode if we are modifying the tip
    	if(tip.existsOnRemoteServer()) {
    		content.setText(tip.content);
    		categorySelector.setSelection(tip.category-1);
    		title.setText(this.getResources().getString(R.string.tips_update_title));
    	}
    	
    	Button saveButton = (Button) view.findViewById(R.id.save_tip);
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
        	HashMap<Integer, String> valuesTipCategories = Tip.tipCategories();
            LayoutInflater inflater=getLayoutInflater();
            View row=inflater.inflate(R.layout.tip_row, parent, false);
            
            TextView label=(TextView)row.findViewById(R.id.tip_category);
            
            String categoryCode = valuesTipCategories.get((Integer) position+1);
            String categoryText = getResources().getString(
					getResources().getIdentifier("tips.categories.".concat(categoryCode), "string", getPackageName()));
            label.setText(categoryText);
            label.setTypeface(AppBase.getTypefaceLight());

            ImageView icon=(ImageView) row.findViewById(R.id.tip_category_icon);
            icon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(
            		"tip_".concat(categoryCode).concat("_icon"), "drawable", getPackageName())));
            return row;
        }
    }
	
}
