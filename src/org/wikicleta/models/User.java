package org.wikicleta.models;

import java.util.Date;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.wikicleta.common.AppBase;


import android.content.Context;
import android.content.SharedPreferences;

public class User {
	
	static SharedPreferences preferences = AppBase.currentActivity.
			getSharedPreferences("Wikicleta-User",Context.MODE_PRIVATE);

	
	public static void storeWithParams(Map<String,String> params, String token) {
		Date date = new Date();
		
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("name", params.get("name"));
		editor.putString("email", params.get("email"));
		editor.putString("token", token);
		editor.putLong("updated-at", date.getTime());
		editor.commit();
	}
	
	public static String fetchAuthenticationToken() {
		return preferences.getString("token", "");
	}
	
	public static boolean isSignedIn() {
		long date = preferences.getLong("updated-at", 0);
		int days = Days.daysBetween(new DateTime(new Date(date)), new DateTime(new Date())).getDays();
		if(days < 1) {
			return !(User.fetchAuthenticationToken().length() > 0);
		}
		return false;
	}
	
}
