package org.wikicleta.services.routes;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import org.wikicleta.R;
import org.wikicleta.activities.routes.NewRouteActivity;
import org.wikicleta.common.Constants;
import org.wikicleta.helpers.Formatters;
import org.wikicleta.helpers.GeoHelpers;
import org.wikicleta.helpers.NotificationBuilder;
import org.wikicleta.helpers.NotificationBuilder.Ticker;
import org.wikicleta.helpers.routes.RouteUploader;
import org.wikicleta.models.Instant;
import org.wikicleta.models.Route;
import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class RouteTrackingService extends LocationAwareService {
	
	public static final int UPLOAD_ROUTE = 1;
	public static final int NOTIFY_ABOUT_STALLED = 2;
	
	// Route uploading fields
	protected RouteUploader routeUploader;
	protected NotificationBuilder notification;
	protected final IBinder localBinder = new RoutesServiceBinder();
    private UploaderTask routeUploaderTask;

    // Route timing fields
	protected Timer timer;
	public int seconds = 0;
	public float averageSpeed = 0;
	public float accumulatedDistance = 0;
	public ArrayList<Instant> coordinateVector;

	protected Intent notificationIntent;
	
	private int NOTIFICATION_ID = 1337;
	
	// Control flag
	public boolean isTracking;
    
	// For bindService calls
	@Override
	public IBinder onBind(Intent intent) {
		this.startTracking();
		return localBinder;
	}
	
	// For startService calls
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		this.startTracking();
		return Service.START_NOT_STICKY;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		reset();
        this.notification = new NotificationBuilder(this);
        notificationIntent = new Intent(this, NewRouteActivity.class); 

	}
	 
	@Override
	public void onDestroy() {
		this.stopTracking();
	}

	/*
	 * Basic track recording methods BEGIN
	 */
	
	public void reset() {
		coordinateVector = new ArrayList<Instant>();
		seconds = 0;
		averageSpeed = 0;
		accumulatedDistance = 0;
		if(this.routeUploader == null)
			this.routeUploader = new RouteUploader();
		
	}
	
	public void pauseRecordingAndNotify() {
		this.pauseRecording();
		notification.addNotification(NOTIFICATION_ID, 
				"Wikicleta", this.getResources().getString(R.string.routes_recording_paused), notificationIntent, Ticker.MESSAGE, true);
	}
	
	public void pauseRecording() {
		this.isTracking = false;
		this.pauseTimer();
		disableLocationManager();
	}
	
	public void resumeRecording() {
	    if (!isTracking) {
	    	isTracking=true;
	    	this.resumeTimer();
	    	enableLocationManager();
	    	
	    	notification.addNotification(NOTIFICATION_ID, 
					"Wikicleta", this.getResources().getString(R.string.routes_recording_resumed), notificationIntent, Ticker.MESSAGE, true);
	      
	    }
	}
	
	public void stopTracking() {
		pauseRecording();
		this.stopForeground(true);
	}
	
	public void startTracking() {
      Intent notificationIntent = new Intent(this, NewRouteActivity.class);  
      Notification note = notification.addNotification(NOTIFICATION_ID, 
				"Wikicleta", this.getResources().getString(R.string.routes_recording_service_start), notificationIntent, Ticker.MESSAGE, false);
      
      startForeground(NOTIFICATION_ID, note);
	}

	/*
	 * Basic track recording methods END
	 */
	
	/*
	 * Methods for routes recording
	 */
	
	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
		addLocation(location);
		this.notifyFieldsUpdated();
		Log.e("WIKICLETA", "Coords in vector: " + String.valueOf(coordinateVector.size()));
		Log.e("WIKICLETA", "Time from loc: " + String.valueOf(seconds));
		if(this.boundActivity instanceof NavigationListener)
			((NavigationListener) this.boundActivity).locationUpdated();
	}
	
	protected void notifyFieldsUpdated() {
		if(this.boundActivity instanceof NavigationListener) {
			((NavigationListener) this.boundActivity).onFieldsUpdated();
		}
	}
	
	/*
	 * Methods for routes uploading
	 */
	
	public void addRouteForUpload(Route route) {
		notification.addNotification(Constants.ROUTES_MANAGEMENT_NOTIFICATION_ID, 
				getString(R.string.app_name), getString(R.string.route_being_sent), null, Ticker.MESSAGE, false);		
		this.routeUploader.addRoute(route);
	}
	
	public void uploadStagedRoutes() {
		this.routeUploaderTask = new UploaderTask();
		routeUploaderTask.execute();
	}
	
	public int queuedRoutesCount() {
		return routesQueued().size();
	}
	
	public LinkedList<Route> routesQueued() {
		return this.routeUploader.routesWaitingToUpload();
	}
	
	// Local binder implementation
	public class RoutesServiceBinder extends Binder implements ServiceBinder {
		public RouteTrackingService getService() {
            return RouteTrackingService.this;
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
			
			reset();
			if(listener != null)
				listener.shouldUnblockView();
			return null;
		}
    }
	
	/*
	 * Coordinates methods BEGIN
	 */
	
	public boolean pathIsEmpty() {
		return this.coordinateVector.isEmpty();
	}
	
	/*
	 * Timer functions BEGIN
	 */
	
	private class TimeUpdaterTask extends TimerTask {

		@Override
		public void run() {
			seconds += 1;
			notifyFieldsUpdated();
	    	Log.i("WIKICLETA", Formatters.millisecondsToTime(Formatters.secondsFromMilliseconds(seconds)));
		}
    }
	
	protected void pauseTimer() {
		if(timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
	} 
	
	protected void resumeTimer() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimeUpdaterTask(), 0, 1000);
	} 
	
	/*
	 * Timer functions END
	 */
	
	/*
	 * Location storer functions BEGIN
	 */
	
	public void addLocation(Location location) {
		if(isTracking) {
			// calculate average speed
			float speed = (float) location.getSpeed()*3600 / 1000;
			float distance = 0;

			averageSpeed = (averageSpeed + speed)/2;
			Log.i("Wikicleta", "Speed change " + speed);

			// calculate accumulated distance
			if(lastLocationCatched != null) {
				distance = (float) location.distanceTo(lastLocationCatched)/1000;
				accumulatedDistance += distance;
			} 

			this.lastLocationCatched = location;
			this.coordinateVector.add(new Instant(GeoHelpers.buildGeoPointFromLongitude(lastLocationCatched), speed, Formatters.secondsFromMilliseconds(seconds)));
		}
	}
	
	public Route buildRoute(String name, String tags) {
		this.pauseRecording();		
		Route route = new Route(name, tags, Formatters.secondsFromMilliseconds(seconds), averageSpeed, accumulatedDistance, new Date().getTime());
		route.temporalInstants = coordinateVector;
		return route;
	}
	
	/*
	 * Location storer functions END
	 */
}
