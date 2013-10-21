package org.wikicleta.routing;

import java.util.HashMap;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.activities.RootActivity;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.models.User;
import android.os.AsyncTask;

public class Users {

	public String getPath = "/api/profiles/:user_id.json";
	
	public class Get extends AsyncTask<Void, Void, Boolean> {
    	
		public RootActivity activity;

	    JSONObject object;
	    HashMap<String, String> viewport;
	   
	    public Get(RootActivity activity) {
	    	this.activity = activity;
	    }
	    
		@Override
		protected Boolean doInBackground(Void... args) {
			
			String fetchedString = NetworkOperations.getJSONExpectingString(getPath.replaceFirst(":user_id", String.valueOf(User.id())), false);
			if(fetchedString == null)
				return false;
			
			JSONObject responseObject = (JSONObject) JSONValue.parse(fetchedString);
			if((Boolean) responseObject.get("success")) {
				
				object = (JSONObject) responseObject.get("user");
				
				return true;
			} else {
				return false;
			}
		}	
		
		@Override
		protected void onPostExecute(final Boolean success) {
			if(success) {
				activity.displayUserDetails(object);
			}
		}

	}
}
