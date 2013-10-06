package org.wikicleta.activities.trips;

import java.util.HashMap;
import org.wikicleta.R;
import org.wikicleta.activities.common.LocationAwareMapWithControlsActivity;
import org.wikicleta.common.AppBase;
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
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class TripDetailsActivity extends LocationAwareMapWithControlsActivity implements OnMarkerClickListener {
	HashMap<Marker, MarkerInterface> markers;
	public static Trip selectedTrip;
	ImageView returnIcon;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_trip_details);
		setTheme(R.style.Theme_wikicleta);
		AppBase.currentActivity = this;		
		assignToggleActionsForAutomapCenter();
			
        if(selectedTrip == null)
        	finish();
        
        this.markers = new HashMap<Marker, MarkerInterface>();
        loadTripMetadata(selectedTrip);
        
        TextView daysToEvent = (TextView) this.findViewById(R.id.trip_days_to_event_text);        
        daysToEvent.setTypeface(AppBase.getTypefaceLight());
        daysToEvent.setText(String.format(getResources().getString(selectedTrip.daysToRide()), selectedTrip.daysToEventFromNow));

        TextView eventName = (TextView) this.findViewById(R.id.trip_name_text);        
        eventName.setTypeface(AppBase.getTypefaceStrong());
        eventName.setText(selectedTrip.name);

        
   	 	map.setOnMarkerClickListener(this);

   	 	returnIcon = (ImageView) this.findViewById(R.id.return_button);
		returnIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				overridePendingTransition(0, 0);
				finish();
			}
 		
 	});
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
        	map.animateCamera(CameraUpdateFactory.newLatLngZoom(trip.start.getLatLng(), 19));

        	this.findViewById(R.id.flag_start_button).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
		        	map.animateCamera(CameraUpdateFactory.newLatLngZoom(trip.start.getLatLng(), 19));
				}
        	
        	});
        }
        
        if(trip.end != null) {        	
        	map.animateCamera(CameraUpdateFactory.newLatLngZoom(trip.end.getLatLng(), 19));

        	this.findViewById(R.id.flag_finish_button).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
		        	map.animateCamera(CameraUpdateFactory.newLatLngZoom(trip.end.getLatLng(), 19));
				}
        	
        	});
        } else {
        	this.findViewById(R.id.flag_finish_button).setVisibility(View.GONE);
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
