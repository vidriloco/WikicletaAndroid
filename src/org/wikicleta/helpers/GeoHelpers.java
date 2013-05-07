package org.wikicleta.helpers;

import com.google.android.maps.GeoPoint;

import android.location.Location;
import android.location.LocationManager;

public class GeoHelpers {
		
	public static GeoPoint buildGeoPointFromLongitude(Location location) {
		return buildGeoPointFromLatLon(location.getLatitude(), location.getLongitude());
	}
	
	public static GeoPoint buildGeoPointFromLatLon(double lat, double lon) {
		return new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
	}
	
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
	public static float distanceBetweenGeoPoints(GeoPoint first, GeoPoint second) {
		Location firstLocation = buildLocationFromLatLon(first.getLatitudeE6()/1E6, first.getLongitudeE6()/1E6);
		Location secondLocation = buildLocationFromLatLon(second.getLatitudeE6()/1E6, second.getLongitudeE6()/1E6);
		
		return firstLocation.distanceTo(secondLocation)/1000;
	}
}
