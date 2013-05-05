package org.wikicleta.activities;

import java.util.ArrayList;
import java.util.HashMap;
import org.wikicleta.R;
import org.wikicleta.adapters.MenuOptionsListAdapter;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.common.activities.LocationAwareMapActivity;
import org.wikicleta.helpers.SlidingMenuAndActionBarHelper;
import org.wikicleta.layers.LayersConnector;
import org.wikicleta.layers.LayersConnectorListener;
import org.wikicleta.routes.activities.NewRouteActivity;
import org.wikicleta.views.PinchableMapView.OnPanChangeListener;
import org.wikicleta.views.PinchableMapView.OnZoomChangeListener;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.nineoldandroids.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainMapActivity extends LocationAwareMapActivity implements LayersConnectorListener {
	
	protected static int ROUTE_ACTION=0;
	protected static int PLACE_ACTION=1;
	protected static int BIKE_FRIENDLY_ACTION=2;
	protected static int BICIBUS_ACTION=3;
	protected static int HIGHLIGHT_ACTION=4;
	
	protected LayersConnector layersConnector;
	protected LinearLayout toolBarView;
	
	protected AlertDialog addMenu;
	protected AlertDialog toggleLayersMenu;
	
	protected ArrayList<Integer> overlays;
	MenuOptionsListAdapter selectedLayersMenuAdapter;
	
	protected boolean userIsPanning;
	
	private ObjectAnimator uploaderAnimator;
	protected ImageView loadingLayersIcon;
	protected ImageView layersIcon;
	
	Handler handler = new Handler();
	boolean handlerRunning = false;
	
	@SuppressLint("UseSparseArrays")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_main_map);
		setTheme(R.style.Theme_wikicleta);
		overlays = new ArrayList<Integer>();
		layersConnector = new LayersConnector(this);
		
		AppBase.currentActivity = this;		
        toolBarView = (LinearLayout) findViewById(R.id.toolbar);
                            	
    	final ImageView centerMapViewEnabled = (ImageView) findViewById(R.id.centermap_search_button);
    	final ImageView centerMapViewDisabled = (ImageView) findViewById(R.id.centermap_search_button_enabled);

    	loadingLayersIcon = (ImageView) findViewById(R.id.reloading_icon);
    	
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
				
		buildToggleMenu();
    	findViewById(R.id.map_add_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addMenu.show();
			}
		});
    	
    	
    	layersIcon = (ImageView) findViewById(R.id.map_layers_button);
    	layersIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleLayersMenu.show();
			}
		});
		
		mapView.setOnZoomChangeListener(new OnZoomChangeListener() {

			@Override
			public void onZoomChange(MapView view, int newZoom, int oldZoom) {
				reloadActiveLayers();
			}
			
		});
		
		mapView.setOnPanChangeListener(new OnPanChangeListener() {

			@Override
			public void onPanChange(MapView view, GeoPoint newCenter,
					GeoPoint oldCenter) {
				reloadActiveLayers();
			}			
		});

	}
	
	public void reloadActiveLayers() {
		toggleLayers(selectedLayersMenuAdapter.getSelectedValuesForPositions());
	}
	
	Runnable cancelableDelayedLoadingAnimation = new Runnable() {
		   @Override
		   public void run() {
			   if(!handlerRunning) {
				   handlerRunning = true;
				   uploaderAnimator = ObjectAnimator.ofFloat(loadingLayersIcon, "rotation", 0, 360);
				   uploaderAnimator.setRepeatCount(ObjectAnimator.INFINITE);
				   uploaderAnimator.setDuration(1500);
				   layersIcon.setVisibility(View.GONE);
				   loadingLayersIcon.setVisibility(View.VISIBLE);
			       uploaderAnimator.start();
			   } 
		   }
	};
	
	@Override
	public void showLoadingState() {
		handler.postDelayed(cancelableDelayedLoadingAnimation, 10L);        
	}
	
	@Override
	public void hideLoadingState() {
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
		    	layersIcon.setVisibility(View.VISIBLE);
		    	loadingLayersIcon.setVisibility(View.GONE);
				if(uploaderAnimator != null)
					uploaderAnimator.cancel();
				handlerRunning = false;
			}
			
		});
	}
	
	protected void buildToggleMenu() {
		AlertDialog.Builder toggleBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_toggle, null);
        
        Integer[] layers = {
        		Constants.ROUTES_OVERLAY, 
        		Constants.BIKE_PARKING_OVERLAY, 
        		Constants.BIKE_WORKSHOPS_AND_STORES_OVERLAY,
        		Constants.BIKE_SHARING_OVERLAY,
        		Constants.TIPS_OVERLAY};
        
        TextView menuTitle = (TextView) view.findViewById(R.id.dialog_menu_title);
        menuTitle.setTypeface(AppBase.getTypefaceStrong());
        
        view.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				toggleLayersMenu.dismiss();
			}
        	
        });
        
	    final ListView listview = (ListView) view.findViewById(R.id.layers_menu_list_view);
	    selectedLayersMenuAdapter = new MenuOptionsListAdapter(this, layers);
	    listview.setAdapter(selectedLayersMenuAdapter);
	    listview.getCheckedItemPositions();
	    listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

	    listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterParent, View view, int position, long id) {
				selectedLayersMenuAdapter.setSelectedPosition(position);
				toggleLayersMenu.dismiss();
				toggleLayers(selectedLayersMenuAdapter.getSelectedValuesForPositions());
			}

	    });

    	toggleBuilder.setView(view);
    	toggleLayersMenu = toggleBuilder.create();
    	toggleLayersMenu.setOnShowListener(new OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {
				Log.e("WIKICLETA", String.valueOf(overlays.contains(Constants.BIKE_SHARING_OVERLAY)));
			}
    		
    	});
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
        dialogTitle.setTypeface(AppBase.getTypefaceStrong());
        
        TextView routesTitle = (TextView) view.findViewById(R.id.route_title);
        routesTitle.setTypeface(AppBase.getTypefaceStrong());
        
        TextView highlightTitle = (TextView) view.findViewById(R.id.tips_title);
        highlightTitle.setTypeface(AppBase.getTypefaceStrong());
        
        TextView bicibusTitle = (TextView) view.findViewById(R.id.bicibus_title);
        bicibusTitle.setTypeface(AppBase.getTypefaceStrong());
        
        TextView bikeparkingTitle = (TextView) view.findViewById(R.id.bike_parking_title);
        bikeparkingTitle.setTypeface(AppBase.getTypefaceStrong());
        
        TextView bikeRepairTitle = (TextView) view.findViewById(R.id.bike_repair_title);
        bikeRepairTitle.setTypeface(AppBase.getTypefaceStrong());
        
        view.findViewById(R.id.route_dialog_group).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addMenu.dismiss();
				AppBase.launchActivity(NewRouteActivity.class);
				finish();
			}
        });
        
        view.findViewById(R.id.tips_dialog_group).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addMenu.dismiss();
				AppBase.launchActivity(org.wikicleta.tips.activities.ModifyingActivity.class);
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
				AppBase.launchActivity(org.wikicleta.bikeparkings.activities.ModifyingActivity.class);
			}
        });
        
        view.findViewById(R.id.bike_repair_dialog_group).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addMenu.dismiss();
			}
        });
        
    	addBuilder.setView(view);
		addMenu = addBuilder.create();
	}
	
	protected void toggleLayers(ArrayList<Integer> layers) {
		
		mapView.getOverlays().clear();
		mapView.getOverlays().add(locationOverlay);
		
		if(layers.isEmpty())
			this.overlayFinishedLoading(false);
		for(Integer layer : layers) {
			if(layer == Constants.BIKE_SHARING_OVERLAY)
				mapView.getOverlays().add(layersConnector.getBikeSharingOverlay());
			else if(layer == Constants.TIPS_OVERLAY)
				mapView.getOverlays().add(layersConnector.getTipsOverlay());
			else if(layer == Constants.BIKE_PARKING_OVERLAY)
				mapView.getOverlays().add(layersConnector.getParkingsOverlay());
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
		this.buildAddMenu();
	}
	
	@Override
	public void overlayFinishedLoading(final boolean status) {
		this.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mapView.invalidate();
				mapView.refreshDrawableState();
				hideLoadingState();
			}
			
		});

	}

	@Override
	public HashMap<String, String> getCurrentViewport() {
		HashMap<String, String> viewport = new HashMap<String, String>();
		
		GeoPoint leftBottom = (GeoPoint) mapView.getProjection().fromPixels(0, mapView.getHeight());
		GeoPoint rightTop = (GeoPoint) mapView.getProjection().fromPixels(mapView.getWidth(), 0);
		viewport.put("sw", String.valueOf(leftBottom.getLatitudeE6()/1E6).concat(",").concat(String.valueOf(leftBottom.getLongitudeE6()/1E6)));
		viewport.put("ne", String.valueOf(rightTop.getLatitudeE6()/1E6).concat(",").concat(String.valueOf(rightTop.getLongitudeE6()/1E6)));

		return viewport;
	}

	@Override
	public Activity getActivity() {
		return this;
	}
	
	
}
