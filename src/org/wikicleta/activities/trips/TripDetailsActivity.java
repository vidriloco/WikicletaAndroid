package org.wikicleta.activities.trips;

import java.util.HashMap;

import org.wikicleta.R;
import org.wikicleta.activities.common.LocationAwareMapWithControlsActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Toasts;
import org.wikicleta.models.MarkerInterface;
import org.wikicleta.models.Segment;
import org.wikicleta.models.Trip;
import org.wikicleta.models.TripPoi;
import org.wikicleta.views.TripPoiViews;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nineoldandroids.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class TripDetailsActivity extends LocationAwareMapWithControlsActivity implements OnMarkerClickListener {
	AlertDialog dialog;
	ObjectAnimator loadingAnimator;
	HashMap<Marker, MarkerInterface> markers;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.trip_details_activity);
		setTheme(R.style.Theme_wikicleta);
		AppBase.currentActivity = this;		
		assignToggleActionsForAutomapCenter();
			
        final Trip trip = (Trip) getIntent().getSerializableExtra("trip");
        if(trip == null)
        	finish();
        
        loadTripMetadata(trip);
        
        this.findViewById(R.id.info_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(trip.details != null)
					Toasts.showToastWithMessage(TripDetailsActivity.this, trip.details, R.drawable.info_icon);
			}
        	
        });
        
        TextView daysToEvent = (TextView) this.findViewById(R.id.trip_daysToEvent_text);        
        daysToEvent.setTypeface(AppBase.getTypefaceLight());

        //daysToEvent.setText(trip.daysToEvent);

        this.markers = new HashMap<Marker, MarkerInterface>();
   	 	map.setOnMarkerClickListener(this);

	}
	
	public void loadPOIsForTrip(Trip trip) {
		for(TripPoi tripPoi : trip.pois) {
			tripPoi.getDrawable();
			Marker marker = this.map.addMarker(new MarkerOptions()
            .position(tripPoi.getLatLng())
            .icon(BitmapDescriptorFactory.fromResource(tripPoi.getDrawable())));
			this.markers.put(marker, tripPoi);
		}
	}
	
	public void loadTripMetadata(final Trip trip) {
		
        if(trip.start != null) {
        	((TextView) this.findViewById(R.id.route_start_text)).setTypeface(AppBase.getTypefaceStrong());
        	
        	map.animateCamera(CameraUpdateFactory.newLatLngZoom(trip.start.getLatLng(), 19));

        	this.findViewById(R.id.route_start_button).setVisibility(View.VISIBLE);
        	this.findViewById(R.id.route_start_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
	        	map.animateCamera(CameraUpdateFactory.newLatLngZoom(trip.start.getLatLng(), 19));
			}
        	
        	});
        }
        
        if(trip.end != null) {
        	((TextView) this.findViewById(R.id.route_end_text)).setTypeface(AppBase.getTypefaceStrong());
        	
        	map.animateCamera(CameraUpdateFactory.newLatLngZoom(trip.end.getLatLng(), 19));

        	this.findViewById(R.id.route_end_button).setVisibility(View.VISIBLE);
        	this.findViewById(R.id.route_end_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
	        	map.animateCamera(CameraUpdateFactory.newLatLngZoom(trip.end.getLatLng(), 19));
			}
        	
        	});
        }
        
		for(Segment segment : trip.segments) {
			PolylineOptions options = new PolylineOptions();
			for(LatLng point : segment.points)
				options.add(point);
			options.color(Color.parseColor(segment.color));
			this.map.addPolyline(options);
		}
		this.loadPOIsForTrip(trip);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		MarkerInterface markerIn = (MarkerInterface) markers.get(marker);
		TripPoiViews.buildViewForTripPoi(this, (TripPoi) markerIn);
		return true;
	}
}
