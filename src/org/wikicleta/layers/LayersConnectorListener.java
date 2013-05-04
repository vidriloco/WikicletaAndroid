package org.wikicleta.layers;

import java.util.HashMap;
import android.app.Activity;

public interface LayersConnectorListener {
	void overlayFinishedLoading(boolean status);
	void hideLoadingState();
	void showLoadingState();
	HashMap<String, String> getCurrentViewport();
	
	Activity getActivity();
}
