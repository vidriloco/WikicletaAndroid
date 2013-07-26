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
	
	public Instant(LatLng coordinate, float speed, long time){
		this.latitude = coordinate.latitude;
		this.longitude = coordinate.longitude;
		this.speed = speed;
		this.time = time;
	}
	
	public float getLatitude() {
		return (float) (this.latitude/ 1E6);
	}
	
	public float getLongitude() {
		return (float) (this.longitude/1E6);
	}
	
	public LatLng geoPoint() {
		return new LatLng(this.latitude, this.longitude);
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
