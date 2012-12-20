package com.wikicleta.activities;

import java.util.LinkedHashMap;
import java.util.Map;

import org.mobility.wikicleta.R;

import com.wikicleta.common.AppBase;
import com.wikicleta.common.NetworkOperations;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class RoutesSavingActivity extends Activity {
	
	// UI references.
	private EditText nameView;
	private EditText tagsView;
	
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
    		
    					Map<String, Map<String, Object>> superParams = new LinkedHashMap<String, Map<String, Object>>();
    					
    					Map<String, Object> params = RoutesActivity.currentPath.closePath();
    					params.put("name", nameView.getText().toString());
    					params.put("tags", tagsView.getText().toString());
    					
    					superParams.put("/api/sessions", params);
    					Log.i("Wikicleta", "Sending route params  ... ");
    					
    					try {
							NetworkOperations.postTo("", superParams);
						} catch (Exception e) {}
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
