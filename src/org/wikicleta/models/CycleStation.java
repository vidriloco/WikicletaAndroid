package org.wikicleta.models;

import org.wikicleta.R;

import com.google.android.maps.GeoPoint;

public class CycleStation {
	
	public String name;
	public GeoPoint location;
	public int availableSlots;
	public int availableBikes;
	public int id;

	public CycleStation(int id, String name, GeoPoint location, int availableSlots, int availableBikes) {
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
}
