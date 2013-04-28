package org.wikicleta.layers;

import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.layers.components.EcobiciStation;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class BikeSharingOverlay extends ItemizedOverlay<OverlayItem> implements IdentifiableOverlay {
    private ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
	protected String url = "http://api.citybik.es/ecobici.json";
	
	protected OverlayReadyListener overlayListener;
	
    public BikeSharingOverlay(Drawable marker) {
        super(boundCenterBottom(marker));
        this.fetch();
    }
 
    public BikeSharingOverlay(Drawable marker, OverlayReadyListener listener) {
        this(marker);
        this.overlayListener = listener;
    }
    
    public void fetch() {
    	EcobiciFetching fetching = new EcobiciFetching();
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
        OverlayItem item = overlayItems.get(i);
        AlertDialog.Builder dialog = new AlertDialog.Builder(AppBase.currentActivity);
        dialog.setTitle(item.getTitle());
        dialog.setMessage(item.getSnippet());
        dialog.show();
        return true;
    }
    
    public void notifyOverlayIsReady() {
    	overlayListener.onOverlayPrepared(this, Constants.BIKE_SHARING_OVERLAY);
    }
    
    public class EcobiciFetching extends AsyncTask<Void, Void, Boolean> {
    	
    	ProgressDialog progressDialog;
    	
		@Override
		protected Boolean doInBackground(Void... params) {
			String parsedValue = NetworkOperations.getJSONExpectingString(url, true);
			JSONArray objectList = (JSONArray) JSONValue.parse(parsedValue);
			
			overlayItems.clear();
			@SuppressWarnings("unchecked")
			Iterator<JSONObject> iterator = (Iterator<JSONObject>) objectList.iterator();
			while(iterator.hasNext()) {
				JSONObject object = iterator.next();
				addOverlay(EcobiciStation.buildFrom(object));
			}
			
			return true;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ProgressDialog.show(AppBase.currentActivity, "", 
		            "Cargando ...", true);
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			notifyOverlayIsReady();
			progressDialog.hide();
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