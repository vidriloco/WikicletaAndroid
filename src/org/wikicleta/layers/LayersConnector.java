package org.wikicleta.layers;

import java.util.Date;
import org.wikicleta.R;
import org.wikicleta.common.Constants;
import android.app.Activity;

public class LayersConnector {
	LayersConnectorListener listener;
	BikeSharingOverlay bikeSharingOverlay;
	Date bikeSharingOverlayLastFetched;
	
	public LayersConnector(LayersConnectorListener activity) {
		this.listener = activity;
	}
	
	public BikeSharingOverlay getBikeSharingOverlay() {
		Date currentDate = new Date();
		if(bikeSharingOverlayLastFetched == null ||
				(currentDate.getTime()-bikeSharingOverlayLastFetched.getTime()) > Constants.MAX_AWAITING_TIME_BETWEEN_LAYER_RELOADING ) {
			listener.showLoadingState();
			bikeSharingOverlay = new BikeSharingOverlay(listener);
			bikeSharingOverlayLastFetched = currentDate;
		} 
		
		return bikeSharingOverlay;
	}
	
	public TipsOverlay getTipsOverlay() {
		listener.showLoadingState();
		Activity activity = listener.getActivity();
		return new TipsOverlay(activity.getResources().getDrawable(R.drawable.cycling), (LayersConnectorListener) activity);
	}
	
}
