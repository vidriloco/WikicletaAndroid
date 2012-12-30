package com.wikicleta.activities;

import java.util.ArrayList;

import org.mobility.wikicleta.R;

import com.wikicleta.adapters.RoutesListAdapter;
import com.wikicleta.common.AppBase;
import com.wikicleta.common.Constants;
import com.wikicleta.helpers.NotificationBuilder;
import com.wikicleta.models.Route;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class DraftRoutesActivity extends Activity {

	public static ArrayList<Route> routeList;
	public static Route selectedRoute;
	RoutesListAdapter adapter;
	ListView list;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppBase.currentActivity = this;
		this.setContentView(R.layout.activity_routes_list);
		loadQueuedRoutes();
		this.drawRoutesList();
		
		NotificationBuilder.clearNotification(Constants.DRAFT_ROUTES_NOTIFICATION_ID);
	}
	
	public static int queuedRoutesCount() {
		loadQueuedRoutes();
		return routeList.size();
	}
	
	private static void loadQueuedRoutes() {
		DraftRoutesActivity.routeList = Route.queued();
	}
	
	public void drawRoutesList() {
		Log.e("Wikicleta", "Redrawing");
    	list = (ListView) findViewById(R.id.list);
    	
        // Getting adapter by passing xml data ArrayList
    	reloadAdapter();
        
        // Click event for single list row
        list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
				RouteDetailsActivity.currentRoute = (Route) adapter.getItem(pos);
				launchRouteDetailsActivity();
			}
        });
        
        list.setOnItemLongClickListener(new OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
				selectedRoute = (Route) adapter.getItem(pos);
				openContextMenu(list);
                return true;
            }
        }); 
        
        registerForContextMenu(list);
    }
	
	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.route_drafts_menu , menu);
    }
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.route_draft_destroy:
            	discardRoute();
            	reloadAdapter();
                break;
            case R.id.route_draft_details:
            	launchRouteDetailsActivity();
                break;
        }
        
        return true;
    }
	
	public void discardRoute() {
    	selectedRoute.delete();
		loadQueuedRoutes();
		adapter.notifyDataSetChanged();
		if(routeList.isEmpty()) {
			this.launchDraftsRoutesActivity();
		}
		
	}
	
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
}
