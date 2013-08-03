package org.wikicleta.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class Trip implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public long remoteId;
	public String name;
	public String daysToEvent;
	public City city;
	
	public String details;
	public ArrayList<TripPoi> pois;
	public ArrayList<Segment> segments;
	
	public TripPoi start;
	public TripPoi end;

	public Trip(long remoteId, String name, String daysToEvent) {
		this.remoteId = remoteId;
		this.name = name;
		this.daysToEvent = daysToEvent;
		this.pois = new ArrayList<TripPoi>();
		this.segments = new ArrayList<Segment>();
	}
	
	public void mergeExtraInfo(JSONObject object) {
		this.details = (String) object.get("details");
		JSONArray tripPois = (JSONArray) object.get("trip_pois");
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> tripPoisIterator = (Iterator<JSONObject>) tripPois.iterator();
		while(tripPoisIterator.hasNext()) {
			JSONObject tripPoi = tripPoisIterator.next();
			LatLng point = new LatLng((Double) tripPoi.get("lat"), (Double) tripPoi.get("lon"));
			long category = (Long) tripPoi.get("category");
			
			TripPoi newTripPoi = new TripPoi(
					(String) tripPoi.get("name"),
					(String) tripPoi.get("details"),
					 (int) category,
					(String) tripPoi.get("icon_name"),
					point);
			
			if(newTripPoi.isOfCategory("finish_flag")) {
				this.end = newTripPoi;
			} else if(newTripPoi.isOfCategory("start_flag")) {
				this.start = newTripPoi;
			}
			
			this.pois.add(newTripPoi);
		}
		
		JSONArray segments = (JSONArray) object.get("segments");
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> segmentsIterator = (Iterator<JSONObject>) segments.iterator();
		while(segmentsIterator.hasNext()) {
			ArrayList<LatLng> points = new ArrayList<LatLng>();

			JSONObject segment = segmentsIterator.next();
			JSONArray pairsArray = (JSONArray) segment.get("points");
			@SuppressWarnings("unchecked")
			Iterator<JSONArray> pairIterator = (Iterator<JSONArray>) pairsArray.iterator();
			while(pairIterator.hasNext()) {
				JSONArray pointInArray = pairIterator.next();
				LatLng point = new LatLng((Double) pointInArray.get(0),(Double) pointInArray.get(1));
				points.add(point);
			}
			
			this.segments.add(new Segment((Long) segment.get("id"), (String) segment.get("color"), points));
		}
		
	}
}
