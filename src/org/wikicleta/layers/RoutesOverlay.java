package org.wikicleta.layers;

import java.util.ArrayList;
import java.util.Random;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.helpers.GeoHelpers;
import org.wikicleta.models.Instant;
import org.wikicleta.models.Route;
import org.wikicleta.routes.activities.RouteDetailsActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class RoutesOverlay extends Overlay implements IdentifiableOverlay {

    protected int id;
    private ArrayList<Instant> instants;
    private Paint paint;
    private GeoPoint keyPointStart;
    private GeoPoint keyPointEnd;
    Bitmap bmpStart;
    Bitmap bmpEnd;
    Projection projection;
    
    public boolean detailedView;
    
    private Point lastPoint;
    
    protected Route route;
    
	@Override
	public int getIdentifier() {
		return id;
	}

    public RoutesOverlay(int identifier, Route route) {
    	this.id = identifier;
    	this.instants = route.instants();
    	this.keyPointStart = GeoHelpers.buildGeoPointFromLongitude(route.getStartingLocation());
    	this.keyPointEnd = GeoHelpers.buildGeoPointFromLongitude(route.getEndingLocation());
    	this.bmpStart =  BitmapFactory.decodeResource(AppBase.currentActivity.getResources(),  R.drawable.start); 
    	this.bmpEnd =  BitmapFactory.decodeResource(AppBase.currentActivity.getResources(),  R.drawable.finish);    
    	this.route = route;
    	this.paint = new Paint();
    	Random rnd = new Random();
    	paint.setARGB(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    	this.detailedView = false;
    }
    
    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);
        
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(paint.getColor());
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        projection = mapView.getProjection();
        
        Point point1 = new Point();
        Point point2 = new Point();
        
        int drawPointsCount = 0;
        for (int i = 1; i < instants.size(); i++) {
        	Instant instant1 = instants.get(i-1);
        	Instant instant2 = instants.get(i);
        	
        	GeoPoint geoPoint1 = instant1.geoPoint();
        	GeoPoint geoPoint2 = instant2.geoPoint();
        
        	projection.toPixels(geoPoint1, point1);
        	projection.toPixels(geoPoint2, point2);
            paint.setStyle(Paint.Style.STROKE);
        	canvas.drawLine(point1.x, point1.y, point2.x, point2.y, paint);
        	if(detailedView && instant2.speed >= 14 && drawPointsCount < 30) {
        		paint.setStyle(Paint.Style.FILL);
        		canvas.drawCircle(point2.x, point2.y, instant2.speed, paint);
        		drawPointsCount += 1;
        	}
        }
        
        Point keyPointStartPxl = new Point();
    	projection.toPixels(keyPointStart, keyPointStartPxl);
        
        canvas.drawBitmap(bmpStart, keyPointStartPxl.x-24, keyPointStartPxl.y-30, null); 
        lastPoint = keyPointStartPxl;
        
        if(detailedView) {
        	Point keyPointEndPxl = new Point();
        	projection.toPixels(keyPointEnd, keyPointEndPxl);
            canvas.drawBitmap(bmpEnd, keyPointEndPxl.x-24, keyPointEndPxl.y-30, null);
        }
        
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event, MapView mapView)  {
    	super.onTouchEvent(event, mapView);
        // TODO Auto-generated method stub
    	float touchedX = event.getX();
    	float touchedY = event.getY();
    	
        int action = event.getAction();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
        	if(lastPoint == null)
        		return false;
        	touchOnRouteStart(touchedX, touchedY);
        	touchOnIntermediatePoint(touchedX, touchedY);
            break;
        case MotionEvent.ACTION_MOVE:
            break;
        case MotionEvent.ACTION_UP:
            break;
        case MotionEvent.ACTION_CANCEL:
            break;
        case MotionEvent.ACTION_OUTSIDE:
            break;
        default:
        }
        return false; // processed
    }
    
    protected void touchOnIntermediatePoint(float touchedX, float touchedY) {
    	
    	for (Instant instant : instants) {
            Point point = new Point();
        	GeoPoint geoPoint = instant.geoPoint();
        	projection.toPixels(geoPoint, point);
        	if((point.x-route.averageSpeed) < touchedX && (point.x+route.averageSpeed) > touchedX &&
        		(point.y-route.averageSpeed) < touchedY && (point.y+route.averageSpeed) > touchedY) {
        			if(detailedView)
        				this.displayInstantDetails(instant);
        	}
        }
    }
    
    public void displayInstantDetails(Instant instant) {    	
    	int seconds = (int) (instant.time / 1000) % 60 ;
    	int minutes = (int) ((instant.time / (1000*60)) % 60);
    	int hours   = (int) ((instant.time / (1000*60*60)) % 24);
    	String details = "Velocidad: ".concat(String.valueOf(instant.speed).concat(" km/h"));
    	details = details.concat(" Tiempo: ").concat(String.valueOf(hours)+"h:"+String.valueOf(minutes)+"m:"+String.valueOf(seconds)+"s");
    	Toast.makeText(AppBase.currentActivity, details, Toast.LENGTH_SHORT).show();
    }
    
    protected void touchOnRouteStart(float touchedX, float touchedY) {
    	if((lastPoint.x-bmpStart.getWidth()/2) < touchedX && (lastPoint.x+bmpStart.getWidth()/2) > touchedX &&
    			(lastPoint.y-bmpStart.getHeight()/2) < touchedY && (lastPoint.y+bmpStart.getHeight()/2) > touchedY) {
    		Bundle bundle = new Bundle();
        	bundle.putLong("routeId", route.getId());
        	if(AppBase.currentActivity.getClass() != RouteDetailsActivity.class)
        		AppBase.launchActivityWithBundle(RouteDetailsActivity.class, bundle);
    	}
    }

}
