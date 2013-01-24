package org.wikicleta.layers;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public interface OverlayReadyListener {
	public void onOverlayPrepared(ItemizedOverlay<OverlayItem> overlay, int kind);
}
