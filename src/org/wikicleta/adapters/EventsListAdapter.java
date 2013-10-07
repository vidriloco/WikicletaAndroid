package org.wikicleta.adapters;

import org.interfaces.EventInterface;
import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.CyclingGroup;
import org.wikicleta.models.Trip;
import org.wikicleta.routing.Others;
import org.wikicleta.routing.Others.ImageUpdater;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventsListAdapter extends ArrayAdapter<EventInterface> {
	  protected final Context context;
	  protected final EventInterface[] values;
	  protected LayoutInflater inflater;
	  	  
	  public EventsListAdapter(Context context, EventInterface[] values) {
	    super(context, R.layout.layers_menu_list_item, values);
	    this.context = context;
	    this.values = values;
	    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  }
		
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = inflater.inflate(R.layout.item_event_on_list, parent, false);
	    TextView textnameView = (TextView) rowView.findViewById(R.id.event_name_text);
	    textnameView.setTypeface(AppBase.getTypefaceStrong());
	    TextView textdaysToEventView = (TextView) rowView.findViewById(R.id.event_days_to_text);
	    textdaysToEventView.setTypeface(AppBase.getTypefaceLight());

	    TextView eventKindTextView = (TextView) rowView.findViewById(R.id.event_kind_text);
	    eventKindTextView.setTypeface(AppBase.getTypefaceLight());
	    
	    String pic = null;
	    EventInterface selectedMarkerObj = values[position];
	    if(selectedMarkerObj instanceof CyclingGroup) {
	    	CyclingGroup selected = (CyclingGroup) selectedMarkerObj;
	    	textnameView.setText(selected.name);
	    	textdaysToEventView.setText(String.format(context.getResources().getString(selected.daysToRide()), selected.daysToEventFromNow));
	    	if(selected.hasPic()) {
	    		pic = selected.pic;
	    	}
	    	eventKindTextView.setText(context.getResources().getString(R.string.event_ride));
	    } else if(selectedMarkerObj instanceof Trip) {
	    	Trip selected = (Trip) selectedMarkerObj;
	    	textnameView.setText(selected.name);
	    	textdaysToEventView.setText(String.format(context.getResources().getString(selected.daysToRide()), selected.daysToEventFromNow));
	    	if(selected.hasPic()) {
	    		pic = selected.pic;
	    	}
	    	eventKindTextView.setText(context.getResources().getString(R.string.event_trip));
	    }
	                
	    ImageView imageView = (ImageView) rowView.findViewById(R.id.event_picture);
	    ImageUpdater updater = Others.getImageFetcher();
        updater.setImageAndImageProcessor(imageView, Others.ImageProcessor.SCALE_FOR_LIST);
        updater.execute(pic);
        
	    return rowView;
	  }
	  
} 