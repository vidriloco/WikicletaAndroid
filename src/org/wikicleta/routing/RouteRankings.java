package org.wikicleta.routing;

import java.util.HashMap;
import org.interfaces.RemoteFetchingDutyListener;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.R;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.models.Route;
import org.wikicleta.models.RouteRanking;
import org.wikicleta.models.User;
import org.wikicleta.routing.Others.Cruds;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;

public class RouteRankings {

	protected String postPath="/api/route_rankings";
	protected String detailsPath="/api/route_rankings/:route_id/:user_id";
	
	public class Get extends AsyncTask<Route, Void, Boolean> {
    	
		RemoteFetchingDutyListener listener;
		JSONObject responseObject;
		
	    public Get(RemoteFetchingDutyListener listener) {
	    	this.listener = listener;
	    }
	    
		@Override
		protected Boolean doInBackground(Route... args) {
			Route route = args[0];

			String path = detailsPath.replaceFirst(":route_id", String.valueOf(route.remoteId)).replaceFirst(":user_id", String.valueOf(User.id()));
			String fetchedString = NetworkOperations.getJSONExpectingString(path, false);
			if(fetchedString == null)
				return false;
			
			JSONObject object = (JSONObject) JSONValue.parse(fetchedString);
			if((Boolean) object.get("success")) {
				responseObject = (JSONObject) object.get("route_ranking");
				return true;
			} else {
				return false;
			}
		}	
		
		@Override
		protected void onPostExecute(final Boolean success) {
			if(success) {
				long speedIdx = 0;
				long comfortIdx = 0;
				long safetyIdx = 0;
				if(responseObject != null) {
					if(responseObject.containsKey("speed_index"))
						speedIdx = (Long) responseObject.get("speed_index");
					if(responseObject.containsKey("comfort_index"))
						comfortIdx = (Long) responseObject.get("comfort_index");
					if(responseObject.containsKey("safety_index"))
						safetyIdx = (Long) responseObject.get("safety_index");
				}

				listener.onSuccess(new RouteRanking((int) safetyIdx, (int) speedIdx, (int) comfortIdx));
			} else {
				listener.onFailed("Get");
			}
		}
		
		@Override
		protected void onCancelled() {
			listener.onFailed("Get");
		}

	}

	
	public class Post extends AsyncTask<RouteRanking, Void, Boolean> {
		private RouteRanking routeRanking;
		public Cruds mode = Cruds.CREATE;
		AlertDialog dialog;
		RemoteFetchingDutyListener listener;
		
	    public Post(RemoteFetchingDutyListener listener) {
	    	this.listener = listener;
	    }
		
		@Override
		protected Boolean doInBackground(RouteRanking... args) {
			routeRanking = args[0];
			HashMap<String, Object> auth = new HashMap<String, Object>();
			auth.put("auth_token", User.token());
			return (NetworkOperations.postJSONTo(postPath, routeRanking.toJSON(auth)) == 200);
		}
		
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = DialogBuilder.buildLoadingDialogWithMessage((Activity) listener, R.string.uploading).create();
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
