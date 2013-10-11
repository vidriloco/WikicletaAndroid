package org.wikicleta.activities.routes;

import org.wikicleta.R;
import org.wikicleta.activities.DiscoverActivity;
import org.wikicleta.activities.common.ActivityWithLocationAwareMap;
import org.wikicleta.common.AppBase;
import org.wikicleta.helpers.NotificationBuilder;
import org.wikicleta.models.Route;
import org.wikicleta.services.routes.RouteTrackingService;
import org.wikicleta.services.routes.ServiceConstructor;
import org.wikicleta.services.routes.ServiceListener;
import org.wikicleta.layers.RouteOverlay;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RouteDetailsActivity extends ActivityWithLocationAwareMap implements ServiceListener {
	private LinearLayout bottomToolBarView;
	private LinearLayout topToolBarView;
	
	protected NotificationBuilder notification;
	protected RouteOverlay routesOverlay;
	public Route currentRoute;
	public AlertDialog.Builder alertDialog;
	protected AlertDialog toggleLayersMenu;
	
	//Service
	protected RouteTrackingService theService;
	ServiceConstructor serviceInitializator;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState,R.layout.activity_route_details);
		setTheme(R.style.Theme_wikicleta);
		
        AppBase.currentActivity = this;
        this.centerMapOnCurrentLocationByDefault = false;
        
        Bundle bundle = this.getIntent().getExtras();
        currentRoute = Route.findById(bundle.getLong("routeId"));
        
        if(currentRoute == null)
        	AppBase.launchActivity(DiscoverActivity.class);
        
        TextView textView = (TextView) this.findViewById(R.id.route_name);
        textView.setText(currentRoute.name);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//this.locationOverlay.disableMyLocation();
	}
	
	public static int queuedRoutesCount() {
		return 0;
	}
	
	public void drawControls() {
		if(currentRoute != null && !currentRoute.isDraft) {
	        /*final ImageView closeMoreIcon = (ImageView) findViewById(R.id.close_button);
	        closeMoreIcon.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					Intent intentActivity = new Intent(AppBase.currentActivity, UserProfileActivity.class);
					AppBase.currentActivity.startActivity(intentActivity);
				}
		    	
		    });*/
			/*
	        topToolBarView = (LinearLayout) findViewById(R.id.top_panel_route_status);
	        bottomToolBarView = (LinearLayout) findViewById(R.id.bottom_panel_route_status_actions);
	        
	        // Save button preparation
	        findViewById(R.id.route_save_button).setOnClickListener(new OnClickListener() {
	        	
				public void onClick(View arg0) {		
					Bundle bundle = new Bundle();
					bundle.putString("fragment", ActivityFragment.class.getName());
					notification.addNotification(Constants.ROUTES_MANAGEMENT_NOTIFICATION_ID, 
					getString(R.string.app_name), getString(R.string.route_being_sent), null, Ticker.MESSAGE);
					AppBase.launchActivityWithBundle(UserProfileActivity.class, bundle);
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
							AppBase.launchActivity(MainMapActivity.class);
						}
					});
					alertDialog.setNegativeButton("No", null);
					alertDialog.setNeutralButton(null, null);
					alertDialog.show();
				}
		    	
		    });
	        */
		} else {
			/*final ImageView closeMoreIcon = (ImageView) findViewById(R.id.close_button);
	        closeMoreIcon.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					Intent intentActivity = new Intent(AppBase.currentActivity, RoutesActivity.class);
					AppBase.currentActivity.startActivity(intentActivity);
				}
		    	
		    });*/
	        
			final CharSequence[] layersItems = {"Detalles de Ruta", "Puntos de riesgo", "Lugares bici-amigables"};
			boolean[] layersSelected = {true, false, false};
	    	AlertDialog.Builder toggleBuilder = new AlertDialog.Builder(this);

	    	toggleBuilder.setTitle("Mostrar/Ocultar");
	    	toggleBuilder.setMultiChoiceItems(layersItems, layersSelected, new DialogInterface.OnMultiChoiceClickListener() {
	    		@Override
	            public void onClick(DialogInterface dialog, int item, boolean isChecked) {
	    			if(item == 0) {
	    				if(isChecked)
	    					topToolBarView.setVisibility(View.VISIBLE);
	    				else
	    					topToolBarView.setVisibility(View.GONE);
	    			}
	    		}
	    	});
	    	
			toggleLayersMenu = toggleBuilder.create();

		}
		
        topToolBarView.setVisibility(View.VISIBLE);
        bottomToolBarView.setVisibility(View.VISIBLE);
	}

	@Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.route_details_layers_menu , menu);
    }
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {

		if(item.getItemId() == R.id.route_layers_basic_info_item) {
        	this.topToolBarView.setVisibility(View.VISIBLE);
		} else if(item.getItemId() == R.id.route_layers_none_item) {
        	this.topToolBarView.setVisibility(View.GONE);
		}
        
        return true;
    }
	
	protected void drawRoutePath() {
		if(currentRoute != null) {
			//routesOverlay = new RouteOverlay(((int) (long) currentRoute.getId()), null);
			//mapView.getOverlays().add(routesOverlay);
			
			//routesOverlay.detailedView = true;
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		serviceInitializator = new ServiceConstructor(this);
        serviceInitializator.start(RouteTrackingService.class);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
        serviceInitializator.stop();
	}
	
	@Override
	public void afterServiceConnected(Service service) {
		if(service instanceof RouteTrackingService)
			this.theService = (RouteTrackingService) service;
	}
}
