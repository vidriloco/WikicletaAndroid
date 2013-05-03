package org.wikicleta.layers.components;

import org.json.simple.JSONObject;
import org.wikicleta.helpers.GeoHelpers;
import org.wikicleta.models.CycleStation;
import android.content.Context;
import com.google.android.maps.GeoPoint;

public class CycleStationOverlayItem extends BaseOverlayItem {
	
	public CycleStation associatedStation;
	
	public CycleStationOverlayItem(Context ctx, GeoPoint point, String arg1, String arg2) {
		super(ctx, point, "", "");
	}
	
	public CycleStationOverlayItem(Context ctx, int id, String name, GeoPoint location, int freeParkings, int availableBikes) {
		this(ctx, location, "", "");
		this.associatedStation = new CycleStation(id, name, location, freeParkings, availableBikes);
		this.setMarkerWithDrawable(associatedStation.status());
	}
	
	public static CycleStationOverlayItem buildFrom(Context ctx, JSONObject object) {
		long id = (Long) object.get("number");
		String name = (String) object.get("name");
		GeoPoint location = GeoHelpers.buildGeoPointFromLatLon(
				(Long) object.get("lat") /1E6, (Long) object.get("lng")/1E6);
		long freeParking = (Long) object.get("free");
		long bikes = (Long) object.get("bikes");
		return new CycleStationOverlayItem(ctx, (int) id, name, location, (int) freeParking, (int) bikes);
	}
}
