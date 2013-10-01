package org.wikicleta.adapters;

import java.util.ArrayList;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuOptionsListAdapter extends ArrayAdapter<Integer> {
	  private final Context context;
	  private final Integer[] values;
	  private LayoutInflater inflater;
	  
	  protected ArrayList<Integer> selectedPos = new ArrayList<Integer>();
	  
	  public MenuOptionsListAdapter(Context context, Integer[] values) {
	    super(context, R.layout.layers_menu_list_item, values);
	    this.context = context;
	    this.values = values;
	    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	  }

	  public void setSelectedPosition(Integer pos){
		  if(selectedPos.contains(pos))
			  selectedPos.remove(pos);
		  else
			  selectedPos.add(pos);
		  // inform the view of this change
		  notifyDataSetChanged();
	  }
	  
	  public ArrayList<Integer> getSelectedValuesForPositions() {
		  ArrayList<Integer> selectedOnes = new ArrayList<Integer>();
		  for(Integer pos : selectedPos) {
			  selectedOnes.add(values[pos]);
		  }
		  return selectedOnes;
	  }
		
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = inflater.inflate(R.layout.layers_menu_list_item, parent, false);
	    TextView textView = (TextView) rowView.findViewById(R.id.layer_title);
	    ImageView imageView = (ImageView) rowView.findViewById(R.id.layer_icon);
    	textView.setTypeface(AppBase.getTypefaceStrong());

	    if(values[position]==Constants.BIKE_SHARING_OVERLAY) {
	    	textView.setText(context.getResources().getString(R.string.toggle_menu_bike_sharing));
	    	imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.bike_sharings_layers_icon));
	    } else if(values[position]==Constants.BIKE_WORKSHOPS_AND_STORES_OVERLAY) {
	    	textView.setText(context.getResources().getString(R.string.toggle_menu_bike_workshops_and_stores));
	    	imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.workshops_layers_icon));
	    } else if(values[position]==Constants.BIKE_PARKING_OVERLAY) {
	    	textView.setText(context.getResources().getString(R.string.toggle_menu_bike_parking));
	    	imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.parkings_layers_icon));
	    } else if(values[position]==Constants.TIPS_OVERLAY) {
	    	textView.setText(context.getResources().getString(R.string.toggle_menu_tips));
	    	imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.tips_layers_icon));
	    } else if(values[position]==Constants.ROUTES_OVERLAY) {
	    	textView.setText(context.getResources().getString(R.string.toggle_menu_routes));
	    	imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.routes_layers_icon));
	    } else if(values[position] == Constants.CYCLEPATHS_OVERLAY) {
	    	textView.setText(context.getResources().getString(R.string.toggle_menu_cyclepaths));
	    	imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.bicycle_lanes_layers_icon));
	    }
	    
	    if(selectedPos.contains(Integer.valueOf(position)))
	    	rowView.setBackgroundResource(R.drawable.background_selected);
	    else
	    	rowView.setBackgroundResource(R.drawable.background_deselected);

	    return rowView;
	  }
	  
	} 