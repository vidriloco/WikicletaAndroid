package org.wikicleta.common.activities;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LocationAwareMapWithControlsActivity extends LocationAwareMapActivity {
	protected ImageView centerOnMapOn;
	protected ImageView centerOnMapOff;
	protected LinearLayout toolbar;

	
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
	
	protected boolean shouldEnableMyLocationOnResume() {
		return centerOnMapOn.getVisibility() == View.VISIBLE;
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
	
}
