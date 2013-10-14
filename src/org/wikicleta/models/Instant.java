package org.wikicleta.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.json.simple.JSONObject;

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
	
	@Column(name = "CreatedAt")
	public long createdAt;
	
	public Instant() {
		
	}
	
	public Instant(Location lastLocationCatched, float speed, long time){
		this.latitude = lastLocationCatched.getLatitude();
		this.longitude = lastLocationCatched.getLongitude();
		this.speed = speed;
		this.time = time;
		this.createdAt = new Date().getTime();
	}
	
	public Instant(double lat, double lon, float speed, long time, Date date){
		this.latitude = lat;
		this.longitude = lon;
		this.speed = speed;
		this.time = time;
		this.createdAt = date.getTime();
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
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("lat", this.latitude);
		params.put("lon", this.longitude);
		params.put("speed", this.speed);
		params.put("time", this.time);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		params.put("created_at", sdf.format(new Date(this.createdAt)));
		params.put("updated_at", sdf.format(new Date(this.createdAt)));
		return params;
	}
	
	public static Instant buildFrom(JSONObject object) {
		long elapsedTime = (Long) object.get("elapsed_time");
		float speedAt = Float.parseFloat( (String) object.get("speed_at"));
		
		double lat = (Double) object.get("lat");
		double lon = (Double) object.get("lon");
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date creationDate = null;
		try {
			creationDate = df.parse((String)  object.get("str_created_at"));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new Instant(lat, lon, speedAt, elapsedTime, creationDate);
	}
	
	@Column(name = "Route")
	public Route route;
	
}
