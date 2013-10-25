package org.wikicleta.interfaces;

import android.graphics.Bitmap;

public interface ImageFetchedListener {

	public void imageFetchedSucceded(Bitmap bitmap);
	public void imageFetchedFailed();
}
