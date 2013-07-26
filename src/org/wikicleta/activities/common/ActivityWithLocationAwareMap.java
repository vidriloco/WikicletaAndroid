package org.wikicleta.activities.common;

import org.wikicleta.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ActivityWithLocationAwareMap extends Activity {
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
		MapFragment mapFragment = (MapFragment) this.getFragmentManager().findFragmentById(R.id.map_fragment);
		map = mapFragment.getMap();
        this.setMapToDefaultValues();  
    }
	
	@Override
	protected void onResume() {
		super.onResume();
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
