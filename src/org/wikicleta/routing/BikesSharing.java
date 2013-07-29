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

public class BikesSharing {
	public class GetEcobici extends AsyncTask<Void, Void, Boolean> {
		protected String url = "http://api.citybik.es/ecobici.json";
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
		protected Boolean doInBackground(Void... params) {
			String parsedValue = NetworkOperations.getJSONExpectingString(url, true);
			if(parsedValue == null)
				return false;
			objectList = (JSONArray) JSONValue.parse(parsedValue);
			return true;
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
