package com.wikicleta.async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import android.content.Intent;
import android.os.AsyncTask;
import com.activeandroid.ActiveAndroid;
import com.wikicleta.activities.ActivityFeedsActivity;
import com.wikicleta.common.AppBase;
import com.wikicleta.common.Constants;
import com.wikicleta.common.NetworkOperations;
import com.wikicleta.helpers.NotificationBuilder;
import com.wikicleta.models.Instant;
import com.wikicleta.models.Route;

public class RouteSavingAsync extends AsyncTask<Void, Void, Boolean> {
	protected Route route;
	protected String jsonValue;
	
	public boolean commitSuccessful;
	
	public RouteSavingAsync(Route route) {
		this.route = route;
	}
	
	protected void upload() {
		
		if(this.route.isDraft()) {
			commitSuccessful = (200 == NetworkOperations.postJSONTo("/api/sessions", route.jsonRepresentation));
			route.jsonRepresentation = null;
		} else {
			String json = this.generateJSONValue();
			int status = NetworkOperations.postJSONTo("/api/sessions", json);
			if(status == 200) {
				commitSuccessful = true;
			} else {
				route.jsonRepresentation = jsonValue;
			}
		}
		
		commitLocally(route);
	}
	
	protected String generateJSONValue() {
		Map<String, Map<String, Object>> superParams = new LinkedHashMap<String, Map<String, Object>>();
		Map<String, Object> params = route.toHashMap();
		
		ArrayList<HashMap<String, Object>> instants = new ArrayList<HashMap<String, Object>>();
		for(Instant instant : route.instants()) {
			instants.add(instant.toHashMap());
		}
		
		params.put("coordinates", instants);
		superParams.put("session", params);
		return JSONValue.toJSONString(superParams);
	}
	
	@Override
	protected Boolean doInBackground(Void... procParams) {
		this.route.isUploading = true;

		Intent intentActivity = new Intent(AppBase.currentActivity, ActivityFeedsActivity.class);
		AppBase.currentActivity.startActivity(intentActivity);
		this.upload();
		NotificationBuilder.clearNotification(Constants.ROUTES_SYNCING_NOTIFICATIONS_ID);
		return true;
	}
	
	public void commitLocally(Route route)  {
		route.isUploading = false;
		ActiveAndroid.beginTransaction();
		route.save();
		for (Instant instant : route.instants()) {
		    instant.route = route;
		    instant.save();
		}
		ActiveAndroid.setTransactionSuccessful();
		ActiveAndroid.endTransaction();
	}
}