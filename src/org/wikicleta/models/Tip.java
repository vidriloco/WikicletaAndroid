package org.wikicleta.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONValue;

import android.annotation.SuppressLint;
import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@SuppressLint("SimpleDateFormat")
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
	
	@Column(name = "UserId")
	public long userId;
	
	public Tip(String content, Integer category, int lat, int lon, long userId) {
		this.content = content;
		this.category = category;
		this.latitude = lat;
		this.longitude = lon;
		this.userId = userId;
		this.createdAt = Calendar.getInstance().getTimeInMillis();
	}
	
	public Tip() {
		
	}
	
	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("category", this.category);
		params.put("content", this.content);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String formattedDate = sdf.format(new Date(this.createdAt));
		
		params.put("created_at", formattedDate);
		params.put("updated_at", formattedDate);
		params.put("user_id", this.userId);
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
	
	public String toJSON(HashMap<String, Object> object) {
		HashMap<String, Object> tipEnvelope = toHashMap();
		tipEnvelope.put("extras", object);
		return JSONValue.toJSONString(tipEnvelope);
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
