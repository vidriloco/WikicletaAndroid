package org.wikicleta.models;

import org.json.simple.JSONObject;
import org.wikicleta.R;
import org.wikicleta.interfaces.MarkerInterface;
import org.wikicleta.interfaces.RemoteModelInterface;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class CycleStation implements MarkerInterface, RemoteModelInterface {
	
	public String name;
	public LatLng location;
	public int availableSlots;
	public int availableBikes;
	protected Marker marker;
	public String agency;

	public CycleStation(String agency, String name, LatLng location, int availableSlots, int availableBikes) {
		this.agency = agency;
		this.availableBikes = availableBikes;
		this.availableSlots = availableSlots;
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
		String name = (String) object.get("name");
		LatLng location = new LatLng((Double) object.get("lat"), (Double) object.get("lon"));
		long freeParking = (Long) object.get("free_slots");
		long bikes = (Long) object.get("bikes_available");
		String agency = (String) object.get("agency");
		return new CycleStation(agency, name, location, (int) freeParking, (int) bikes);
	}

	@Override
	public LatLng getLatLng() {
		return this.location;
	}

	@Override
	public int getDrawable() {
		return this.status();
	}

	@Override
	public Marker getAssociatedMarker() {
		return marker;
	}

	@Override
	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	@Override
	public long getRemoteId() {
		return 0;
	}

	@Override
	public String getKind() {
		return null;
	}

}
