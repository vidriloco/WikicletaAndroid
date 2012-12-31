package com.wikicleta.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;
import android.location.Location;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Routes")
public class Route extends Model {
	
	@Column(name = "Name")
	public String name;
	
	@Column(name = "Tags")
	public String tags;
	
	@Column(name = "ElapsedTime")
	public long elapsedTime;
	
	@Column(name = "AverageSpeed")
	public float averageSpeed;
	
	@Column(name = "Kilometers")
	public float kilometers;
	
	@Column(name = "CreatedAt")
	public long createdAt;
	
	@Column(name = "Json")
	public String jsonRepresentation;
	
	public List<Instant> instants() {
		return getMany(Instant.class, "Route");
	}
	
	public Route() {
		
	}
	
	public Route(String name, String tags, long elapsedTime, float averageSpeed, float kilometers, long createdAt) {
		this(elapsedTime, averageSpeed, kilometers, createdAt);
		this.name = name;
		this.tags = tags;
	}
	
	public Route(long elapsedTime, float averageSpeed, float kilometers, long createdAt) {
		this.elapsedTime = elapsedTime;
		this.averageSpeed = averageSpeed;
		this.kilometers = kilometers;
		this.createdAt = createdAt;
	}
	
	public HashMap<String, Object> toHashMap() {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("name", this.name);
		params.put("tags", this.tags);
		params.put("elapsedTime", this.elapsedTime);
		params.put("averageSpeed", this.averageSpeed);
		params.put("kilometers", this.kilometers);
		params.put("createdAt", this.createdAt);
		
		return params;
	}
	
	public void setJsonRepresentation(String representation) {
		jsonRepresentation = StringEscapeUtils.escapeJava(representation);
	}
	
	public String getJsonRepresentation(String representation) {
		return StringEscapeUtils.unescapeJava(jsonRepresentation);
	}
	
	public static ArrayList<Route> queued() {
		return new Select().from(Route.class).execute();
	}
	
	public boolean isDraft() {
		return this.jsonRepresentation.length() != 0;
	}
	
	public Location getStartingLocation() {
		return this.instants().get(0).location();
	}
	
	@Override
	public void delete() {
		super.delete();
		
		for(Instant instant : this.instants()) {
			instant.delete();
		}
	}
}
