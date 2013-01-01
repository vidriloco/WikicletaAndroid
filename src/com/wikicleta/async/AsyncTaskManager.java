package com.wikicleta.async;

import java.util.LinkedList;

import android.os.AsyncTask;

public class AsyncTaskManager {
	private static LinkedList<AsyncTask<Void, Void, Boolean>> routesTasks;
	
	public static void pushNewRouteTask(RouteSavingAsync task) {
		if(routesTasks == null)
			routesTasks = new LinkedList<AsyncTask<Void, Void, Boolean>>();
		routesTasks.add(task);
	}
	
}
