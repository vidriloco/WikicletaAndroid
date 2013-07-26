package org.wikicleta.layers.common;

import java.util.Date;
import org.wikicleta.common.Constants;
import org.wikicleta.layers.bikesharing.BikeSharingOverlay;

public class LayersConnector {
	public LayersConnectorListener listener;
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
			//bikeSharingOverlay = new BikeSharingOverlay(listener);
			//bikeSharingOverlayLastFetched = currentDate;
		} 
		
		return bikeSharingOverlay;
	}
}
