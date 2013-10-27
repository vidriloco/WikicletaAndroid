package org.wikicleta.views;

import java.text.DecimalFormat;
import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.helpers.GeoHelpers;
import org.wikicleta.layers.common.LayersConnectorListener;
import org.wikicleta.models.CycleStation;
import com.google.android.gms.maps.model.LatLng;
import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class CycleStationViews  {
	public static void buildViewForCycleStation(LayersConnectorListener listener, final CycleStation item) {
    	Activity activity = listener.getActivity();
    	
    	final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_cycle_station_details, null);
        
        TextView bikeSharingTitle = (TextView) view.findViewById(R.id.bike_sharing_title);
        bikeSharingTitle.setTypeface(AppBase.getTypefaceStrong());
        
        TextView cycleStationName = (TextView) view.findViewById(R.id.cycle_station_title);
        cycleStationName.setTypeface(AppBase.getTypefaceLight());
        cycleStationName.setText(item.name);
        
        TextView cycleStationDistance = (TextView) view.findViewById(R.id.cycle_station_distance);
        cycleStationName.setTypeface(AppBase.getTypefaceLight());
        
        LatLng location = listener.getLastLocation();
        if(location == null)
        	cycleStationDistance.setText(R.string.could_not_get_distance);
        else {
        	DecimalFormat df = new DecimalFormat("##.##");
			
			String distanceFormat = activity.getResources().getString(R.string.distance_from_location);
			String distanceFormatted = String.format(distanceFormat, df.format(
					GeoHelpers.distanceBetweenGeoPoints(item.location, location)));  
			cycleStationDistance.setText(distanceFormatted);
        }
        
        cycleStationDistance.setTypeface(AppBase.getTypefaceStrong());
        	
        
        TextView bikeCountTextView = (TextView) view.findViewById(R.id.bikes_text_for_number);
        bikeCountTextView.setTypeface(AppBase.getTypefaceLight());
        
        TextView bikeCount = (TextView) view.findViewById(R.id.bikes_number);
        bikeCount.setTypeface(AppBase.getTypefaceStrong());

        TextView slotCountTextView = (TextView) view.findViewById(R.id.slots_text_for_number);
        slotCountTextView.setTypeface(AppBase.getTypefaceLight());

        TextView slotCount = (TextView) view.findViewById(R.id.slots_number);
        slotCount.setTypeface(AppBase.getTypefaceStrong());

        bikeCount.setText(String.valueOf(item.availableBikes));
        slotCount.setText(String.valueOf(item.availableSlots));

        ImageView icon = (ImageView) view.findViewById(R.id.bike_sharing_status);
        icon.setImageResource(item.status());
        
        if(item.availableBikes != 1)
        	bikeCountTextView.setText(activity.getResources().getString(R.string.many_available_bikes));
        if(item.availableSlots != 1)
        	slotCountTextView.setText(activity.getResources().getString(R.string.many_available_slots));
       
        
        view.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
        	
        });
        
        dialog.setContentView(view);
        
        dialog.show();
	}
}
