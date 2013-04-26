package org.wikicleta.tips.activities;

import org.wikicleta.R;
import org.wikicleta.activities.LocationAwareMapActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.helpers.SlidingMenuAndActionBarHelper;
import org.wikicleta.views.PinOverlay;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NewTipActivity extends LocationAwareMapActivity {
	protected ImageView centerOnMapOn;
	protected ImageView centerOnMapOff;
	protected LinearLayout toolbar;
	protected PinOverlay pinOverlay;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.tips_activity_new);
		setTheme(R.style.Theme_wikicleta);

		AppBase.currentActivity = this;
    	
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
				Bundle bundleArgs = new Bundle();
				bundleArgs.putInt("lat", pinOverlay.getLocation().getLatitudeE6());
				bundleArgs.putInt("lon", pinOverlay.getLocation().getLongitudeE6());

				AppBase.launchActivityWithBundle(SavingTipActivity.class, bundleArgs);
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
	
}
