package org.wikicleta.helpers;

import com.google.android.maps.GeoPoint;

import android.location.Location;

public class GeoHelpers {
		
	public static GeoPoint buildFromLongitude(Location location) {
		return buildFromLatLon(location.getLatitude(), location.getLongitude());
	}
	
	public static GeoPoint buildFromLatLon(double lat, double lon) {
		return new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
	}

}
