package org.wikicleta.interfaces;

public interface RemoteFetchingDutyListener {

	public void onSuccess(Object duty);
	public void onFailed(Object message);
	public void onFailed();
}
