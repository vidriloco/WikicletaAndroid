package org.wikicleta.models;

import java.util.HashMap;

import android.location.Location;
import android.location.LocationManager;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.android.gms.maps.model.LatLng;

@Table(name = "Instants")
public class Instant extends Model {
	@Column(name = "Speed")
	public float speed;
	
	@Column(name = "Latitude")
	public double latitude;
	
	@Column(name = "Longitude")
	public double longitude;
	
	@Column(name = "Time")
	public long time;
	
	public Instant() {
		
	}
	
	public Instant(Location lastLocationCatched, float speed, long time){
		this.latitude = lastLocationCatched.getLatitude();
		this.longitude = lastLocationCatched.getLongitude();
		this.speed = speed;
		this.time = time;
	}
	
	public LatLng geoPoint() {
		return new LatLng(this.latitude, this.longitude);
	}
	
	public Location location() {
		Location loc = new Location(LocationManager.GPS_PROVIDER);
		loc.setLatitude(this.latitude);
		loc.setLongitude(this.longitude);
		return loc;
	}
	
	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> coord = new HashMap<String, Object>();
		coord.put("lat", this.latitude);
		coord.put("lon", this.longitude);
		coord.put("speed", this.speed);
		coord.put("time", this.time);
		return coord;
	}
	
	@Column(name = "Route")
	public Route route;
	
}
