package org.wikicleta.activities;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

import java.util.ArrayList;
import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.helpers.SlidingMenuAndActionBarHelper;
import org.wikicleta.layers.BikeSharingOverlay;
import org.wikicleta.layers.IdentifiableOverlay;
import org.wikicleta.layers.OverlayReadyListener;
import org.wikicleta.layers.RoutesOverlay;
import org.wikicleta.models.Route;
import org.wikicleta.routes.activities.NewRouteActivity;
import org.wikicleta.routes.services.RoutesService;
import org.wikicleta.routes.services.ServiceConstructor;
import org.wikicleta.routes.services.ServiceListener;
import org.wikicleta.views.RouteOverlay;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainMapActivity extends LocationAwareMapActivity implements ServiceListener, OverlayReadyListener {
	
	protected static int ROUTE_ACTION=0;
	protected static int PLACE_ACTION=1;
	protected static int BIKE_FRIENDLY_ACTION=2;
	protected static int BICIBUS_ACTION=3;
	protected static int HIGHLIGHT_ACTION=4;

	protected LinearLayout toolBarView;
	
	protected RouteOverlay routeOverlay;

	protected AlertDialog addMenu;
	protected AlertDialog toggleLayersMenu;
	
	//Service
	protected RoutesService theService;
	ServiceConstructor serviceInitializator;
	
	protected ArrayList<Integer> overlays;
	
	@SuppressLint("UseSparseArrays")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_routes_on_map);
		setTheme(R.style.Theme_wikicleta);
		
		AppBase.currentActivity = this;
		startService(new Intent(this, RoutesService.class));

        toolBarView = (LinearLayout) findViewById(R.id.toolbar);
                            	
    	findViewById(R.id.map_add_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addMenu.show();
			}
		});
    	
    	findViewById(R.id.map_layers_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleLayersMenu.show();
			}
		});
    	
    	final ImageView centerMapViewEnabled = (ImageView) findViewById(R.id.centermap_search_button);
    	final ImageView centerMapViewDisabled = (ImageView) findViewById(R.id.centermap_search_button_enabled);

    	centerMapViewEnabled.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				locationOverlay.enableMyLocation();
				centerMapViewEnabled.setVisibility(View.GONE);
				centerMapViewDisabled.setVisibility(View.VISIBLE);
			}
		});
    	
    	centerMapViewDisabled.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				locationOverlay.disableMyLocation();
				centerMapViewEnabled.setVisibility(View.VISIBLE);
				centerMapViewDisabled.setVisibility(View.GONE);
			}
		});
    	
    	
    	
        SlidingMenuAndActionBarHelper.load(this);
				
		buildAddMenu();
		buildToggleMenu();

    	//addBuilder.setTitle("Agregar");
    	/*addBuilder.setItems(addItems, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	Log.i("WIKICLETA", String.valueOf(item));
		    	if(item == ROUTE_ACTION) {
		    		AppBase.launchActivity(NewRouteActivity.class);
		    	} else if(item == 1){
		    		
		    	}
		    	
		    }
		});*/
		
		/*final CharSequence[] layersItems = {"Rutas y bici-buses", "Lugares bici-amigables", "Bici-estacionamientos", "Puntos de riesgo", "Bicicletas Pœblicas"};
		
    	AlertDialog.Builder toggleBuilder = new AlertDialog.Builder(this);

    	toggleBuilder.setTitle("Mostrar/Ocultar capas");
    	toggleBuilder.setMultiChoiceItems(layersItems, null, new DialogInterface.OnMultiChoiceClickListener() {
    		@Override
            public void onClick(DialogInterface dialog, int item, boolean isChecked) {
    			if(item == Constants.BIKESHARING_OVERLAY) {
    				if(isChecked && !overlays.contains(Constants.BIKESHARING_OVERLAY))
    					overlays.add(Constants.BIKESHARING_OVERLAY);
    				else if(!isChecked) {
    					overlays.remove((Integer) Constants.BIKESHARING_OVERLAY);
    				}
    			}
    			
    		}
    	});
    	
    	toggleBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				toggleLayer(Constants.BIKESHARING_OVERLAY);
			}
    		
    	});
		
		toggleLayersMenu = toggleBuilder.create();*/
		overlays = new ArrayList<Integer>();
	}
	
	protected void buildToggleMenu() {
		AlertDialog.Builder toggleBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        
        View view = inflater.inflate(R.layout.dialog_toggle, null);
        view.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toggleLayer(Constants.BIKESHARING_OVERLAY);
				toggleLayer(Constants.ROUTES_OVERLAY);
				toggleLayersMenu.dismiss();
				mapView.invalidate();
				mapView.refreshDrawableState();
			}
        	
        });
        
        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_menu_title);
        dialogTitle.setTypeface(AppBase.getDefaultTypeface("Bold"));
        
        TextView routesTitle = (TextView) view.findViewById(R.id.route_title);
        routesTitle.setTypeface(AppBase.getDefaultTypeface("Bold"));
        
        TextView highlightTitle = (TextView) view.findViewById(R.id.highlight_title);
        highlightTitle.setTypeface(AppBase.getDefaultTypeface("Bold"));
        
        TextView bikeparkingTitle = (TextView) view.findViewById(R.id.bike_parking_title);
        bikeparkingTitle.setTypeface(AppBase.getDefaultTypeface("Bold"));
        
        TextView bikeSharingTitle = (TextView) view.findViewById(R.id.bikesharing_title);
        bikeSharingTitle.setTypeface(AppBase.getDefaultTypeface("Bold"));
        
        view.findViewById(R.id.route_dialog_group).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!overlays.contains(Constants.ROUTES_OVERLAY)) {
					overlays.add(Constants.ROUTES_OVERLAY);
					v.setBackgroundResource(R.drawable.menu_item_background_selected);
				} else {
					v.setBackgroundResource(R.drawable.menu_item_background);
					overlays.remove((Integer) Constants.ROUTES_OVERLAY);
				}
			}
        });
        
        view.findViewById(R.id.highlight_dialog_group).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!overlays.contains(Constants.HIGHLIGHTS_OVERLAY)) {
					overlays.add(Constants.HIGHLIGHTS_OVERLAY);
					v.setBackgroundResource(R.drawable.menu_item_background_selected);
				} else {
					v.setBackgroundResource(R.drawable.menu_item_background);
					overlays.remove((Integer) Constants.HIGHLIGHTS_OVERLAY);
				}
			}
        });
        
        view.findViewById(R.id.bike_parking_dialog_group).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!overlays.contains(Constants.BIKEPARKING_OVERLAY)) {
					overlays.add(Constants.BIKEPARKING_OVERLAY);
					v.setBackgroundResource(R.drawable.menu_item_background_selected);
				} else {
					v.setBackgroundResource(R.drawable.menu_item_background);
					overlays.remove((Integer) Constants.BIKEPARKING_OVERLAY);
				}
			}
        });
        
        view.findViewById(R.id.bikesharing_dialog_group).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!overlays.contains(Constants.BIKESHARING_OVERLAY)) {
					overlays.add(Constants.BIKESHARING_OVERLAY);
					v.setBackgroundResource(R.drawable.menu_item_background_selected);
				} else {
					v.setBackgroundResource(R.drawable.menu_item_background);
					overlays.remove((Integer) Constants.BIKESHARING_OVERLAY);
				}
			}
        });
        
        toggleBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				toggleLayer(Constants.BIKESHARING_OVERLAY);
				toggleLayer(Constants.ROUTES_OVERLAY);
			}
    		
    	});
		
    	toggleBuilder.setView(view);
    	toggleLayersMenu = toggleBuilder.create();
	}
	
	protected void buildAddMenu() {
    	AlertDialog.Builder addBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        
        View view = inflater.inflate(R.layout.dialog_add, null);
        view.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addMenu.dismiss();
			}
        	
        });
        
        TextView dialogTitle = (TextView) view.findViewById(R.id.dialog_menu_title);
        dialogTitle.setTypeface(AppBase.getDefaultTypeface("Bold"));
        
        TextView routesTitle = (TextView) view.findViewById(R.id.route_title);
        routesTitle.setTypeface(AppBase.getDefaultTypeface("Bold"));
        
        TextView highlightTitle = (TextView) view.findViewById(R.id.highlight_title);
        highlightTitle.setTypeface(AppBase.getDefaultTypeface("Bold"));
        
        TextView bicibusTitle = (TextView) view.findViewById(R.id.bicibus_title);
        bicibusTitle.setTypeface(AppBase.getDefaultTypeface("Bold"));
        
        TextView bikeparkingTitle = (TextView) view.findViewById(R.id.bike_parking_title);
        bikeparkingTitle.setTypeface(AppBase.getDefaultTypeface("Bold"));
        
        view.findViewById(R.id.route_dialog_group).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				animate(v).alpha(1).setDuration(100);
				AppBase.launchActivity(NewRouteActivity.class);
			}
        });
        
        view.findViewById(R.id.highlight_dialog_group).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addMenu.dismiss();
			}
        });
        
        view.findViewById(R.id.bicibus_dialog_group).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addMenu.dismiss();
			}
        });
        
        view.findViewById(R.id.bike_parking_dialog_group).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addMenu.dismiss();
			}
        });
        
    	addBuilder.setView(view);
		addMenu = addBuilder.create();
	}
	
	
	protected void toggleLayer(int layer) {
		if(layer == Constants.BIKESHARING_OVERLAY) {
			Overlay layerFound = findOverlayByIdentifier(Constants.BIKESHARING_OVERLAY);
			
			if(layerFound == null && overlays.contains(Constants.BIKESHARING_OVERLAY))
				new BikeSharingOverlay(this.getResources().getDrawable(R.drawable.cycling), this);
			else if(!overlays.contains(Constants.BIKESHARING_OVERLAY)) {
				BikeSharingOverlay overlay = (BikeSharingOverlay) layerFound;
				mapView.getOverlays().remove(overlay);
			}
				
		} else if(layer == Constants.ROUTES_OVERLAY) {
			boolean containedInOverlay = overlays.contains(Constants.ROUTES_OVERLAY);
			for(Route route : Route.queued()) {
				int routeId = Constants.ROUTES_OVERLAY+ (int) ((long) route.getId());
				
				Overlay layerFound = findOverlayByIdentifier(routeId);
				if(layerFound == null && containedInOverlay) {
					RoutesOverlay overlay = new RoutesOverlay(routeId, route);
					mapView.getOverlays().add(overlay);
				} else if(!containedInOverlay && layerFound != null) {
					RoutesOverlay overlay = (RoutesOverlay) layerFound;
					mapView.getOverlays().remove(overlay);
				}
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		locationOverlay.disableMyLocation();
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
	protected void onPause() {		
		super.onPause();
	}
	
	@Override
	public void afterServiceConnected(Service service) {
		if(service instanceof RoutesService) {
			this.theService = (RoutesService) service;
			theService.notifyAboutStalledRoutes();
			
			routeOverlay = new RouteOverlay(theService.routeRecorder.coordinateVector);
			mapView.getOverlays().add(routeOverlay);
		}
	}

	public Overlay findOverlayByIdentifier(int identifier) {
		Overlay layerFound = null;
		for(Overlay overlay : mapView.getOverlays()) {
			if(overlay instanceof IdentifiableOverlay) {
				IdentifiableOverlay idOverlay = (IdentifiableOverlay) overlay;
				if(idOverlay.getIdentifier() == identifier) {
					layerFound = overlay;
				}
			}
		}
		return layerFound;
	}
	
	@Override
	public void onOverlayPrepared(ItemizedOverlay<OverlayItem> overlay, int kind) {
		Overlay layerFound = findOverlayByIdentifier(kind);
		if(layerFound != null)
			this.mapView.getOverlays().remove(layerFound);
		this.mapView.getOverlays().add(overlay);
	}
}
