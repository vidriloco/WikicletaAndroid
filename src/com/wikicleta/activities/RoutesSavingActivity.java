package com.wikicleta.activities;

import org.mobility.wikicleta.R;

import com.wikicleta.common.AppBase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class RoutesSavingActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        AppBase.currentActivity = this;

        this.setContentView(R.layout.activity_routes_saving);
        
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
    					
    				}
    	});
	}
}
