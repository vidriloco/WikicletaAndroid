package org.wikicleta.services.routes;

import java.util.LinkedList;
import org.wikicleta.R;
import org.wikicleta.activities.UserProfileActivity;
import org.wikicleta.common.Constants;
import org.wikicleta.fragments.user_profile.DraftsFragment;
import org.wikicleta.helpers.NotificationBuilder;
import org.wikicleta.helpers.NotificationBuilder.Ticker;
import org.wikicleta.helpers.routes.RouteRecorder;
import org.wikicleta.helpers.routes.RouteUploader;
import org.wikicleta.models.Route;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class RoutesService extends LocationAwareService {
	
	public static final int UPLOAD_ROUTE = 1;
	public static final int NOTIFY_ABOUT_STALLED = 2;
	
	// Route uploading methods
	protected RouteUploader routeUploader;
	protected NotificationBuilder notification;
	protected final IBinder localBinder = new RoutesServiceBinder();
    private UploaderTask routeUploaderTask;

    // Route recording methods
	public RouteRecorder routeRecorder;
    
	@Override
	public IBinder onBind(Intent intent) {
		return localBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		reload();
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
	
	public void reload() {
		this.routeUploader = new RouteUploader();
		this.routeRecorder = new RouteRecorder(this);
	}
	
	/*
	 * Methods for routes recording
	 */
	
	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
		this.routeRecorder.addLocation(location);
		this.notifyFieldsUpdated();
		Log.e("WIKICLETA", "Time from loc: " + String.valueOf(this.routeRecorder.seconds));
		if(this.boundActivity instanceof NavigationListener)
			((NavigationListener) this.boundActivity).locationUpdated();
	}
	
	public void updateTimingDisplay() {
		this.notifyFieldsUpdated();
	}
	
	protected void notifyFieldsUpdated() {
		if(this.boundActivity instanceof NavigationListener) {
			((NavigationListener) this.boundActivity).onFieldsUpdated();
		}
	}
	
	public void addRecordedRouteToUploader(String name, String tags) {
		this.addRouteForUpload(routeRecorder.buildRoute(name, tags));
	}
	
	public void pauseRecording() {
		disableLocationManager();
		routeRecorder.pause();
	}
	
	public void resumeRecording() {
		enableLocationManager();
		routeRecorder.resume();
	}
	
	/*
	 * Methods for routes uploading
	 */
	
	public void addRouteForUpload(Route route) {
		notification.addNotification(Constants.ROUTES_MANAGEMENT_NOTIFICATION_ID, 
				getString(R.string.app_name), getString(R.string.route_being_sent), null, Ticker.MESSAGE);		
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
			
			Intent notificationIntent = new Intent(this, UserProfileActivity.class);  
			notificationIntent.putExtra("fragment", DraftsFragment.class.getName());
			notification.addNotification(Constants.ROUTES_MANAGEMENT_NOTIFICATION_ID, 
					countString, "Seleccionar para revisarlas y subirlas", notificationIntent, Ticker.TITLE);
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
			RoutesServiceListener listener = null;
			if(boundActivity instanceof RoutesServiceListener) {
				listener = (RoutesServiceListener) boundActivity;
				listener.shouldBlockView();
			}
				
						
			while(routeUploader.peekNext() != null) {
				routeUploader.uploadNext();
			}
			
			reload();
			if(listener != null)
				listener.shouldUnblockView();
			return null;
		}
    }
}
