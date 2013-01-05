package org.wikicleta.activities;

import java.util.ArrayList;

import org.wikicleta.R;
import org.wikicleta.adapters.RoutesListAdapter;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.Route;
import org.wikicleta.services.RoutesManagementService;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ActivitiesFeedActivity extends Activity {
	public static ArrayList<Route> routeList;
	public static Route selectedRoute;
	RoutesListAdapter adapter;
	ListView list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppBase.currentActivity = this;
		this.setContentView(R.layout.activity_activity_feeds);
		
		startService(new Intent(this, RoutesManagementService.class));
		
		loadQueuedRoutes();
		this.drawRoutesList();
				
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
	
	protected Route getRouteById(long id) {
		for(Route route : routeList) {
			if(route.getId() == id) {
				return route;
			}
		}
		return null;
	}
}
