package org.wikicleta.adapters;

import java.util.List;
import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.LightPOI;
import com.ocpsoft.pretty.time.PrettyTime;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LightPOIsListAdapter extends ArrayAdapter<LightPOI> {

	
	protected final Context context;
	protected final List<LightPOI> objects;
	protected LayoutInflater inflater;
	protected boolean showKind;
  	  
	public LightPOIsListAdapter(Context context, List<LightPOI> objects, boolean showKind) {
		super(context, R.layout.item_light_on_list, objects);
		this.context = context;
		this.objects = objects;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.showKind = showKind;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    LightPOI lightPOI = objects.get(position);
	    
		View rowView = inflater.inflate(R.layout.item_light_on_list, parent, false);
	    TextView lightTitleText = (TextView) rowView.findViewById(R.id.light_title_text);
	    lightTitleText.setTypeface(AppBase.getTypefaceStrong());
	    
	    TextView lightDescriptionText = (TextView) rowView.findViewById(R.id.light_description_text);
	    lightDescriptionText.setTypeface(AppBase.getTypefaceLight());

	    TextView lastLightUpdatedText = (TextView) rowView.findViewById(R.id.last_light_updated_text);
	    lastLightUpdatedText.setTypeface(AppBase.getTypefaceLight());
	    if(this.showKind) {
	    	rowView.findViewById(R.id.kind_of_light_poi_container).setVisibility(View.VISIBLE);
	    	TextView kindText = (TextView) rowView.findViewById(R.id.kind_of_light_poi_text);
	    	kindText.setTypeface(AppBase.getTypefaceStrong());
	    	kindText.setText(lightPOI.kindString(context));
	    	kindText.setVisibility(View.VISIBLE);
	    }
	    
	    lightTitleText.setText(lightPOI.getTitle(context));
	    lightDescriptionText.setText(lightPOI.description);
	    
	    PrettyTime ptime = new PrettyTime();
	    lastLightUpdatedText.setText(this.context.getResources().getString(R.string.updated_on).concat(" ").concat(ptime.format(lightPOI.updatedAt)));
	    
	    return rowView;
	}
}
