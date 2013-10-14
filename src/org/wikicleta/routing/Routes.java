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
import org.wikicleta.activities.routes.RouteDetailsActivity;
import org.wikicleta.activities.routes.RoutesConnectorInterface;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.common.Toasts;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.layers.common.LayersConnectorListener;
import org.wikicleta.models.Route;
import org.wikicleta.models.RoutePerformance;
import org.wikicleta.models.User;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Button;

public class Routes {
	
	protected String postPath="/api/routes";
	protected String putPath="/api/routes/:id";
	protected String showPath="/api/routes/:id";
	protected String getPath="/api/routes?";
	protected String deletePath="/api/routes/:id";
	
	protected String performancesPath="/api/routes/:id/performances";
	
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
	
	public class Show extends AsyncTask<Route, Void, Boolean> {
    	
		public RoutesConnectorInterface connector;
		Route route;

	    JSONObject routeExtras;
	   
	    public Show(RoutesConnectorInterface connector) {
	    	this.connector = connector;
	    }
	    
		@Override
		protected Boolean doInBackground(Route... args) {
			route = args[0];
			String fetchedString = NetworkOperations.getJSONExpectingString(showPath.replace(":id", String.valueOf(route.remoteId)), false);

			if(fetchedString == null)
				return false;

			JSONObject object = (JSONObject) JSONValue.parse(fetchedString);
			if((Boolean) object.get("success")) {
				routeExtras = (JSONObject) object.get("route");
				return true;
			} else {
				return false;
			}
		}	
		
		@Override
		protected void onPostExecute(final Boolean success) {
			JSONArray jsonPath = (JSONArray) routeExtras.get("path");

			double [][] path = new double[jsonPath.size()][2];

			if(success) {
				@SuppressWarnings("unchecked")
				Iterator<JSONArray> pathIterator = (Iterator<JSONArray>) jsonPath.iterator();
				int i = 0;
				while(pathIterator.hasNext()) {
					JSONArray json = pathIterator.next();
					path[i][0] = (Double) json.get(0);
					path[i][1] = (Double) json.get(1);
					i++;
				}
				
				route.path = path;

			}
			
			connector.routeDetailsFinishedLoading(success);
		}
		
		@Override
		protected void onCancelled() {
			connector.routePerformancesDidNotLoad(false);
		}
	}
	
	public class Performances extends AsyncTask<Route, Void, Boolean> {
    	
		public RoutesConnectorInterface connector;
		Route route;

	    JSONObject routeExtras;
	   
	    public Performances(RoutesConnectorInterface connector) {
	    	this.connector = connector;
	    }
	    
		@Override
		protected Boolean doInBackground(Route... args) {
			route = args[0];
			String fetchedString = NetworkOperations.getJSONExpectingString(performancesPath.replace(":id", String.valueOf(route.remoteId)), false);

			if(fetchedString == null)
				return false;

			JSONObject object = (JSONObject) JSONValue.parse(fetchedString);
			if((Boolean) object.get("success")) {
				routeExtras = (JSONObject) object.get("route");
				return true;
			} else {
				return false;
			}
		}	
		
		@Override
		protected void onPostExecute(final Boolean success) {
			JSONArray jsonPerformances = (JSONArray) routeExtras.get("performances");

			if(success) {
				ArrayList<RoutePerformance> routePerformances = new ArrayList<RoutePerformance>();
				@SuppressWarnings("unchecked")
				Iterator<JSONObject> performancesIterator = (Iterator<JSONObject>) jsonPerformances.iterator();
				while(performancesIterator.hasNext()) {
					JSONObject object = performancesIterator.next();
					routePerformances.add(RoutePerformance.buildFrom(object));
				}
				route.persistedRoutePerformances = routePerformances;
			}
			
			connector.routePerformancesFinishedLoading(success);
		}
		
		@Override
		protected void onCancelled() {
			connector.routeDetailsDidNotLoad(false);
		}
	}
	
	public class Delete extends AsyncTask<Route, Void, Boolean> {
		
		Route route;
		AlertDialog dialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = DialogBuilder.buildLoadingDialogWithMessage(AppBase.currentActivity, R.string.destroying).create();
			dialog.show();
		}
		
