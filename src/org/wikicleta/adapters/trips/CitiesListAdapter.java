package org.wikicleta.adapters.trips;

import java.util.ArrayList;
import org.wikicleta.R;
import org.wikicleta.activities.trips.TripDetailsActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.City;
import org.wikicleta.models.Trip;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CitiesListAdapter extends ArrayAdapter<City> {
	  private final Context context;
	  private final ArrayList<City> cities;
	  private LayoutInflater inflater;
	  	  
	  public CitiesListAdapter(Context context, ArrayList<City> cities) {
	    super(context, R.layout.layers_menu_list_item, cities);
	    this.context = context;
	    this.cities = cities;
	    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  }

		
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = inflater.inflate(R.layout.trip_city_item, parent, false);
	    City city = cities.get(position);
	    
	    TextView cityName = (TextView) rowView.findViewById(R.id.city_name);
	    cityName.setText(city.name);
    	cityName.setTypeface(AppBase.getTypefaceStrong());
    	
    	TextView cityTrips = (TextView) rowView.findViewById(R.id.city_trips);
    	cityTrips.setTypeface(AppBase.getTypefaceLight());
    	if(city.trips.size() == 1)
    		cityTrips.setText(R.string.trip_on_city);
    	else if(city.trips.size() == 0)
    		cityTrips.setText(R.string.trip_none);
    	else
    		cityTrips.setText(String.format(context.getResources().getString(R.string.trips_on_city), city.trips.size()));
    	
    	ImageView cityBackground = (ImageView) rowView.findViewById(R.id.city_background);
    	if(city.name.equalsIgnoreCase("Guadalajara"))
    		cityBackground.setImageResource(R.drawable.guadalajara);
    	else if(city.name.equalsIgnoreCase("Monterrey"))
    		cityBackground.setImageResource(R.drawable.monterrey);
    	
    	LinearLayout tripsListed = (LinearLayout) rowView.findViewById(R.id.trips_listed);

        for(final Trip trip : city.trips) {
        	View tripView = inflater.inflate(R.layout.trip_item, parent, false);
        	
        	final TextView tripName = (TextView) tripView.findViewById(R.id.trip_name);
    	    tripName.setText(trip.name);
    	    tripName.setTypeface(AppBase.getTypefaceStrong());
    	    
    	    TextView daysToEvent = (TextView) tripView.findViewById(R.id.trip_daysToEvent);
    	    daysToEvent.setText(trip.daysToEvent);
    	    daysToEvent.setTypeface(AppBase.getTypefaceLight());
        	
        	tripsListed.addView(tripView);
        	tripView.setOnTouchListener(new OnTouchListener() {

    			@Override
    			public boolean onTouch(View v, MotionEvent event) {
    				if(event.getAction() == MotionEvent.ACTION_UP) {
    					Bundle bundle = new Bundle();
        				bundle.putSerializable("trip", trip);
        				AppBase.launchActivityWithBundle(TripDetailsActivity.class, bundle);
    				}
    				return true;
    			}
        		
        	});
        }
    	
    	
	    return rowView;
	  }
	  
	} 