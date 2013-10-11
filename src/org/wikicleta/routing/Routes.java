package org.wikicleta.routing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.R;
import org.wikicleta.activities.DiscoverActivity;
import org.wikicleta.activities.routes.NewRouteActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.common.Toasts;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.layers.common.LayersConnectorListener;
import org.wikicleta.models.Route;
import org.wikicleta.models.User;
import org.wikicleta.routing.Others.Cruds;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Button;

public class Routes {
	
	protected String postPath="/api/routes";
	protected String putPath="/api/routes/:id";

	protected String getPath="/api/routes?";
	
	public class Get extends AsyncTask<Void, Void, Boolean> {
    	
		public LayersConnectorListener connector;
		public ArrayList<Route> items;

	    JSONArray objectList;
	    HashMap<String, String> viewport;
	   
	    public Get(LayersConnectorListener connector) {
	    	this.connector = connector;
	    }
	   
	    @Override
	    protected void onPreExecute() {
			viewport = connector.getCurrentViewport();
			items = new ArrayList<Route>();
	    }
	    
		@Override
		protected Boolean doInBackground(Void... args) {
			
			String params = "viewport[sw]=".concat(viewport.get("sw")).concat("&viewport[ne]=").concat(viewport.get("ne"));
			String fetchedString = NetworkOperations.getJSONExpectingString(getPath.concat(params), false);
			if(fetchedString == null)
				return false;
			
			JSONObject object = (JSONObject) JSONValue.parse(fetchedString);
			if((Boolean) object.get("success")) {
				objectList = (JSONArray) object.get("routes");
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
					items.add(Route.buildFrom(json));
				}
			}
			connector.overlayFinishedLoadingWithPayload(success, items);
		}
		
		@Override
		protected void onCancelled() {
			connector.overlayFinishedLoading(false);
		}

	}
	
	
	public class PostOrPut extends AsyncTask<Route, Void, Boolean> {
		private Route route;
		public NewRouteActivity activity;
		public Cruds mode = Cruds.CREATE;
		AlertDialog dialog;

		@Override
		protected Boolean doInBackground(Route... args) {
			route = args[0];
			HashMap<String, Object> auth = new HashMap<String, Object>();
			auth.put("auth_token", User.token());
			int requestStatus = 404;
			if(mode == Cruds.CREATE)
				requestStatus = NetworkOperations.postJSONTo(postPath, route.toJSON(auth));
			else if(mode == Cruds.MODIFY)
				requestStatus = NetworkOperations.putJSONTo(putPath.replace(":id", String.valueOf(route.remoteId)), route.toJSON(auth));
			
			return requestStatus == 200;
		}
		
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = DialogBuilder.buildLoadingDialogWithMessage(activity, R.string.uploading).create();
			dialog.show();
		}
		
	    protected void onPostExecute(Boolean success) {
	    	dialog.dismiss();
	    	if(success) {
				if(route != null && route.getId() != null)
					route.delete();
	    		if(mode == Cruds.CREATE)
	    			Toasts.showToastWithMessage(activity, R.string.route_saved_successfully, R.drawable.success_icon);
				else if(mode == Cruds.MODIFY)
					Toasts.showToastWithMessage(activity, R.string.route_updated_successfully, R.drawable.success_icon);
	    		AppBase.launchActivity(DiscoverActivity.class);
	    	} else {
	    		int message = (mode == Cruds.CREATE) ? R.string.route_not_saved : R.string.route_not_updated;
	    		
	    		AlertDialog.Builder builder = DialogBuilder.buildAlertWithTitleAndMessage(activity, R.string.notification, message);
	    		
	    		// Only allowing to save drafts when creating a new route
	    		if(mode == Cruds.CREATE) {
	    			builder = builder.setNeutralButton(activity.getResources().getString(R.string.save_as_draft), new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog,int id) {
	    					route.save();
		    				AppBase.launchActivity(DiscoverActivity.class);
		    				Toasts.showToastWithMessage(activity, R.string.route_sent_to_drafts, R.drawable.archive_icon);
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
	    			}
	    		});
	    		final AlertDialog alert = builder.create();
	    		
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
