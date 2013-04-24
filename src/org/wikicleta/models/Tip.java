package org.wikicleta.models;

import java.util.HashMap;

import org.json.simple.JSONValue;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Tips")
public class Tip extends ModelExt {
	
	public String tipEndpoint;
	
	@Column(name = "Category")
	public int category;
	
	@Column(name = "Content")
	public String content;
	
	@Column(name = "Latitude")
	public int latitude;
	
	@Column(name = "Longitude")
	public int longitude;
	
	public Tip(String content, Integer category, int lat, int lon) {
		this.content = content;
		this.category = category;
		this.latitude = lat;
		this.longitude = lon;
	}
	
	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("category", this.category);
		params.put("content", this.content);
		params.put("created_at", this.createdAt);
		HashMap<String, Float> coordinates = new HashMap<String, Float>();
		coordinates.put("lat", (float) (latitude/1E6));
		coordinates.put("lon", (float) (longitude/1E6));
		params.put("coordinates", coordinates);
		HashMap<String, Object> cover = new HashMap<String, Object>();
		cover.put("tip", params);
		return cover;
	}
	
	public String toJSON() {
		return JSONValue.toJSONString(toHashMap());
	}
	
	protected static void commitLocally(Tip tip) {
		ActiveAndroid.beginTransaction();

		tip.save();
		
		ActiveAndroid.setTransactionSuccessful();
		ActiveAndroid.endTransaction();
	}
}
