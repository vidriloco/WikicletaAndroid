package org.wikicleta.routes.helpers;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.wikicleta.helpers.GeoHelpers;
import org.wikicleta.models.Instant;
import org.wikicleta.models.Route;
import org.wikicleta.routes.services.RoutesService;
import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;

public class RouteRecorder {
	public ArrayList<Instant> coordinateVector;
	protected boolean tracking;
	
	protected long startTime;
	protected long endTime;
	protected Location lastLocation;
	
	protected float averageSpeed = 0;
	protected float accumulatedDistance = 0;
	
	public String timeTextValue;
	public String speedTextValue;
	public String distanceTextValue;
	
	protected TimeUpdaterTask timeUpdater;
	protected Timer timer;
	
	protected DecimalFormat decimalFormat = new DecimalFormat("##.##");
	protected RoutesService routesService;
		
	// TODO: Get rounding not using NewApi
	
	@SuppressLint("NewApi")
	public RouteRecorder(RoutesService routesService) {
		this.routesService = routesService;
			
		this.coordinateVector = new ArrayList<Instant>();
		this.timeUpdater = new TimeUpdaterTask();

		decimalFormat.setRoundingMode(RoundingMode.DOWN);
		this.reset();

		this.coordinateVector.add(new Instant(GeoHelpers.buildFromLatLon(19.428704, -99.168563), 0.3f, 44233));
		this.coordinateVector.add(new Instant(GeoHelpers.buildFromLatLon(19.430566, -99.164615), 1.0f, 59432));
		this.coordinateVector.add(new Instant(GeoHelpers.buildFromLatLon(19.431457, -99.162469), 8.0f, 67534));
		this.coordinateVector.add(new Instant(GeoHelpers.buildFromLatLon(19.431780, -99.161568), 33.0f, 70564));
	}
	
	public void addLocation(Location location) {
		Log.i("Wikicleta", "Location updated");
		if(tracking) {
			// calculate average speed
			float speed = (float) location.getSpeed()*3600 / 1000;
			float distance = 0;

			averageSpeed = (averageSpeed + speed)/2;
			speedTextValue = decimalFormat.format(speed).concat(" km/h");
			Log.i("Wikicleta", "Speed change " + speed);

			// calculate accumulated distance
			if(lastLocation != null) {
				distance = (float) location.distanceTo(lastLocation)/1000;
				accumulatedDistance += distance;
			} 

			distanceTextValue = decimalFormat.format(accumulatedDistance).concat(" km");
			this.lastLocation = location;
			this.coordinateVector.add(new Instant(GeoHelpers.buildFromLongitude(lastLocation), speed, overallElapsedTime()));
		}
	}
	
	public Route buildRoute(String name, String tags) {
		this.pause();		
		Route route = new Route(name, tags, overallElapsedTime(), averageSpeed, accumulatedDistance, new Date().getTime());
		route.temporalInstants = coordinateVector;
		return route;
	}
	
	public void reset() {
		startTime = 0;
		this.coordinateVector.clear();
		averageSpeed = 0;
		accumulatedDistance = 0;
				
		this.timeTextValue = null;
		this.speedTextValue = null;
		this.distanceTextValue = null;

		if(this.timer != null)
			this.timer.cancel();
		this.timer = new Timer();
		
		this.setToTracking(false);
	}
	
	public boolean isEmpty() {
		return this.coordinateVector.isEmpty();
	}
	
	public void pause() {
		this.setToTracking(false);
	}
	
	public void resume() {
		this.setToTracking(true);
	}
	
	public boolean isPaused() {
		return !this.tracking;
	}
	
	protected void setToTracking(boolean tracking) {
		this.tracking = tracking;
		timer.purge();
		
		if(tracking) {			
			if(startTime == 0)
				startTime = System.currentTimeMillis();
			else
				startTime += System.currentTimeMillis()-endTime;
            timer.schedule(timeUpdater, 100);
		} else {
            endTime = System.currentTimeMillis();
		}
	} 
	
	
	private long overallElapsedTime() {
		return System.currentTimeMillis() - startTime;
	}
	
	private class TimeUpdaterTask extends TimerTask {

		@Override
		public void run() {
			int seconds = (int) (overallElapsedTime() / 1000);
		    int minutes = seconds / 60;
		    seconds     = seconds % 60;
	       
		    String timeString = "";
	       
		    if(minutes > 0 && minutes < 10)
	    	   timeString = "0" + String.valueOf(minutes) + ":";
		    else if(minutes >= 10) 
	    	   timeString = String.valueOf(minutes) + ":";
		    else {
	    	   timeString = "00:";
		    }
	       
		    if (seconds < 10)
	    	   timeString += "0"+seconds;
		    else
	    	   timeString += String.valueOf(seconds);
	       
		    timeTextValue = timeString;
		    
		    routesService.updateTimingDisplay();
		    if(!isPaused()) {
			   timer.schedule(timeUpdater, 100);
		    }
		}
    }
}
