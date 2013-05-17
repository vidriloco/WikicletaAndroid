package org.wikicleta.adapters.trips;

import java.util.ArrayList;
import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.Trip;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TripsListAdapter extends ArrayAdapter<Trip> {
	  private final Context context;
	  private final ArrayList<Trip> trips;
	  private LayoutInflater inflater;
	  	  
	  public TripsListAdapter(Context context, ArrayList<Trip> trips) {
	    super(context, R.layout.layer_item, trips);
	    this.context = context;
	    this.trips = trips;
	    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  }

		
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = inflater.inflate(R.layout.trip_item, parent, false);
	    Trip trip = trips.get(position);
	    
	    TextView tripName = (TextView) rowView.findViewById(R.id.trip_name);
	    tripName.setText(trip.name);
	    tripName.setTypeface(AppBase.getTypefaceStrong());
	    
	    TextView daysToEvent = (TextView) rowView.findViewById(R.id.trip_daysToEvent);
	    daysToEvent.setText(trip.daysToEvent);
	    daysToEvent.setTypeface(AppBase.getTypefaceLight());
	    
	    return rowView;
	  }
	  
	} 