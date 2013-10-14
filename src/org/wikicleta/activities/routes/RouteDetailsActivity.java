package org.wikicleta.activities.routes;

import org.wikicleta.R;
import org.wikicleta.activities.DiscoverActivity;
import org.wikicleta.activities.common.LocationAwareMapWithMarkersActivity;
import org.wikicleta.adapters.PerformancesListAdapter;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.FieldValidators;
import org.wikicleta.common.interfaces.FavoritesConnectorInterface;
import org.wikicleta.helpers.NotificationBuilder;
import org.wikicleta.helpers.SimpleAnimatorListener;
import org.wikicleta.models.Route;
import org.wikicleta.models.User;
import org.wikicleta.routing.Favorites;
import org.wikicleta.routing.Routes;
import org.wikicleta.services.routes.RouteTrackingService;
import org.wikicleta.services.routes.ServiceConstructor;
import org.wikicleta.views.RouteViews;
import org.wikicleta.layers.RouteOverlay;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class RouteDetailsActivity extends LocationAwareMapWithMarkersActivity implements RoutesConnectorInterface, FavoritesConnectorInterface {
	
	protected NotificationBuilder notification;
	protected RouteOverlay routesOverlay;
	public static Route currentRoute;
	public AlertDialog.Builder alertDialog;
	protected AlertDialog toggleLayersMenu;
	
	//Service
	protected RouteTrackingService theService;
	ServiceConstructor serviceInitializator;
	private ImageView returnIcon;
	private ImageView moreInfoIcon;
	private ImageView performancesIcon;
	private ImageView nonFavoritedIcon;
	private ImageView favoritedIcon;
	
	private Dialog performancesDialog;

	private ObjectAnimator favoritedAnimator;
	
	public void onCreate(Bundle savedInstanceState) {
		attemptCenterOnLocationAtStart = false;
		super.onCreate(savedInstanceState,R.layout.activity_route_details);
		setTheme(R.style.Theme_wikicleta);
		
        AppBase.currentActivity = this;
        
        if(currentRoute == null)
        	AppBase.launchActivity(DiscoverActivity.class);
        else {
        	map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentRoute.originCoordinate, 15));
            
            returnIcon = (ImageView) this.findViewById(R.id.return_button);
        	returnIcon.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				overridePendingTransition(0, 0);
    				finish();
    			}
        		
        	});
        	
        	moreInfoIcon = (ImageView) this.findViewById(R.id.more_info_button);
        	moreInfoIcon.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				RouteViews.buildViewDetailsExtra(RouteDetailsActivity.this, currentRoute);
    			}
        		
        	});
        	
        	performancesIcon = (ImageView) this.findViewById(R.id.performances_button);
        	performancesIcon.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				performancesDialog = RouteViews.buildPerformancesView(RouteDetailsActivity.this, currentRoute);
    			}
        		
        	});
        	
        	favoritedIcon = (ImageView) this.findViewById(R.id.favorited_button_icon);
        	favoritedIcon.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				favoritedIcon.setClickable(false);
    				Favorites.Post unMarker = new Favorites().new Post(RouteDetailsActivity.this, "unmark");
    				unMarker.execute(String.valueOf(currentRoute.remoteId), "Route", String.valueOf(User.id()));
    				runAnimator(favoritedIcon);
    			}
        		
        	});
        	
        	nonFavoritedIcon = (ImageView) this.findViewById(R.id.non_favorited_button_icon);
        	nonFavoritedIcon.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				nonFavoritedIcon.setClickable(false);

    				Favorites.Post marker = new Favorites().new Post(RouteDetailsActivity.this, "mark");
    				marker.execute(String.valueOf(currentRoute.remoteId), "Route", String.valueOf(User.id()));
    				runAnimator(nonFavoritedIcon);
    			}
        		
        	});
        	
        	
        	fetchAndDrawRoute();
        	loadMarkers();
        	
        	Favorites.Marked markedInvestigator = new Favorites().new Marked(RouteDetailsActivity.this);
			markedInvestigator.execute(String.valueOf(currentRoute.remoteId), "Route", String.valueOf(User.id()));
			runAnimator(nonFavoritedIcon);
        }
	}
	
	private void runAnimator(final View view) {
		favoritedAnimator = ObjectAnimator.ofFloat(view, "alpha", 1, 0.4f, 1);
    	favoritedAnimator.setDuration(3000);
    	favoritedAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        favoritedAnimator.start();
        favoritedAnimator.addListener(new SimpleAnimatorListener() {
        	@Override
        	public void onAnimationCancel(Animator animation) {
        		ObjectAnimator.ofFloat(view, "alpha", 0.4f, 1, 1).start();      		
        	}
        });
	}
	
	private void loadMarkers() {
		map.addMarker(new MarkerOptions()
        .position(currentRoute.originCoordinate)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_flag_marker)));
		
		map.addMarker(new MarkerOptions()
        .position(currentRoute.endCoordinate)
        .icon(BitmapDescriptorFactory.fromResource(R.drawable.finish_flag_marker)));
	}
	
	private void fetchAndDrawRoute() {
		if(currentRoute.hasNoPathLoaded()) {
			Routes routes = new Routes();
	    	Routes.Show routesFetcher = routes.new Show(this);
	    	routesFetcher.execute(currentRoute);
		}
	}

	public void attemptUpdate(Dialog dialog) {
		EditText nameView = (EditText) dialog.findViewById(R.id.route_name);
		EditText detailsView = (EditText) dialog.findViewById(R.id.route_details);
		CheckBox routeIsPrivate = (CheckBox) dialog.findViewById(R.id.route_is_private);
		
		String routeName = nameView.getText().toString();
		String routeDetails = detailsView.getText().toString();

		if(FieldValidators.isFieldEmpty(routeName)) {
			nameView.setError(getResources().getString(R.string.tips_input_empty));
			return;
		}
		
		if(FieldValidators.isFieldEmpty(routeDetails)) {
			detailsView.setError(getResources().getString(R.string.tips_input_empty));
			return;
		}
		
		currentRoute.updateAttributes(routeName, routeDetails, !routeIsPrivate.isChecked());
		
		Routes.Put updater = new Routes().new Put();
		updater.activity = this;
		updater.execute(currentRoute);
	}
	
	@Override
	public void routeDetailsFinishedLoading(boolean status) {
		if(status) {
			PolylineOptions options = new PolylineOptions();
			for(int i=0; i < currentRoute.path.length ; i++) {
				options.add(new LatLng(currentRoute.path[i][1], currentRoute.path[i][0]));
			}
			
			options.color(R.color.wikicleta_blue);
			this.map.addPolyline(options);
		} else {
			routeDetailsDidNotLoad(status);
		}


	}

	@Override
	public void routeDetailsDidNotLoad(boolean status) {
		overridePendingTransition(0, 0);
		finish();
	}

	@Override
	public void routePerformancesFinishedLoading(boolean status) {
		performancesDialog.setContentView(R.layout.dialog_performances_list);
		TextView dialogTitle = (TextView) performancesDialog.findViewById(R.id.dialog_title);
        dialogTitle.setTypeface(AppBase.getTypefaceStrong());
        
        ((TextView) performancesDialog.findViewById(R.id.username_title_text)).setTypeface(AppBase.getTypefaceStrong());
        ((TextView) performancesDialog.findViewById(R.id.date_title_text)).setTypeface(AppBase.getTypefaceStrong());
        ((TextView) performancesDialog.findViewById(R.id.time_title_text)).setTypeface(AppBase.getTypefaceStrong());
        ((TextView) performancesDialog.findViewById(R.id.velocity_title_text)).setTypeface(AppBase.getTypefaceStrong());

        final ListView listview = (ListView) performancesDialog.findViewById(R.id.list_events);
        PerformancesListAdapter listAdapter = new PerformancesListAdapter(this, currentRoute.persistedRoutePerformances);
	    listview.setAdapter(listAdapter);
	    
		((TextView) performancesDialog.findViewById(R.id.challenge_or_check_in_text)).setTypeface(AppBase.getTypefaceStrong());

	}

	@Override
	public void routePerformancesDidNotLoad(boolean status) {
		performancesDialog.dismiss();
		// Add toast with failure legend
	}

	@Override
	public void onFavoritedItemChangedState(boolean isFavorite) {
		favoritedAnimator.cancel();
		nonFavoritedIcon.setClickable(true);
		favoritedIcon.setClickable(true);

		if(isFavorite) {
			nonFavoritedIcon.setVisibility(View.GONE);
			favoritedIcon.setVisibility(View.VISIBLE);
		} else {
			nonFavoritedIcon.setVisibility(View.VISIBLE);
			favoritedIcon.setVisibility(View.GONE);
		}
			
	}

}
