package org.wikicleta.services;

import org.wikicleta.models.Route;

public class Intercommunicator {
	protected static Route route;
	
	public static void setRoute(Route newRoute) {
		if(route == null)
			route = newRoute;

		synchronized(route) {
			route = newRoute;
		}
	}
	
	public static Route fetchRouteAndClear() {
		if(route == null)
			return route;
		synchronized(route) {
			Route copyRoute = route;
			route = null;
			return copyRoute;
		}
	}
}
