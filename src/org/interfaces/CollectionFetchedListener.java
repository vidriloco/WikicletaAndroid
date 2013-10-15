package org.interfaces;

import java.util.ArrayList;
import java.util.HashMap;

import org.wikicleta.models.LightPOI;

public interface CollectionFetchedListener {

	public void onFinishedFetchingCollection(HashMap<String, ArrayList<LightPOI>> collection);
	public void onFailedFetchingCollection();
}
