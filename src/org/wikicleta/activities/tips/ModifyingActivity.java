package org.wikicleta.activities.tips;

import java.util.HashMap;
import org.wikicleta.R;
import org.wikicleta.activities.common.LocationAwareMapWithControlsActivity;
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
import android.widget.AdapterView.OnItemSelectedListener;

public class ModifyingActivity extends LocationAwareMapWithControlsActivity {

	protected PinOverlay pinOverlay;
	public AlertDialog  formView;
	
	protected Spinner categorySelector;
	protected EditText content;
	
	public Tip tip;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.select_poi_on_map);
		setTheme(R.style.Theme_wikicleta);
		
		AppBase.currentActivity = this;
    	assignToggleActionsForAutomapCenter();

    	tip = (Tip) getIntent().getSerializableExtra("tip");
		if(tip != null && tip.existsOnRemoteServer()) {
			// We are on editing mode
			turnOffLocation();
			this.mapView.getController().animateTo(new GeoPoint(tip.latitude, tip.longitude));
			SlidingMenuAndActionBarHelper.setDefaultFontForActionBarWithTitle(this, R.string.tips_edit_title);
		} else {
			tip = new Tip();
	    	// TODO: Move to cancelable alert
	    	showToastMessage();
	    	SlidingMenuAndActionBarHelper.setDefaultFontForActionBar(this);
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
				tip.latitude = pinOverlay.getLocation().getLatitudeE6();
				tip.longitude = pinOverlay.getLocation().getLongitudeE6();
				displaySaveForm();
			}
    		
    	});
    	
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
    	
    	TipsCategoriesArrayAdapter adapter = new TipsCategoriesArrayAdapter(this, 0, Tip.getCategoriesValues());
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
    		title.setText(this.getResources().getString(R.string.actions_update));
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