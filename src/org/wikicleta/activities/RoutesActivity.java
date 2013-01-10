package org.wikicleta.activities;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.helpers.SlidingMenuAndActionBarHelper;
import org.wikicleta.helpers.RouteTracer;
import org.wikicleta.helpers.SimpleAnimatorListener;
import org.wikicleta.routes.services.RoutesService;
import org.wikicleta.routes.services.ServiceConstructor;
import org.wikicleta.routes.services.ServiceListener;
import org.wikicleta.views.RouteOverlay;
import com.nineoldandroids.animation.*;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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
 * - Fix recording buttons and route timer for when canceling discarding of route and route recording was stopped
 */
public class RoutesActivity extends LocationAwareMapActivity implements ServiceListener {

	protected enum TaskPanel {Main, Recording};
	protected TaskPanel currentTaskPanel;
	
	protected RelativeLayout titleBarView;
	protected LinearLayout toolBarView;
	protected LinearLayout recordRouteToolbarView;
	protected LinearLayout statsToolbarView;	
	
	private ImageView recButton;
	private ImageView pauseButton;
	private ImageView flagButton;
	
	static RouteTracer currentPath;
	protected RouteOverlay routeOverlay;

	AlertDialog.Builder builder;
	protected AlertDialog.Builder alertDialog;

	//Service
	protected RoutesService theService;
	ServiceConstructor serviceInitializator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_routes_on_map);
		AppBase.currentActivity = this;
		startService(new Intent(this, RoutesService.class));

        titleBarView = (RelativeLayout) findViewById(R.id.titlebar);
        toolBarView = (LinearLayout) findViewById(R.id.toolbar);
        recordRouteToolbarView = (LinearLayout) findViewById(R.id.route_recording_toolbar);
        statsToolbarView = (LinearLayout) findViewById(R.id.stats_panel);
                
		showToolbarFor(TaskPanel.Main);
        
        alertDialog = new AlertDialog.Builder(this);
        
		currentPath = new RouteTracer((TextView) findViewById(R.id.speed_number), 
				(TextView) findViewById(R.id.time_elapsed_number),
				(TextView) findViewById(R.id.distance_number));
		
		routeOverlay = new RouteOverlay(currentPath.instantList);
		mapView.getOverlays().add(routeOverlay);
    	
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
					alertDialog.setMessage("�Deseas descartar esta ruta?");
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
				flagButton.setVisibility(View.VISIBLE);
				enableLocationManager();
			}
		});
    	
    	pauseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pauseRecording(true);
				disableLocationManager();
			}
		});
    	
    	flagButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {				
				if(currentPath.isEmpty()) {
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
					if(currentPath.isPaused())
						resumeRecording(true);
					pauseRecording(true);
					
					Intent intentActivity = new Intent(AppBase.currentActivity, RoutesSavingActivity.class);
					AppBase.currentActivity.startActivity(intentActivity);
				}
			}
    	});
    	
        SlidingMenuAndActionBarHelper.load(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if(currentPath.isPaused())
			this.disableLocationManager();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.enableLocationManager();
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

	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
		currentPath.addLocation(location);
		mapView.postInvalidate();
	}
	
	protected void showToolbarFor(TaskPanel newTask) {
		AnimatorSet showSet = new AnimatorSet();
		AnimatorSet hideSet = new AnimatorSet();

        if(newTask == TaskPanel.Main) {
        	if(this.currentTaskPanel == TaskPanel.Recording) {
        		// Hide previous panel (recording panel)
        		hideSet.addListener(new SimpleAnimatorListener() {
					@Override
					public void onAnimationEnd(Animator animation) {
						recordRouteToolbarView.setVisibility(View.GONE);
						statsToolbarView.setVisibility(View.GONE);						
					}
        		});
        		hideSet.playTogether(ObjectAnimator.ofFloat(recordRouteToolbarView, "alpha", 0), 
        				ObjectAnimator.ofFloat(recordRouteToolbarView, "translationY", Constants.DY_TRANSLATION));
        		hideSet.start();
        	}
        	
    		showSet.playTogether(ObjectAnimator.ofFloat(toolBarView, "alpha", 0.8f),
    				ObjectAnimator.ofFloat(toolBarView, "translationY", -Constants.DY_TRANSLATION));
    		showSet.addListener(new SimpleAnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {
					toolBarView.setVisibility(View.VISIBLE);
				}
    		});
    		showSet.start();
        } else if(newTask == TaskPanel.Recording) {
        	if(this.currentTaskPanel == TaskPanel.Main) {
        		// Hide previous panel (main panel)
        		hideSet.addListener(new SimpleAnimatorListener() {
					@Override
					public void onAnimationEnd(Animator animation) {
						toolBarView.setVisibility(View.GONE);					
					}
        		});
        		hideSet.playTogether(ObjectAnimator.ofFloat(toolBarView, "alpha", 0), 
        				ObjectAnimator.ofFloat(toolBarView, "translationY", Constants.DY_TRANSLATION));    
        		hideSet.start();
        	}

    		showSet.playTogether(ObjectAnimator.ofFloat(recordRouteToolbarView, "alpha", 0.8f),
    				ObjectAnimator.ofFloat(recordRouteToolbarView, "translationY", -Constants.DY_TRANSLATION));
    		showSet.addListener(new SimpleAnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {
                    recordRouteToolbarView.setVisibility(View.VISIBLE);
					statsToolbarView.setVisibility(View.VISIBLE);
				}
    		});
    		showSet.start();
        }
        this.currentTaskPanel = newTask;
	}
}
