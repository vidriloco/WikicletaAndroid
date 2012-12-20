package com.wikicleta.helpers;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import com.google.android.maps.GeoPoint;

import android.annotation.TargetApi;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class PathTrace {
	public ArrayList<GeoPoint> locationList;
	public boolean tracking;
	
	protected long startTime;
	protected long endTime;
	protected Location lastLocation;
	
	protected float averageSpeed = 0;
	protected float accumulatedDistance = 0;
	
	protected TextView timeTextView;
	protected TextView speedTextView;
	protected TextView distanceTextView;
	
	protected DecimalFormat decimalFormat = new DecimalFormat("##.##");
	
	private Handler mHandler;

	public PathTrace(TextView textViewSpeed, TextView textViewTime, TextView textViewDistance) {
		this.locationList = new ArrayList<GeoPoint>();
		this.timeTextView = textViewTime;
		this.speedTextView = textViewSpeed;
		this.distanceTextView = textViewDistance;
		
		mHandler = new Handler();
		decimalFormat.setRoundingMode(RoundingMode.DOWN);
		
		this.locationList.add(GeoHelpers.buildFromLatLon(19.428704, -99.168563));
		this.locationList.add(GeoHelpers.buildFromLatLon(19.430566, -99.164615));
		this.locationList.add(GeoHelpers.buildFromLatLon(19.431457, -99.162469));
		this.locationList.add(GeoHelpers.buildFromLatLon(19.431780, -99.161568));
		
	}
	
	public void addLocation(Location location) {
		Log.i("Wikicleta", "Location updated");
		if(tracking) {
			// calculate average speed
			float speed = (float) location.getSpeed()*3600 / 1000;
			float distance = 0;

			averageSpeed = (averageSpeed + speed)/2;
			speedTextView.setText(decimalFormat.format(speed).concat(" km/h"));
			Log.i("Wikicleta", "Speed change " + speed);

			// calculate accumulated distance
			if(lastLocation != null) {
				distance = (float) location.distanceTo(lastLocation)/1000;
				accumulatedDistance += distance;
			} 

			distanceTextView.setText(decimalFormat.format(distance).concat(" km"));
			this.lastLocation = location;
			this.locationList.add(GeoHelpers.buildFromLongitude(lastLocation));
		}
	}
	
	public Map<String, Object> closePath() {
		this.pause();
		
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		// Speed in km/s
		map.put("speed", averageSpeed);
		// Elapsed time in milliseconds
		map.put("time", overallElapsedTime());
		// Distance in meters
		map.put("distance", accumulatedDistance);
		// Coordinates
		
		LinkedList<HashMap<String, Float>> coordinateList = new LinkedList<HashMap<String, Float>>();
		for(GeoPoint point : locationList) {
			HashMap<String, Float> coord = new HashMap<String, Float>();
			coord.put("lat", (float) (point.getLatitudeE6() / 1E6));
			coord.put("lon", (float) (point.getLongitudeE6() / 1E6));
			coordinateList.add(coord);
		}
		
		map.put("coordinates", coordinateList);
		return map;
	}
	
	public void reset() {
		startTime = 0;
		this.locationList.clear();
		averageSpeed = 0;
		accumulatedDistance = 0;
		
		this.setToTracking(false);
		
		// Reset fields
		if(timeTextView != null)
			timeTextView.setText("--");
		if(speedTextView != null)
			speedTextView.setText("--");
		if(distanceTextView != null)
			distanceTextView.setText("--");

	}
	
	public boolean isEmpty() {
		return this.locationList.isEmpty();
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
		
		if(tracking) {
			if(startTime == 0)
				startTime = System.currentTimeMillis();
			else
				startTime += System.currentTimeMillis()-endTime;
			mHandler.removeCallbacks(mUpdateTimeTask);
            mHandler.postDelayed(mUpdateTimeTask, 100);
		} else {
			mHandler.removeCallbacks(mUpdateTimeTask);
            endTime = System.currentTimeMillis();
		}
	}
	
	private long overallElapsedTime() {
		return System.currentTimeMillis() - startTime;
	}
	
	private Runnable mUpdateTimeTask = new Runnable() {
		   public void run() {
			   //Log.i("Wikicleta", "Time now " + startTime + " Accum time " + overallElapsedTime());
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
		       
	    	   timeTextView.setText(timeString);
		       mHandler.postDelayed(mUpdateTimeTask, 100);
		   }
	};
}
