package org.wikicleta.layers;

import java.util.ArrayList;
import java.util.Random;
import org.wikicleta.layers.common.IdentifiableOverlay;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class RouteOverlay {
/*
    protected int id;
    private ArrayList<LatLng> geoPoints;
    private Paint paint;
    Projection projection;
    Point point1 = new Point();
    Point point2 = new Point();
    Point keyPointStartPxl = new Point();
    Point keyPointEndPxl = new Point();
    
    public boolean detailedView;
    
	@Override
	public int getIdentifier() {
		return id;
	}

	public RouteOverlay(int identifier, ArrayList<LatLng> geoPoints, String color) {
		this(identifier, geoPoints);
    	paint.setColor(Color.parseColor(color));
	}
	
    public RouteOverlay(int identifier, ArrayList<LatLng> geoPoints) {
    	this.id = identifier;
    	this.geoPoints = geoPoints;    
    	this.paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(paint.getColor());
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        
    	Random rnd = new Random();
    	paint.setARGB(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    	this.detailedView = false;
    }
    
    @Override
    public void draw(Canvas canvas, GoogleMap mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);
        
        if(projection == null)
        	projection = mapView.getProjection();
        
        for (int i = 1; i < geoPoints.size(); i++) {
        	LatLng geoPoint1 = geoPoints.get(i-1);
        	LatLng geoPoint2 = geoPoints.get(i);
        
        	projection.toPixels(geoPoint1, point1);
        	projection.toPixels(geoPoint2, point2);
        	canvas.drawLine(point1.x, point1.y, point2.x, point2.y, paint);
        }
    }
*/
}
