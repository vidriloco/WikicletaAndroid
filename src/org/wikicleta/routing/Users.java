package org.wikicleta.routing;

import java.util.HashMap;
import org.interfaces.RemoteFetchingDutyListener;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.R;
import org.wikicleta.activities.RootActivity;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.models.User;
import org.wikicleta.routing.Others.Cruds;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;

public class Users {

	public String userPath = "/api/profiles/:user_id.json";

	public class Get extends AsyncTask<Void, Void, Boolean> {
    	
		public RootActivity activity;

	    JSONObject object;
	    HashMap<String, String> viewport;
	   
	    public Get(RootActivity activity) {
	    	this.activity = activity;
	    }
	    
		@Override
		protected Boolean doInBackground(Void... args) {
			
			String fetchedString = NetworkOperations.getJSONExpectingString(userPath.replaceFirst(":user_id", String.valueOf(User.id())), false);
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
				activity.userDetailsReceived(object);
			}
		}

	}
	
	public class Post extends AsyncTask<HashMap<String, Object>, Void, Boolean> {
		private HashMap<String, Object> params;
		public Cruds mode = Cruds.CREATE;
		AlertDialog dialog;
		RemoteFetchingDutyListener listener;
		
	    public Post(RemoteFetchingDutyListener listener) {
	    	this.listener = listener;
	    }
		
		@Override
		protected Boolean doInBackground(HashMap<String, Object>... args) {
			params = args[0];
			
			HashMap<String, Object> auth = new HashMap<String, Object>();
			auth.put("auth_token", User.token());
			params.put("extras", auth);
			return (NetworkOperations.postJSONTo(userPath.replaceFirst(":user_id", String.valueOf(User.id())), JSONValue.toJSONString(params)) == 200);
		}
		
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = DialogBuilder.buildLoadingDialogWithMessage((Activity) listener, R.string.updating_your_profile).create();
			dialog.show();
		}
		
	    protected void onPostExecute(Boolean success) {
	    	dialog.dismiss();
	    	if(success) {
				dialog.dismiss();
				listener.onSuccess("Post");
	    	} else {
	    		listener.onFailed("Post");
	    	}
	    }

	     
	}
}
