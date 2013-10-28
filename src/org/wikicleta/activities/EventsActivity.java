package org.wikicleta.activities;

import java.util.ArrayList;
import java.util.Collections;
import org.wikicleta.R;
import org.wikicleta.activities.common.LocationAwareMapWithMarkersActivity;
import org.wikicleta.analytics.AnalyticsBase;
import org.wikicleta.common.AppBase;
import org.wikicleta.helpers.EventProximityComparator;
import org.wikicleta.interfaces.EventInterface;
import org.wikicleta.interfaces.MarkerInterface;
import org.wikicleta.models.CyclingGroup;
import org.wikicleta.models.Trip;
import org.wikicleta.routing.Trips;
import org.wikicleta.routing.CyclingGroups;
import org.wikicleta.views.CyclingGroupViews;
import org.wikicleta.views.EventsListingViewBuilder;
import org.wikicleta.views.TripViews;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class EventsActivity extends LocationAwareMapWithMarkersActivity {
	ImageView returnIcon;
	ImageView switchButtonIcon;
	
	@SuppressLint("UseSparseArrays")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_events);
		
		AnalyticsBase.reportLoggedInEvent("On Events Activity", getApplicationContext());
		
		returnIcon = (ImageView) this.findViewById(R.id.return_button);
    	returnIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppBase.launchActivity(RootActivity.class);
			}
    		
    	});
    	
    	switchButtonIcon = (ImageView) this.findViewById(R.id.list_switch_button);
    	switchButtonIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AnalyticsBase.reportLoggedInEvent("Events Activity: Displayed List", getApplicationContext());

				EventsListingViewBuilder.buildView(EventsActivity.this);
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
		MarkerInterface markerIn = (MarkerInterface) markers.get(marker.getPosition());
		if(markerIn instanceof CyclingGroup) {
			CyclingGroup cy = (CyclingGroup) markerIn;
			CyclingGroupViews.buildViewForCyclingGroup(this, cy);
			
			AnalyticsBase.reportLoggedInEvent("Events Activity: Clicked cycling group", getApplicationContext(), "cycling-group-id", String.valueOf(cy.remoteId));
		} if(markerIn instanceof Trip) {
			Trip tr = (Trip) markerIn;
			TripViews.buildViewForTrip(this, tr);
			
			AnalyticsBase.reportLoggedInEvent("Events Activity: Clicked trip", getApplicationContext(), "trip-id", String.valueOf(tr.remoteId));
		}
			
		return true;
	}
	
	public EventInterface [] visibleMarkers() {
		ArrayList<EventInterface> markersList = new ArrayList<EventInterface>();
		
		for(LatLng coordinate : this.markers.keySet()) {
			markersList.add((EventInterface) this.markers.get(coordinate));
		}
		
		Collections.sort(markersList, new EventProximityComparator());
		
		EventInterface [] eventsOrdered = new EventInterface[markersList.size()];
		int i=0;
		for(EventInterface event : markersList) {
			eventsOrdered[i] = event;
			i++;
		}
		
		return eventsOrdered;
	}
	
	public void centerOnEvent(MarkerInterface marker) {
		this.map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getLatLng(), 18));
	}

	protected void toggleLayers() {
		this.showLoadingState();
    	
    	Trips trips = new Trips();
		Trips.Get tripsFetcher = trips.new Get(this);
		tripsFetcher.execute();
		
    	CyclingGroups cyclingGroups = new CyclingGroups();
    	CyclingGroups.Get cyclingGroupsFetcher = cyclingGroups.new Get(this);
    	cyclingGroupsFetcher.execute();

	}

}