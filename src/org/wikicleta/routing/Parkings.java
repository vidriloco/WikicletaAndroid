package org.wikicleta.routing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.R;
import org.wikicleta.activities.DiscoverActivity;
import org.wikicleta.activities.parkings.ModifyingActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.common.Toasts;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.layers.common.LayersConnectorListener;
import org.wikicleta.models.LightPOI;
import org.wikicleta.models.Parking;
import org.wikicleta.models.User;
import org.wikicleta.routing.Others.Cruds;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.widget.Button;

public class Parkings {
	
	protected String postPath="/api/parkings";
	protected String putPath="/api/parkings/:id";
	protected String getPath="/api/parkings?";
	protected String deletePath="/api/parkings/:id";
		
	public class Delete extends AsyncTask<Parking, Void, Boolean> {
		
		Parking parking;
		public DiscoverActivity activity;
		Dialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = DialogBuilder.buildLoadingDialogWithMessage(activity, R.string.destroying);
			dialog.show();
		}
		
		@Override
		protected Boolean doInBackground(Parking... params) {
			parking = params[0];
			
			HashMap<String, Object> auth = new HashMap<String, Object>();
			auth.put("auth_token", User.token());
			HashMap<String, Object> extras = new HashMap<String, Object>();
			extras.put("extras", auth);
			int requestStatus = NetworkOperations.postJSONTo(deletePath.replace(":id", String.valueOf(parking.remoteId)), JSONObject.toJSONString(extras));
			return requestStatus == 200;
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			dialog.dismiss();
			if(success) {
				activity.reloadActiveLayersWithMapClearing();
				Toasts.showToastWithMessage(activity, R.string.parkings_deleted_successfully, R.drawable.success_icon);
			} else {
				Toasts.showToastWithMessage(activity, R.string.parkings_did_not_deleted, R.drawable.failure_icon);
			}
		}
		
	}
	
	public class Get extends AsyncTask<Void, Void, Boolean> {
	
		public LayersConnectorListener connector;
		public ArrayList<Parking> items;

	    JSONArray objectList;
	    HashMap<String, String> viewport;
	   
	    public Get(LayersConnectorListener connector) {
	    	this.connector = connector;
	    }
	   
	    @Override
	    protected void onPreExecute() {
			viewport = connector.getCurrentViewport();
			items = new ArrayList<Parking>();
	    }
	    
		@Override
		protected Boolean doInBackground(Void... args) {

			String params = "viewport[sw]=".concat(viewport.get("sw")).concat("&viewport[ne]=").concat(viewport.get("ne"));
			String fetchedString = NetworkOperations.getJSONExpectingStringGzipped(getPath.concat(params), false);
			if(fetchedString == null)
				return false;
			JSONObject object = (JSONObject) JSONValue.parse(fetchedString);
			if((Boolean) object.get("success")) {
				objectList = (JSONArray) object.get("parkings");
				return true;
			} else {
				return false;
			}
		}	
		
		@Override
		protected void onPostExecute(final Boolean success) {
			if(success) {
				items.clear();
				@SuppressWarnings("unchecked")
				Iterator<JSONObject> iterator = (Iterator<JSONObject>) objectList.iterator();
				while(iterator.hasNext()) {
					JSONObject json = iterator.next();
					try {
						items.add(Parking.buildFrom(json));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			connector.overlayFinishedLoadingWithPayload(success, items);
		}
		
		@Override
		protected void onCancelled() {
			connector.overlayFinishedLoading(false);
		}

	}
	
	public class PostOrPut extends AsyncTask<Parking, Void, Boolean> {
		private Parking parking;
		public ModifyingActivity activity;
		public Cruds mode = Cruds.CREATE;
		Dialog dialog;
		
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = DialogBuilder.buildLoadingDialogWithMessage(activity, R.string.uploading);
			dialog.show();
		}
		
		protected Boolean doInBackground(Parking... args) {
			parking = args[0];
			HashMap<String, Object> auth = new HashMap<String, Object>();
			auth.put("auth_token", User.token());
			
			int requestStatus = 404;
			if(mode == Cruds.CREATE)
				requestStatus = NetworkOperations.postJSONTo(postPath, parking.toJSON(auth));
			else if(mode == Cruds.MODIFY)
				requestStatus = NetworkOperations.putJSONTo(putPath.replace(":id", String.valueOf(parking.remoteId)), parking.toJSON(auth));

			return requestStatus == 200;
		}
		
	    protected void onPostExecute(Boolean success) {	    
	    	dialog.dismiss();
	    	if(success) {
	    		LightPOI lighPoi = new LightPOI(parking.getTitle(), parking.getDetails(), parking.latitude, parking.longitude, parking.getKind(), parking.getDate());
	    		if(parking != null && parking.getId() != null)
					parking.delete();
	    		if(mode == Cruds.CREATE)
	    			Toasts.showToastWithMessage(activity, R.string.parkings_uploaded_successfully, R.drawable.success_icon);
				else if(mode == Cruds.MODIFY)
					Toasts.showToastWithMessage(activity, R.string.parkings_changes_uploaded_successfully, R.drawable.success_icon);
	    		DiscoverActivity.selectedPoi = lighPoi;
	    		AppBase.launchActivity(DiscoverActivity.class);
	    		activity.finish();
	    	} else {
	    		activity.dialog.hide();
	    		int message = (mode == Cruds.CREATE) ? R.string.parkings_not_commited : R.string.parkings_changes_not_commited;
	    		
	    		AlertDialog.Builder builder = DialogBuilder.buildAlertWithTitleAndMessage(activity, R.string.notification, message);
	    		
	    		// Only allowing to save drafts when creating a new tip
	    		if(mode == Cruds.CREATE) {
	    			builder = builder.setNeutralButton(activity.getResources().getString(R.string.save_as_draft), new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog,int id) {
	    					parking.save();
		    				AppBase.launchActivity(DiscoverActivity.class);
		    				Toasts.showToastWithMessage(activity, R.string.parkings_sent_to_drafts, R.drawable.archive_icon);
		    	    		activity.finish();
	    				}
	    			});
	    		}
	    		
	    		builder.setNegativeButton(activity.getResources().getString(R.string.discard), new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog,int id) {
	    				AppBase.launchActivity(DiscoverActivity.class);
	    				activity.finish();
	    			}
	    		}).setPositiveButton(activity.getResources().getString(R.string.retry), new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog,int id) {
	    				dialog.dismiss();
	    				activity.dialog.show();
	    			}
	    		});
	    		final AlertDialog alert = builder.create();
	    		
	    		alert.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface arg0) {
						activity.dialog.show();
					}
	    			
	    		});
	    		
	    		alert.setOnShowListener(new DialogInterface.OnShowListener() {
	    		    @Override
	    		    public void onShow(DialogInterface dialog) {
	    		        Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);
	    		        btnPositive.setTextSize(13);
	    		        btnPositive.setTypeface(AppBase.getTypefaceStrong());
	    		        
	    		        Button btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE);
	    		        btnNegative.setTextSize(13);
	    		        btnNegative.setTypeface(AppBase.getTypefaceStrong());
	    		        
	    		        Button btnNeutral = alert.getButton(Dialog.BUTTON_NEUTRAL);
	    		        btnNeutral.setTextSize(13);
	    		        btnNeutral.setTypeface(AppBase.getTypefaceStrong());

	    		    }
	    		});
	    		
	    		alert.show();
	    	}
	    }

	     
	 }
}
