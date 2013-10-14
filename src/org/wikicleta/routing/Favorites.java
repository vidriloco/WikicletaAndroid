package org.wikicleta.routing;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.common.interfaces.FavoritesConnectorInterface;
import org.wikicleta.models.User;

import android.os.AsyncTask;

public class Favorites {

	protected String getPath="/api/favorites/marked/:object_id/:object_type/:user_id";
	protected String postPath="/api/favorites/:mode";

	public class Marked extends AsyncTask<String, Void, Boolean> {
    	
		public FavoritesConnectorInterface connector;
		protected boolean isFavorite;
		
	    public Marked(FavoritesConnectorInterface connector) {
	    	this.connector = connector;
	    }
	    
		@Override
		protected Boolean doInBackground(String... args) {
			String path = getPath.replaceFirst(":object_id", args[0]).
					replaceFirst(":object_type", args[1]).
					replaceFirst(":user_id", args[2]);
			String fetchedString = NetworkOperations.getJSONExpectingString(path, false);
			if(fetchedString == null)
				return false;
			
			JSONObject object = (JSONObject) JSONValue.parse(fetchedString);
			if((Boolean) object.get("success")) {
				isFavorite = (Boolean) object.get("is_favorite");
				return true;
			} else {
				return false;
			}
		}	
		
		@Override
		protected void onPostExecute(final Boolean success) {
			
			if(success) {
				connector.onFavoritedItemChangedState(isFavorite);
			}
		}
		
		@Override
		protected void onCancelled() {
			connector.onFavoritedItemChangedState(false);
		}

	}
	
	public class Post extends AsyncTask<String, Void, Boolean> {
    	
		private FavoritesConnectorInterface connector;
		private String mode;
		
	    public Post(FavoritesConnectorInterface connector, String mode) {
	    	this.connector = connector;
	    	this.mode = mode;
	    }
	    
		@Override
		protected Boolean doInBackground(String... args) {
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("favorited_object_id", args[0]);
			params.put("favorited_object_type", args[1]);
			params.put("user_id", args[2]);
						
			HashMap<String, Object> cover = new HashMap<String, Object>();
			cover.put("favorite", params);
			
			HashMap<String, Object> extras = new HashMap<String, Object>();
			extras.put("auth_token", User.token());
			cover.put("extras", extras);
			NetworkOperations.postJSONExpectingStringTo(postPath.replaceFirst(":mode", mode), 
					JSONValue.toJSONString(cover));
			return true;
		}	
		
		@Override
		protected void onPostExecute(final Boolean success) {
			
			if(success) {
				connector.onFavoritedItemChangedState(mode.equalsIgnoreCase("mark"));
			}
		}
		
		@Override
		protected void onCancelled() {
			connector.onFavoritedItemChangedState(false);
		}

	}
}
