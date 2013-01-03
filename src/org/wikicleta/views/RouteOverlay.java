package org.wikicleta.views;

import java.util.List;

import org.wikicleta.models.Instant;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class RouteOverlay extends Overlay {
	private List<Instant> instantList;
    private Paint paint;
    private int color;
    
    public RouteOverlay(List<Instant> instantList) {
    	this.paint = new Paint();
    	this.color = Color.BLUE;
    	this.instantList = instantList;
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

        for (int i = 1; i < instantList.size(); i++) {
         Instant instantOne = instantList.get(i-1);
         Instant instantTwo = instantList.get(i);
        
         projection.toPixels(instantOne.geoPoint(), point1);
         projection.toPixels(instantTwo.geoPoint(), point2);
            canvas.drawLine(point1.x, point1.y, point2.x, point2.y, paint);
        }
    }
}
