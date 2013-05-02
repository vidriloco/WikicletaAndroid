package org.wikicleta.layers;

import java.util.HashMap;
import android.app.Activity;

public interface LayersConnectorListener {
	public void onOverlayReady();
	
	public HashMap<String, String> getCurrentViewport();
	
	public Activity getActivity();
}
