package org.wikicleta.activities;

import java.util.HashMap;
import org.wikicleta.R;
import org.wikicleta.activities.common.LocationAwareMapWithMarkersActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.CyclingGroup;
import org.wikicleta.models.MarkerInterface;
import org.wikicleta.models.Trip;
import org.wikicleta.routing.Trips;
import org.wikicleta.routing.CyclingGroups;
import org.wikicleta.views.CyclingGroupViews;
import org.wikicleta.views.TripViews;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;

public class EventsActivity extends LocationAwareMapWithMarkersActivity {
	ImageView returnIcon;
	
	@SuppressLint("UseSparseArrays")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_events);
		setTheme(R.style.Theme_wikicleta);

		assignToggleActionsForAutomapCenter();
		
		returnIcon = (ImageView) this.findViewById(R.id.return_button);
    	returnIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppBase.launchActivity(RootActivity.class);
			}
    		
    	});
    	
    	// Listeners for map
    	map.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition position) {
				toggleLayers();
			}
    		
    	});
   	 	map.setOnMarkerClickListener(this);
	}
	
	@Override
	public boolean onMarkerClick(Marker marker) {
		MarkerInterface markerIn = (MarkerInterface) markers.get(marker);
		if(markerIn instanceof CyclingGroup)
			CyclingGroupViews.buildViewForCyclingGroup(this, (CyclingGroup) markerIn);
		if(markerIn instanceof Trip) {
			TripViews.buildViewForTrip(this, (Trip) markerIn);
		}
			
		return true;
	}

	protected void toggleLayers() {
		markers = new HashMap<Marker, MarkerInterface>();
		map.clear();
		this.showLoadingState();
    	CyclingGroups cyclingGroups = new CyclingGroups();
    	CyclingGroups.Get cyclingGroupsFetcher = cyclingGroups.new Get(this);
    	cyclingGroupsFetcher.execute();
    	
    	Trips trips = new Trips();
		Trips.Get tripsFetcher = trips.new Get(this);
		tripsFetcher.execute();
	}

}