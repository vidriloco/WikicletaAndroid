package org.wikicleta.layers.common;

import java.util.Date;
import org.wikicleta.R;
import org.wikicleta.common.Constants;
import org.wikicleta.layers.bikesharing.BikeSharingOverlay;
import org.wikicleta.layers.parkings.ParkingsOverlay;
import org.wikicleta.layers.tips.TipsOverlay;
import org.wikicleta.layers.workshops.WorkshopsOverlay;

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
		return new TipsOverlay(activity.getResources().getDrawable(R.drawable.cycling), listener);
	}
	
	public ParkingsOverlay getParkingsOverlay() {
		listener.showLoadingState();
		Activity activity = listener.getActivity();
		return new ParkingsOverlay(activity.getResources().getDrawable(R.drawable.parking_government_provided_icon), listener);
	}
	
	public WorkshopsOverlay getWorkshopsOverlay() {
		listener.showLoadingState();
		Activity activity = listener.getActivity();
		return new WorkshopsOverlay(activity.getResources().getDrawable(R.drawable.workshop_icon), listener);
	}
}
