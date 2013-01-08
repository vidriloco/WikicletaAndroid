package org.wikicleta.services;

import java.util.LinkedList;
import org.wikicleta.R;
import org.wikicleta.activities.UserProfileActivity;
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
import android.util.Log;

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
		reloadRouteUploadManager();
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
	
	public void reloadRouteUploadManager() {
		this.routeUploader = new RouteUploader();
	}
	
	public void addRouteForUpload(Route route) {
		notification.addNotification(Constants.ROUTES_MANAGEMENT_NOTIFICATION_ID, 
				getString(R.string.app_name), getString(R.string.route_being_sent), null);		
		this.routeUploader.addRoute(route);
	}
	
	public void uploadStagedRoutes() {
		this.routeUploaderTask = new UploaderTask();
		routeUploaderTask.execute();
	}
	
	public void notifyAboutStalledRoutes() {
		Log.i("WIKICLETA", "Rutas "+ String.valueOf(queuedRoutesCount()));
		if(queuedRoutesCount() > 0) {		
			
			String countString = this.getString(R.string.route_drafts_notification_total_one);
			if(queuedRoutesCount() != 1)
				countString = this.getString(R.string.route_drafts_notification_total_many, queuedRoutesCount());			 
			
			notification.addNotification(Constants.ROUTES_MANAGEMENT_NOTIFICATION_ID, 
					getString(R.string.app_name), countString, UserProfileActivity.class);
		}		
	}
	
	public int queuedRoutesCount() {
		return routesQueued().size();
	}
	
	public LinkedList<Route> routesQueued() {
		return this.routeUploader.routesWaitingToUpload();
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
			
			// Check if boundActivity is of the right type
			RoutesServiceListener listener = (RoutesServiceListener) boundActivity;
			if(boundActivity instanceof RoutesServiceListener)
				listener.shouldBlockView();
						
			while(routeUploader.peekNext() != null) {
				routeUploader.uploadNext();
				Log.e("WIKICLETA", "Error al subir");
			}
			
			reloadRouteUploadManager();
			if(boundActivity instanceof RoutesServiceListener)
				listener.shouldUnblockView();
			Log.e("WIKICLETA", "Stopped");
			return null;
		}
    }
}
