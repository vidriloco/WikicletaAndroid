package com.wikicleta.activities;

import org.mobility.wikicleta.R;

import com.wikicleta.async.RouteSavingAsync;
import com.wikicleta.common.AppBase;
import com.wikicleta.common.Constants;
import com.wikicleta.helpers.NotificationBuilder;
import com.wikicleta.models.Route;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
    					notification.addNotification(Constants.ROUTES_SYNCING_NOTIFICATIONS_ID, 
    							getString(R.string.app_name), getString(R.string.route_being_sent), null);
    					
    					Route route = RoutesActivity.currentPath.buildRoute(
    							nameView.getText().toString(), 
    							tagsView.getText().toString());
    					RouteSavingAsync routeSavingTask = new RouteSavingAsync(route);
    					routeSavingTask.execute((Void) null);
    				}
    	});
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
