package org.wikicleta.tips.activities;

import java.util.HashMap;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.helpers.DataStructures;
import org.wikicleta.helpers.SlidingMenuAndActionBarHelper;
import org.wikicleta.models.Tip;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SavingTipActivity extends Activity {
	
	protected Spinner categorySelector;
	protected HashMap<String, Integer> categoryTipValues;
	
	protected int lat;
	protected int lon;
	protected int selectedCategory;
	protected EditText content;
	
	protected Tip tip;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_wikicleta);
		
        AppBase.currentActivity = this;
        this.populateTipCategoryValues();
        
        this.setContentView(R.layout.tips_activity_saving);
    	SlidingMenuAndActionBarHelper.setDefaultFontForActionBar(this);
    	ActionBar actionBar = (ActionBar) this.findViewById(R.id.actionbar);

        actionBar.addAction(new Action() {

			@Override
			public int getDrawable() {
				return R.drawable.close_icon;
			}

			@Override
			public void performAction(View view) {
				finish();
			}
        	
        });
        
    	Bundle extras = this.getIntent().getExtras();
    	
    	categorySelector = (Spinner) findViewById(R.id.tipCategorySelector);
    	String[] strings = {"danger","alert","sightseeing"};
    	TipsCategoriesArrayAdapter adapter = new TipsCategoriesArrayAdapter(this, R.id.tip_category, strings);
    	categorySelector.setAdapter(adapter);
    	categorySelector.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				selectedCategory = pos+1;
				Log.i("WIKICLETA", String.valueOf(selectedCategory) + parent.getItemAtPosition(pos).toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
    		
    	});
    	
    	content = (EditText) findViewById(R.id.tips_content);    	
    	Button saveButton = (Button) findViewById(R.id.save_tip);
    	saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(content.getText().length() >= Constants.CHARACTERS_LENGTH_MIN) {
					tip = new Tip(content.getText().toString(), selectedCategory, lat, lon);
					 new PostTipAsyncTask().execute();

				} else {
					showAlertMinTextNotReachedMessage();
				}
			}
    		
    	});
    	
    	if(!extras.containsKey("lat") && !extras.containsKey("lon")) {
    		finish();
    	} else {
    		lat = (Integer) extras.get("lat");
    		lon = (Integer) extras.get("lon");
    	}
	}
	
	protected void showAlertMinTextNotReachedMessage() {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.message,
		                               (ViewGroup) findViewById(R.id.toast_layout_root));

		ImageView iconImage = (ImageView) layout.findViewById(R.id.message_icon);
		iconImage.setImageDrawable(this.getResources().getDrawable(R.drawable.alert_icon));
		TextView text = (TextView) layout.findViewById(R.id.message_text);
		text.setTypeface(AppBase.getTypefaceLight());
		text.setText(R.string.tips_alert_min_input_length_not_reached);
		Toast toast = new Toast(getApplicationContext());
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
	}

	public void populateTipCategoryValues() {
		categoryTipValues = new HashMap<String, Integer>();
		categoryTipValues.put("danger", 1);
		categoryTipValues.put("alert", 2);
		categoryTipValues.put("sightseeing", 3);
	}
	
	private class PostTipAsyncTask extends AsyncTask<Void, Void, Void> {
		int requestStatus;
		
		@Override
		protected Void doInBackground(Void... args0) {
			Log.e("WIKICLETA", tip.toJSON());
			requestStatus = NetworkOperations.postJSONTo("/api/tips", tip.toJSON());
			return null;
		}
		
		protected void onProgressUpdate(Void... nada) {
			
	    }

	    protected void onPostExecute(Void nada) {
	    	if(requestStatus == 200) {
	    		Log.e("WIKICLETA", "Mostrar ventana");
	    	}
	    }

	     
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

            ImageView icon=(ImageView) row.findViewById(R.id.tip_category_icon);
            icon.setImageDrawable(getResources().getDrawable(getResources().getIdentifier(
            		"tip_".concat(categoryCode).concat("_icon"), "drawable", getPackageName())));
            return row;
            }
        }
}
