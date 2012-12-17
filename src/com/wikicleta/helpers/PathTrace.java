package com.wikicleta.helpers;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

import android.location.Location;

public class PathTrace {
	public ArrayList<GeoPoint> locationList;
	public boolean tracking;
	
	public PathTrace() {
		this.locationList = new ArrayList<GeoPoint>();
		
		this.locationList.add(GeoHelpers.buildFromLatLon(19.428704, -99.168563));
		this.locationList.add(GeoHelpers.buildFromLatLon(19.430566, -99.164615));
		this.locationList.add(GeoHelpers.buildFromLatLon(19.431457, -99.162469));
		this.locationList.add(GeoHelpers.buildFromLatLon(19.431780, -99.161568));

		this.tracking = false;
	}
	
	public void addLocation(Location location) {
		if(tracking) {
			this.locationList.add(GeoHelpers.buildFromLongitude(location));
		}
	}
	
	public void reset() {
		this.setToTracking(false);
		this.locationList.clear();
	}
	
	public void setToTracking(boolean tracking) {
		this.tracking = tracking;
	}
}
