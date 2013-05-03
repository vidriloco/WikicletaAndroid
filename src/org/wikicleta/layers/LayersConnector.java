package org.wikicleta.layers;

import java.util.Date;
import org.wikicleta.R;
import org.wikicleta.common.Constants;
import android.app.Activity;

public class LayersConnector {
	Activity activity;
	BikeSharingOverlay bikeSharingOverlay;
	Date bikeSharingOverlayLastFetched;
	
	public LayersConnector(Activity activity) {
		this.activity = activity;
	}
	
	public BikeSharingOverlay getBikeSharingOverlay() {
		Date currentDate = new Date();
		if(bikeSharingOverlayLastFetched == null ||
				(currentDate.getTime()-bikeSharingOverlayLastFetched.getTime()) > Constants.MAX_AWAITING_TIME_BETWEEN_LAYER_RELOADING ) {
			bikeSharingOverlay = new BikeSharingOverlay((LayersConnectorListener) activity);
			bikeSharingOverlayLastFetched = currentDate;
		}
		
		return bikeSharingOverlay;
	}
	
	public TipsOverlay getTipsOverlay() {
		return new TipsOverlay(activity.getResources().getDrawable(R.drawable.cycling), (LayersConnectorListener) activity);

	}
	
}
