package com.wikicleta.activities;

import org.mobility.wikicleta.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wikicleta.async.AsyncTaskManager;
import com.wikicleta.async.RouteSavingAsync;
import com.wikicleta.common.AppBase;
import com.wikicleta.common.Constants;
import com.wikicleta.helpers.NotificationBuilder;
import com.wikicleta.models.Route;
import com.wikicleta.views.PinchableMapView;
import com.wikicleta.views.RouteOverlay;
import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

public class RouteDetailsActivity extends LocationAwareMapActivity {
	private PinchableMapView mapView;
	private LinearLayout bottomToolBarView;
	private LinearLayout topToolBarView;
	
	protected NotificationBuilder notification;
	protected RouteOverlay routeOverlay;
	public static Route currentRoute;
	public AlertDialog.Builder alertDialog;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState,R.layout.activity_route_details);
        AppBase.currentActivity = this;
        this.centerMapOnCurrentLocationByDefault = false;
        
        TextView routeNameView = (TextView) findViewById(R.id.route_name);
        
        if(currentRoute != null)
        	routeNameView.setText(currentRoute.name);
               
        this.mapView = (PinchableMapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(false);
        
        drawRoutePath();
        drawControls();
        
        this.setMapToLocation(currentRoute.getStartingLocation());
        this.notification = new NotificationBuilder(this);
	}
	
	public void drawControls() {
		if(currentRoute != null && currentRoute.isDraft()) {
	        final ImageView closeMoreIcon = (ImageView) findViewById(R.id.close_button);
	        closeMoreIcon.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					Intent intentActivity = new Intent(AppBase.currentActivity, ActivityFeedsActivity.class);
					AppBase.currentActivity.startActivity(intentActivity);
				}
		    	
		    });
			
	        topToolBarView = (LinearLayout) findViewById(R.id.top_panel_route_status);
	        bottomToolBarView = (LinearLayout) findViewById(R.id.bottom_panel_route_status_actions);
	        
	        // Save button preparation
	        findViewById(R.id.route_save_button).setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					RouteSavingAsync routeSavingTask = new RouteSavingAsync(currentRoute);
					AsyncTaskManager.pushNewRouteTask(routeSavingTask);
					
					notification.addNotification(Constants.ROUTES_SYNCING_NOTIFICATIONS_ID, 
							getString(R.string.app_name), getString(R.string.route_being_sent), null);
					
					Intent intentActivity = new Intent(AppBase.currentActivity, ActivityFeedsActivity.class);
					AppBase.currentActivity.startActivity(intentActivity);
				}
		    	
		    });
	        
	        alertDialog = new AlertDialog.Builder(this);
	        
	        // Discard button preparation
	        findViewById(R.id.route_discard_button).setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					alertDialog.setTitle("Pregunta");
					alertDialog.setMessage("ÀDeseas descartar esta ruta?");
					alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							currentRoute.delete();
							notification.addNotification(Constants.ROUTES_SYNCING_NOTIFICATIONS_ID, 
	    							getString(R.string.app_name), getString(R.string.route_being_destroyed), null);
							
							Intent intentActivity = new Intent(AppBase.currentActivity, ActivityFeedsActivity.class);
							AppBase.currentActivity.startActivity(intentActivity);
						}
					});
					alertDialog.setNegativeButton("No", null);
					alertDialog.setNeutralButton(null, null);
					alertDialog.show();
				}
		    	
		    });
	        
		} else {
			final ImageView closeMoreIcon = (ImageView) findViewById(R.id.close_button);
	        closeMoreIcon.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					Intent intentActivity = new Intent(AppBase.currentActivity, RoutesActivity.class);
					AppBase.currentActivity.startActivity(intentActivity);
				}
		    	
		    });
	        
	        topToolBarView = (LinearLayout) findViewById(R.id.top_panel_extra_route_info);
	        bottomToolBarView = (LinearLayout) findViewById(R.id.bottom_panel_route_actions);
	        
	        // Buttons preparations
	        final ImageView layersMenuButton = (ImageView) findViewById(R.id.route_layers_button);
	        layersMenuButton.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					openContextMenu(layersMenuButton);
				}
		    	
		    });
	        registerForContextMenu(layersMenuButton);
		}
		
        topToolBarView.setVisibility(View.VISIBLE);
        bottomToolBarView.setVisibility(View.VISIBLE);
        animate(topToolBarView).alpha((float) 0.8);
        animate(bottomToolBarView).alpha((float) 0.8).translationYBy(-Constants.DY_TRANSLATION);
	}

	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.route_details_layers_menu , menu);
    }
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.route_layers_basic_info_item:
            	this.topToolBarView.setVisibility(View.VISIBLE);
                break;
            case R.id.route_layers_highlights_item:
                break;
            case R.id.route_layers_none_item:
            	this.topToolBarView.setVisibility(View.GONE);
            	break;
            case R.id.route_layers_places_item:
            	break;
        }
        
        return true;
    }
	
	protected void drawRoutePath() {
		if(currentRoute != null) {
			routeOverlay = new RouteOverlay(currentRoute.instants());
			mapView.getOverlays().add(routeOverlay);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
