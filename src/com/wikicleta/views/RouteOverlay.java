package com.wikicleta.views;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import com.wikicleta.helpers.PathTrace;

public class RouteOverlay extends Overlay {
	private PathTrace trace;
    private Paint paint;
    private int color;
    
    public RouteOverlay(PathTrace trace) {
    	this.paint = new Paint();
    	this.color = Color.BLUE;
    	this.trace = trace;
    }
    
    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        Projection projection = mapView.getProjection();

        Point point1 = new Point();
        Point point2 = new Point();

        for (int i = 1; i < trace.locationList.size(); i++) {
         GeoPoint geoPoint1 = trace.locationList.get(i-1);
         GeoPoint geoPoint2 = trace.locationList.get(i);
        
         projection.toPixels(geoPoint1, point1);
         projection.toPixels(geoPoint2, point2);
            canvas.drawLine(point1.x, point1.y, point2.x, point2.y, paint);
        }
    }
}
