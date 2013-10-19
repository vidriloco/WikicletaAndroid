package org.wikicleta.models;

import java.io.Serializable;
import java.util.HashMap;

import org.interfaces.MarkerInterface;
import org.wikicleta.common.AppBase;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import android.annotation.SuppressLint;
import android.content.Context;

public class TripPoi implements Serializable, MarkerInterface {

	public String name;
	public String details;
	public String iconName;
	public double latitude;
	public double longitude;
	public int category;
	private Context ctx = AppBase.currentActivity.getApplicationContext();
	protected Marker marker;

	@SuppressLint("UseSparseArrays")
	protected static HashMap<Integer, String> categories = new HashMap<Integer, String>();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TripPoi(String name, String details, int category, String iconName, LatLng point) {
		this.name = name;
		this.details = details;
		this.category = category;
		this.iconName = iconName;
		this.latitude = point.latitude;
		this.longitude = point.longitude;
	}
	
	public String details() {
		if(details == null) {
			return "";
		}
		return details.replace("<p>", "").replace("</p>", "\n").concat("\n").replace("<ul><li>", "\n- ").replace("</li><li>", "\n- ").replace("</li></ul>", "");
	}
	
	public String categoryString() {
		return TripPoi.getCategories().get(this.category);
	}
	
	public boolean isOfCategory(String categoryString) {
		return categoryString.equalsIgnoreCase(this.categoryString());
	}
		
	public static HashMap<Integer, String> getCategories() {
		if(categories.isEmpty()) {
			categories.put(1, "service_station");
			categories.put(2, "ambulance");
			categories.put(3, "paramedic");
			categories.put(4, "bike_lending");
			categories.put(5, "direction_mark");
			categories.put(6, "km_mark");
			categories.put(7, "transport_connection");
			categories.put(8, "sightseeing");
			categories.put(9, "start_flag");
			categories.put(10, "finish_flag");
			categories.put(11, "grouped_services");
			categories.put(12, "cycling_learning");
			categories.put(13, "free_grouped_services");

		}
		return categories;
	}
	
	public static String[] getCategoriesValues() {
		String[] categoriesStrs = new String[getCategories().size()];
		for(int i=1; i< getCategories().size() ; i++) {
			categoriesStrs[i-1] = getCategories().get(i);
		}
		return categoriesStrs;
	}

	@Override
	public LatLng getLatLng() {
		return new LatLng(latitude, longitude);
	}

	@Override
	public int getDrawable() {
		int identifier = -1;

		if(this.categoryString()=="transport_connection") {
			String resourceName = this.iconName;
			identifier=this.ctx.getResources().getIdentifier(resourceName, "drawable", ctx.getPackageName());
		} else {
			String resourceName = this.categoryString().concat("_marker");
			identifier=this.ctx.getResources().getIdentifier(resourceName, "drawable", ctx.getPackageName());
		}
		
		return identifier;
	}

	@Override
	public Marker getAssociatedMarker() {
		return marker;
	}
	
	@Override
	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	@Override
	public long getRemoteId() {
		// TODO Auto-generated method stub
		return 0;
	}

}
