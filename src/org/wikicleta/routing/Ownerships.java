package org.wikicleta.routing;

import java.util.ArrayList;
import java.util.Iterator;
import org.interfaces.RemoteFetchingDutyListener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.models.LightPOI;
import org.wikicleta.models.User;
import android.os.AsyncTask;

public class Ownerships {
	protected String listPath="/api/ownerships/list/:user_id";

	public class List extends AsyncTask<Void, Void, Boolean> {
    	
		public RemoteFetchingDutyListener listener;
		public ArrayList<LightPOI> collection;
	    JSONArray objectList;
	   
	    public List(RemoteFetchingDutyListener listener) {
	    	this.listener = listener;
	    }

		@Override
		protected Boolean doInBackground(Void... args) {
			String fetchedString = NetworkOperations.getJSONExpectingString(listPath.replaceFirst(":user_id", String.valueOf(User.id())), false);
			if(fetchedString == null)
				return false;
			
			JSONObject object = (JSONObject) JSONValue.parse(fetchedString);
			if((Boolean) object.get("success")) {
				objectList = (JSONArray) object.get("ownerships");
				return true;
			} else {
				return false;
			}
		}	
		
		@Override
		protected void onPostExecute(final Boolean success) {
			
			if(success) {
				collection = new ArrayList<LightPOI>();
				processList((JSONArray) objectList);
			}
			listener.onFinished(collection);
		}
		
		@Override
		protected void onCancelled() {
			listener.onFailed();
		}
		
		protected void processList(JSONArray list) {
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> iterator = (Iterator<JSONObject>) list.iterator();
			while(iterator.hasNext()) {
				JSONObject json = iterator.next();
				collection.add(LightPOI.buildFrom(json));
			}
		}

	}
}
