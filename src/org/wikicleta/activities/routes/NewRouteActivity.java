package org.wikicleta.activities.routes;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;

import org.wikicleta.R;
import org.wikicleta.activities.RootActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.FieldValidators;
import org.wikicleta.helpers.Formatters;
import org.wikicleta.models.Route;
import org.wikicleta.models.User;
import org.wikicleta.routing.Routes;
import org.wikicleta.routing.Others.Cruds;
import org.wikicleta.services.routes.NavigationListener;
import org.wikicleta.services.routes.RouteTrackingService;
import org.wikicleta.services.routes.ServiceConstructor;
import org.wikicleta.services.routes.ServiceListener;
import com.nineoldandroids.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewRouteActivity extends Activity implements ServiceListener, NavigationListener {
	
	protected RelativeLayout recordRouteToolbarView;
	protected RelativeLayout gpsWaitingView;
	protected RelativeLayout statsView;
	
	private ImageView recButton;
	private ImageView pauseButton;
	private ImageView saveButton;
	
	protected TextView speedTextValue;
	protected TextView timeTextValue;
	protected TextView distanceTextValue;
	protected TextView routesRecordingTitle;
	protected DecimalFormat decimalFormat;
	
	//Service
	protected RouteTrackingService theService;
	ServiceConstructor serviceInitializator;

	protected boolean firstLocationReceived = false;
	private ObjectAnimator uploaderAnimator;
		
	protected EditText nameView;
	protected EditText detailsView;
	protected CheckBox routeIsPrivate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		AppBase.currentActivity = this;
		this.setContentView(R.layout.activity_new_route);
		setTheme(R.style.Theme_wikicleta);

		decimalFormat = new DecimalFormat("##.##");
		decimalFormat.setRoundingMode(RoundingMode.DOWN);
        recordRouteToolbarView = (RelativeLayout) findViewById(R.id.toggable_group);
        statsView = (RelativeLayout) findViewById(R.id.stats_container);
        gpsWaitingView = (RelativeLayout) findViewById(R.id.waiting_for_gps_container);
        
        this.uploaderAnimator = ObjectAnimator.ofFloat(gpsWaitingView, "alpha", 1, 0.2f, 1);
    	this.uploaderAnimator.setDuration(3000);
    	this.uploaderAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        this.uploaderAnimator.start();
        
        TextView gpsWaiting = (TextView) findViewById(R.id.waiting_for_gps);
        gpsWaiting.setTypeface(AppBase.getTypefaceStrong());
        
        ImageView returnIcon = (ImageView) this.findViewById(R.id.return_button);
    	
    	recButton = (ImageView) findViewById(R.id.routes_rec_button);
    	saveButton = (ImageView) findViewById(R.id.routes_finish_button);
    	pauseButton = (ImageView) findViewById(R.id.routes_pause_button);
    	
		routesRecordingTitle = (TextView) findViewById(R.id.routes_recording_state_title);

    	speedTextValue = (TextView) findViewById(R.id.speed_number);
    	timeTextValue = (TextView) findViewById(R.id.time_elapsed_number);
    	distanceTextValue = (TextView) findViewById(R.id.distance_number);

    	recButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				resumeRecording(true);
			}
		});
    	
    	pauseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				theService.pauseRecordingAndNotify();
				pauseRecording(true);
			}
		});
    	
    	saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {				
				if(!theService.pathIsEmpty()) {
					if(!theService.isTracking)
						resumeRecording(true);
					pauseRecording(true);
					theService.pauseRecording();
					buildAndDisplaySaveDialog();
				}
			}
    	});
    	
    	returnIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if(gpsWaitingView.getVisibility() == View.VISIBLE) {
					AppBase.launchActivity(RootActivity.class);
				} else {
					final boolean wasRecording;
					if(!theService.pathIsEmpty()) {
						wasRecording = !theService.isTracking;
						pauseRecording(true);
						theService.pauseRecording();
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewRouteActivity.this);
						alertDialog.setTitle(getResources().getString(R.string.question));
						alertDialog.setMessage(getResources().getString(R.string.routes_discard_route_question));
						// If the user chooses 'Yes', then
						alertDialog.setPositiveButton(getResources().getString(R.string.confirm_yes), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								cancelRecording();
							}
						});
						// If user chooses 'No', then the dialog closes
						alertDialog.setNegativeButton(getResources().getString(R.string.confirm_no), new DialogInterface.OnClickListener() {
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
			}
    		
    	});
	}
	
	protected void buildAndDisplaySaveDialog() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        
        View view = inflater.inflate(R.layout.dialog_route_save, null);
		alertDialog.setView(view);
		final AlertDialog dialog = alertDialog.create();
		
		nameView = (EditText) view.findViewById(R.id.route_name);
		detailsView = (EditText) view.findViewById(R.id.route_details);
		routeIsPrivate = (CheckBox) view.findViewById(R.id.route_is_private);
		
        Button saveButton = (Button) view.findViewById(R.id.save_route);
        saveButton.setTypeface(AppBase.getTypefaceStrong());
        
        ((TextView) view.findViewById(R.id.dialog_menu_title)).setTypeface(AppBase.getTypefaceStrong());
        
        ImageView closeImage = (ImageView) view.findViewById(R.id.dialog_close);
        closeImage.setOnClickListener(new OnClickListener () {
        	
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
        	
        });
        
        saveButton.setOnClickListener(new OnClickListener () {

			@Override
			public void onClick(View v) {
				attemptCommit();
			}
        	
        });
        
        dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				if(!theService.isTracking) {
					resumeRecording(true);
				} 
			}
        	
        });
        dialog.show();
	}
	
	public void attemptCommit() {
		String routeName = nameView.getText().toString();
		String routeDetails = detailsView.getText().toString();
		
		Route route = new Route(routeName, routeDetails, 
				Formatters.secondsFromMilliseconds(theService.seconds), 
				theService.averageSpeed, theService.accumulatedDistance, new Date().getTime(), theService.coordinateVector, User.id());
		route.isPublic = !routeIsPrivate.isChecked();

		if(FieldValidators.isFieldEmpty(routeName)) {
			nameView.setError(getResources().getString(R.string.tips_input_empty));
			return;
		}
		
		if(FieldValidators.isFieldEmpty(routeDetails)) {
			detailsView.setError(getResources().getString(R.string.tips_input_empty));
			return;
		}
		
		Routes.PostOrPut poster = new Routes().new PostOrPut();
		poster.activity = this;
		if(route.existsOnRemoteServer())
			poster.mode = Cruds.MODIFY;
		poster.execute(route);
	}
	
	protected void cancelRecording() {
		theService.stopTracking();
		serviceInitializator.stop();
		resetUI();
		AppBase.launchActivity(RootActivity.class);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(!theService.isTracking)
			theService.pauseRecording();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		overridePendingTransition(R.anim.right_to_left, R.anim.fade_to_black);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		serviceInitializator = new ServiceConstructor(this);
        serviceInitializator.start(RouteTrackingService.class);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
        //serviceInitializator.stop();
	}
	
	@Override
	public void afterServiceConnected(Service service) {
		if(service instanceof RouteTrackingService) {
			this.theService = (RouteTrackingService) service;
		}
	}
	
	protected void resetUI() {
		recButton.setVisibility(View.VISIBLE);
		pauseButton.setVisibility(View.GONE);
		saveButton.setVisibility(View.GONE);
		
		this.timeTextValue.setText(getString(R.string.dashes));
		this.distanceTextValue.setText(getString(R.string.dashes));
		this.speedTextValue.setText(getString(R.string.dashes));
		theService.reset();
	}
	
	/*
	 *  Pauses the recording of a new route
	 *  @param controlSwitch will change the buttons state if true
	 */
	protected void pauseRecording(boolean controlSwitch) {
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
		// Show pause button and hide rec button
		if(controlSwitch) {
			pauseButton.setVisibility(View.VISIBLE);
			recButton.setVisibility(View.GONE);
		}
		routesRecordingTitle.setText(R.string.routes_recording_resumed);
		theService.resumeRecording();
	}
	
	protected void toggleControls() {
		if(!theService.isTracking) {
			recButton.setVisibility(View.VISIBLE);
			pauseButton.setVisibility(View.GONE);
		} else {
			recButton.setVisibility(View.GONE);
			pauseButton.setVisibility(View.VISIBLE);
			saveButton.setVisibility(View.VISIBLE);
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

	public void showSaveButton() {
    	if(saveButton.getVisibility() == View.GONE)
    		saveButton.setVisibility(View.VISIBLE);
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
				speedTextValue.setText(decimalFormat.format(theService.averageSpeed).concat(" km/h"));
				timeTextValue.setText(Formatters.millisecondsToTime(theService.seconds*1000));
				distanceTextValue.setText(decimalFormat.format(theService.accumulatedDistance).concat(" km"));
				
				if(!theService.coordinateVector.isEmpty())
					showSaveButton();
		    }
		});

	}
}
