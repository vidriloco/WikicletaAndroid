package org.wikicleta.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class City implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name;
	public int remoteId;
	
	public ArrayList<Trip> trips;
	
	public City(String name, ArrayList<Trip> trips) {
		this.name = name;
		this.trips = trips;
		for(Trip trip : trips) {
			trip.city = this;
		}
	}
	
	public static City buildFrom(JSONObject object) {
		String name = (String) object.get("name");
		//String cityURL = (String) object.get("city");
		JSONArray trips = (JSONArray) object.get("trips");
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> iterator = (Iterator<JSONObject>) trips.iterator();
		
		ArrayList<Trip> tripsList = new ArrayList<Trip>();
		while(iterator.hasNext()) {
			JSONObject trip = iterator.next();
			long remoteId = (Long) trip.get("id");
			String tripName = (String) trip.get("name");
			String daysToEvent = (String) trip.get("days_to_event");
			
			tripsList.add(new Trip(remoteId, tripName, daysToEvent));
		}
		return new City(name, tripsList);
	}
}
