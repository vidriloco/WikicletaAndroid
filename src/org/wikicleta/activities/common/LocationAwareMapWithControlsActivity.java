package org.wikicleta.activities.common;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LocationAwareMapWithControlsActivity extends ActivityWithLocationAwareMap implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener,LocationListener {
	protected LinearLayout centerOnMapOn;
	protected LinearLayout centerOnMapOff;
	protected LinearLayout toolbar;
	protected LocationClient locationClient;
	protected LocationRequest locationRequest;
	 
	protected void assignToggleActionsForAutomapCenter() {
		centerOnMapOff = (LinearLayout) findViewById(R.id.centermap_search_button);
    	centerOnMapOn = (LinearLayout) findViewById(R.id.centermap_search_button_enabled);
    	
    	((TextView) this.findViewById(R.id.centermap_button_text)).setTypeface(AppBase.getTypefaceStrong());
    	((TextView) this.findViewById(R.id.centermap_button_enabled_text)).setTypeface(AppBase.getTypefaceStrong());

    	
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
		if(locationClient == null) {
			int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
			if(resp == ConnectionResult.SUCCESS){
				locationClient = new LocationClient(this,this,this);
				locationRequest = LocationRequest.create();
				locationRequest.setInterval(1000);
			}
		}
		
		locationClient.connect();

		map.setMyLocationEnabled(true);
		
		centerOnMapOff.setVisibility(View.GONE);
		centerOnMapOn.setVisibility(View.VISIBLE);
		
	}
	
	protected void turnOffLocation() {
		map.setMyLocationEnabled(false);
		locationClient.removeLocationUpdates(this);
		this.locationClient.disconnect();

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

	@Override
	public void onLocationChanged(Location location) {
		map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
		Log.e("WIKICLETA", "Updated");
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		locationClient.requestLocationUpdates(locationRequest, this);		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
}
