package org.wikicleta.routes.helpers;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.wikicleta.helpers.Formatters;
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

		decimalFormat.setRoundingMode(RoundingMode.DOWN);
		this.reset();
	}
	
	public void addLocation(Location location) {
		Log.i("Wikicleta", "Adding location");
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
			this.coordinateVector.add(new Instant(GeoHelpers.buildGeoPointFromLongitude(lastLocation), speed, overallElapsedTime()));
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
		reScheduleTask();
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
			reScheduleTask();
		} else {
            endTime = System.currentTimeMillis();
		}
	} 
	
	
	private long overallElapsedTime() {
		return System.currentTimeMillis() - startTime;
	}
	
	private void reScheduleTask() {
		if(this.timer != null)
			this.timer.cancel();
		this.timer = new Timer();
		this.timeUpdater = new TimeUpdaterTask();
		if(!isPaused()) {
			timer.schedule(timeUpdater, 100);
		}
	}
	
	private class TimeUpdaterTask extends TimerTask {

		@Override
		public void run() {
		    timeTextValue = Formatters.millisecondsToTime(overallElapsedTime());
		    
		    routesService.updateTimingDisplay();
		    reScheduleTask();
		}
    }
}
