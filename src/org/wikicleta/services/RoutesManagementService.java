package org.wikicleta.services;

import org.wikicleta.R;
import org.wikicleta.activities.ActivitiesFeedActivity;
import org.wikicleta.async.RouteUploader;
import org.wikicleta.common.Constants;
import org.wikicleta.helpers.NotificationBuilder;
import org.wikicleta.models.Route;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class RoutesManagementService extends Service {
	
	public static final int UPLOAD_ROUTE = 1;
	public static final int NOTIFY_ABOUT_STALLED = 2;
	
	protected RouteUploader routeUploader;
	protected NotificationBuilder notification;
	protected final IBinder localBinder = new RoutesManagementBinder();
	
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
		notification.clearNotification(Constants.ROUTES_SYNCING_NOTIFICATIONS_ID);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return Service.START_STICKY;
	}
	
	public void uploadRoute(Route route) {
		if(this.routeUploader != null)
			this.routeUploader.uploadRoute(route);
		notification.addNotification(Constants.ROUTES_SYNCING_NOTIFICATIONS_ID, 
				getString(R.string.app_name), getString(R.string.route_being_sent), null);
	}
	
	public void notifyAboutStalledRoutes() {
		if(queuedRoutesCount() > 0) {			
			String countString = ActivitiesFeedActivity.queuedRoutesCount() == 1 ? 
					this.getString(R.string.route_drafts_notification_total_one) : 
						this.getString(R.string.route_drafts_notification_total_many, ActivitiesFeedActivity.queuedRoutesCount()); 
			
			notification.addNotification(Constants.DRAFT_ROUTES_NOTIFICATION_ID, 
					getString(R.string.app_name), countString, ActivitiesFeedActivity.class);
		}		
	}
	
	protected int queuedRoutesCount() {
		return Route.queued().size();
	}
	
	// Local binder implementation
	public class RoutesManagementBinder extends Binder implements ServiceBinder {
		public RoutesManagementService getService() {
            return RoutesManagementService.this;
        }
    }
}
