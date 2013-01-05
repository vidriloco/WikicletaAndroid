package org.wikicleta.async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONValue;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.models.Instant;
import org.wikicleta.models.Route;

import com.activeandroid.ActiveAndroid;

public class RouteUploader {

	public boolean uploadRoute(Route route) {
		if(route == null)
			return false;
		boolean commitSuccessful = false;
		if (route.isDraft()) {
			commitSuccessful = (200 == NetworkOperations.postJSONTo(
					"/api/sessions", route.jsonRepresentation));
		} else {
			
			String json = this.generateJSONValue(route);
			commitSuccessful = (200 == NetworkOperations.postJSONTo("/api/sessions", json));
			if (!commitSuccessful)
				route.jsonRepresentation = json;
		}

		if(commitSuccessful)
			route.jsonRepresentation = new String();
		
		commitLocally(route);
		
		return commitSuccessful;
	}
	
	protected String generateJSONValue(Route route) {
		Map<String, Map<String, Object>> superParams = new LinkedHashMap<String, Map<String, Object>>();
		Map<String, Object> params = route.toHashMap();

		ArrayList<HashMap<String, Object>> instants = new ArrayList<HashMap<String, Object>>();
		for (Instant instant : route.instants()) {
			instants.add(instant.toHashMap());
		}

		params.put("coordinates", instants);
		superParams.put("session", params);
		return JSONValue.toJSONString(superParams);
	}
	
	protected void commitLocally(Route route) {
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