package com.wikicleta.activities;

import org.mobility.wikicleta.R;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.wikicleta.common.AppBase;
import com.wikicleta.views.PinchableMapView;

import android.os.Bundle;
import android.view.KeyEvent;

public class DashboardActivity extends MapActivity {

	public PinchableMapView mapView;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppBase.currentActivity = this;
		
		setContentView(R.layout.android_dashboard);
		
		mapView = (PinchableMapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(false);
        this.setMapToDefaultValues();
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
	
	protected GeoPoint getDefaultLocation() {
	     return new GeoPoint((int) (19.412423 * 1E6), (int) (-99.169207 * 1E6));
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        moveTaskToBack(true);
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
