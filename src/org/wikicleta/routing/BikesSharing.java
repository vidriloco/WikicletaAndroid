package org.wikicleta.routing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.layers.common.LayersConnectorListener;
import org.wikicleta.models.CycleStation;
import android.os.AsyncTask;
import android.util.Log;

public class BikesSharing {
	public class GetEcobici extends AsyncTask<Void, Void, Boolean> {
		protected String getPath="/api/cycle_stations?";
		public LayersConnectorListener connector;
		public ArrayList<CycleStation> items;

	    JSONArray objectList;
	    HashMap<String, String> viewport;
    	
	    public GetEcobici(LayersConnectorListener connector) {
	    	this.connector = connector;
	    }
	   
	    @Override
	    protected void onPreExecute() {
			viewport = connector.getCurrentViewport();
			items = new ArrayList<CycleStation>();
	    }
	    
		@Override
		protected Boolean doInBackground(Void... args) {
			String params = "viewport[sw]=".concat(viewport.get("sw")).concat("&viewport[ne]=").concat(viewport.get("ne"));
			String fetchedString = NetworkOperations.getJSONExpectingStringGzipped(getPath.concat(params), false);
			Log.i("WIKICLETA", fetchedString);
			if(fetchedString == null)
				return false;
			
			JSONObject object = (JSONObject) JSONValue.parse(fetchedString);
			if((Boolean) object.get("success")) {
				objectList = (JSONArray) object.get("cycle_stations");
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if(success) {
				items.clear();
				@SuppressWarnings("unchecked")
				Iterator<JSONObject> iterator = (Iterator<JSONObject>) objectList.iterator();
				while(iterator.hasNext()) {
					JSONObject json = iterator.next();
					items.add(CycleStation.buildFrom(json));
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
