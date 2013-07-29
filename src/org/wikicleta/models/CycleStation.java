package org.wikicleta.models;

import org.json.simple.JSONObject;
import org.wikicleta.R;
import com.google.android.gms.maps.model.LatLng;

public class CycleStation implements MarkerInterface {
	
	public String name;
	public LatLng location;
	public int availableSlots;
	public int availableBikes;
	public int id;

	public CycleStation(int id, String name, LatLng location, int availableSlots, int availableBikes) {
		this.availableBikes = availableBikes;
		this.availableSlots = availableSlots;
		this.id = id;
		this.location = location;
		this.name = name;
	}
	
	public int stationCapacity() {
		return this.availableSlots+this.availableBikes;
	}
	
	/**
	 * Returns the cycle-station status associated to a resource drawable
	 * @return
	 */
	public int status() {
		if(availableSlots == 0 || availableBikes == 0)
			return R.drawable.bike_sharing_red;
		// If percentage of available parking slots or available bikes is less than 30%, yellow marker
		else if((availableSlots*100)/stationCapacity() < 30 || (availableBikes*100)/stationCapacity() < 30)
			return R.drawable.bike_sharing_yellow;
		return R.drawable.bike_sharing_green;
	}
	
	public static CycleStation buildFrom(JSONObject object) {
		long id = (Long) object.get("number");
		String name = (String) object.get("name");
		LatLng location = new LatLng(((Long) object.get("lat"))/1E6 , ((Long) object.get("lng"))/1E6);
		long freeParking = (Long) object.get("free");
		long bikes = (Long) object.get("bikes");
		return new CycleStation((int) id, name, location, (int) freeParking, (int) bikes);
	}

	@Override
	public LatLng getLatLng() {
		return this.location;
	}

	@Override
	public int getDrawable() {
		return this.status();
	}
}
