package com.wikicleta.activities;

import org.mobility.wikicleta.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MyLocationOverlay;
import com.wikicleta.common.AppBase;
import com.wikicleta.helpers.GeoHelpers;
import com.wikicleta.helpers.PathTrace;
import com.wikicleta.views.PinchableMapView;
import com.wikicleta.views.RouteOverlay;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

public class DashboardActivity extends MapActivity implements LocationListener {

	public PinchableMapView mapView;
	private LocationManager locationManager;
	private MyLocationOverlay locationOverlay;
	
	private ImageView recButton;
	private ImageView pauseButton;
	
	protected PathTrace currentPath;
	protected RouteOverlay routeOverlay;
	
	protected LocationListener listener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppBase.currentActivity = this;
		listener = this;
		
		setContentView(R.layout.android_dashboard);
		
		mapView = (PinchableMapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(false);
        this.setMapToDefaultValues();
        
		currentPath = new PathTrace();
		routeOverlay = new RouteOverlay(currentPath);
		mapView.getOverlays().add(routeOverlay);
		
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);

    	locationOverlay = new MyLocationOverlay(this, mapView);
    	mapView.getOverlays().add(locationOverlay);
    	
    	locationOverlay.enableMyLocation();
    	locationOverlay.disableCompass();
    	
    	recButton = (ImageView) findViewById(R.id.routes_rec_button);
    	pauseButton = (ImageView) findViewById(R.id.routes_pause_button);
    	
    	recButton.setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					recButton.setVisibility(View.GONE);
					pauseButton.setVisibility(View.VISIBLE);
					currentPath.reset();
					mapView.postInvalidate();
					currentPath.setToTracking(true);
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
							100, 10, listener);
				}
		});
    	
    	pauseButton.setOnClickListener(
			new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					pauseButton.setVisibility(View.GONE);
					recButton.setVisibility(View.VISIBLE);
					currentPath.setToTracking(false);
			    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
			    			10000, 10, listener);
				}
		});
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        moveTaskToBack(true);
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onLocationChanged(Location location) {
		this.setMapToLocation(location);
		currentPath.addLocation(location);
		mapView.postInvalidate();
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}
