package org.wikicleta.routing;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.activities.trips.TripDetailsActivity;
import org.wikicleta.activities.trips.TripsListActivity;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.models.City;
import org.wikicleta.models.Trip;

import android.os.AsyncTask;

public class CityTrips {
	protected String getPath="/api/city_trips";
	protected String detailsPath="/api/trips/:id";

	public class Details extends AsyncTask<Trip, Void, Boolean> {
    	
		public TripDetailsActivity activity;
		JSONObject tripObject;
		public Trip trip;
		@Override
		protected void onPreExecute() {
			this.activity.onFetchingTripStarted();
		}
		
		
		@Override
		protected Boolean doInBackground(Trip... args) {
			trip=args[0];
			String fetchedString = NetworkOperations.getJSONExpectingString(detailsPath.replace(":id", String.valueOf(trip.remoteId)), false);
			if(fetchedString == null)
				return false;
			
			JSONObject object = (JSONObject) JSONValue.parse(fetchedString);
			if((Boolean) object.get("success")) {
				tripObject = (JSONObject) object.get("trip");
				return true;
			} else {
				return false;
			}
		}	
		
		@Override
		protected void onPostExecute(final Boolean success) {
			if(success) {
				trip.mergeExtraInfo(tripObject);
				activity.onSuccessfulTripFetching(trip);
			} else {
				activity.onUnsuccessfulTripFetching();
			}
		}
		
		@Override
		protected void onCancelled() {
			activity.onUnsuccessfulTripFetching();
		}

	}
	
	public class Get extends AsyncTask<Void, Void, Boolean> {
    	
		public TripsListActivity activity;
		JSONArray objectList;
			
		@Override
		protected void onPreExecute() {
			this.activity.onFetchingCitiesStarted();
		}
		
		
		@Override
		protected Boolean doInBackground(Void... args) {
			String fetchedString = NetworkOperations.getJSONExpectingString(getPath, false);
			if(fetchedString == null)
				return false;
			
			JSONObject object = (JSONObject) JSONValue.parse(fetchedString);
			if((Boolean) object.get("success")) {
				objectList = (JSONArray) object.get("city_trips");
				return true;
			} else {
				return false;
			}
		}	
		
		@Override
		protected void onPostExecute(final Boolean success) {
			if(success) {
				ArrayList<City> cities = new ArrayList<City>();
				Iterator<JSONObject> iterator = (Iterator<JSONObject>) objectList.iterator();
				while(iterator.hasNext()) {
					cities.add(City.buildFrom(iterator.next()));
				}
				
				this.activity.onSuccessfulRetrievalOfCityTrips(cities);
			} else {
				this.activity.onUnsuccessfulRetrievalOfCityTrips();
			}
		}
		
		@Override
		protected void onCancelled() {
			this.activity.onUnsuccessfulRetrievalOfCityTrips();
		}

		}
}
