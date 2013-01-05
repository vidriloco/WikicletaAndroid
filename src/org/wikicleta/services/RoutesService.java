package org.wikicleta.services;

import org.wikicleta.R;
import org.wikicleta.activities.ActivitiesFeedActivity;
import org.wikicleta.async.RouteUploader;
import org.wikicleta.common.Constants;
import org.wikicleta.helpers.NotificationBuilder;
import org.wikicleta.models.Route;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

public class RoutesService extends Service {
	
	public static final int UPLOAD_ROUTE = 1;
	public static final int NOTIFY_ABOUT_STALLED = 2;
	
	protected Activity boundActivity;
	protected RouteUploader routeUploader;
	protected NotificationBuilder notification;
	protected final IBinder localBinder = new RoutesServiceBinder();
	
    private UploaderTask routeUploaderTask;
    
	@Override
	public IBinder onBind(Intent intent) {
		return localBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.routeUploader = new RouteUploader();
        this.notification = new NotificationBuilder(this);
	}
	 
	@Override
	public void onDestroy() {
		super.onDestroy();
		notification.clearNotification(Constants.ROUTES_MANAGEMENT_NOTIFICATION_ID);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return Service.START_STICKY;
	}
	
	public void addRouteForUpload(Route route) {
		notification.addNotification(Constants.ROUTES_MANAGEMENT_NOTIFICATION_ID, 
				getString(R.string.app_name), getString(R.string.route_being_sent), null);		
		this.routeUploader.addRoute(route);
	}
	
	public void executeStagedRoutesForUpload() {
		this.routeUploaderTask = new UploaderTask();
		routeUploaderTask.execute();
	}
	
	public void notifyAboutStalledRoutes() {
		if(queuedRoutesCount() > 0) {			
			String countString = ActivitiesFeedActivity.queuedRoutesCount() == 1 ? 
					this.getString(R.string.route_drafts_notification_total_one) : 
						this.getString(R.string.route_drafts_notification_total_many, ActivitiesFeedActivity.queuedRoutesCount()); 
			
			notification.addNotification(Constants.ROUTES_MANAGEMENT_NOTIFICATION_ID, 
					getString(R.string.app_name), countString, ActivitiesFeedActivity.class);
		}		
	}
	
	protected int queuedRoutesCount() {
		return Route.queued().size();
	}
	
	// Local binder implementation
	public class RoutesServiceBinder extends Binder implements ServiceBinder {
		public RoutesService getService() {
            return RoutesService.this;
        }

		@Override
		public void setBindingActivity(Activity activity) {
			boundActivity = activity;
		}
    }
	
	private class UploaderTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Route route = routeUploader.peekNext();
			if(route == null)
				return null;
			
			// Check if boundActivity is of the right type
			RoutesServiceListener listener = null;
			if(boundActivity instanceof RoutesServiceListener) {
				listener = (RoutesServiceListener) boundActivity;
				listener.shouldBlockElement(route);
			}
			
			// Attempt upload previously stored route
			if(routeUploader.uploadNext()) {
				notification.addNotification(Constants.ROUTES_MANAGEMENT_NOTIFICATION_ID, 
						getString(R.string.app_name), getString(R.string.route_did_upload), null);
				if(listener != null)
					listener.routeDidUpload(route);
			} else {
				notification.addNotification(Constants.ROUTES_MANAGEMENT_NOTIFICATION_ID, 
						getString(R.string.app_name), getString(R.string.route_didnt_upload), null);
				if(listener != null) {
					listener.routeDidNotUpload(route);
				}
			}
			return null;
		}
    }
}
