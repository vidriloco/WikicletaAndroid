package org.wikicleta.routes.fragments;

import org.wikicleta.R;
import org.wikicleta.activities.UserProfileActivity;
import org.wikicleta.adapters.RoutesListAdapter;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.helpers.NotificationBuilder;
import org.wikicleta.models.Route;
import org.wikicleta.routes.activities.RouteDetailsActivity;
import com.nineoldandroids.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityFragment extends Fragment {
	
    ListView listUnsynced;
    ListView listUploaded;
    int selectedListItem;
    View fragmentView;
    
    LinearLayout emptyRoutesLayout;

    LinearLayout unsyncedRoutesLayout;
	TextView unsyncedRoutesTitle;
	
	ImageView routeResyncer;
	ObjectAnimator uploaderAnimator;
	
	public static ProfileFragment newInstance(int index) {
		ProfileFragment f = new ProfileFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_activity, container, false);

        // Prepare the views for uploaded routes
        this.emptyRoutesLayout = (LinearLayout) fragmentView.findViewById(R.id.empty_routes_group);
        
        // Prepare the views for queued routes
        this.unsyncedRoutesLayout = (LinearLayout) fragmentView.findViewById(R.id.unsynced_routes_group);
        this.unsyncedRoutesTitle = (TextView) fragmentView.findViewById(R.id.unsynced_route_title);
        this.routeResyncer = (ImageView) fragmentView.findViewById(R.id.route_resyncer);
        
    	this.uploaderAnimator = ObjectAnimator.ofFloat(routeResyncer, "rotation", 0, 360);
    	this.uploaderAnimator.setDuration(800);
    	this.uploaderAnimator.setRepeatCount(ObjectAnimator.INFINITE);
    	
        return fragmentView;
    } 
	
	protected ListView drawRoutesList(int listType, final RoutesListAdapter adapter) {
		ListView list = (ListView) fragmentView.findViewById(listType);
    	this.registerForContextMenu(list);
        // Click event for single list row
        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
            	Route route = (Route) adapter.getItem(pos);
                    
            	Bundle bundle = new Bundle();
            	bundle.putLong("routeId", route.getId());
            	AppBase.launchActivityWithBundle(RouteDetailsActivity.class, bundle);
            }
        });
        
		list.setAdapter(adapter);
		return list;
    }
	
	@Override
	public void onResume() {
		super.onResume();
		this.drawView();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = this.getActivity().getMenuInflater();
	    inflater.inflate(R.menu.routes_menu, menu);
	    
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	    selectedListItem = info.position;
	}
	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
    	Route route = (Route) listUnsynced.getAdapter().getItem(selectedListItem);

	    switch (item.getItemId()) {
	        case R.id.route_destroy:
                route.delete();
                unblockUI();
	            return true;
	        case R.id.route_details:
                
            	Bundle bundle = new Bundle();
            	bundle.putLong("routeId", route.getId());
            	AppBase.launchActivityWithBundle(RouteDetailsActivity.class, bundle);
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	public void drawLists() {
		final UserProfileActivity activity = (UserProfileActivity) this.getActivity();

        if(Route.uploaded().size() > 0) {
    		this.listUploaded = this.drawRoutesList(R.id.list_synced, new RoutesListAdapter(activity, Route.uploaded()));
        } else {
        	this.emptyRoutesLayout.setVisibility(View.VISIBLE);
        }
        
        if(Route.queued().size() > 0) {
			this.unsyncedRoutesLayout.setVisibility(View.VISIBLE);
		} else {
			this.unsyncedRoutesLayout.setVisibility(View.GONE);
		}
                
		listUnsynced = this.drawRoutesList(R.id.list_unsynced, new RoutesListAdapter(activity, Route.queued()));
	}
	
	public void drawView() {
		this.drawLists();
        fragmentView.findViewById(R.id.route_resyncer).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((UserProfileActivity) getActivity()).theService.uploadStagedRoutes();
			}
        	
        });
        
        this.unsyncedRoutesLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.e("WIKICLETA", "Error error");
				if(listUnsynced.getVisibility() == View.GONE) {
					listUnsynced.setVisibility(View.VISIBLE);
					Log.e("WIKICLETA", "Hacer visible");
				}
				else {
					listUnsynced.setVisibility(View.GONE);
					Log.e("WIKICLETA", "Ocultar");

				}
			}
        	
        });
	}
	
	public void blockUI() {
		this.uploaderAnimator.start();
    	this.unsyncedRoutesTitle.setText(getString(R.string.routes_syncing_title));
    	
    	this.unsyncedRoutesLayout.setClickable(false);
    	
    	this.listUnsynced.setVisibility(View.GONE);
	}
	
	public void unblockUI() {
		int queuedRoutes = Route.queued().size();
    	String routesMsj = getString(R.string.routes_unsynced_title, queuedRoutes);
        if(queuedRoutes == 1)
        	routesMsj = getString(R.string.route_unsynced_title);
        
        this.unsyncedRoutesTitle.setText(routesMsj);
		this.uploaderAnimator.cancel();
		
    	this.unsyncedRoutesLayout.setClickable(true);
    	
		this.drawLists();
		this.listUnsynced.setVisibility(View.VISIBLE);
		
		new NotificationBuilder(this.getActivity()).clearNotification(Constants.ROUTES_MANAGEMENT_NOTIFICATION_ID);
	}
	
	protected UserProfileActivity getUserProfileActivity() {
		return (UserProfileActivity) this.getActivity();
	}
}
