package org.interfaces;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public interface MarkerInterface {
	public LatLng getLatLng();
	public int getDrawable();
	public Marker getAssociatedMarker();
	public void setMarker(Marker marker);
}
