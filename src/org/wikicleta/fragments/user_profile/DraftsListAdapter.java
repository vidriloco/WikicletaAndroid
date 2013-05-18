package org.wikicleta.fragments.user_profile;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.Draft;

import com.ocpsoft.pretty.time.PrettyTime;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DraftsListAdapter extends BaseAdapter {

    private LinkedList<Draft> drafts;
    private static LayoutInflater inflater=null;
    Activity activity; 
    
    public DraftsListAdapter(Activity activity, LinkedList<Draft> drafts) {
    	this.drafts = drafts;
    	this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    public int getCount() {
        return drafts.size();
    }
 
    public Object getItem(int position) {
        return drafts.get(position);
    }
 
    public long getItemId(int position) {
        return drafts.get(position).hashCode();
    }
 
    @SuppressLint("SimpleDateFormat")
	public View getView(int position, View view, ViewGroup parent) {
        if(view==null)
        	view = inflater.inflate(R.layout.draft_row, null);
        
        Draft draft = (Draft) this.drafts.get(position);
        
        Configuration mConfiguration = new Configuration();
        final TimeZone mTimeZone = Calendar.getInstance(mConfiguration.locale).getTimeZone();
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("dd/MM HH:mm");
        mSimpleDateFormat.setTimeZone(mTimeZone);        

        TextView category = (TextView) view.findViewById(R.id.draft_category);
        TextView details = (TextView) view.findViewById(R.id.draft_contents);
        TextView date = (TextView) view.findViewById(R.id.draft_created_at);

    	String categoryText = activity.getResources().getString(
    			activity.getResources().getIdentifier(draft.getCategory(), "string", activity.getPackageName()));
    	category.setText(categoryText);

    	PrettyTime ptime = new PrettyTime();
        date.setText(activity.getResources().getString(R.string.saved_as_draft_on).concat(" ").concat(ptime.format(draft.getDate())));
        
    	
        details.setText(draft.getDetails());
        category.setTypeface(AppBase.getTypefaceStrong());
        details.setTypeface(AppBase.getTypefaceLight());
        date.setTypeface(AppBase.getTypefaceLight());
        return view;
    }
}
