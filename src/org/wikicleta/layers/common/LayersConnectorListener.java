package org.wikicleta.layers.common;

import java.util.HashMap;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;

public interface LayersConnectorListener {
	void overlayFinishedLoading(boolean status);
	void overlayFinishedLoadingWithPayload(boolean status, Object payload);
	void hideLoadingState();
	void showLoadingState();
	LatLng getLastLocation();
	
	HashMap<String, String> getCurrentViewport();
	
	Activity getActivity();
}
