package com.wikicleta.activities;

import java.util.ArrayList;

import org.mobility.wikicleta.R;

import com.wikicleta.adapters.RoutesListAdapter;
import com.wikicleta.async.AsyncTaskUIUpdater;
import com.wikicleta.async.AsyncTaskManager;
import com.wikicleta.common.AppBase;
import com.wikicleta.common.Constants;
import com.wikicleta.helpers.NotificationBuilder;
import com.wikicleta.models.Route;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ActivityFeedsActivity extends Activity implements AsyncTaskUIUpdater {

	public static ArrayList<Route> routeList;
	public static Route selectedRoute;
	RoutesListAdapter adapter;
	ListView list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		NotificationBuilder.clearNotification(Constants.ROUTES_SYNCING_NOTIFICATIONS_ID);

		AppBase.currentActivity = this;
		this.setContentView(R.layout.activity_activity_feeds);
		loadQueuedRoutes();
		this.drawRoutesList();
		
		NotificationBuilder.clearNotification(Constants.DRAFT_ROUTES_NOTIFICATION_ID);
		
		this.findViewById(R.id.back_icon).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intentActivity = new Intent(AppBase.currentActivity, RoutesActivity.class);
				AppBase.currentActivity.startActivity(intentActivity);
			}
        });
		
	}
	
	@Override
	public void onStart() {
		super.onStart();
		AsyncTaskManager.executePendingTasks();
		this.reloadAdapter();
	}
	
	public static int queuedRoutesCount() {
		loadQueuedRoutes();
		return routeList.size();
	}
	
	private static void loadQueuedRoutes() {
		routeList = Route.queued();
	}
	
	public void drawRoutesList() {
    	list = (ListView) findViewById(R.id.list);
    	
        // Getting adapter by passing xml data ArrayList
    	reloadAdapter();
        
        // Click event for single list row
        list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
				Route route = (Route) adapter.getItem(pos);
				if(!route.isBlocked) {
					RouteDetailsActivity.currentRoute = route;
					launchRouteDetailsActivity();
				}
			}
        });
    }
	
	
	/*public void discardRoute() {
    	selectedRoute.delete();
		loadQueuedRoutes();
		adapter.notifyDataSetChanged();
		if(routeList.isEmpty()) {
			this.launchDraftsRoutesActivity();
		}
		
	}*/
	
	public void reloadAdapter() {
		adapter = new RoutesListAdapter(this, routeList);
        list.setAdapter(adapter);
	}
	
	public void launchRouteDetailsActivity() {
		Intent intentActivity = new Intent(AppBase.currentActivity, RouteDetailsActivity.class);
		AppBase.currentActivity.startActivity(intentActivity);
	}
	
	public void launchDraftsRoutesActivity() {
		Intent intentActivity = new Intent(AppBase.currentActivity, RoutesActivity.class);
		AppBase.currentActivity.startActivity(intentActivity);
	}


	
	@Override
	public void updateUI(Object object, boolean blockItem) {
		if(object instanceof Route) {
			Route route = this.getRouteById(((Route) object).getId());
			if(route == null) {
				routeList.add((Route) object);
			} else {
				route.isBlocked = true;
			}
			this.reloadAdapter();
		}
	}
	
	protected Route getRouteById(long id) {
		for(Route route : routeList) {
			if(route.getId() == id) {
				return route;
			}
		}
		return null;
	}
}
