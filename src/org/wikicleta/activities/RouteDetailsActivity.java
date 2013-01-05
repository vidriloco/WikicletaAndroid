package org.wikicleta.activities;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.helpers.NotificationBuilder;
import org.wikicleta.models.Route;
import org.wikicleta.services.RoutesService;
import org.wikicleta.services.ServiceConstructor;
import org.wikicleta.services.ServiceListener;
import org.wikicleta.views.PinchableMapView;
import org.wikicleta.views.RouteOverlay;

import android.app.AlertDialog;
import android.app.Service;
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
import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

public class RouteDetailsActivity extends LocationAwareMapActivity implements ServiceListener {
	private PinchableMapView mapView;
	private LinearLayout bottomToolBarView;
	private LinearLayout topToolBarView;
	
	protected NotificationBuilder notification;
	protected RouteOverlay routeOverlay;
	public static Route currentRoute;
	public AlertDialog.Builder alertDialog;
	
	//Service
	protected RoutesService theService;
	ServiceConstructor serviceInitializator;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState,R.layout.activity_route_details);
        AppBase.currentActivity = this;
        this.centerMapOnCurrentLocationByDefault = false;
        
        if(currentRoute == null)
        	AppBase.launchActivity(RoutesActivity.class);
               
        this.mapView = (PinchableMapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(false);
        
        drawRoutePath();
        drawControls();
        
        TextView routeNameView = (TextView) findViewById(R.id.route_name);
    	routeNameView.setText(currentRoute.name);
        this.setMapToLocation(currentRoute.getStartingLocation());
        this.notification = new NotificationBuilder(this);
	}
	
	public void drawControls() {
		if(currentRoute != null && currentRoute.isDraft()) {
	        final ImageView closeMoreIcon = (ImageView) findViewById(R.id.close_button);
	        closeMoreIcon.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					Intent intentActivity = new Intent(AppBase.currentActivity, ActivitiesFeedActivity.class);
					AppBase.currentActivity.startActivity(intentActivity);
				}
		    	
		    });
			
	        topToolBarView = (LinearLayout) findViewById(R.id.top_panel_route_status);
	        bottomToolBarView = (LinearLayout) findViewById(R.id.bottom_panel_route_status_actions);
	        
	        // Save button preparation
	        findViewById(R.id.route_save_button).setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {		
					theService.addRouteForUpload(currentRoute);

					AppBase.launchActivity(ActivitiesFeedActivity.class);
				}
		    	
		    });
	        
	        alertDialog = new AlertDialog.Builder(this);
	        
	        // Discard button preparation
	        findViewById(R.id.route_discard_button).setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					alertDialog.setTitle("Pregunta");
					alertDialog.setMessage("�Deseas descartar esta ruta?");
					alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							currentRoute.delete();
							notification.addNotification(Constants.ROUTES_MANAGEMENT_NOTIFICATION_ID, 
	    							getString(R.string.app_name), getString(R.string.route_being_destroyed), null);
							
							Intent intentActivity = new Intent(AppBase.currentActivity, ActivitiesFeedActivity.class);
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
	protected void onStart() {
		super.onStart();
		serviceInitializator = new ServiceConstructor(this);
        serviceInitializator.start(RoutesService.class);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
        serviceInitializator.stop();
	}
	
	@Override
	public void afterServiceConnected(Service service) {
		if(service instanceof RoutesService)
			this.theService = (RoutesService) service;
	}
	
	
}
