package org.wikicleta.models;

import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONValue;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.NetworkOperations;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

public class User {
	
	static SharedPreferences preferences;

	static SharedPreferences getPreferences() {
		return AppBase.currentActivity.getSharedPreferences("Wikicleta-User",Context.MODE_PRIVATE);
	}
	
	public static void storeWithParams(Map<String,String> params, String token) {
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putString("token", token);

		if(params.containsKey("full_name"))
			editor.putString("full_name", params.get("full_name"));
		if(params.containsKey("email"))
			editor.putString("email", params.get("email"));
		if(params.containsKey("username"))
			editor.putString("username", params.get("username"));
		if(params.containsKey("identifier"))
			editor.putLong("id", Long.parseLong(params.get("identifier")));
		if(params.containsKey("created-at"))
			editor.putLong("created-at", Long.parseLong(params.get("created_at_ms")));
		editor.commit();
	}
	
	public static String token() {
		return getPreferences().getString("token", "");
	}
	
	public static String username() {
		return getPreferences().getString("username", "");
	}
	
	public static Long id() {
		return getPreferences().getLong("id", 0);
	}
	
	public static boolean isRegisteredLocally() {
		return token().length() > 0;	
	}
	
	public static void destroy() {
		SharedPreferences.Editor editor = getPreferences().edit();
		
		//new SessionDestroyTask().execute();
		editor.clear();
		editor.commit();
		
		Bike.destroyAll();
	}
	
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public static class SessionDestroyTask extends AsyncTask<Void, Void, Boolean> {
				
		@Override
		protected Boolean doInBackground(Void... params) {
			HashMap<String, Object> user = new HashMap<String, Object>();
			user.put("user_login", preferences.getString("username", ""));
			user.put("_method", "DELETE");
			NetworkOperations.postJSONTo("/api/users/sign_out", JSONValue.toJSONString(user));
			return true;
		}


		@Override
		protected void onPostExecute(final Boolean success) {
			
		}

	}
}
