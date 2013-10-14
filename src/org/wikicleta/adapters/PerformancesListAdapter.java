package org.wikicleta.adapters;

import java.text.DecimalFormat;
import java.util.ArrayList;
import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.helpers.Formatters;
import org.wikicleta.models.RoutePerformance;
import org.wikicleta.routing.Others;
import org.wikicleta.routing.Others.ImageUpdater;
import com.ocpsoft.pretty.time.PrettyTime;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PerformancesListAdapter extends ArrayAdapter<RoutePerformance> {
	  protected final Context context;
	  protected final ArrayList<RoutePerformance> values;
	  protected LayoutInflater inflater;
	  	  
	  public PerformancesListAdapter(Context context, ArrayList<RoutePerformance> values) {
	    super(context, R.layout.layers_menu_list_item, values);
	    this.context = context;
	    this.values = values;
	    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  }
		
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
		RoutePerformance selectedPerformance = values.get(position);

		View rowView = inflater.inflate(R.layout.performance_on_list, parent, false);
	    TextView usernameTextView = (TextView) rowView.findViewById(R.id.username_text);
	    usernameTextView.setTypeface(AppBase.getTypefaceStrong());
	    usernameTextView.setText(selectedPerformance.username);
	    
		DecimalFormat format=new DecimalFormat("#.##");
		
	    TextView averageSpeedTextView = (TextView) rowView.findViewById(R.id.average_speed_text);
	    averageSpeedTextView.setTypeface(AppBase.getTypefaceLight());
	    averageSpeedTextView.setText(String.valueOf(format.format(selectedPerformance.averageSpeed)));
	    	    
	    ((TextView) rowView.findViewById(R.id.metric_unit_speed_text)).setTypeface(AppBase.getTypefaceStrong());
	    
	    TextView averageTimingTextView = (TextView) rowView.findViewById(R.id.average_timing_text);
	    averageTimingTextView.setTypeface(AppBase.getTypefaceLight());
	    averageTimingTextView.setText(Formatters.millisecondsToTime(selectedPerformance.elapsedTime));
	    ((TextView) rowView.findViewById(R.id.metric_unit_timing_text)).setTypeface(AppBase.getTypefaceStrong());

	    PrettyTime ptime = new PrettyTime();
	    ((TextView) rowView.findViewById(R.id.date_text)).setTypeface(AppBase.getTypefaceLight());
	    ((TextView) rowView.findViewById(R.id.date_text)).setText(ptime.format(selectedPerformance.createdAt));
        
	    if(selectedPerformance.hasPic()) {
	    	ImageView imageView = (ImageView) rowView.findViewById(R.id.user_picture);
		    ImageUpdater updater = Others.getImageFetcher();
	        updater.setImageAndImageProcessor(imageView, Others.ImageProcessor.ROUND_FOR_MINI_USER_PROFILE);
	        updater.execute(selectedPerformance.picURL);
	    }
	    
        
	    return rowView;
	  }
	  
} 
