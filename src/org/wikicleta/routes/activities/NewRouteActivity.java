package org.wikicleta.routes.activities;

import org.wikicleta.R;
import org.wikicleta.activities.MainMapActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.helpers.SlidingMenuAndActionBarHelper;
import org.wikicleta.routes.services.NavigationListener;
import org.wikicleta.routes.services.RoutesService;
import org.wikicleta.routes.services.ServiceConstructor;
import org.wikicleta.routes.services.ServiceListener;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.nineoldandroids.animation.ObjectAnimator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewRouteActivity extends Activity implements ServiceListener, NavigationListener {
	
	protected LinearLayout recordRouteToolbarView;
	
	protected RelativeLayout gpsWaitingView;
	protected RelativeLayout statsView;
	
	private ImageView recButton;
	private ImageView pauseButton;
	private ImageView flagButton;
	
	protected TextView speedTextValue;
	protected TextView timeTextValue;
	protected TextView distanceTextValue;
	protected TextView routesRecordingTitle;
	
	AlertDialog.Builder builder;
	protected AlertDialog.Builder alertDialog;

	//Service
	protected RoutesService theService;
	ServiceConstructor serviceInitializator;

	protected boolean firstLocationReceived = false;
	private ObjectAnimator uploaderAnimator;
	
	protected ActionBar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setTheme(R.style.Theme_wikicleta);
		AppBase.currentActivity = this;
		this.setContentView(R.layout.new_route_activity);

		startService(new Intent(this, RoutesService.class));

        recordRouteToolbarView = (LinearLayout) findViewById(R.id.route_recording_toolbar);
        statsView = (RelativeLayout) findViewById(R.id.stats_container);
        gpsWaitingView = (RelativeLayout) findViewById(R.id.waiting_for_gps_container);
        
        this.uploaderAnimator = ObjectAnimator.ofFloat(gpsWaitingView, "alpha", 1, 0.2f, 1);
    	this.uploaderAnimator.setDuration(3000);
    	this.uploaderAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        this.uploaderAnimator.start();
        
        TextView gpsWaiting = (TextView) findViewById(R.id.waiting_for_gps);
        gpsWaiting.setTypeface(AppBase.getTypefaceStrong());
        
        alertDialog = new AlertDialog.Builder(this);
    	
    	recButton = (ImageView) findViewById(R.id.routes_rec_button);
    	flagButton = (ImageView) findViewById(R.id.routes_finish_button);
    	pauseButton = (ImageView) findViewById(R.id.routes_pause_button);
    	
		routesRecordingTitle = (TextView) findViewById(R.id.routes_recording_state_title);

    	speedTextValue = (TextView) findViewById(R.id.speed_number);
    	timeTextValue = (TextView) findViewById(R.id.time_elapsed_number);
    	distanceTextValue = (TextView) findViewById(R.id.distance_number);

    	recButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				resumeRecording(true);
				flagButton.setVisibility(View.VISIBLE);
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
					setMessage("No puedes guardar una ruta vac�a").
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
					alertDialog.setMessage("�Deseas descartar esta ruta?");
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
    	
    	actionBar = (ActionBar) this.findViewById(R.id.actionbar);
    	
    	SlidingMenuAndActionBarHelper.setDefaultFontForActionBar(this);
    	
        actionBar.addAction(new Action() {

			@Override
			public int getDrawable() {
				return R.drawable.close_icon;
			}

			@Override
			public void performAction(View view) {
				finish();
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
			
	        theService.enableLocationManager();
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
		routesRecordingTitle.setText(R.string.routes_recording_paused);
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
		routesRecordingTitle.setText(R.string.routes_recording_resumed);
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
		if(statsView.getVisibility() == View.GONE)
			statsView.setVisibility(View.VISIBLE);
		if(gpsWaitingView.getVisibility() == View.VISIBLE) {
			this.uploaderAnimator.cancel();
			gpsWaitingView.setVisibility(View.GONE);
		}
		
		this.findViewById(R.id.wikicleta_logo).setVisibility(View.VISIBLE);
		actionBar.removeAllActions();
		
		TextView speedTitle = (TextView) findViewById(R.id.speed_title);
		speedTitle.setTypeface(AppBase.getTypefaceStrong());
		TextView timeTitle = (TextView) findViewById(R.id.time_title);
		timeTitle.setTypeface(AppBase.getTypefaceStrong());
		TextView distanceTitle = (TextView) findViewById(R.id.distance_title);
		distanceTitle.setTypeface(AppBase.getTypefaceStrong());
		
		routesRecordingTitle.setTypeface(AppBase.getTypefaceLight());
		
        speedTextValue.setTypeface(AppBase.getTypefaceLight());
        timeTextValue.setTypeface(AppBase.getTypefaceLight());
        distanceTextValue.setTypeface(AppBase.getTypefaceLight());

        
	}
	
	public void locationUpdated() {
		runOnUiThread(new Runnable() {
		    public void run() {
		    	if(!firstLocationReceived) {
		    		firstLocationReceived();
		    		firstLocationReceived = true;
		    	}
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
