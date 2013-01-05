package org.wikicleta.activities;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.helpers.NotificationBuilder;
import org.wikicleta.models.Route;
import org.wikicleta.services.RoutesManagementService;
import org.wikicleta.services.ServiceListener;
import org.wikicleta.services.ServiceConstructor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class RoutesSavingActivity extends Activity implements ServiceListener {
	
	// UI references.
	private EditText nameView;
	private EditText tagsView;
	
	//Notifications
	NotificationBuilder notification;
	
	//Service
	protected RoutesManagementService theService;
	ServiceConstructor serviceInitializator;
	
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
    					
    					Route route = RoutesActivity.currentPath.buildRoute(
    							nameView.getText().toString(), 
    							tagsView.getText().toString());
    					
    					theService.uploadRoute(route);
    					AppBase.launchActivity(ActivitiesFeedActivity.class);
    				}
    	});
	}
    
    public void afterServiceConnected(RoutesManagementService service) {
    	this.theService = service;
    }
    
	@Override
	protected void onStart() {
		super.onStart();
		serviceInitializator = new ServiceConstructor(this);
        serviceInitializator.start(RoutesManagementService.class);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
        serviceInitializator.stop();
		
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

	@Override
	public void afterServiceConnected(Service service) {
		if(service instanceof RoutesManagementService)
			this.theService = (RoutesManagementService) service;
	}
	
}
