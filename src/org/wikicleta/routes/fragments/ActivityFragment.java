package org.wikicleta.routes.fragments;

import org.wikicleta.R;
import org.wikicleta.activities.RouteDetailsActivity;
import org.wikicleta.activities.UserProfileActivity;
import org.wikicleta.adapters.RoutesListAdapter;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.Route;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ActivityFragment extends Fragment {
	
	RoutesListAdapter adapter;
    ListView list;
	
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
        final View view = inflater.inflate(R.layout.fragment_activity, container, false);
        drawRoutesList(view, R.id.list);
        this.reloadAdapter();
        return view;
    } 
	
	public void drawRoutesList(View view, int listType) {
        list = (ListView) view.findViewById(listType);
        
        // Click event for single list row
        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                    Route route = (Route) adapter.getItem(pos);
                    if(!route.isBlocked) {
                        RouteDetailsActivity.currentRoute = route;
                    	AppBase.launchActivityAnimated(RouteDetailsActivity.class);
                    }
            }
        });
		list.setAdapter(adapter);
    }
	
	public void reloadAdapter() {
		if(this.getActivity() != null) {
			UserProfileActivity activity = (UserProfileActivity) this.getActivity();
			adapter = new RoutesListAdapter(this.getActivity(), activity.theService.routesQueued(), activity.theService.isUploadingRoutes());
			if(list != null)
				list.setAdapter(adapter);
		}
		
	}
}
