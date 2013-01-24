package org.wikicleta.activities;

import java.util.ArrayList;
import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.helpers.SlidingMenuAndActionBarHelper;
import org.wikicleta.layers.BikeSharingOverlay;
import org.wikicleta.layers.IdentifiableOverlay;
import org.wikicleta.layers.OverlayReadyListener;
import org.wikicleta.routes.activities.NewRouteActivity;
import org.wikicleta.routes.services.RoutesService;
import org.wikicleta.routes.services.ServiceConstructor;
import org.wikicleta.routes.services.ServiceListener;
import org.wikicleta.views.RouteOverlay;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainMapActivity extends LocationAwareMapActivity implements ServiceListener, OverlayReadyListener {
	
	protected static int ROUTE_ACTION=0;
	protected static int PLACE_ACTION=1;
	protected static int BIKE_FRIENDLY_ACTION=2;
	protected static int BICIBUS_ACTION=3;
	protected static int HIGHLIGHT_ACTION=4;

	protected RelativeLayout titleBarView;
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

        titleBarView = (RelativeLayout) findViewById(R.id.titlebar);
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
    	
        SlidingMenuAndActionBarHelper.load(this);
        
		AnimatorSet animationSet = new AnimatorSet();
		animationSet.playTogether(ObjectAnimator.ofFloat(toolBarView, "alpha", 1),
				ObjectAnimator.ofFloat(toolBarView, "translationY", -Constants.DY_TRANSLATION));
		animationSet.start();
		
		final CharSequence[] addItems = {"Ruta", "Lugar bici-amigable", "Bici-estacionamiento", "Bici-bus", "Punto de riesgo"};
		
    	AlertDialog.Builder addBuilder = new AlertDialog.Builder(this);

    	addBuilder.setTitle("Agregar");
    	addBuilder.setItems(addItems, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	Log.i("WIKICLETA", String.valueOf(item));
		    	if(item == ROUTE_ACTION) {
		    		AppBase.launchActivity(NewRouteActivity.class);
		    	} else if(item == 1){
		    		
		    	}
		    	
		    }
		});
		addMenu = addBuilder.create();
		
		final CharSequence[] layersItems = {"Rutas y bici-buses", "Lugares bici-amigables", "Bici-estacionamientos", "Puntos de riesgo", "Bicicletas Pœblicas"};
		
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
		
		toggleLayersMenu = toggleBuilder.create();
		overlays = new ArrayList<Integer>();
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
	protected void onPause() {
		super.onPause();
		theService.disableLocationManager();
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
