package com.wikicleta.activities;

import org.mobility.wikicleta.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MyLocationOverlay;
import com.wikicleta.common.AppBase;
import com.wikicleta.common.Constants;
import com.wikicleta.helpers.GeoHelpers;
import com.wikicleta.helpers.NotificationBuilder;
import com.wikicleta.helpers.RouteTracer;
import com.wikicleta.models.Instant;
import com.wikicleta.models.Route;
import com.wikicleta.views.PinchableMapView;
import com.wikicleta.views.RouteOverlay;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/*
 * TODOS:
 * 
 * - Stop updating location if no route recording is being done
 * - Increase update frequency of route by distance/time
 * - Fix recording buttons and route timer for when canceling discarding of route
 */

public class RoutesActivity extends MapActivity implements LocationListener {

	protected enum TaskPanel {Main, Recording};
	protected TaskPanel currentTaskPanel;
	
	public PinchableMapView mapView;
	
	protected RelativeLayout titleBarView;
	protected LinearLayout toolBarView;
	protected LinearLayout recordRouteToolbarView;
	protected LinearLayout statsToolbarView;	
	
	private ImageView recButton;
	private ImageView pauseButton;
	private ImageView flagButton;
	
	private LocationManager locationManager;
	static RouteTracer currentPath;

	private MyLocationOverlay locationOverlay;
	protected RouteOverlay routeOverlay;

	AlertDialog.Builder builder;

	protected LocationListener listener;
	
	protected AlertDialog.Builder alertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppBase.currentActivity = this;
		listener = this;

		setContentView(R.layout.activity_routes_on_map);
		
		mapView = (PinchableMapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(false);
		
        titleBarView = (RelativeLayout) findViewById(R.id.titlebar);
        toolBarView = (LinearLayout) findViewById(R.id.toolbar);
        recordRouteToolbarView = (LinearLayout) findViewById(R.id.route_recording_toolbar);
        statsToolbarView = (LinearLayout) findViewById(R.id.stats_panel);
                
		showToolbarFor(TaskPanel.Main);
        
        this.setMapToDefaultValues();
        alertDialog = new AlertDialog.Builder(this);
        
		currentPath = new RouteTracer((TextView) findViewById(R.id.speed_number), 
				(TextView) findViewById(R.id.time_elapsed_number),
				(TextView) findViewById(R.id.distance_number));
		
		routeOverlay = new RouteOverlay(currentPath.instantList);
		mapView.getOverlays().add(routeOverlay);
		
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, this);

    	locationOverlay = new MyLocationOverlay(this, mapView);
    	mapView.getOverlays().add(locationOverlay);
    	
    	locationOverlay.enableMyLocation();
    	locationOverlay.disableCompass();
    	
    	recButton = (ImageView) findViewById(R.id.routes_rec_button);
    	flagButton = (ImageView) findViewById(R.id.routes_finish_button);
    	pauseButton = (ImageView) findViewById(R.id.routes_pause_button);

    	alertDialog = new AlertDialog.Builder(this);
    	
