package org.wikicleta.layers.components;

import org.json.simple.JSONObject;
import org.wikicleta.helpers.GeoHelpers;
import com.google.android.maps.GeoPoint;

public class EcobiciStation extends BikeSharingStation {

	public EcobiciStation(int id, String name, GeoPoint location,
			int freeParkings, int availableBikes) {
		super(id, name, location, freeParkings, availableBikes);
	}
	
	public static EcobiciStation buildFrom(JSONObject object) {
		long id = (Long) object.get("number");
		String name = (String) object.get("name");
		GeoPoint location = GeoHelpers.buildGeoPointFromLatLon(
				(Long) object.get("lat") /1E6, (Long) object.get("lng")/1E6);
		long freeParking = (Long) object.get("free");
		long bikes = (Long) object.get("bikes");
		return new EcobiciStation((int) id, name, location, (int) freeParking, (int) bikes);
	}
}
