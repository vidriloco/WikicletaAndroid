package org.wikicleta.routing;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.layers.common.LayersConnectorListener;
import org.wikicleta.models.Trip;

import android.os.AsyncTask;

public class Trips {
	protected String getPath="/api/trips?";
	
	public class Get extends AsyncTask<Void, Void, Boolean> {
    	
		public LayersConnectorListener connector;
		public ArrayList<Trip> items;
		
		JSONArray objectList;
	    HashMap<String, String> viewport;

	    public Get(LayersConnectorListener connector) {
	    	this.connector = connector;
	    }
		
	    @Override
	    protected void onPreExecute() {
			viewport = connector.getCurrentViewport();
			items = new ArrayList<Trip>();
	    }
		
		
		@Override
		protected Boolean doInBackground(Void... args) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date today = Calendar.getInstance().getTime();      
			
			String params = "viewport[sw]=".concat(viewport.get("sw")).concat("&viewport[ne]=").concat(viewport.get("ne"));
			params = params.concat("&extras[date]=").concat(df.format(today));
			String fetchedString = NetworkOperations.getJSONExpectingString(getPath.concat(params), false);
			
			if(fetchedString == null)
				return false;
			
			JSONObject object = (JSONObject) JSONValue.parse(fetchedString);
			if((Boolean) object.get("success")) {
				objectList = (JSONArray) object.get("trips");
				return true;
			} else {
				return false;
			}
		}	
		
		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(final Boolean success) {
			if(success) {
				items.clear();
				Iterator<JSONObject> iterator = (Iterator<JSONObject>) objectList.iterator();
				while(iterator.hasNext()) {
					JSONObject json = iterator.next();
					items.add(Trip.buildFrom(json));
				}
			}
			
			connector.overlayFinishedLoadingWithPayload(success, items);
		}
		
		@Override
		protected void onCancelled() {
			connector.overlayFinishedLoading(false);
		}

	}
}
