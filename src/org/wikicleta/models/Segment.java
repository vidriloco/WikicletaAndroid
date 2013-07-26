package org.wikicleta.models;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

public class Segment implements Serializable {

	private static final long serialVersionUID = 1L;
	public long id;
	public String color;
	public ArrayList<LatLng> points;
	
	public Segment(long id, String color, ArrayList<LatLng> points) {
		this.id = id;
		this.color = color;
		this.points = points;
	}
	
}
