	package org.wikicleta.activities;

import org.wikicleta.R;
import org.wikicleta.helpers.GeoHelpers;
import org.wikicleta.views.PinchableMapView;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class LocationAwareMapActivity extends MapActivity {
	protected PinchableMapView mapView;
	protected LocationManager locationManager;
	protected CustomMyLocationOverlay locationOverlay;
	
	protected boolean centerMapOnCurrentLocationByDefault;
	
	public void onCreate(Bundle savedInstanceState, int layoutID) {
		super.onCreate(savedInstanceState);
		this.setContentView(layoutID);
		this.initialize();
	}
	
	protected void initialize() {
		this.centerMapOnCurrentLocationByDefault = true;
		this.mapView = (PinchableMapView) findViewById(R.id.mapview);
        this.mapView.setBuiltInZoomControls(false);
        this.setMapToDefaultValues();
                
    	locationOverlay = new CustomMyLocationOverlay(this, mapView);
    	mapView.getOverlays().add(locationOverlay);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(shouldEnableMyLocationOnResume()) 
			locationOverlay.enableMyLocation();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		locationOverlay.disableMyLocation();
	}
	
	protected boolean shouldEnableMyLocationOnResume() {
		return true;
	}
	
	protected void setMapToDefaultValues() {
		mapView.getController().animateTo(this.getDefaultLocation());
        mapView.getController().setZoom(18);
	}
	
	protected void setMapToLocation(Location location) {
		if(location != null)
			mapView.getController().animateTo(GeoHelpers.buildGeoPointFromLongitude(location));
	}
	
	protected GeoPoint getDefaultLocation() {
		return GeoHelpers.buildGeoPointFromLatLon(19.412423, -99.169207);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	protected class CustomMyLocationOverlay extends MyLocationOverlay {

		public CustomMyLocationOverlay(Context arg0, MapView arg1) {
			super(arg0, arg1);
		}
		
		@Override
		public void onLocationChanged(Location location) {
			super.onLocationChanged(location);
			setMapToLocation(location);
		}

		
	}
	
}
