package org.wikicleta.views;

import org.wikicleta.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class PinOverlay extends Overlay {
	
	private Bitmap bmp;
	
	private int x = 0;
	private int y = 0;
	
	private int centerMapX = 0;
	private int centerMapY = 0;
	
	private Projection proj;
	
	public PinOverlay(Activity activity) {
        this.bmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.wikicleta_pin);
	}
	
	@Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);

        if (!shadow) {
            if(this.x == 0 && this.y == 0) {
                this.x = mapView.getWidth()/2 - bmp.getWidth() / 2;                                                       
                this.y = mapView.getHeight()/2 - bmp.getHeight();     
            }
            canvas.drawBitmap(bmp, x, y, null);
        }
        
        if(centerMapX == 0 && centerMapY == 0) {
        	this.centerMapX = mapView.getWidth()/2;
        	this.centerMapY = mapView.getHeight()/2;
        }
        
        if(proj == null)
        	proj = mapView.getProjection();

    }
	
	public GeoPoint getLocation() {
		return proj.fromPixels(centerMapX, centerMapY);
	}
}
