	package org.wikicleta.activities;

import org.wikicleta.R;
import org.wikicleta.helpers.GeoHelpers;
import org.wikicleta.services.RoutesManagementService;
import org.wikicleta.services.RoutesManagementService.RoutesManagementBinder;
import org.wikicleta.views.PinchableMapView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MyLocationOverlay;

public class LocationAwareMapActivity extends MapActivity implements LocationListener {
	protected PinchableMapView mapView;
	protected LocationManager locationManager;
	private MyLocationOverlay locationOverlay;
	protected boolean locationManagerEnabled;
	
	protected boolean centerMapOnCurrentLocationByDefault;
	
	// Service connectors
	protected RoutesManagementService theService;
    protected boolean serviceBound = false;
    
	protected void onCreate(Bundle savedInstanceState, int layoutID) {
		super.onCreate(savedInstanceState);
		this.setContentView(layoutID);
		this.centerMapOnCurrentLocationByDefault = true;
		
		mapView = (PinchableMapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(false);
        this.setMapToDefaultValues();
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        this.enableLocationManager();
        
    	locationOverlay = new MyLocationOverlay(this, mapView);
    	mapView.getOverlays().add(locationOverlay);
    	
    	locationOverlay.enableMyLocation();
    	locationOverlay.disableCompass();
	}
	
	protected void setMapToDefaultValues() {
		mapView.getController().animateTo(this.getDefaultLocation());
        mapView.getController().setZoom(18);
	}
	
	protected void setMapToLocation(Location location) {
		mapView.getController().animateTo(GeoHelpers.buildFromLongitude(location));
	}
	
	protected GeoPoint getDefaultLocation() {
		return GeoHelpers.buildFromLatLon(19.412423, -99.169207);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if(this.centerMapOnCurrentLocationByDefault)
			this.setMapToLocation(location);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isLocationManagerEnabled() {
		return this.locationManagerEnabled;
	}
	
	
	protected void enableLocationManager() {
		if(!locationManagerEnabled) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
					100, 10, this);
			locationManagerEnabled = true;
		}
	}
	
	protected void disableLocationManager() {
		if(locationManagerEnabled) {
			locationManager.removeUpdates(this);
			locationManagerEnabled = false;
		}
	}
	
	// Decide if we should leave a service implementation on this activity
	/** Defines callbacks for service binding, passed to bindService() */
    protected ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
        	RoutesManagementBinder binder = (RoutesManagementBinder) service;
            theService = binder.getService();
            serviceBound = true;
            afterServiceConnected();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        	serviceBound = false;
        }
    };
    
    protected void afterServiceConnected() {
    }
    
	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent(this, RoutesManagementService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
	}
}
