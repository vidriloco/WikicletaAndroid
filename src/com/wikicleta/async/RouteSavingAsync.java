package com.wikicleta.async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.simple.JSONValue;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.wikicleta.activities.ActivityFeedsActivity;
import com.wikicleta.common.AppBase;
import com.wikicleta.common.Constants;
import com.wikicleta.common.NetworkOperations;
import com.wikicleta.helpers.NotificationBuilder;
import com.wikicleta.models.Instant;
import com.wikicleta.models.Route;

public class RouteSavingAsync extends AsyncTask<Void, Void, Void> {
	public Route route;

	public boolean commitSuccessful;

	public RouteSavingAsync(Route route) {
		this.route = route;
	}

	protected void upload() {
		if (this.route.isDraft()) {
			commitSuccessful = (200 == NetworkOperations.postJSONTo(
					"/api/sessions", route.jsonRepresentation));
		} else {
			String json = this.generateJSONValue();
			commitSuccessful = (200 == NetworkOperations.postJSONTo(
					"/api/sessions", json));
			if (!commitSuccessful)
				this.route.jsonRepresentation = json;
		}

		commitLocally();
	}

	protected String generateJSONValue() {
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

	@Override
	protected Void doInBackground(Void... procParams) {
		Intent intentActivity = new Intent(AppBase.currentActivity,
				ActivityFeedsActivity.class);
		AppBase.currentActivity.startActivity(intentActivity);
		this.upload();
		NotificationBuilder
				.clearNotification(Constants.ROUTES_SYNCING_NOTIFICATIONS_ID);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		AsyncTaskManager.deregisterTask(route.getId());

		if (AppBase.currentActivity instanceof AsyncTaskUIUpdater) {
			Log.e("WIKICLETA", "Cerrando ...");
			AsyncTaskUIUpdater uiUpdater = (AsyncTaskUIUpdater) AppBase.currentActivity;
			uiUpdater.updateUI(this.route, false);
		}
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (AppBase.currentActivity instanceof AsyncTaskUIUpdater) {

			AsyncTaskUIUpdater uiUpdater = (AsyncTaskUIUpdater) AppBase.currentActivity;
			uiUpdater.updateUI(this.route, true);
		}
	}

	public void commitLocally() {
		ActiveAndroid.beginTransaction();
		if (this.commitSuccessful)
			this.route.jsonRepresentation = new String();

		this.route.save();
		for (Instant instant : this.route.instants()) {
			instant.route = this.route;
			instant.save();
		}
		ActiveAndroid.setTransactionSuccessful();
		ActiveAndroid.endTransaction();
	}
}