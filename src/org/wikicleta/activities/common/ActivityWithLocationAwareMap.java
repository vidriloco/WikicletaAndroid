package org.wikicleta.activities.common;

import org.wikicleta.R;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import android.os.Bundle;

public class ActivityWithLocationAwareMap extends SherlockFragmentActivity {
	protected GoogleMap map;
	protected LatLng lastKnownLocation;
	protected boolean centerMapOnCurrentLocationByDefault;
	
	public void onCreate(Bundle savedInstanceState, int layoutID) {
		super.onCreate(savedInstanceState);
		this.setContentView(layoutID);
		this.initialize();
	}
	
	protected void initialize() {
		this.centerMapOnCurrentLocationByDefault = true;
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
		map = mapFragment.getMap();
        this.setMapToDefaultValues();  
    }
	
	@Override
	protected void onResume() {
		super.onResume();
		overridePendingTransition(R.anim.right_to_left, R.anim.fade_to_black);
		this.map.setMyLocationEnabled(shouldEnableMyLocationOnResume());
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		this.map.setMyLocationEnabled(false);
	}
	
	protected boolean shouldEnableMyLocationOnResume() {
		return true;
	}
	
	protected void setMapToDefaultValues() {
		map.getUiSettings().setZoomControlsEnabled(false);
		map.getUiSettings().setZoomGesturesEnabled(true);
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(this.getDefaultLocation(), 18.0f));
   	 	map.getUiSettings().setMyLocationButtonEnabled(false);
	}
	
	protected void setMapToLocation(LatLng location) {
		if(location != null)
			map.animateCamera(CameraUpdateFactory.newLatLng(location));
	}
	
	protected LatLng getDefaultLocation() {
		return new LatLng(19.412423, -99.169207);
	}
	
	protected LatLng getCurrentOrDefaultLocation() {
		if(this.lastKnownLocation != null)
			return this.lastKnownLocation;
		else
			return this.getDefaultLocation();
	}
	
}
