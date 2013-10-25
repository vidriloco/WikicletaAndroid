package org.wikicleta.helpers;

import com.google.android.gms.maps.model.LatLng;

import android.location.Location;
import android.location.LocationManager;

public class GeoHelpers {
	
	public static Location buildLocationFromLatLon(double lat, double lon) {
		Location location = new Location(LocationManager.GPS_PROVIDER);
		location.setLatitude(lat);
		location.setLongitude(lon);
		return location;
	}

	/**
	 * Returns the distance in meters between two given GeoPoints
	 * @param first GeoPoint 
	 * @param second GeoPoint
	 * @return a float with the distance given in meters
	 */
	public static float distanceBetweenGeoPoints(LatLng first, LatLng second) {
		Location firstLocation = buildLocationFromLatLon(first.latitude, first.longitude);
		Location secondLocation = buildLocationFromLatLon(second.latitude, second.longitude);
		
		return firstLocation.distanceTo(secondLocation)/1000;
	}
}
