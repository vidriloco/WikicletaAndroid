package org.wikicleta.models;

import java.util.HashMap;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONValue;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "Tips")
public class Tip extends Model {
	
	public String tipEndpoint;
	
	@Column(name = "Category")
	public int category;
	
	@Column(name = "Content")
	public String content;
	
	@Column(name = "Latitude")
	public int latitude;
	
	@Column(name = "Longitude")
	public int longitude;
	
	@Column(name = "Json")
	public String jsonRepresentation;
	
	@Column(name = "CreatedAt")
	public long createdAt;
	
	public Tip(String content, Integer category, int lat, int lon) {
		this.content = content;
		this.category = category;
		this.latitude = lat;
		this.longitude = lon;
	}
	
	public Tip() {
		
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
	
	public void setJsonRepresentation(String representation) {
		jsonRepresentation = StringEscapeUtils.escapeJava(representation);
	}
	
	public String getJsonRepresentation(String representation) {
		return StringEscapeUtils.unescapeJava(jsonRepresentation);
	}
	
	public boolean isDraft() {
		if(this.jsonRepresentation == null)
			return false;
		return !(this.jsonRepresentation.length() == 0);
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
