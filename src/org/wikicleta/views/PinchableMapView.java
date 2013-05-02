package org.wikicleta.views;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

public class PinchableMapView extends MapView {
	
	private PinchableMapView thisInstance;
	private long timeOutBetweenEvents = 200L;
	private boolean isTouched = false;
	private GeoPoint lastCenter;
	private int lastZoom;
	private Timer zoomEventDelayTimer = new Timer();
	private Timer panEventDelayTimer = new Timer();

	private OnZoomChangeListener zoomChangeListener;
	private OnPanChangeListener panChangeListener;
	
	public interface OnZoomChangeListener {
	    public void onZoomChange(MapView view, int newZoom, int oldZoom);
	}

	public interface OnPanChangeListener {
	    public void onPanChange(MapView view, GeoPoint newCenter, GeoPoint oldCenter);
	}
	
	public PinchableMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		thisInstance = this;
		
		this.lastCenter = this.getMapCenter();
		this.lastZoom= this.getZoomLevel();
	}

	private long lastTouchTime = -1;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			long thisTime = System.currentTimeMillis();
			if (thisTime - lastTouchTime < 250) {
				// Double tap
				this.getController().zoomInFixing((int) ev.getX(), (int) ev.getY());
				lastTouchTime = -1;
			} else {
				lastTouchTime = thisTime;
			}
		} 
		
		isTouched = (ev.getAction() == MotionEvent.ACTION_DOWN || ev.getAction() == MotionEvent.ACTION_MOVE);
	
		return super.onInterceptTouchEvent(ev);
	}
	
	
	public void setOnZoomChangeListener(OnZoomChangeListener zoomListener) {
		zoomChangeListener = zoomListener;
	}

	public void setOnPanChangeListener(OnPanChangeListener panListener) {
		panChangeListener = panListener;
	}
	
	@Override
	public void computeScroll() {
	    super.computeScroll();
	    
	    if(zoomChangeListener != null) {
	    	if (getZoomLevel() != lastZoom) {
		        // if computeScroll called before timer counts down we should drop it and start it over again
		    	zoomEventDelayTimer.cancel();
		    	zoomEventDelayTimer = new Timer();
		    	zoomEventDelayTimer.schedule(new TimerTask() {
		            @Override
		            public void run() {
		            	zoomChangeListener.onZoomChange(thisInstance, getZoomLevel(), lastZoom);
		                lastZoom = getZoomLevel();
		            }
		        }, timeOutBetweenEvents);
		    }
	    }
	    
	    if(panChangeListener != null) {
		    // Send event only when map's center has changed and user stopped touching the screen
		    if (!lastCenter.equals(getMapCenter()) || !isTouched) {
		    	panEventDelayTimer.cancel();
		    	panEventDelayTimer = new Timer();
		    	panEventDelayTimer.schedule(new TimerTask() {
		            @Override
		            public void run() {
		            	panChangeListener.onPanChange(thisInstance, getMapCenter(), lastCenter);
		                lastCenter = getMapCenter();
		            }
		        }, timeOutBetweenEvents);
		    }
	    }
	    
	}
}