		@Override
		protected Boolean doInBackground(Route... params) {
			route = params[0];
			
			HashMap<String, Object> auth = new HashMap<String, Object>();
			auth.put("auth_token", User.token());
			HashMap<String, Object> extras = new HashMap<String, Object>();
			extras.put("extras", auth);
			int requestStatus = NetworkOperations.postJSONTo(deletePath.replace(":id", String.valueOf(route.remoteId)), JSONObject.toJSONString(extras));
			return requestStatus == 200;
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
	    	dialog.dismiss();
			if(success) {
				Toasts.showToastWithMessage(AppBase.currentActivity, R.string.route_deleted_successfully, R.drawable.success_icon);
				AppBase.launchActivity(DiscoverActivity.class);
			} else {
				Toasts.showToastWithMessage(AppBase.currentActivity, R.string.route_not_deleted, R.drawable.failure_icon);
			}
		}
		
	}
	
	public class Put extends AsyncTask<Route, Void, Boolean> {
		private Route route;
		public RouteDetailsActivity activity;
		AlertDialog dialog;

		@Override
		protected Boolean doInBackground(Route... args) {
			route = args[0];
			HashMap<String, Object> auth = new HashMap<String, Object>();
			auth.put("auth_token", User.token());			
			return NetworkOperations.putJSONTo(putPath.replace(":id", String.valueOf(route.remoteId)), route.toJSONForPut(auth)) == 200;
		}
		
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = DialogBuilder.buildLoadingDialogWithMessage(activity, R.string.uploading).create();
			dialog.show();
		}
		
	    protected void onPostExecute(Boolean success) {
	    	dialog.dismiss();
	    	if(success) {
    			Toasts.showToastWithMessage(activity, R.string.route_updated_successfully, R.drawable.success_icon);
	    		dialog.dismiss();
	    	} else {	    		
	    		AlertDialog.Builder builder = DialogBuilder.buildAlertWithTitleAndMessage(activity, R.string.notification, R.string.route_not_updated);
	    		builder.setNeutralButton(activity.getResources().getString(R.string.neutral), new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog,int id) {
    					
    				}
    			});
	    		
	    		final AlertDialog subDialog = builder.create();
	    		
	    		subDialog.setOnShowListener(new DialogInterface.OnShowListener() {
	    		    @Override
	    		    public void onShow(DialogInterface dialog) {
	    		        
	    		        Button btnNeutral = subDialog.getButton(Dialog.BUTTON_NEUTRAL);
	    		        btnNeutral.setTextSize(13);
	    		        btnNeutral.setTypeface(AppBase.getTypefaceStrong());

	    		    }
	    		});
	    		
	    		subDialog.show();
	    	}
	    }
	}
	
	
	/*
	 * else if(mode == Cruds.MODIFY)
	 * requestStatus = NetworkOperations.putJSONTo(putPath.replace(":id", String.valueOf(route.remoteId)), route.toJSON(auth));
	 * Toasts.showToastWithMessage(activity, R.string.route_updated_successfully, R.drawable.success_icon);
	 * 
	 */
	
	public class Post extends AsyncTask<Route, Void, Boolean> {
		private Route route;
		public NewRouteActivity activity;
		AlertDialog dialog;

		@Override
		protected Boolean doInBackground(Route... args) {
			route = args[0];
			HashMap<String, Object> auth = new HashMap<String, Object>();
			auth.put("auth_token", User.token());			
			return NetworkOperations.postJSONTo(postPath, route.toJSON(auth)) == 200;
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
    			Toasts.showToastWithMessage(activity, R.string.route_saved_successfully, R.drawable.success_icon);
	    		activity.resetAll();
	    		dialog.dismiss();
	    		activity.finish();
	    		AppBase.launchActivity(DiscoverActivity.class);
	    	} else {	    		
	    		AlertDialog.Builder builder = DialogBuilder.buildAlertWithTitleAndMessage(activity, R.string.notification, R.string.route_not_saved);
	    		
    			builder = builder.setNeutralButton(activity.getResources().getString(R.string.save_as_draft), new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog,int id) {
    					route.save();
	    				AppBase.launchActivity(DiscoverActivity.class);
	    				Toasts.showToastWithMessage(activity, R.string.route_sent_to_drafts, R.drawable.archive_icon);
	    	    		activity.finish();
    				}
    			});
	    		
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
