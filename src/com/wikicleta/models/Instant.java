package com.wikicleta.models;

import java.util.HashMap;

import android.location.Location;
import android.location.LocationManager;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.android.maps.GeoPoint;

@Table(name = "Instants")
public class Instant extends Model {
	@Column(name = "Speed")
	public float speed;
	
	@Column(name = "Latitude")
	public int latitude;
	
	@Column(name = "Longitude")
	public int longitude;
	
	@Column(name = "Time")
	public long time;
	
	public Instant() {
		
	}
	
	public Instant(GeoPoint coordinate, float speed, long time){
		this.latitude = coordinate.getLatitudeE6();
		this.longitude = coordinate.getLongitudeE6();
		this.speed = speed;
		this.time = time;
	}
	
	public float getLatitude() {
		return (float) (this.latitude/ 1E6);
	}
	
	public float getLongitude() {
		return (float) (this.longitude/1E6);
	}
	
	public GeoPoint geoPoint() {
		return new GeoPoint(this.latitude, this.longitude);
	}
	
	public Location location() {
		Location loc = new Location(LocationManager.GPS_PROVIDER);
		loc.setLatitude(this.getLatitude());
		loc.setLongitude(this.getLongitude());
		return loc;
	}
	
	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> coord = new HashMap<String, Object>();
		coord.put("lat", this.getLatitude());
		coord.put("lon", this.getLongitude());
		coord.put("speed", this.speed);
		return coord;
	}
	
	@Column(name = "Route")
	public Route route;
	
}
