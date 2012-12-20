package com.wikicleta.activities;

import org.mobility.wikicleta.R;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MyLocationOverlay;
import com.wikicleta.common.AppBase;
import com.wikicleta.helpers.GeoHelpers;
import com.wikicleta.helpers.PathTrace;
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
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RoutesActivity extends MapActivity implements LocationListener {

	protected enum TaskPanel {Main, Recording};
	protected TaskPanel currentTaskPanel;
	
	public PinchableMapView mapView;
	
	protected RelativeLayout titleBarView;
	protected LinearLayout toolBarView;
	protected LinearLayout recordRouteToolbarView;
	protected LinearLayout statsToolbarView;
		
	protected int DY_TRANSLATION=100;	
	
	private ImageView recButton;
	private ImageView pauseButton;
	private ImageView flagButton;
	
	private LocationManager locationManager;
	static PathTrace currentPath;

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

		setContentView(R.layout.android_routes);
		
		mapView = (PinchableMapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(false);
        

        titleBarView = (RelativeLayout) findViewById(R.id.titlebar);
        toolBarView = (LinearLayout) findViewById(R.id.toolbar);
        recordRouteToolbarView = (LinearLayout) findViewById(R.id.route_recording_toolbar);
        statsToolbarView = (LinearLayout) findViewById(R.id.stats_panel);
                
		showToolbarFor(TaskPanel.Main);
        
        this.setMapToDefaultValues();
        alertDialog = new AlertDialog.Builder(this);
        
		currentPath = new PathTrace((TextView) findViewById(R.id.speed_number), 
				(TextView) findViewById(R.id.time_elapsed_number),
				(TextView) findViewById(R.id.distance_number));
		
		routeOverlay = new RouteOverlay(currentPath);
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
        		this.recordRouteToolbarView.animate().alpha(0).translationYBy(DY_TRANSLATION).setListener(new AnimatorListener(){

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
						// TODO Auto-generated method stub
						
					}});
        	}
        		
        	this.toolBarView.animate().alpha((float) 0.8).translationYBy(-DY_TRANSLATION).setListener(new AnimatorListener(){

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
        		this.toolBarView.animate().alpha(0).translationYBy(DY_TRANSLATION).setListener(new AnimatorListener(){

					@Override
					public void onAnimationCancel(Animator animation) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onAnimationEnd(Animator animation) {
						toolBarView.setVisibility(View.GONE);
					}

					@Override
					public void onAnimationRepeat(Animator animation) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onAnimationStart(Animator animation) {
						// TODO Auto-generated method stub
						
					}});
        	this.recordRouteToolbarView.animate().alpha((float) 0.8).translationYBy(-DY_TRANSLATION).setListener(new AnimatorListener(){

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
