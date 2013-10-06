package org.wikicleta.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.wikicleta.R;
import org.wikicleta.common.NetworkOperations;


import com.google.android.gms.maps.model.LatLng;

public class Trip implements Serializable, DraftModel, MarkerInterface {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public long remoteId;
	public String name;
	public int daysToEventFromNow;
	
	public String details;
	public ArrayList<TripPoi> pois;
	public ArrayList<Segment> segments;
	
	public TripPoi start;
	public TripPoi end;
	public LatLng coordinate;
	public String pic;

	public Trip(long remoteId, String name, int daysToEvent, LatLng point) {
		this.remoteId = remoteId;
		this.name = name;
		this.daysToEventFromNow = daysToEvent;
		this.pois = new ArrayList<TripPoi>();
		this.segments = new ArrayList<Segment>();
		this.coordinate = point;
	}
	
	public void mergeExtraInfo(JSONObject object) {
		this.details = (String) object.get("details");
		this.pic = NetworkOperations.serverHost.concat((String) object.get("pic"));
		JSONArray tripPois = (JSONArray) object.get("pois");
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
		
		JSONArray segments = (JSONArray) object.get("paths");
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
	
	public static Trip buildFrom(JSONObject json) {
		long remoteId = (Long) json.get("id");
		String tripName = (String) json.get("name");
		long daysToEventTmp = (Long) json.get("calculated_days_to_event");
		
		LatLng point = new LatLng((Double) json.get("lat"), (Double) json.get("lon"));
		Trip trip = new Trip(remoteId, tripName, (int) daysToEventTmp, point);
		trip.mergeExtraInfo(json);
		return trip;
	}
	
	public boolean hasPic() {
		if(this.pic == null)
			return false;
		return !this.pic.isEmpty();
	}
	
	public int daysToRide() {
	    if(daysToEventFromNow==0)
	    	return R.string.event_today;
	    else if(daysToEventFromNow==1000)
	        return R.string.event_unknown;
	    else if(daysToEventFromNow==1)
	        return R.string.event_tomorrow;
	    else 
	        return R.string.event_other;
	}

	@Override
	public LatLng getLatLng() {
		return coordinate;
	}

	@Override
	public int getDrawable() {
		return R.drawable.cycling_learning_icon;
	}

	@Override
	public String getContent() {
		return null;
	}

	@Override
	public String getCategoryName() {
		return null;
	}

	@Override
	public Date getDate() {
		return null;
	}

	@Override
	public Long getId() {
		return this.remoteId;
	}

	@Override
	public void delete() {
		
	}

	@Override
	public boolean requiresCategoryTranslation() {
		return false;
	}
}
