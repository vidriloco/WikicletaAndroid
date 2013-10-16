package org.wikicleta.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.simple.JSONObject;
import org.wikicleta.R;

import android.content.Context;

public class LightPOI {

	public String title;
	public Integer category;
	public String description;
	public double lat;
	public double lon;
	public String kind;
	public Date updatedAt;
	
	LightPOI(String title, String description, double lat, double lon, String kind, Date updatedAt) {
		this.title = title;
		this.description = description;
		this.lat = lat;
		this.lon = lon;
		this.kind = kind;
		this.updatedAt = updatedAt;
	}
	
	LightPOI(Integer category, String description, double lat, double lon, String kind, Date updatedAt) {
		this.category = category;
		this.description = description;
		this.lat = lat;
		this.lon = lon;
		this.kind = kind;
		this.updatedAt = updatedAt;
	}

	public static LightPOI buildFrom(JSONObject json) {
		return buildFrom((String) json.get("kind"), json);
	}

	
	public static LightPOI buildFrom(String kind, JSONObject json) {
		String title = null;
		long category = -1;
		if(json.get("title") instanceof Long) {
			category = (Long) json.get("title");
		} else {
			title = (String) json.get("title");
		}
		
		String details = (String) json.get("description");

		double lat = (Double) json.get("lat");
		double lon = (Double) json.get("lon");

	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date updateDate = null;
		try {
			updateDate = df.parse((String)  json.get("str_updated_at"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(title == null)
			return new LightPOI((int) category, details, lat, lon, kind, updateDate);
		else
			return new LightPOI(title, details, lat, lon, kind, updateDate);
	}
	
	public String getTitle(Context context) {
		if(title==null) {
			if(kind.equalsIgnoreCase("Tip")) {
				return context.getResources().getString(
						context.getResources().getIdentifier(
		        				"tips.categories.".concat(Tip.getCategories().get(this.category)), "string", context.getPackageName()));
			} else {
				return context.getResources().getString(
						context.getResources().getIdentifier(
		        				"parkings.kinds.".concat(Parking.getKinds().get(this.category)), "string", context.getPackageName()));
			}
		} else 
			return title;
	}

	public CharSequence kindString(Context context) {
		if(this.kind.equalsIgnoreCase("Tip")) 
			return context.getResources().getString(R.string.tip);
		else if(this.kind.equalsIgnoreCase("Parking"))
			return context.getResources().getString(R.string.parking);
		else if(this.kind.equalsIgnoreCase("Route"))
			return context.getResources().getString(R.string.route);
		else
			return context.getResources().getString(R.string.workshop);
	}
}
