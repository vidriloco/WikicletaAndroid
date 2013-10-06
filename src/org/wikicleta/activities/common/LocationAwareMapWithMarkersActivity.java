package org.wikicleta.activities.common;

import java.util.ArrayList;
import java.util.HashMap;
import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.helpers.SimpleAnimatorListener;
import org.wikicleta.layers.common.LayersConnectorListener;
import org.wikicleta.models.MarkerInterface;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

public class LocationAwareMapWithMarkersActivity extends LocationAwareMapWithControlsActivity implements LayersConnectorListener, OnMarkerClickListener {
	
	protected ObjectAnimator uploaderAnimator;
	protected ObjectAnimator uploaderContainerAnimator;
	protected LinearLayout loadingLayersContainer;
	protected ImageView loadingLayersIcon;
	
	protected Handler handler = new Handler();
	protected boolean handlerRunning = false;
	protected HashMap<Marker, MarkerInterface> markers;
	
	@SuppressLint("UseSparseArrays")
	@Override
	public void onCreate(Bundle savedInstanceState, int layoutID) {
		shouldAnimateWithCustomTransition = true;
		super.onCreate(savedInstanceState, layoutID);
    	
		assignToggleActionsForAutomapCenter();
		AppBase.currentActivity = this;
		
		// Assign icons
		loadingLayersIcon = (ImageView) this.findViewById(R.id.spinner_indicator);
		loadingLayersContainer = (LinearLayout) this.findViewById(R.id.mutable_box_container);
    	
    	// Listeners for map
    	map.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition position) {
				reloadActiveLayers();
			}
    		
    	});
   	 	map.setOnMarkerClickListener(this);
	}

	public void reloadActiveLayers() {
		
	}
	
	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void overlayFinishedLoading(final boolean status) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Handler handlerTimer = new Handler();
		        handlerTimer.postDelayed(new Runnable(){
		            public void run() {
						hideLoadingState();
		          }}, 2000);
			}
			
		});

	}

	@Override
	public void showLoadingState() {
		handler.postDelayed(cancelableDelayedLoadingAnimation, 10L);        
	}
	
	@Override
	public void hideLoadingState() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(uploaderAnimator != null) {
					uploaderContainerAnimator.cancel();
					uploaderAnimator.cancel();
					
				}
				handlerRunning = false;
			}
			
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void overlayFinishedLoadingWithPayload(boolean status, Object payload) {
		
    	for(MarkerInterface markerInterfaced : (ArrayList<MarkerInterface>) payload) { 
			Marker marker = map.addMarker(new MarkerOptions()
            .position(markerInterfaced.getLatLng())
            .icon(BitmapDescriptorFactory.fromResource(markerInterfaced.getDrawable())));
    		markers.put(marker, markerInterfaced);
    	}		
    	this.overlayFinishedLoading(status);
	}

	@Override
	public LatLng getLastLocation() {
		return this.lastKnownLocation;
	}

	@Override
	public HashMap<String, String> getCurrentViewport() {
		HashMap<String, String> viewport = new HashMap<String, String>();
		
		RelativeLayout mapContainer = (RelativeLayout) findViewById(R.id.map_container);
		
		LatLng leftBottom = (LatLng) map.getProjection().fromScreenLocation(new Point(0, mapContainer.getHeight()));
		LatLng rightTop = (LatLng) map.getProjection().fromScreenLocation(new Point(mapContainer.getWidth(), 0));
		viewport.put("sw", String.valueOf(leftBottom.latitude).concat(",").concat(String.valueOf(leftBottom.longitude)));
		viewport.put("ne", String.valueOf(rightTop.latitude).concat(",").concat(String.valueOf(rightTop.longitude)));
		return viewport;
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	protected Runnable cancelableDelayedLoadingAnimation = new Runnable() {
		   @Override
		   public void run() {
			   if(!handlerRunning) {
				   handlerRunning = true;
				   uploaderContainerAnimator = ObjectAnimator.ofFloat(loadingLayersContainer, "alpha", 0, 1, 1);
				   
				   uploaderAnimator = ObjectAnimator.ofFloat(loadingLayersIcon, "rotation", 0, 360);
				   uploaderAnimator.setRepeatCount(ObjectAnimator.INFINITE);
				   uploaderAnimator.setDuration(1500);
			       uploaderAnimator.start();
			       uploaderAnimator.addListener(new SimpleAnimatorListener() {

						@Override
						public void onAnimationCancel(Animator animation) {
							ObjectAnimator.ofFloat(loadingLayersContainer, "alpha", 1, 0, 0).setDuration(1000).start();
						}
						
					});
				   uploaderContainerAnimator.setDuration(1500);
			       uploaderContainerAnimator.start();
			   } 
		   }
	};
}