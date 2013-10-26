package org.wikicleta.activities;

import java.util.ArrayList;
import org.wikicleta.R;
import org.wikicleta.activities.common.LocationAwareMapWithMarkersActivity;
import org.wikicleta.adapters.MenuOptionsListAdapter;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.helpers.SlidingMenuBuilder;
import org.wikicleta.interfaces.MarkerInterface;
import org.wikicleta.models.CycleStation;
import org.wikicleta.models.LightPOI;
import org.wikicleta.models.Parking;
import org.wikicleta.models.Route;
import org.wikicleta.models.Tip;
import org.wikicleta.models.Workshop;
import org.wikicleta.routing.BikesSharing;
import org.wikicleta.routing.Parkings;
import org.wikicleta.routing.Routes;
import org.wikicleta.routing.Tips;
import org.wikicleta.routing.Workshops;
import org.wikicleta.views.CycleStationViews;
import org.wikicleta.views.ParkingViews;
import org.wikicleta.views.RouteViews;
import org.wikicleta.views.TipViews;
import org.wikicleta.views.WorkshopViews;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class DiscoverActivity extends LocationAwareMapWithMarkersActivity {
	
	protected static int ROUTE_ACTION=0;
	protected static int PLACE_ACTION=1;
	protected static int BIKE_FRIENDLY_ACTION=2;
	protected static int BICIBUS_ACTION=3;
	protected static int HIGHLIGHT_ACTION=4;
	
	MenuOptionsListAdapter selectedLayersMenuAdapter;
	protected ImageView rightMenuToggler;
	protected ImageView returnIcon;
	protected LinearLayout toggableGroup;
	
	public static LightPOI selectedPoi;
	
	@SuppressLint("UseSparseArrays")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(selectedPoi != null)
			this.attemptCenterOnLocationAtStart = false;
		super.onCreate(savedInstanceState, R.layout.activity_main_map);
		setTheme(R.style.Theme_wikicleta);
		
		// Assign icons
		returnIcon = (ImageView) this.findViewById(R.id.return_button);

		toggableGroup = (LinearLayout) this.findViewById(R.id.toggable_group);
		
    	final SlidingMenu rightMenu = SlidingMenuBuilder.loadOnRight(this);
		rightMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
    	
		buildToggleMenu(rightMenu);

    	rightMenuToggler = (ImageView) findViewById(R.id.right_menu_toggler);
    	rightMenuToggler.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rightMenu.toggle();
			}
		});
    	
    	rightMenu.setOnClosedListener(new OnClosedListener() {

			@Override
			public void onClosed() {
				reloadActiveLayersWithMapClearing();
			}
    		
    	});
		
    	
    	returnIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppBase.launchActivity(RootActivity.class);
			}
    		
    	});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.reloadActiveLayersWithMapClearing();
    	centerMapOnPointWithLayerEnabled();
	}
	
	public void centerMapOnPointWithLayerEnabled() {
		if(selectedPoi != null) {
			this.firstLocationReceived = true;
			this.selectedLayersMenuAdapter.setSelectedString(selectedPoi.kind, true);
			this.map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(selectedPoi.lat, selectedPoi.lon), 18));
			selectedPoi = null;
		}
	}
	
	public void reloadActiveLayersWithMapClearing() {
		map.clear();
		markers.clear();
		this.reloadActiveLayers();
	}
	
	public void reloadActiveLayers() {
		toggleLayers(selectedLayersMenuAdapter.getSelectedValuesForPositions());
	}
	
	protected void buildToggleMenu(SlidingMenu menu) {
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.layers_menu_list, null);
        
        final Integer[] layers = {
        		Constants.ROUTES_OVERLAY, 
        		Constants.BIKE_PARKING_OVERLAY, 
        		Constants.BIKE_WORKSHOPS_AND_STORES_OVERLAY,
        		Constants.BIKE_SHARING_OVERLAY,
        		//Constants.CYCLEPATHS_OVERLAY,
        		Constants.TIPS_OVERLAY};
        
	    final ListView listview = (ListView) view.findViewById(R.id.layers_menu_list_view);
	    selectedLayersMenuAdapter = new MenuOptionsListAdapter(this, layers);
	    listview.setAdapter(selectedLayersMenuAdapter);
	    listview.getCheckedItemPositions();
	    listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

	    listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterParent, View view, int position, long id) {
				selectedLayersMenuAdapter.setSelectedPosition(position);
			}

	    });

	    menu.setMenu(view);
	}
	
	protected void toggleLayers(ArrayList<Integer> layers) {
		if(layers.isEmpty())
			this.overlayFinishedLoading(false);
		for(Integer layer : layers) {
            if(layer == Constants.BIKE_SHARING_OVERLAY) {
            	this.showLoadingState();
            	BikesSharing bikesSharing = new BikesSharing();
            	BikesSharing.GetEcobici bikesSharingFetcher = bikesSharing.new GetEcobici(this);
            	bikesSharingFetcher.execute();
            } else if(layer == Constants.TIPS_OVERLAY) {
            	this.showLoadingState();
            	Tips tips = new Tips();
            	Tips.Get tipsFetcher = tips.new Get(this);
            	tipsFetcher.execute();
            } else if(layer == Constants.BIKE_PARKING_OVERLAY) {
            	this.showLoadingState();
            	Parkings parkings = new Parkings();
            	Parkings.Get parkingsFetcher = parkings.new Get(this);
            	parkingsFetcher.execute();
            } else if(layer == Constants.BIKE_WORKSHOPS_AND_STORES_OVERLAY) {
            	this.showLoadingState();
            	Workshops workshops = new Workshops();
        		Workshops.Get workshopsFetcher = workshops.new Get(this);
            	workshopsFetcher.execute();
            } else if(layer == Constants.ROUTES_OVERLAY) {
            	this.showLoadingState();
            	Routes routes = new Routes();
            	Routes.Get routesFetcher = routes.new Get(this);
            	routesFetcher.execute();
            } 
		}
	}
	
	@Override
	public boolean onMarkerClick(Marker marker) {
		MarkerInterface markerIn = (MarkerInterface) markers.get(marker.getPosition());
		if(markerIn instanceof Workshop)
			WorkshopViews.buildViewForWorkshop(this, (Workshop) markerIn);
		else if(markerIn instanceof Parking)
			ParkingViews.buildViewForParking(this, (Parking) markerIn);
		else if(markerIn instanceof Tip)
			TipViews.buildViewForTip(this, (Tip) markerIn);
		else if(markerIn instanceof CycleStation)
			CycleStationViews.buildViewForCycleStation(this, (CycleStation) markerIn);
		else if(markerIn instanceof Route)
			RouteViews.buildViewDetails(this, (Route) markerIn);
		return true;
	}
	
	
}
