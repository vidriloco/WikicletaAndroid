package org.wikicleta.views;

import org.wikicleta.R;
import org.wikicleta.activities.EventsActivity;
import org.wikicleta.adapters.EventsListAdapter;
import org.wikicleta.common.AppBase;
import org.wikicleta.interfaces.EventInterface;
import org.wikicleta.interfaces.MarkerInterface;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class EventsListingViewBuilder {

	public static void buildEmptyView(final EventsActivity activity) {
		final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_events_list_empty, null);
        
        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_menu_title);
        dialogTitle.setTypeface(AppBase.getTypefaceStrong());
        dialog.setContentView(view);
        
        TextView emptyMessageText = (TextView) view.findViewById(R.id.empty_message_text);
        emptyMessageText.setTypeface(AppBase.getTypefaceStrong());
        
        view.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
        	
        });
        
        dialog.show();
	}
	
	public static void buildView(final EventsActivity activity) {
		EventInterface [] events = activity.visibleMarkers();
		
		if(events.length == 0) {
			buildEmptyView(activity);
		} else {
			final Dialog dialog = new Dialog(activity);
	        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

	        LayoutInflater inflater = activity.getLayoutInflater();
	        View view = inflater.inflate(R.layout.dialog_events_list, null);
	        
	        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_menu_title);
	        dialogTitle.setTypeface(AppBase.getTypefaceStrong());
	        
	        final ListView listview = (ListView) view.findViewById(R.id.list_events);
	        final EventsListAdapter listAdapter = new EventsListAdapter(activity, activity.visibleMarkers());
		    listview.setAdapter(listAdapter);
		    listview.getCheckedItemPositions();
		    listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	        
	        dialog.setContentView(view);
	        
		    if(events.length >= 4)
		    	listview.getLayoutParams().height = 120*4;
	        
		    
	        view.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
	        	
	        });
	        
		    listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapterParent, View view, int position, long id) {
					dialog.dismiss();
					activity.centerOnEvent((MarkerInterface) listAdapter.getItem(position));
				}

		    });
	        
		    dialog.show();
		}
		
        
	}

}
