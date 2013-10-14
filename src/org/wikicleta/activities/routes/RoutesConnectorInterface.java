package org.wikicleta.activities.routes;

public interface RoutesConnectorInterface {

	public void routeDetailsFinishedLoading(boolean status);
	public void routeDetailsDidNotLoad(boolean status);
	
	public void routePerformancesFinishedLoading(boolean status);
	public void routePerformancesDidNotLoad(boolean status);
}
