package org.wikicleta.services.routes;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;

public class LocationAwareService extends Service implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
LocationListener {

	protected Activity boundActivity;
	protected static LocationRequest locationRequest;
    protected LocationClient locationClient;

	protected static boolean locationManagerEnabled;
	
	protected Location lastLocationCatched;

	// Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;

	@Override
	public void onCreate() {
		locationRequest = LocationRequest.create();
		locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		locationRequest.setInterval(MILLISECONDS_PER_SECOND*UPDATE_INTERVAL_IN_SECONDS);
		locationRequest.setFastestInterval(MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS);

		locationClient = new LocationClient(this, this, this);

        this.enableLocationManager();
	}
	
	@Override
	public void onLocationChanged(Location location) {
		this.lastLocationCatched = location;	
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void enableLocationManager() {
		if(!locationManagerEnabled) {
			locationClient.connect();
			locationManagerEnabled = true;
		}
	}
	
	public void disableLocationManager() {
		if(locationManagerEnabled) {
			locationClient.disconnect();
			locationManagerEnabled = false;
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// If already requested, start periodic updates
        locationClient.requestLocationUpdates(locationRequest, this);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

}
