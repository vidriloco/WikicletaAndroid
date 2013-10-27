package org.wikicleta.interfaces;

public interface FragmentNotificationsInterface {

	public void triggerFetch();
	public void notifyIsNowVisible();
	public void notifyDataFetched();
	public void notifyDataFailedToLoad();
}