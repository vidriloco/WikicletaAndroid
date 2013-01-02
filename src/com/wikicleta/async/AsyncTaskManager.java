package com.wikicleta.async;

import java.util.HashMap;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncTaskManager {
	private static HashMap<Long, AsyncTask<Void, Void, Void>> routesTasks;
	
	public static void pushNewRouteTask(RouteSavingAsync task) {
		initialize();
		if(!routesTasks.containsKey(task.route.getId()))
			routesTasks.put(task.route.getId(), task);
	}
	
	public static void executePendingTasks() {
		initialize();
		Log.e("WIKICLETA", String.valueOf(routesTasks.size()));
		for(AsyncTask<Void, Void, Void> task : routesTasks.values()) {
			if(!(task.getStatus() == AsyncTask.Status.RUNNING)) {
				task.execute((Void) null);
				break;
			}
		}
	}
	
	public static void deregisterTask(long id) {
		routesTasks.remove(id);
		executePendingTasks();
	}
	
	protected static void initialize() {
		if(routesTasks == null)
			routesTasks = new HashMap<Long, AsyncTask<Void, Void, Void>>();
	}
	
}
