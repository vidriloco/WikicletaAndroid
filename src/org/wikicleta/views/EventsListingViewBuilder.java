package org.wikicleta.views;

import org.wikicleta.R;
import org.wikicleta.activities.EventsActivity;
import org.wikicleta.adapters.EventsListAdapter;
import org.wikicleta.common.AppBase;
import org.wikicleta.interfaces.EventInterface;
import org.wikicleta.interfaces.MarkerInterface;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class EventsListingViewBuilder {

	public static void buildEmptyView(final EventsActivity activity) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_events_list_empty, null);
        
        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_menu_title);
        dialogTitle.setTypeface(AppBase.getTypefaceStrong());
        dialog.setView(view);
        
        TextView emptyMessageText = (TextView) view.findViewById(R.id.empty_message_text);
        emptyMessageText.setTypeface(AppBase.getTypefaceStrong());
        
        final AlertDialog visibleDialog = dialog.create();
        view.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				visibleDialog.dismiss();
			}
        	
        });
        
        visibleDialog.show();
	}
	
	public static void buildView(final EventsActivity activity) {
		EventInterface [] events = activity.visibleMarkers();
		
		if(events.length == 0) {
			buildEmptyView(activity);
		} else {
			AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
	        LayoutInflater inflater = activity.getLayoutInflater();
	        final View view = inflater.inflate(R.layout.dialog_events_list, null);
	        
	        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_menu_title);
	        dialogTitle.setTypeface(AppBase.getTypefaceStrong());
	        
	        final ListView listview = (ListView) view.findViewById(R.id.list_events);
	        final EventsListAdapter listAdapter = new EventsListAdapter(activity, activity.visibleMarkers());
		    listview.setAdapter(listAdapter);
		    listview.getCheckedItemPositions();
		    listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	        
	        dialog.setView(view);
	        final AlertDialog visibleDialog = dialog.create();
	        
	        view.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					visibleDialog.dismiss();
				}
	        	
	        });
	        
		    listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapterParent, View view, int position, long id) {
					visibleDialog.dismiss();
					activity.centerOnEvent((MarkerInterface) listAdapter.getItem(position));
				}

		    });
	        
	        visibleDialog.show();
		}
		
        
	}

}
