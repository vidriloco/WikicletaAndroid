package org.wikicleta.models;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.android.maps.GeoPoint;

public class Segment implements Serializable {

	private static final long serialVersionUID = 1L;
	public long id;
	public String color;
	public ArrayList<GeoPoint> points;
	
	public Segment(long id, String color, ArrayList<GeoPoint> points) {
		this.id = id;
		this.color = color;
		this.points = points;
	}
	
}
