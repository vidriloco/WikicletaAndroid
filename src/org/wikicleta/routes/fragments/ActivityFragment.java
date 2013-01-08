package org.wikicleta.routes.fragments;

import org.wikicleta.R;
import org.wikicleta.activities.RouteDetailsActivity;
import org.wikicleta.activities.UserProfileActivity;
import org.wikicleta.activities.UserProfileActivity.ViewStatus;
import org.wikicleta.adapters.RoutesListAdapter;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.Route;
import com.nineoldandroids.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
    	this.uploaderAnimator.setRepeatMode(ObjectAnimator.RESTART);
    	
        return fragmentView;
    } 
	
	protected ListView drawRoutesList(int listType, final RoutesListAdapter adapter) {
		ListView list = (ListView) fragmentView.findViewById(listType);
        // Click event for single list row
        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                    Route route = (Route) adapter.getItem(pos);
                    RouteDetailsActivity.currentRoute = route;
                    AppBase.launchActivityAnimated(RouteDetailsActivity.class);
            }
        });
		list.setAdapter(adapter);
		return list;
    }
	
	public void drawView() {
		final UserProfileActivity activity = (UserProfileActivity) this.getActivity();

        if(Route.uploaded().size() > 0) {
    		this.listUploaded = this.drawRoutesList(R.id.list, new RoutesListAdapter(activity, Route.uploaded()));
        } else {
        	this.emptyRoutesLayout.setVisibility(View.VISIBLE);
        }
        

        fragmentView.findViewById(R.id.route_resyncer).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(activity.currentViewStatus == ViewStatus.UNBLOCK)
					getUserProfileActivity().theService.uploadStagedRoutes();
			}
        	
        });
        this.unsyncedRoutesLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(activity.currentViewStatus == ViewStatus.UNBLOCK) {
					if(listUnsynced.getVisibility() == View.GONE)
						listUnsynced.setVisibility(View.VISIBLE);
					else
						listUnsynced.setVisibility(View.GONE);
				}
			}
        	
        });
        
        int queuedRoutes = getUserProfileActivity().theService.queuedRoutesCount();
        if(queuedRoutes > 0) {
            this.unsyncedRoutesLayout.setVisibility(View.VISIBLE);
            
            String routesMsj = getString(R.string.routes_unsynced_title, queuedRoutes);
            if(queuedRoutes == 1)
            	routesMsj = getString(R.string.route_unsynced_title);
            
            this.unsyncedRoutesTitle.setText(routesMsj);
            
            if(activity.currentViewStatus == ViewStatus.UNBLOCK)
            	this.uploaderAnimator.end();
            else {
            	this.uploaderAnimator.start();
            	this.unsyncedRoutesTitle.setText(getString(R.string.routes_syncing_title));
            }
    		this.listUnsynced = this.drawRoutesList(R.id.list_unsynced, new RoutesListAdapter(activity, activity.theService.routesQueued()));
        }
	}
	
	protected UserProfileActivity getUserProfileActivity() {
		return (UserProfileActivity) this.getActivity();
	}
}
