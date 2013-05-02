package org.wikicleta.models;

import java.util.ArrayList;
import org.json.simple.JSONObject;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Bikes")
public class Bike extends Model {
	
	@Column(name = "RemoteId")
	public long remoteId;
	
	@Column(name = "Name")
	public String name;
	
	@Column(name = "Brand")
	public String brand;
	
	@Column(name = "ImageURL")
	public String imageURL;
	
	@Column(name = "UpdatedAt")
	public long updatedAt;
	
	@Column(name = "Likes")
	public Long likesCount;
	
	public Bike() {
		
	}
	
	public Bike(long remoteId, String name, String brand, Long updatedAt, String imageURL, Long likes) {
		this.remoteId = remoteId;
		this.updateAttrs(name, brand, updatedAt, imageURL, likes);
	}
	
	public static Bike find(Long id) {
		if(id == null)
			return null;
		ArrayList<Bike> bikesFound = new Select().from(Bike.class).where("RemoteId = ?", id).execute();
		if(bikesFound.isEmpty())
			return null;
		else
			return bikesFound.get(0);
	}
	
	public static Bike newFormJSON(JSONObject object) {
		return new Bike((Long) object.get("id"), (String) object.get("name"), 
				(String) object.get("brand"), 
				Long.parseLong((String) object.get("updated_at_ms")), (String) object.get("bike_photo_url"),
				(Long) object.get("likes_count"));
	}
	
	public static void destroyAll() {
		Bike.delete(Bike.class);
	}
	
	public void updateAttrsFromJSON(JSONObject object) {
		updateAttrs((String) object.get("name"), 
				(String) object.get("brand"), 
				Long.parseLong((String) object.get("updated_at_ms")), (String) object.get("bike_photo_url"),
				(Long) object.get("likes_count"));
	}
	
	public void updateAttrs(String name, String brand, Long updatedAt, String imageURL, Long likesCount) {
		this.brand = brand;
		this.name = name;
		this.updatedAt = updatedAt;
		this.imageURL = imageURL;
		this.likesCount = likesCount;
	}
}
