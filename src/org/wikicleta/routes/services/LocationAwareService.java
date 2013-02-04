package org.wikicleta.routes.services;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationAwareService extends Service implements LocationListener {

	protected Activity boundActivity;
	protected static LocationManager locationManager;
	protected static boolean locationManagerEnabled;
	
	public Location lastLocationCatched;
	
	@Override
	public void onCreate() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.enableLocationManager();
	}
	
	@Override
	public void onLocationChanged(Location location) {
		this.lastLocationCatched = location;	
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void enableLocationManager() {
		if(!locationManagerEnabled) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
					100, 1, this);
			locationManagerEnabled = true;
		}
	}
	
	public void disableLocationManager() {
		if(locationManagerEnabled) {
			locationManager.removeUpdates(this);
			locationManagerEnabled = false;
		}
	}

}