    	findViewById(R.id.routes_add_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showToolbarFor(TaskPanel.Recording);
			}
		});
    	
    	findViewById(R.id.routes_back_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!currentPath.isEmpty()) {
					pauseRecording(false);

					alertDialog.setTitle("Pregunta");
					alertDialog.setMessage("ÀDeseas descartar esta ruta?");
					// If the user chooses 'Yes', then
					alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Resetting the captured path and backing to main floatant menu
							resetControls();
							currentPath.reset();
						}
					});
					// If user chooses 'No', then the dialog closes
					alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Resume tracking (with auto-recording)
							resumeRecording(false);
							
						}
					});
					alertDialog.setNeutralButton(null, null);
					alertDialog.show();
				} else {
					resetControls();
					currentPath.reset();
				}
			}
		});
    	
    	findViewById(R.id.routes_search_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toolBarView.setVisibility(View.GONE);
			}
		});
    	
    	recButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mapView.postInvalidate();
				resumeRecording(true);
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
						100, 10, listener);
				flagButton.setVisibility(View.VISIBLE);
				
			}
		});
    	
    	pauseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pauseRecording(true);
		    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 
		    			10000, 10, listener);
			}
		});
    	
    	flagButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {				
				if(currentPath.isEmpty()) {
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
					if(currentPath.isPaused())
						resumeRecording(true);
					pauseRecording(true);
					
					Intent intentActivity = new Intent(AppBase.currentActivity, RoutesSavingActivity.class);
					AppBase.currentActivity.startActivity(intentActivity);
				}
			}
    	});
    	
        this.checkForQueuedRoutes();
        int instants = Instant.all(Instant.class).size();
        int routes = Route.all(Route.class).size();
        Log.e("Wikicleta", "Instants count: " + instants);
        Log.e("Wikicleta", "Routes count: " + routes);
        
	}

	
	protected void checkForQueuedRoutes() {		
		if(DraftRoutesActivity.queuedRoutesCount() > 0) {
			
			NotificationBuilder notification = new NotificationBuilder(this);
			
			String countString = DraftRoutesActivity.queuedRoutesCount() == 1 ? 
					this.getString(R.string.route_drafts_notification_total_one) : 
						this.getString(R.string.route_drafts_notification_total_many, DraftRoutesActivity.queuedRoutesCount()); 
			
			notification.addNotification(Constants.DRAFT_ROUTES_NOTIFICATION_ID, 
					getString(R.string.app_name), countString, DraftRoutesActivity.class);
			
			TextView routesQueuedTextView = (TextView) findViewById(R.id.drafts_queued_text);
			// set routes count on text view
	        routesQueuedTextView.setText(String.valueOf(DraftRoutesActivity.queuedRoutesCount()));
	        findViewById(R.id.drafts_text_container).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
				}
			});
		}
		
		//this.toggleQueuedRoutesButton();
	}
	
	// Refactor this views
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	public void toggleQueuedRoutesButton() {
		if(DraftRoutesActivity.queuedRoutesCount() > 0) {
			ImageView button = (ImageView) findViewById(R.id.drafts_button_icon);
			View border = (View) findViewById(R.id.drafts_border);
			RelativeLayout grouper = (RelativeLayout) findViewById(R.id.drafts_text_container);
			
			int visibilityForAll = View.GONE;
			int alpha = 0;
			if(button.getVisibility() == View.GONE) {
				visibilityForAll = View.VISIBLE;
				alpha = 1;
			}
			button.setVisibility(visibilityForAll);
			button.animate().alpha(alpha);
	        
	        border.setVisibility(visibilityForAll);
	        border.animate().alpha(alpha);
	        
	        grouper.setVisibility(visibilityForAll);
	        grouper.animate().alpha(alpha);
		}
	}
	
	protected void resetControls() {
		showToolbarFor(TaskPanel.Main);	
		recButton.setVisibility(View.VISIBLE);
		pauseButton.setVisibility(View.GONE);
		flagButton.setVisibility(View.GONE);
	}
	
	/*
	 *  Pauses the recording of a new route
	 *  @param controlSwitch will change the buttons state if true
	 */
	protected void pauseRecording(boolean controlSwitch) {
		currentPath.pause();
		
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
		currentPath.resume();
		
		// Show pause button and hide rec button
		if(controlSwitch) {
			pauseButton.setVisibility(View.VISIBLE);
			recButton.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected void setMapToDefaultValues() {
		mapView.getController().animateTo(this.getDefaultLocation());
        mapView.getController().setZoom(18);
	}
	
	protected void setMapToLocation(Location location) {
		mapView.getController().animateTo(GeoHelpers.buildFromLongitude(location));
	}
	
	protected GeoPoint getDefaultLocation() {
		return GeoHelpers.buildFromLatLon(19.412423, -99.169207);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        moveTaskToBack(true);
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onLocationChanged(Location location) {
		this.setMapToLocation(location);
		currentPath.addLocation(location);
		mapView.postInvalidate();
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	protected void showToolbarFor(TaskPanel newTask) {

        if(newTask == TaskPanel.Main) {
        	if(this.currentTaskPanel == TaskPanel.Recording) {
        		this.recordRouteToolbarView.animate().alpha(0).translationYBy(Constants.DY_TRANSLATION).setListener(new AnimatorListener(){

					@Override
					public void onAnimationCancel(Animator animation) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						recordRouteToolbarView.setVisibility(View.GONE);
						statsToolbarView.setVisibility(View.GONE);
					}

					@Override
					public void onAnimationRepeat(Animator animation) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onAnimationStart(Animator animation) {
						//toggleQueuedRoutesButton();
					}});
        	}
        		
        	this.toolBarView.animate().alpha((float) 0.8).translationYBy(-Constants.DY_TRANSLATION).setListener(new AnimatorListener(){

				@Override
				public void onAnimationCancel(Animator animation) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onAnimationEnd(Animator animation) {
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onAnimationStart(Animator animation) {
					toolBarView.setVisibility(View.VISIBLE);
				}});
        } else if(newTask == TaskPanel.Recording) {
        	if(this.currentTaskPanel == TaskPanel.Main) 
        		this.toolBarView.animate().alpha(0).translationYBy(Constants.DY_TRANSLATION).setListener(new AnimatorListener(){

					@Override
					public void onAnimationCancel(Animator animation) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						toolBarView.setVisibility(View.GONE);
						//toggleQueuedRoutesButton();
					}

					@Override
					public void onAnimationRepeat(Animator animation) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onAnimationStart(Animator animation) {
						// TODO Auto-generated method stub
					}});
        	this.recordRouteToolbarView.animate().alpha((float) 0.8).translationYBy(-Constants.DY_TRANSLATION).setListener(new AnimatorListener(){

				@Override
				public void onAnimationCancel(Animator animation) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onAnimationEnd(Animator animation) {
					recordRouteToolbarView.setVisibility(View.VISIBLE);
					statsToolbarView.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onAnimationStart(Animator animation) {
					// TODO Auto-generated method stub
					
				}});;
        }
        this.currentTaskPanel = newTask;
        
        /*TranslateAnimation translation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0, Animation.RELATIVE_TO_SELF,-100);
        translation.setDuration(1000);
        
        translation.setFillAfter(true); //HERE
        translation.setFillEnabled(true);
        toolBarView.startAnimation(translation);     */   
	}
}
