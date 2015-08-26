package org.wikicleta.activities.common;

import org.wikicleta.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class LocationAwareMapWithControlsActivity extends ActivityWithLocationAwareMap implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener {
	protected ImageView centerOnMapOn;
	protected ImageView centerOnMapOff;
	protected LinearLayout toolbar;
	protected LocationRequest locationRequest;
	protected boolean firstLocationReceived;
	protected boolean attemptCenterOnLocationAtStart=true;
	
	private GoogleApiClient locationClient = null;
	
	@SuppressLint("UseSparseArrays")
	@Override
	public void onCreate(Bundle savedInstanceState, int layoutID) {
		super.onCreate(savedInstanceState, layoutID);
		assignToggleActionsForAutomapCenter();
		if(attemptCenterOnLocationAtStart)
			turnOnLocation();
	}
	
	protected void assignToggleActionsForAutomapCenter() {
		centerOnMapOff = (ImageView) findViewById(R.id.centermap_search_button_disabled);
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
		if(locationClient == null) {
			int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
			if(resp == ConnectionResult.SUCCESS){
				locationClient = new GoogleApiClient.Builder(getApplicationContext())
	            .addApi(LocationServices.API)
	            .addConnectionCallbacks(this)
	            .addOnConnectionFailedListener(this)
	            .build();
				locationRequest = new LocationRequest();
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
		
		if(locationClient != null  && locationClient.isConnected()) {
	        LocationServices.FusedLocationApi.removeLocationUpdates(locationClient, this);
			this.locationClient.disconnect();
		}


		centerOnMapOff.setVisibility(View.VISIBLE);
		centerOnMapOn.setVisibility(View.GONE);

	}


	@Override
	public void onLocationChanged(Location location) {
		map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
		lastKnownLocation = new LatLng(location.getLatitude(), location.getLongitude());

		if(!this.firstLocationReceived) {
			firstLocationReceived = true;
			this.turnOffLocation();
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(getCurrentOrDefaultLocation(), 18.0f));
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
        LocationServices.FusedLocationApi.requestLocationUpdates(locationClient, locationRequest, this);
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
	}
	
}
