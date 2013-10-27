package org.wikicleta.routing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.R;
import org.wikicleta.activities.DiscoverActivity;
import org.wikicleta.activities.workshops.ModifyingActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.common.Toasts;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.layers.common.LayersConnectorListener;
import org.wikicleta.models.User;
import org.wikicleta.models.Workshop;
import org.wikicleta.routing.Others.Cruds;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.AsyncTask;
import android.widget.Button;

public class Workshops {
	
	protected String getPath="/api/workshops?";
	protected String postPath="/api/workshops";
	protected String putPath="/api/workshops/:id";
	protected String deletePath="/api/workshops/:id";

	public class Delete extends AsyncTask<Workshop, Void, Boolean> {
		
		Workshop workshop;
		public DiscoverActivity activity;
		Dialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = DialogBuilder.buildLoadingDialogWithMessage(activity, R.string.destroying);
			dialog.show();
		}
		
		@Override
		protected Boolean doInBackground(Workshop... params) {
			workshop = params[0];
			
			HashMap<String, Object> auth = new HashMap<String, Object>();
			auth.put("auth_token", User.token());
			HashMap<String, Object> extras = new HashMap<String, Object>();
			extras.put("extras", auth);
			int requestStatus = NetworkOperations.postJSONTo(deletePath.replace(":id", String.valueOf(workshop.remoteId)), JSONObject.toJSONString(extras));
			return requestStatus == 200;
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			dialog.dismiss();
			if(success) {
				activity.reloadActiveLayersWithMapClearing();
				Toasts.showToastWithMessage(activity, R.string.workshops_deleted_successfully, R.drawable.success_icon);
			} else {
				Toasts.showToastWithMessage(activity, R.string.workshops_did_not_deleted, R.drawable.failure_icon);
			}
		}
		
	}

	public class Get extends AsyncTask<Void, Void, Boolean> {
	
		public LayersConnectorListener connector;
		public ArrayList<Workshop> items;

	    JSONArray objectList;
	    HashMap<String, String> viewport;

	    public Get(LayersConnectorListener connector) {
	    	this.connector = connector;
	    }
	   
	    @Override
	    protected void onPreExecute() {
			viewport = connector.getCurrentViewport();
			items = new ArrayList<Workshop>();
	    }
	    
		@Override
		protected Boolean doInBackground(Void... args) {

			String params = "viewport[sw]=".concat(viewport.get("sw")).concat("&viewport[ne]=").concat(viewport.get("ne"));
			String fetchedString = NetworkOperations.getJSONExpectingString(getPath.concat(params), false);
			if(fetchedString == null)
				return false;
			JSONObject object = (JSONObject) JSONValue.parse(fetchedString);
			if((Boolean) object.get("success")) {
				objectList = (JSONArray) object.get("workshops");
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
					items.add(Workshop.buildFrom(json));
				}
			}
			connector.overlayFinishedLoadingWithPayload(success, items);
		}
		
		@Override
		protected void onCancelled() {
			connector.overlayFinishedLoading(false);
		}

	}
	
	public class PostOrPut extends AsyncTask<Workshop, Void, Boolean> {
		private Workshop workshop;
		public ModifyingActivity activity;
		public Cruds mode = Cruds.CREATE;
		Dialog dialog;

		protected void onPreExecute() {
			super.onPreExecute();
			dialog = DialogBuilder.buildLoadingDialogWithMessage(activity, R.string.uploading);
			dialog.show();
		}
		
		@Override
		protected Boolean doInBackground(Workshop... args) {
			workshop = args[0];
			HashMap<String, Object> auth = new HashMap<String, Object>();
			auth.put("auth_token", User.token());
			int requestStatus = 404;
			if(mode == Cruds.CREATE)
				requestStatus = NetworkOperations.postJSONTo(postPath, workshop.toJSON(auth));
			else if(mode == Cruds.MODIFY)
				requestStatus = NetworkOperations.putJSONTo(putPath.replace(":id", String.valueOf(workshop.remoteId)), workshop.toJSON(auth));

			return requestStatus == 200;
		}
		
	    protected void onPostExecute(Boolean success) {
	    	dialog.dismiss();
	    	
	    	if(success) {
	    		if(workshop != null && workshop.getId() != null)
					workshop.delete();
	    		if(mode == Cruds.CREATE)
	    			Toasts.showToastWithMessage(activity, R.string.workshops_uploaded_successfully, R.drawable.success_icon);
				else if(mode == Cruds.MODIFY)
					Toasts.showToastWithMessage(activity, R.string.workshops_changes_uploaded_successfully, R.drawable.success_icon);
	    		AppBase.launchActivity(DiscoverActivity.class);
	    	} else {
	    		activity.dialog.hide();
	    		int message = (mode == Cruds.CREATE) ? R.string.workshops_not_commited : R.string.workshops_changes_not_commited;
	    		
	    		AlertDialog.Builder builder = DialogBuilder.buildAlertWithTitleAndMessage(activity, R.string.notification, message);
	    		
	    		// Only allowing to save drafts when creating a new tip
	    		if(mode == Cruds.CREATE) {
	    			builder = builder.setNeutralButton(activity.getResources().getString(R.string.save_as_draft), new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog,int id) {
	    					workshop.save();
		    				AppBase.launchActivity(DiscoverActivity.class);
		    				Toasts.showToastWithMessage(activity, R.string.workshops_sent_to_drafts, R.drawable.archive_icon);
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
