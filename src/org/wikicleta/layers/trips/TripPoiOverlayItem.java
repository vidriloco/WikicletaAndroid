package org.wikicleta.layers.trips;

import org.wikicleta.layers.common.BaseOverlayItem;
import org.wikicleta.models.TripPoi;
import android.content.Context;
import com.google.android.maps.GeoPoint;

public class TripPoiOverlayItem extends BaseOverlayItem {

	TripPoi associatedTripPoi;
	
	public TripPoiOverlayItem(Context ctx, TripPoi tripPoi) {
		super(ctx, new GeoPoint(tripPoi.latitude, tripPoi.longitude), "", "");
		this.associatedTripPoi = tripPoi;
		this.setIconAccordingToCategory();
	}
	
	public int getDrawable() {
		int identifier = -1;

		if(this.associatedTripPoi.categoryString()=="transport_connection") {
			String resourceName = this.associatedTripPoi.iconName;
			identifier=this.ctx.getResources().getIdentifier(resourceName, "drawable", ctx.getPackageName());
		} else {
			String resourceName = this.associatedTripPoi.categoryString().concat("_marker");
			identifier=this.ctx.getResources().getIdentifier(resourceName, "drawable", ctx.getPackageName());
		}
		
		return identifier;
	}
	
	public void setIconAccordingToCategory() {
		this.setMarkerWithDrawable(this.getDrawable());
	}
}
