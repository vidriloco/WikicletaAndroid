package org.wikicleta.views;

import org.wikicleta.R;
import org.wikicleta.activities.trips.TripDetailsActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.layers.common.LayersConnectorListener;
import org.wikicleta.models.Trip;
import org.wikicleta.routing.Others;
import org.wikicleta.routing.Others.ImageUpdater;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TripViews {

	public static void buildViewForTrip(LayersConnectorListener listener, Trip trip) {
		Activity activity = listener.getActivity();
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_trip_details, null);
        
        TextView cyclingGroupName = (TextView) view.findViewById(R.id.trip_name_text);
        cyclingGroupName.setTypeface(AppBase.getTypefaceStrong());
        cyclingGroupName.setText(trip.name);
        
        TextView cyclingGroupNextRideDate = (TextView) view.findViewById(R.id.trip_next_date_text);
        cyclingGroupNextRideDate.setTypeface(AppBase.getTypefaceLight());
        cyclingGroupNextRideDate.setText(String.format(activity.getResources().getString(trip.daysToRide()), trip.daysToEventFromNow));
        
        TextView tripsDetailsText = (TextView) view.findViewById(R.id.trip_details_text);
        tripsDetailsText.setTypeface(AppBase.getTypefaceLight());
        tripsDetailsText.setText(trip.details);
        
        TextView tripMoreInfo = (TextView) view.findViewById(R.id.trip_more_info);
        tripMoreInfo.setTypeface(AppBase.getTypefaceStrong());

        LinearLayout tripContainerDetails = (LinearLayout) view.findViewById(R.id.trip_more_info_container_button);
        tripContainerDetails.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
			}
        	
        });
        
        if(trip.hasPic()) {
            ImageView ownerPic = (ImageView) view.findViewById(R.id.pic_image);
            ImageUpdater updater = Others.getImageFetcher();
            updater.setImageAndImageProcessor(ownerPic, Others.ImageProcessor.NONE);
            updater.execute(trip.pic);
        }
        
        dialog.setView(view);
        final AlertDialog visibleDialog = dialog.create();
        
        visibleDialog.show();
	}

	public static void displayItem(Trip trip) {
		Bundle bundle = new Bundle();
		bundle.putSerializable("trip", trip);
		AppBase.launchActivityWithBundle(TripDetailsActivity.class, bundle);
	}
}
