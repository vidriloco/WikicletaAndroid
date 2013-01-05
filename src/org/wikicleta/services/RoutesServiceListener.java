package org.wikicleta.services;

import org.wikicleta.models.Route;

public interface RoutesServiceListener {
	public void routeDidUpload(Route route);
	public void routeDidNotUpload(Route route);
	public void shouldBlockElement(Route route);
}
