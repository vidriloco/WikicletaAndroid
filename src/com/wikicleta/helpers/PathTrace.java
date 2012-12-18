package com.wikicleta.helpers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.android.maps.GeoPoint;

import android.location.Location;
import android.os.Handler;
import android.widget.TextView;

public class PathTrace {
	public ArrayList<GeoPoint> locationList;
	public boolean tracking;
	
	protected long startTime;
	protected long endTime;
	
	protected float averageSpeed = 0;
	protected float distance = 0;
	
	protected TextView timeTextView;
	protected TextView speedTextView;
	protected TextView distanceTextView;

	private Handler mHandler;

	public PathTrace(TextView textViewSpeed, TextView textViewTime, TextView textViewDistance) {
		this.locationList = new ArrayList<GeoPoint>();
		this.timeTextView = textViewTime;
		this.speedTextView = textViewSpeed;
		this.distanceTextView = textViewDistance;
		
		mHandler = new Handler();

		this.locationList.add(GeoHelpers.buildFromLatLon(19.428704, -99.168563));
		this.locationList.add(GeoHelpers.buildFromLatLon(19.430566, -99.164615));
		this.locationList.add(GeoHelpers.buildFromLatLon(19.431457, -99.162469));
		this.locationList.add(GeoHelpers.buildFromLatLon(19.431780, -99.161568));
		
	}
	
	public void addLocation(Location location) {
		if(tracking) {
			
			// calculate average speed
			averageSpeed = averageSpeed + location.getSpeed() / 2;
			speedTextView.setText(String.valueOf(location.getSpeed()/1000).concat(" km/h"));
			
			// calculate accumulated distance
			if(locationList.size() >= 1) {
				GeoPoint lastGeoPoint = locationList.get(locationList.size()-1);
				Location lastLocation = new Location(location);
				lastLocation.setLatitude(lastGeoPoint.getLatitudeE6());
				lastLocation.setLongitude(lastGeoPoint.getLongitudeE6());
				distance = distance + location.distanceTo(lastLocation);
				distanceTextView.setText(String.valueOf((float) distance/1000).concat(" km "));
			}
			
			this.locationList.add(GeoHelpers.buildFromLongitude(location));
		}
	}
	
	public Map<String, Object> closePath() {
		this.setToTracking(false);
		
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("speed", averageSpeed);
		map.put("time", this.overallElapsedTime());
		return map;
	}
	
	public void reset() {
		startTime = 0;
		this.locationList.clear();
		averageSpeed = 0;
		distance = 0;
		
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
	
	public void setToTracking(boolean tracking) {
		this.tracking = tracking;
		if(this.tracking) {
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
		       int seconds = (int) (overallElapsedTime() / 1000);
		       int minutes = seconds / 60;
		       seconds     = seconds % 60;

		       if (seconds < 10) {
		    	   timeTextView.setText("" + minutes + ":0" + seconds);
		       } else {
		    	   timeTextView.setText("" + minutes + ":" + seconds);            
		       }
		     
		       mHandler.postDelayed(mUpdateTimeTask, 100);
		   }
	};
}
