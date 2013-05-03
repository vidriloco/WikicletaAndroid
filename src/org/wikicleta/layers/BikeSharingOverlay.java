package org.wikicleta.layers;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.layers.components.CycleStationOverlayItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class BikeSharingOverlay extends ItemizedOverlay<OverlayItem> implements IdentifiableOverlay {
    private ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
	protected String url = "http://api.citybik.es/ecobici.json";
	
	protected LayersConnectorListener listener;
	
    public BikeSharingOverlay(Drawable marker) {
        super(boundCenterBottom(marker));
        this.populate();
    }
 
    public BikeSharingOverlay(LayersConnectorListener listener) {
        this(listener.getActivity().getResources().getDrawable(R.drawable.bike_sharing_icon));
        this.listener = listener;
        this.fetch();

    }
    
    public void fetch() {
    	EcobiciFetching fetching = new EcobiciFetching();
    	fetching.activity = listener.getActivity();
		fetching.execute((Void) null);
    }
 
    public void addOverlay(OverlayItem overlay) {
    	overlayItems.add(overlay);
        populate();
    }
 
    @Override
    protected OverlayItem createItem(int i) {
        return overlayItems.get(i);
    }
 
    @Override
    public int size() {
        return overlayItems.size();
    }
 
    @Override
    protected boolean onTap(int i) {
    	CycleStationOverlayItem item = (CycleStationOverlayItem) overlayItems.get(i);
    	
    	final Activity activity = listener.getActivity();
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.bike_sharing_station_details, null);
        
        TextView bikeSharingTitle = (TextView) view.findViewById(R.id.bike_sharing_title);
        bikeSharingTitle.setTypeface(AppBase.getTypefaceStrong());
        
        TextView bikeCountTextView = (TextView) view.findViewById(R.id.bikes_text_for_number);
        bikeCountTextView.setTypeface(AppBase.getTypefaceLight());
        
        TextView bikeCount = (TextView) view.findViewById(R.id.bikes_number);
        bikeCount.setTypeface(AppBase.getTypefaceStrong());

        TextView slotCountTextView = (TextView) view.findViewById(R.id.slots_text_for_number);
        slotCountTextView.setTypeface(AppBase.getTypefaceLight());

        TextView slotCount = (TextView) view.findViewById(R.id.slots_number);
        slotCount.setTypeface(AppBase.getTypefaceStrong());

        bikeCount.setText(String.valueOf(item.associatedStation.availableBikes));
        slotCount.setText(String.valueOf(item.associatedStation.availableSlots));

        ImageView icon = (ImageView) view.findViewById(R.id.bike_sharing_status);
        icon.setImageResource(item.associatedStation.status());
        
        if(item.associatedStation.availableBikes != 1)
        	bikeCountTextView.setText(activity.getResources().getString(R.string.many_available_bikes));
        if(item.associatedStation.availableSlots != 1)
        	slotCountTextView.setText(activity.getResources().getString(R.string.many_available_slots));
       
        dialog.setView(view);
        final AlertDialog visibleDialog = dialog.create();
        view.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				visibleDialog.dismiss();
			}
        	
        });
        
        visibleDialog.show();
        return true;
    }
    
    public void notifyOverlayIsReady() {
    	listener.onOverlayReady();
    }
    
    public class EcobiciFetching extends AsyncTask<Void, Void, Boolean> {
    	JSONArray objectList;
    	Activity activity;
    	
		@Override
		protected Boolean doInBackground(Void... params) {
			String parsedValue = NetworkOperations.getJSONExpectingString(url, true);
			if(parsedValue == null)
				return false;
			objectList = (JSONArray) JSONValue.parse(parsedValue);
			return true;
		}


		@Override
		protected void onPostExecute(final Boolean success) {
			if(success) {
				overlayItems.clear();
				@SuppressWarnings("unchecked")
				Iterator<JSONObject> iterator = (Iterator<JSONObject>) objectList.iterator();
				while(iterator.hasNext()) {
					JSONObject object = iterator.next();
					addOverlay(CycleStationOverlayItem.buildFrom(activity, object));
				}
			}
			notifyOverlayIsReady();
		}

		@Override
		protected void onCancelled() {
			
		}
	}

	@Override
	public int getIdentifier() {
		return Constants.BIKE_SHARING_OVERLAY;
	}
}