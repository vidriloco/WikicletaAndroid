package org.wikicleta.services.routes;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import org.wikicleta.R;
import org.wikicleta.activities.routes.NewRouteActivity;
import org.wikicleta.helpers.Formatters;
import org.wikicleta.helpers.NotificationBuilder;
import org.wikicleta.helpers.NotificationBuilder.Ticker;
import org.wikicleta.models.Instant;
import android.app.Activity;
import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class RouteTrackingService extends LocationAwareService {
	
	public static final int UPLOAD_ROUTE = 1;
	public static final int NOTIFY_ABOUT_STALLED = 2;
	
	// Route uploading fields
	protected NotificationBuilder notification;
	protected final IBinder localBinder = new RoutesServiceBinder();

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
		addLocation(location);
		this.notifyFieldsUpdated();
		if(this.boundActivity instanceof NavigationListener)
			((NavigationListener) this.boundActivity).locationUpdated();
	}
	
	protected void notifyFieldsUpdated() {
		if(this.boundActivity instanceof NavigationListener) {
			((NavigationListener) this.boundActivity).onFieldsUpdated();
		}
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

			averageSpeed = (averageSpeed + speed)/2;
			Log.i("Wikicleta", "Speed change " + speed);

			// calculate accumulated distance
			if(lastLocationCatched != null) {
				float distance = (float) location.distanceTo(lastLocationCatched)/1000;
				accumulatedDistance += distance;
			} 
			
			this.lastLocationCatched = location;
			this.coordinateVector.add(new Instant(lastLocationCatched, speed, Formatters.secondsFromMilliseconds(seconds)));
		}
	}
	
	/*
	 * Location storer functions END
	 */
}
