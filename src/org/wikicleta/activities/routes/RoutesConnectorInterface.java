package org.wikicleta.activities.routes;

public interface RoutesConnectorInterface {

	public void pathFinishedLoading(boolean status, double [][] path);
	public void pathDidNotLoad(boolean status);
}
