package org.wikicleta.activities.trips;

import org.wikicleta.R;
import org.wikicleta.activities.common.LocationAwareMapWithControlsActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Toasts;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.helpers.SlidingMenuAndActionBarHelper;
import org.wikicleta.layers.trips.TripPoisOverlay;
import org.wikicleta.models.Segment;
import org.wikicleta.models.Trip;
import org.wikicleta.models.TripPoi;
import org.wikicleta.routing.CityTrips;
import org.wikicleta.routing.CityTrips.Details;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.Action;
import com.nineoldandroids.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class TripDetailsActivity extends LocationAwareMapWithControlsActivity {
	AlertDialog dialog;
	ObjectAnimator loadingAnimator;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.trip_details_activity);
		setTheme(R.style.Theme_wikicleta);
		AppBase.currentActivity = this;		
		assignToggleActionsForAutomapCenter();
			
        final Trip trip = (Trip) getIntent().getSerializableExtra("trip");
        if(trip == null)
        	finish();
		
        this.findViewById(R.id.info_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(trip.details != null)
					Toasts.showToastWithMessage(TripDetailsActivity.this, trip.details, R.drawable.info_icon);
			}
        	
        });
        
        TextView daysToEvent = (TextView) this.findViewById(R.id.trip_daysToEvent_text);
        TextView cityName = (TextView) this.findViewById(R.id.trip_city_text);
        
        cityName.setTypeface(AppBase.getTypefaceStrong());
        daysToEvent.setTypeface(AppBase.getTypefaceLight());

        cityName.setText(trip.city.name);
        daysToEvent.setText(trip.daysToEvent);
        
        SlidingMenuAndActionBarHelper.setDefaultFontForActionBarWithTitle(this, trip.name);

    	ActionBar actionBar = (ActionBar) this.findViewById(R.id.actionbar);

        actionBar.addAction(new Action() {

			@Override
			public int getDrawable() {
				return R.drawable.close_icon;
			}

			@Override
			public void performAction(View view) {
				AppBase.launchActivity(TripsListActivity.class);
			}
        	
        });
        
		dialog = DialogBuilder.buildLoadingDialogWithMessage(TripDetailsActivity.this, R.string.trip_fetching_details).create();
		dialog.show();
		ImageView animatedView = (ImageView) dialog.findViewById(R.id.image_loading);
		
		loadingAnimator = ObjectAnimator.ofFloat(animatedView, "rotation", 0, -90);
		loadingAnimator.setDuration(1200);
		loadingAnimator.setRepeatCount(ObjectAnimator.INFINITE);
		
        Details cityTrips = new CityTrips().new Details();
        cityTrips.activity = this;
        cityTrips.execute(trip);
        
	}
	
	public void loadPOIsForTrip(Trip trip) {
		/*TripPoisOverlay overlay = new TripPoisOverlay(this.getResources().getDrawable(R.drawable.service_station_marker));
		for(TripPoi tripPoi : trip.pois) {
			overlay.addItem(new TripPoiOverlayItem(TripDetailsActivity.this, tripPoi));
		}
		
		this.mapView.getOverlays().add(overlay);
		this.mapView.refreshDrawableState();*/
	}
	
	public void onSuccessfulTripFetching(final Trip trip) {
		hideLoadingDialog();
		
        if(trip.start != null) {
        	((TextView) this.findViewById(R.id.route_start_text)).setTypeface(AppBase.getTypefaceStrong());
        	
        	map.animateCamera(CameraUpdateFactory.newLatLngZoom(trip.start.location(), 19));

        	this.findViewById(R.id.route_start_button).setVisibility(View.VISIBLE);
        	this.findViewById(R.id.route_start_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
	        	map.animateCamera(CameraUpdateFactory.newLatLngZoom(trip.start.location(), 19));
			}
        	
        	});
        }
        
        if(trip.end != null) {
        	((TextView) this.findViewById(R.id.route_end_text)).setTypeface(AppBase.getTypefaceStrong());
        	
        	map.animateCamera(CameraUpdateFactory.newLatLngZoom(trip.end.location(), 19));

        	this.findViewById(R.id.route_end_button).setVisibility(View.VISIBLE);
        	this.findViewById(R.id.route_end_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
	        	map.animateCamera(CameraUpdateFactory.newLatLngZoom(trip.end.location(), 19));
			}
        	
        	});
        }
        
		for(Segment segment : trip.segments) {
			PolylineOptions options = new PolylineOptions();
			for(LatLng point : segment.points)
				options.add(point);
			options.color(Color.parseColor(segment.color));
		}
		
		this.loadPOIsForTrip(trip);
	}
	
	public void onUnsuccessfulTripFetching() {
		hideLoadingDialog();
		
		Toasts.showToastWithMessage(TripDetailsActivity.this, 
				this.getResources().getString(R.string.trips_could_not_fetch), R.drawable.alert_icon, Toast.LENGTH_SHORT);
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {

		    public void run() {
				finish();
		    }

		}, 2500);
	}

	public void onFetchingTripStarted() {
		loadingAnimator.start();
	}
	
	public void hideLoadingDialog() {
		loadingAnimator.cancel();
		dialog.hide();
	}
}
