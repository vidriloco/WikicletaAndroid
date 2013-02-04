package org.wikicleta.routes.activities;

import org.wikicleta.R;
import org.wikicleta.activities.LocationAwareMapActivity;
import org.wikicleta.activities.MainMapActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.routes.services.NavigationListener;
import org.wikicleta.routes.services.RoutesService;
import org.wikicleta.routes.services.ServiceConstructor;
import org.wikicleta.routes.services.ServiceListener;
import org.wikicleta.views.RouteOverlay;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewRouteActivity extends LocationAwareMapActivity implements ServiceListener, NavigationListener {
	
	protected LinearLayout recordRouteToolbarView;
	protected LinearLayout statsToolbarView;
	
	protected LinearLayout gpsWaitingView;
	
	private ImageView recButton;
	private ImageView pauseButton;
	private ImageView flagButton;
	
	protected TextView speedTextValue;
	protected TextView timeTextValue;
	protected TextView distanceTextValue;
	
	protected RouteOverlay routeOverlay;

	AlertDialog.Builder builder;
	protected AlertDialog.Builder alertDialog;

	//Service
	protected RoutesService theService;
	ServiceConstructor serviceInitializator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.new_route_activity);
		setTheme(R.style.Theme_wikicleta);
		
		AppBase.currentActivity = this;
		startService(new Intent(this, RoutesService.class));

        recordRouteToolbarView = (LinearLayout) findViewById(R.id.route_recording_toolbar);
        statsToolbarView = (LinearLayout) findViewById(R.id.stats_panel);
        gpsWaitingView = (LinearLayout) findViewById(R.id.gps_loading);
        
        alertDialog = new AlertDialog.Builder(this);
    	
    	recButton = (ImageView) findViewById(R.id.routes_rec_button);
    	flagButton = (ImageView) findViewById(R.id.routes_finish_button);
    	pauseButton = (ImageView) findViewById(R.id.routes_pause_button);

    	alertDialog = new AlertDialog.Builder(this);
    	
    	speedTextValue = (TextView) findViewById(R.id.speed_number);
    	timeTextValue = (TextView) findViewById(R.id.time_elapsed_number);
    	distanceTextValue = (TextView) findViewById(R.id.distance_number);

    	recButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				resumeRecording(true);
				flagButton.setVisibility(View.VISIBLE);
				mapView.postInvalidate();
			}
		});
    	
    	pauseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pauseRecording(true);
			}
		});
    	
    	flagButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {				
				if(theService.routeRecorder.isEmpty()) {
					pauseRecording(false);

					alertDialog.setTitle("Aviso").
					setMessage("No puedes guardar una ruta vac’a").
					setPositiveButton(null, null).
					setNegativeButton(null, null).
					setNeutralButton("Aceptar", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							resumeRecording(false);
						}
					});
					
					alertDialog.show();
				} else {
					if(theService.routeRecorder.isPaused())
						resumeRecording(true);
					pauseRecording(true);
					
					AppBase.launchActivity(RoutesSavingActivity.class);
				}
			}
    	});
    	
    	this.findViewById(R.id.routes_back_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final boolean wasRecording;
				if(!theService.routeRecorder.isEmpty()) {
					wasRecording = !theService.routeRecorder.isPaused();
					pauseRecording(true);

					alertDialog.setTitle("Pregunta");
					alertDialog.setMessage("ÀDeseas descartar esta ruta?");
					// If the user chooses 'Yes', then
					alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							cancelRecording();
						}
					});
					// If user chooses 'No', then the dialog closes
					alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if(wasRecording) {
								// Resume tracking (with auto-recording)
								resumeRecording(true);
							} 
						}
					});
					alertDialog.setNeutralButton(null, null);
					alertDialog.show();
				} else {
					cancelRecording();
				}				
			}
    		
    	});
	}
	
	protected void cancelRecording() {
		theService.routeRecorder.reset();
		AppBase.launchActivity(MainMapActivity.class);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(theService.routeRecorder.isPaused())
			theService.disableLocationManager();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		serviceInitializator = new ServiceConstructor(this);
        serviceInitializator.start(RoutesService.class);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
        serviceInitializator.stop();
	}
	
	@Override
	public void afterServiceConnected(Service service) {
		if(service instanceof RoutesService) {
			this.theService = (RoutesService) service;
			theService.notifyAboutStalledRoutes();
			
			routeOverlay = new RouteOverlay(theService.routeRecorder.coordinateVector);
			mapView.getOverlays().add(routeOverlay);
	        theService.enableLocationManager();
	        this.firstLocationReceived();
		}
	}
	
	protected void resetRouteRecorder() {
		recButton.setVisibility(View.VISIBLE);
		pauseButton.setVisibility(View.GONE);
		flagButton.setVisibility(View.GONE);
		
		this.timeTextValue.setText(getString(R.string.dashes));
		this.distanceTextValue.setText(getString(R.string.dashes));
		this.speedTextValue.setText(getString(R.string.dashes));
		theService.routeRecorder.reset();
	}
	
	/*
	 *  Pauses the recording of a new route
	 *  @param controlSwitch will change the buttons state if true
	 */
	protected void pauseRecording(boolean controlSwitch) {
		theService.pauseRecording();
		// Show rec button and hide pause button
		if(controlSwitch) {
			recButton.setVisibility(View.VISIBLE);
			pauseButton.setVisibility(View.GONE);
		}
	}
	
	/*
	 *  Resumes the recording of a new route
	 *  @param controlSwitch will change the buttons state if true
	 */
	protected void resumeRecording(boolean controlSwitch) {
		theService.resumeRecording();
		// Show pause button and hide rec button
		if(controlSwitch) {
			pauseButton.setVisibility(View.VISIBLE);
			recButton.setVisibility(View.GONE);
		}
	}
	
	protected void toggleControls() {
		if(theService.routeRecorder.isPaused()) {
			recButton.setVisibility(View.VISIBLE);
			pauseButton.setVisibility(View.GONE);
		} else {
			recButton.setVisibility(View.GONE);
			pauseButton.setVisibility(View.VISIBLE);
			flagButton.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        moveTaskToBack(true);
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public void firstLocationReceived() {
		if(recordRouteToolbarView.getVisibility() == View.GONE)
			recordRouteToolbarView.setVisibility(View.VISIBLE);
		if(statsToolbarView.getVisibility() == View.GONE)
			statsToolbarView.setVisibility(View.VISIBLE);
		if(gpsWaitingView.getVisibility() == View.VISIBLE)
			gpsWaitingView.setVisibility(View.GONE);
	}
	
	public void locationUpdated() {
		Log.e("WIKICLETA", "Pruebas");
		runOnUiThread(new Runnable() {
		    public void run() {
		    	firstLocationReceived();
		    }
		});
	}

	@Override
	public void onFieldsUpdated() {
		runOnUiThread(new Runnable() {
		    public void run() {
				String speed = theService.routeRecorder.speedTextValue;
				String time = theService.routeRecorder.timeTextValue;
				String distance = theService.routeRecorder.distanceTextValue;
				
				if(speed != null)
					speedTextValue.setText(speed);
				if(time != null)
					timeTextValue.setText(time);
				if(distance != null)
					distanceTextValue.setText(distance);
		    }
		});

	}
}
