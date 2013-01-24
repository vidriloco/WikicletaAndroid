package org.wikicleta.layers.components;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class BikeSharingStation extends OverlayItem {
	
	public BikeSharingStation(GeoPoint arg0, String arg1, String arg2) {
		super(arg0, arg1, arg2);
	}
	
	public BikeSharingStation(int id, String name, GeoPoint location, int freeParkings, int availableBikes) {
		this(location, name, "Lugares libres: " + String.valueOf(freeParkings) + " Bicis disponibles: " + String.valueOf(availableBikes));
		this.id = id;
		this.name = name;
		this.location = location;
		this.freeParkings = freeParkings;
		this.availableBikes = availableBikes;
	}
	
	public String name;
	public GeoPoint location;
	public int freeParkings;
	public int availableBikes;
	public int id;
	

}
