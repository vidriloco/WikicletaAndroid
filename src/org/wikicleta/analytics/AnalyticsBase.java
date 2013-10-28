package org.wikicleta.analytics;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.wikicleta.models.User;
import android.content.Context;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

public class AnalyticsBase {

	public static enum BinaryStatus {YES, NO};

	protected static final String MIXPANEL_TOKEN = "61c81ef2212cf0327313ee3cc3b31668";
	protected static MixpanelAPI mixpanel;

	public static void loadInstance(Context context) {
		if(mixpanel == null)
			mixpanel = MixpanelAPI.getInstance(context, MIXPANEL_TOKEN);
	}
	
	public static MixpanelAPI getInstance(Context context) {
		loadInstance(context);
		return mixpanel;
	}
	
	public static void identify() {
		if(User.isRegisteredLocally() && mixpanel != null)
			mixpanel.identify(String.valueOf(User.id()));
	}
	
	public static void flushInstance() {
		if(mixpanel != null)
			mixpanel.flush();
	}
	
	public static void trackEvent(String identifier, JSONObject object) {
		if(mixpanel != null)
			mixpanel.track(identifier, object);
	}
	
	
	public static void reportUnloggedEvent(String identifier, Context context) {
		AnalyticsBase.loadInstance(context);
		JSONObject object = new JSONObject();
		try {
			object.put("DeviceModel", android.os.Build.MODEL);
			object.put("Android Version", String.valueOf(android.os.Build.VERSION.SDK_INT));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AnalyticsBase.trackEvent(identifier, object);
	}
	
	public static void reportUnloggedEvent(String identifier, Context context, String extraKey, String extraValue) {
		AnalyticsBase.loadInstance(context);
		JSONObject object = new JSONObject();
		try {
			object.put(extraKey, extraValue);
			object.put("DeviceModel", android.os.Build.MODEL);
			object.put("Android Version", String.valueOf(android.os.Build.VERSION.SDK_INT));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AnalyticsBase.trackEvent(identifier, object);
	}
	
	public static void reportLoggedInEvent(String identifier, Context context) {
		AnalyticsBase.loadInstance(context);
		org.json.JSONObject object = new org.json.JSONObject();
		try {
			object.put("User-id", String.valueOf(User.id()));
			object.put("Username", User.username());
			object.put("Date", new Date().toString());
			object.put("DeviceModel", android.os.Build.MODEL);
			object.put("Android Version", String.valueOf(android.os.Build.VERSION.SDK_INT));		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AnalyticsBase.trackEvent(identifier, object);
	}
	
	public static void reportLoggedInEvent(String identifier, Context context, String...list) {
		AnalyticsBase.loadInstance(context);
		org.json.JSONObject object = new org.json.JSONObject();
		try {
			object.put("User-id", String.valueOf(User.id()));
			object.put("Username", User.username());
			object.put("Date", new Date().toString());
			object.put("DeviceModel", android.os.Build.MODEL);
			object.put("Android Version", String.valueOf(android.os.Build.VERSION.SDK_INT));
			
			if(list.length >= 2) 
				object.put(list[0], list[1]);
			if(list.length >= 4) 
				object.put(list[2], list[3]);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AnalyticsBase.trackEvent(identifier, object);
	}
	
	public static void registerUser(Long id, Context context) {
		AnalyticsBase.loadInstance(context);
		mixpanel.getPeople().identify(String.valueOf(id));
		mixpanel.getPeople().set("username", User.username());
		mixpanel.getPeople().set("bio", User.bio());
		mixpanel.getPeople().set("email", User.email());
	}
}
