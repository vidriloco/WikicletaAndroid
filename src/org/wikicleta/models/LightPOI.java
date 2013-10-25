package org.wikicleta.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;
import org.wikicleta.R;
import org.wikicleta.interfaces.ListedModelInterface;

import android.content.Context;

public class LightPOI implements ListedModelInterface {

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

	@Override
	public int getDrawable() {
		if(this.kind.equalsIgnoreCase("Tip")) {
			if(Tip.getCategories().get(this.category).equalsIgnoreCase("danger"))
				return R.drawable.tip_danger_icon;
			else if(Tip.getCategories().get(this.category).equalsIgnoreCase("alert"))
				return R.drawable.tip_alert_icon;
			else
				return R.drawable.tip_sightseeing_icon;
		} else if(this.kind.equalsIgnoreCase("Parking")) {
			if(Parking.getKinds().get(this.category) == "government_provided") {
				return R.drawable.parking_government_provided_icon;
			} else if(Parking.getKinds().get(this.category) == "urban_mobiliary") {
				return R.drawable.parking_urban_mobiliary_icon;
			} else 
				return R.drawable.parking_venue_provided_icon;
		} else if(this.kind.equalsIgnoreCase("Route"))
			return R.drawable.start_flag_marker;
		else
			return R.drawable.workshop_icon;
	}

	@Override
	public int getTitle() {
		if(this.kind.equalsIgnoreCase("Tip")) 
			return R.string.tip;
		else if(this.kind.equalsIgnoreCase("Parking"))
			return R.string.parking;
		else if(this.kind.equalsIgnoreCase("Route"))
			return R.string.route;
		else
			return R.string.workshop;
	}

	@Override
	public String getSubtitle() {
		if(this.kind.equalsIgnoreCase("Tip")) 
			return "tips.categories.".concat(Tip.getCategories().get(this.category));
		else if(this.kind.equalsIgnoreCase("Parking"))
			return "parkings.kinds.".concat(Parking.getKinds().get(this.category));
		else
			return null;
	}

	@Override
	public String getDetails() {
		return description;
	}

	@Override
	public Date getDate() {
		return updatedAt;
	}

	@Override
	public String getName() {
		return title;
	}

	@Override
	public String getKind() {
		return this.kind;
	}

	@Override
	public Long getId() {
		return new Long(0);
	}
}
