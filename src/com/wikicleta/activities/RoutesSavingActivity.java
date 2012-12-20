package com.wikicleta.activities;

import java.util.LinkedHashMap;
import java.util.Map;


import org.kroz.activerecord.ActiveRecordException;
import org.kroz.activerecord.DatabaseBuilder;
import org.mobility.wikicleta.R;

import com.wikicleta.common.AppBase;
import com.wikicleta.common.NetworkOperations;
import com.wikicleta.drafts.RouteDraft;

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
	protected DatabaseBuilder builder;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        AppBase.currentActivity = this;

        this.setContentView(R.layout.activity_routes_saving);
        
        this.nameView = (EditText) findViewById(R.id.route_name);
        this.tagsView = (EditText) findViewById(R.id.route_tags);

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
    					RouteSavingAttempt routeSavingTask = new RouteSavingAttempt();
    					routeSavingTask.execute((Void) null);
    				}
    	});
	}
	
	public class RouteSavingAttempt extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... procParams) {
			try {
				Map<String, Map<String, Object>> superParams = new LinkedHashMap<String, Map<String, Object>>();
				
				Map<String, Object> params = RoutesActivity.currentPath.closePath();
				params.put("name", nameView.getText().toString());
				params.put("tags", tagsView.getText().toString());
				
				superParams.put("session", params);
				Log.i("Wikicleta", "Sending route params  ... ");
				
				String result = NetworkOperations.postTo("/api/sessions", superParams);
				Log.i("Wikicleta", result);
				return true;
			} catch (Exception e) {
				try {
					saveRoute();
				} catch (ActiveRecordException e1) {
					e1.printStackTrace();
				}
				return false;
			}
			
		}
		
	}
	
	public void saveRoute() throws ActiveRecordException {
		/*RouteDraft routeDraft = AppBase.getDBConnection().newEntity(RouteDraft.class);
		routeDraft.save();*/
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
