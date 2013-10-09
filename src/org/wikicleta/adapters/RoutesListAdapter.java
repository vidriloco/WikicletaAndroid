package org.wikicleta.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.wikicleta.R;
import org.wikicleta.models.Route;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RoutesListAdapter extends BaseAdapter {

    private LinkedList<Route> routes;
    private static LayoutInflater inflater=null;
    private boolean todaySectionDraw = false;
    
    public RoutesListAdapter(Activity activity, ArrayList<Route> routes_) {
    	routes = new LinkedList<Route>();
    	for(Route route : routes_){
    		routes.add(route);
    	}
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    public RoutesListAdapter(Activity activity, LinkedList<Route> routes, boolean uploaderReloading) {
        this.routes= routes;      
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    
    public RoutesListAdapter(Activity activity, LinkedList<Route> routes) {
    	this(activity, routes, false);
    }
 
    public int getCount() {
        return routes.size();
    }
 
    public Object getItem(int position) {
        return routes.get(position);
    }
 
    public long getItemId(int position) {
        return routes.get(position).getId();
    }
 
    @SuppressLint("SimpleDateFormat")
	public View getView(int position, View view, ViewGroup parent) {
        if(view==null)
        	view = inflater.inflate(R.layout.route_row, null);
        
        Route route = (Route) this.routes.get(position);
        
        Configuration mConfiguration = new Configuration();
        final TimeZone mTimeZone = Calendar.getInstance(mConfiguration.locale).getTimeZone();
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd/MM HH:mm");
        mSimpleDateFormat.setTimeZone(mTimeZone);        
        
        ((TextView) view.findViewById(R.id.route_name)).setText(route.name);
        ((TextView) view.findViewById(R.id.route_tags)).setText(route.details);
        ((TextView) view.findViewById(R.id.route_ranking)).setText(String.valueOf(route.ranking));
        
        if(route.isDraft) {
        	view.findViewById(R.id.route_row).setBackgroundColor(view.getResources().getColor(R.color.route_staged));
        } else {
        	if(!todaySectionDraw) {
        		view.findViewById(R.id.todays_routes_group).setVisibility(View.VISIBLE);
        		todaySectionDraw = true;
        	}
        }

        String publicStatus = view.getContext().getString(R.string.route_privacy_public);
        if(!route.isPublic)
        	publicStatus = view.getContext().getString(R.string.route_privacy_private);
        
        ((TextView) view.findViewById(R.id.route_privacy_status)).setText(publicStatus);
        ((TextView) view.findViewById(R.id.route_date)).setText(mSimpleDateFormat.format(new Date(route.createdAt)));
        return view;
    }
}
