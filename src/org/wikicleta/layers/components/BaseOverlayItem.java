package org.wikicleta.layers.components;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class BaseOverlayItem extends OverlayItem {
	
	Context ctx;
	
	public BaseOverlayItem(Context ctx, GeoPoint arg0, String arg1, String arg2) {
		super(arg0, arg1, arg2);
		this.ctx = ctx;
	}
	
	protected void setMarkerWithDrawable(int drawable) {
		Drawable icon = ctx.getResources().getDrawable(drawable);

		//Set the bounding for the drawable
		icon.setBounds(
		    0 - icon.getIntrinsicWidth() / 2, 0 - icon.getIntrinsicHeight(), 
		    icon.getIntrinsicWidth() / 2, 0);

		//Set the new marker to the overlay
		this.setMarker(icon);		
	}
}
