package com.wikicleta.activities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import org.mobility.wikicleta.R;

import com.activeandroid.ActiveAndroid;
import com.wikicleta.common.AppBase;
import com.wikicleta.common.Constants;
import com.wikicleta.common.NetworkOperations;
import com.wikicleta.helpers.NotificationBuilder;
import com.wikicleta.helpers.Pair;
import com.wikicleta.models.Instant;
import com.wikicleta.models.Route;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class RoutesSavingActivity extends Activity {
	
	// UI references.
	private EditText nameView;
	private EditText tagsView;
	
	//Notifications
	NotificationBuilder notification;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        AppBase.currentActivity = this;

        this.setContentView(R.layout.activity_routes_saving);
        
        this.nameView = (EditText) findViewById(R.id.route_name);
        this.tagsView = (EditText) findViewById(R.id.route_tags);
        this.notification = new NotificationBuilder(this);
        
        findViewById(R.id.cancel).setOnClickListener(
    			new View.OnClickListener() {
    				@Override
    				public void onClick(View view) {
    					AlertDialog.Builder alertDialog = new AlertDialog.Builder(AppBase.currentActivity);
    					alertDialog.setTitle("Descartar ruta");
    					alertDialog.setMessage("ÀQuieres descartar esta ruta?");
    					alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
    						@Override
    						public void onClick(DialogInterface dialog, int which) {
    							Intent intentActivity = new Intent(AppBase.currentActivity, RoutesActivity.class);
    	    					AppBase.currentActivity.startActivity(intentActivity);
    	    					overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
    						}
    					});
    					// If user chooses 'No', then the dialog closes
    					alertDialog.setNegativeButton("No", null);
    					alertDialog.show();
    				}
    	});
        
        findViewById(R.id.save_route).setOnClickListener(
    			new View.OnClickListener() {
    				@Override
    				public void onClick(View view) {
    					notification.addNotification(Constants.SENDING_ROUTE_NOTIFICATION_ID, 
    							getString(R.string.app_name), getString(R.string.route_being_sent), null);
    					RouteSavingAttempt routeSavingTask = new RouteSavingAttempt();
    					routeSavingTask.execute((Void) null);
    				}
    	});
	}
	
	public class RouteSavingAttempt extends AsyncTask<Void, Void, Boolean> {
		Pair<Route, ArrayList<Instant>> routeComponent = null;

		@Override
		protected Boolean doInBackground(Void... procParams) {
			try {
				Map<String, Map<String, Object>> superParams = new LinkedHashMap<String, Map<String, Object>>();
				
				routeComponent = RoutesActivity.currentPath.buildRoute(
						nameView.getText().toString(), 
						tagsView.getText().toString());
								
				superParams.put("session", routeComponent.generateParams());
				Log.i("Wikicleta", "Sending route params  ... ");
				
				String result = NetworkOperations.postTo("/api/sessions", superParams, routeComponent);
				Log.i("Wikicleta", result);
				routeComponent.first.setJsonRepresentation("");
			} catch (Exception e) {
				
			}
			

			RouteDetailsActivity.currentRoute = routeComponent.first;
			Intent intentActivity = new Intent(AppBase.currentActivity, RouteDetailsActivity.class);
			AppBase.currentActivity.startActivity(intentActivity);
			NotificationBuilder.clearNotification(Constants.SENDING_ROUTE_NOTIFICATION_ID);
			saveRoute(routeComponent);
			return true;
		}
		
	}
	
	public void saveRoute(Pair<Route, ArrayList<Instant>> routeComponent)  {
		ActiveAndroid.beginTransaction();
		Route route = routeComponent.first;
		route.save();
		for (Instant instant : routeComponent.second) {
		    instant.route = route;
		    instant.save();
		}
		ActiveAndroid.setTransactionSuccessful();
		ActiveAndroid.endTransaction();
	}

	
	// TODO: Decide whether it could be desirable that upon automatic return to activity 
	// route is still getting tracked. If so, buttons state should behave accordingly 
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	if(RoutesActivity.currentPath != null && RoutesActivity.currentPath.isPaused())
	    		RoutesActivity.currentPath.resume();
	    }
	    return super.onKeyDown(keyCode, event);
	}*/
	
}
