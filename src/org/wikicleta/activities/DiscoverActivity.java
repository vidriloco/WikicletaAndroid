package org.wikicleta.activities;

import java.util.ArrayList;
import java.util.HashMap;
import org.wikicleta.R;
import org.wikicleta.activities.common.LocationAwareMapWithControlsActivity;
import org.wikicleta.adapters.MenuOptionsListAdapter;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.common.Toasts;
import org.wikicleta.helpers.SimpleAnimatorListener;
import org.wikicleta.helpers.SlidingMenuBuilder;
import org.wikicleta.layers.common.LayersConnectorListener;
import org.wikicleta.models.CycleStation;
import org.wikicleta.models.MarkerInterface;
import org.wikicleta.models.Parking;
import org.wikicleta.models.Tip;
import org.wikicleta.models.Workshop;
import org.wikicleta.routing.BikesSharing;
import org.wikicleta.routing.Parkings;
import org.wikicleta.routing.Tips;
import org.wikicleta.routing.Workshops;
import org.wikicleta.views.CycleStationViews;
import org.wikicleta.views.ParkingViews;
import org.wikicleta.views.TipViews;
import org.wikicleta.views.WorkshopViews;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DiscoverActivity extends LocationAwareMapWithControlsActivity implements LayersConnectorListener, OnMarkerClickListener {
	
	protected static int ROUTE_ACTION=0;
	protected static int PLACE_ACTION=1;
	protected static int BIKE_FRIENDLY_ACTION=2;
	protected static int BICIBUS_ACTION=3;
	protected static int HIGHLIGHT_ACTION=4;
	
	protected AlertDialog addMenu;
	
	MenuOptionsListAdapter selectedLayersMenuAdapter;
	
	protected boolean userIsPanning;
	
	private ObjectAnimator uploaderAnimator;
	private ObjectAnimator uploaderContainerAnimator;
	protected LinearLayout loadingLayersContainer;
	protected ImageView loadingLayersIcon;
	protected ImageView rightMenuToggler;
	
	protected ImageView returnIcon;
	
	protected LinearLayout toggableGroup;
	
	Handler handler = new Handler();
	boolean handlerRunning = false;
	protected HashMap<Marker, MarkerInterface> markers;
	
	@SuppressLint("UseSparseArrays")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_main_map);
		setTheme(R.style.Theme_wikicleta);
    	
		assignToggleActionsForAutomapCenter();
		
		AppBase.currentActivity = this;
		
		// Assign icons
		loadingLayersIcon = (ImageView) this.findViewById(R.id.spinner_indicator);
		loadingLayersContainer = (LinearLayout) this.findViewById(R.id.mutable_box_container);
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
				toggleLayers(selectedLayersMenuAdapter.getSelectedValuesForPositions());
			}
    		
    	});
		
    	returnIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppBase.launchActivity(RootActivity.class);
			}
    		
    	});
    	
    	// Listeners for map
    	map.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition position) {
				reloadActiveLayers();
			}
    		
    	});
   	 	map.setOnMarkerClickListener(this);    
		
		map.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				ObjectAnimator animator = null;
				if(toggableGroup.getAlpha() == 0) {
					animator = ObjectAnimator.ofFloat(toggableGroup, "alpha", 0, 1, 1).setDuration(1000);
					animator.addListener(new SimpleAnimatorListener() {
						@Override
						public void onAnimationStart(Animator animation) {
							toggableGroup.setVisibility(View.VISIBLE);
						}
					});
				} else {
					animator = ObjectAnimator.ofFloat(toggableGroup, "alpha", 1, 0, 0).setDuration(1000);
					animator.addListener(new SimpleAnimatorListener() {
						@Override
						public void onAnimationEnd(Animator animation) {
							toggableGroup.setVisibility(View.GONE);
						}
					});
				}
				animator.start();
			}
			
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	public void reloadActiveLayers() {
		toggleLayers(selectedLayersMenuAdapter.getSelectedValuesForPositions());
	}
	
	Runnable cancelableDelayedLoadingAnimation = new Runnable() {
		   @Override
		   public void run() {
			   if(!handlerRunning) {
				   handlerRunning = true;
				   uploaderContainerAnimator = ObjectAnimator.ofFloat(loadingLayersContainer, "alpha", 0, 1, 1);
				   
				   uploaderAnimator = ObjectAnimator.ofFloat(loadingLayersIcon, "rotation", 0, 360);
				   uploaderAnimator.setRepeatCount(ObjectAnimator.INFINITE);
				   uploaderAnimator.setDuration(1500);
			       uploaderAnimator.start();
			       uploaderAnimator.addListener(new SimpleAnimatorListener() {

						@Override
						public void onAnimationCancel(Animator animation) {
							ObjectAnimator.ofFloat(loadingLayersContainer, "alpha", 1, 0, 0).setDuration(1000).start();
						}
						
					});
				   uploaderContainerAnimator.setDuration(1500);
			       uploaderContainerAnimator.start();
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
				if(uploaderAnimator != null) {
					uploaderContainerAnimator.cancel();
					uploaderAnimator.cancel();
					
				}
				handlerRunning = false;
			}
			
		});
	}
	
	protected void buildToggleMenu(SlidingMenu menu) {
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.layers_menu_list, null);
        
        final Integer[] layers = {
        		Constants.ROUTES_OVERLAY, 
        		Constants.BIKE_PARKING_OVERLAY, 
        		Constants.BIKE_WORKSHOPS_AND_STORES_OVERLAY,
        		Constants.BIKE_SHARING_OVERLAY,
        		Constants.CYCLEPATHS_OVERLAY,
        		Constants.TIPS_OVERLAY};
        
	    final ListView listview = (ListView) view.findViewById(R.id.layers_menu_list_view);
	    selectedLayersMenuAdapter = new MenuOptionsListAdapter(this, layers);
	    listview.setAdapter(selectedLayersMenuAdapter);
	    listview.getCheckedItemPositions();
	    listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

	    listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterParent, View view, int position, long id) {
				if(layers[position] == Constants.ROUTES_OVERLAY) {
					Toasts.showToastWithMessage(DiscoverActivity.this, R.string.not_implemented_yet, R.drawable.hand_icon);
				} else {
					selectedLayersMenuAdapter.setSelectedPosition(position);
				}
			}

	    });

	    menu.setMenu(view);
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
				//addMenu.dismiss();
				//AppBase.launchActivity(NewRouteActivity.class);
				//finish();
				addMenu.dismiss();
				Toasts.showToastWithMessage(DiscoverActivity.this, R.string.not_implemented_yet, R.drawable.hand_icon);
			}
        });
        
        view.findViewById(R.id.tips_dialog_group).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addMenu.dismiss();
				AppBase.launchActivity(org.wikicleta.activities.tips.ModifyingActivity.class);
			}
        });
        
        view.findViewById(R.id.bicibus_dialog_group).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addMenu.dismiss();
				Toasts.showToastWithMessage(DiscoverActivity.this, R.string.not_implemented_yet, R.drawable.hand_icon);
			}
        });
        
        view.findViewById(R.id.bike_parking_dialog_group).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addMenu.dismiss();
				AppBase.launchActivity(org.wikicleta.activities.parkings.ModifyingActivity.class);
			}
        });
        
        view.findViewById(R.id.bike_repair_dialog_group).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addMenu.dismiss();
				AppBase.launchActivity(org.wikicleta.activities.workshops.ModifyingActivity.class);

			}
        });
        
    	addBuilder.setView(view);
		addMenu = addBuilder.create();
		getWindow().setBackgroundDrawableResource(android.R.color.transparent); 
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
				Handler handlerTimer = new Handler();
		        handlerTimer.postDelayed(new Runnable(){
		            public void run() {
						hideLoadingState();
		          }}, 2000);
			}
			
		});

	}

	@Override
	public HashMap<String, String> getCurrentViewport() {
		HashMap<String, String> viewport = new HashMap<String, String>();
		
		RelativeLayout mapContainer = (RelativeLayout) findViewById(R.id.map_container);
		
		LatLng leftBottom = (LatLng) map.getProjection().fromScreenLocation(new Point(0, mapContainer.getHeight()));
		LatLng rightTop = (LatLng) map.getProjection().fromScreenLocation(new Point(mapContainer.getWidth(), 0));
		viewport.put("sw", String.valueOf(leftBottom.latitude).concat(",").concat(String.valueOf(leftBottom.longitude)));
		viewport.put("ne", String.valueOf(rightTop.latitude).concat(",").concat(String.valueOf(rightTop.longitude)));
		return viewport;
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	protected void toggleLayers(ArrayList<Integer> layers) {
		markers = new HashMap<Marker, MarkerInterface>();
		map.clear();
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
            }
		}
	}
	
	@Override
	public boolean onMarkerClick(Marker marker) {
		MarkerInterface markerIn = (MarkerInterface) markers.get(marker);
		if(markerIn instanceof Workshop)
			WorkshopViews.buildViewForWorkshop(this, (Workshop) markerIn);
		else if(markerIn instanceof Parking)
			ParkingViews.buildViewForParking(this, (Parking) markerIn);
		else if(markerIn instanceof Tip)
			TipViews.buildViewForTip(this, (Tip) markerIn);
		else if(markerIn instanceof CycleStation)
			CycleStationViews.buildViewForCycleStation(this, (CycleStation) markerIn);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void overlayFinishedLoadingWithPayload(boolean status, Object payload) {
		
    	for(MarkerInterface markerInterfaced : (ArrayList<MarkerInterface>) payload) { 
			Marker marker = map.addMarker(new MarkerOptions()
            .position(markerInterfaced.getLatLng())
            .icon(BitmapDescriptorFactory.fromResource(markerInterfaced.getDrawable())));
    		markers.put(marker, markerInterfaced);
    	}		
    	this.overlayFinishedLoading(status);
	}

	@Override
	public LatLng getLastLocation() {
		return this.lastKnownLocation;
	}
	
	
}
