package org.wikicleta.tips.activities;

import java.util.HashMap;

import org.wikicleta.R;
import org.wikicleta.activities.LocationAwareMapActivity;
import org.wikicleta.activities.MainMapActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.common.FieldValidators;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.helpers.DataStructures;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.helpers.SlidingMenuAndActionBarHelper;
import org.wikicleta.models.Tip;
import org.wikicleta.models.User;
import org.wikicleta.views.PinOverlay;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
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

public class NewTipActivity extends LocationAwareMapActivity {
	protected ImageView centerOnMapOn;
	protected ImageView centerOnMapOff;
	protected LinearLayout toolbar;
	protected PinOverlay pinOverlay;
	protected AlertDialog  formView;
	
	protected Spinner categorySelector;
	protected HashMap<String, Integer> categoryTipValues;
	protected EditText content;
	
	protected Tip tip;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.tips_activity_new);
		setTheme(R.style.Theme_wikicleta);
		populateTipCategoryValues();
		
		AppBase.currentActivity = this;
    	
		tip = new Tip();
		
    	SlidingMenuAndActionBarHelper.setDefaultFontForActionBar(this);
    	
    	this.findViewById(R.id.tips_back_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
    		
    	});
    	
    	pinOverlay = new PinOverlay(this);
    	
    	assignToggleActionsForAutomapCenter();
    	this.mapView.getOverlays().add(pinOverlay);
    	
    	this.toolbar = (LinearLayout) this.findViewById(R.id.tips_toolbar);
    	showToastMessage();
    	
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
		View layout = inflater.inflate(R.layout.message,
		                               (ViewGroup) findViewById(R.id.toast_layout_root));
		

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
				locationOverlay.enableMyLocation();
				centerOnMapOff.setVisibility(View.GONE);
				centerOnMapOn.setVisibility(View.VISIBLE);
			}
		});
    	
    	centerOnMapOn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				locationOverlay.disableMyLocation();
				centerOnMapOff.setVisibility(View.VISIBLE);
				centerOnMapOn.setVisibility(View.GONE);
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
		
		new PostTipAsyncTask().execute();
	}
	
	public void populateTipCategoryValues() {
		categoryTipValues = new HashMap<String, Integer>();
		categoryTipValues.put("danger", 1);
		categoryTipValues.put("alert", 2);
		categoryTipValues.put("sightseeing", 3);
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
    	formView.show();
	}
	
	private class PostTipAsyncTask extends AsyncTask<Void, Void, Boolean> {
		private ProgressDialog progressDialog;
		
		@Override
		protected Boolean doInBackground(Void... args0) {
			HashMap<String, Object> auth = new HashMap<String, Object>();
			auth.put("auth_token", User.token());
			int requestStatus = NetworkOperations.postJSONTo("/api/tips", tip.toJSON(auth));
			return requestStatus == 200;
		}
		
		@Override
		protected void onPreExecute() {
		    super.onPreExecute();
		    progressDialog = ProgressDialog.show(AppBase.currentActivity, "", 
		    		getResources().getString(R.string.tips_uploading), true);
		}

	    protected void onPostExecute(Boolean success) {
	    	progressDialog.dismiss();
	    	
	    	if(success) {
	    		showToastWithSuccessfulCommitMessage();
	    		// Sends to the layers activity
	    		finish();
	    	} else {
	    		formView.hide();
	    		AlertDialog.Builder builder = DialogBuilder.buildAlertWithTitleAndMessage(AppBase.currentActivity, R.string.notification, R.string.tips_not_commited);
	    		builder.setNeutralButton(getResources().getString(R.string.save_as_draft), new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog,int id) {
	    				tip.save();
	    				AppBase.launchActivity(MainMapActivity.class);
	    	    		showToastWithDraftSaveMessage();
	    				finish();
	    			}
	    		}).setNegativeButton(getResources().getString(R.string.discard), new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog,int id) {
	    				AppBase.launchActivity(MainMapActivity.class);
	    				finish();
	    			}
	    		}).setPositiveButton(getResources().getString(R.string.retry), new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog,int id) {
	    				attemptCommit();
	    			}
	    		});
	    		AlertDialog alert = builder.create();
	    		alert.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface arg0) {
						formView.show();
					}
	    			
	    		});
	    		alert.show();
	    	}
	    }

	     
	 }
	
	protected void showToastWithSuccessfulCommitMessage() {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.message,
		                               (ViewGroup) findViewById(R.id.toast_layout_root));
		
		ImageView icon = (ImageView) layout.findViewById(R.id.message_icon);
		icon.setImageDrawable(getResources().getDrawable(R.drawable.success_icon));
		
		TextView text = (TextView) layout.findViewById(R.id.message_text);
		text.setTypeface(AppBase.getTypefaceLight());
		text.setText(R.string.tips_uploaded_successfully);
		Toast toast = new Toast(getApplicationContext());
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.setView(layout);
		toast.show();
	}
	
	protected void showToastWithDraftSaveMessage() {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.message,
		                               (ViewGroup) findViewById(R.id.toast_layout_root));
		
		ImageView icon = (ImageView) layout.findViewById(R.id.message_icon);
		icon.setImageDrawable(getResources().getDrawable(R.drawable.archive_icon));
		
		TextView text = (TextView) layout.findViewById(R.id.message_text);
		text.setTypeface(AppBase.getTypefaceLight());
		text.setText(R.string.tips_sent_to_drafts);
		Toast toast = new Toast(getApplicationContext());
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.setView(layout);
		toast.show();
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
        	HashMap<Integer, String> valuesTipCategories = DataStructures.invert(categoryTipValues);
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
