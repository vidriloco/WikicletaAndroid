package org.wikicleta.routes.services;

import org.wikicleta.models.Route;

public interface RoutesServiceListener {
	public void routeDidUpload(Route route);
	public void routeDidNotUpload(Route route);
	public void shouldBlockView();
	public void shouldUnblockView();

}
