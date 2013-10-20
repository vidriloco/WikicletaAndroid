package org.wikicleta.adapters;

import java.util.List;
import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.models.RankedComment;
import org.wikicleta.models.User;
import org.wikicleta.routing.Others;
import org.wikicleta.routing.Others.ImageUpdater;
import com.ocpsoft.pretty.time.PrettyTime;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CommentsListAdapter extends ArrayAdapter<RankedComment> {

	protected final Context context;
	protected final List<RankedComment> objects;
	protected LayoutInflater inflater;
  	  
	public CommentsListAdapter(Context context, List<RankedComment> objects) {
		super(context, R.layout.item_ranked_comment, objects);
		this.context = context;
		this.objects = objects;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RankedComment rankedComment = objects.get(position);
	    
		View rowView = inflater.inflate(R.layout.item_ranked_comment, parent, false);
	    TextView commentText = (TextView) rowView.findViewById(R.id.comment_content_text);
	    commentText.setTypeface(AppBase.getTypefaceLight());
	    commentText.setText(rankedComment.comment);
	    
	    TextView creationLegend = (TextView) rowView.findViewById(R.id.creation_date_text);
        
	    if(!rankedComment.positive)
	    	((ImageView) rowView.findViewById(R.id.comment_positive_image)).setImageResource(R.drawable.dislike_icon);
	    
        PrettyTime ptime = new PrettyTime();
        creationLegend.setText(ptime.format(rankedComment.createdAt));
        creationLegend.setTypeface(AppBase.getTypefaceLight());
        
        TextView creatorName = (TextView) rowView.findViewById(R.id.contributor_text);
        
        String username = rankedComment.userId == User.id() ? context.getResources().getString(R.string.you) : rankedComment.username;
        
        creatorName.setText(context.getResources().getString(R.string.created_by).concat(" ").concat(username));
        creatorName.setTypeface(AppBase.getTypefaceStrong());
        
        if(rankedComment.hasPic()) {
            ImageView ownerPic = (ImageView) rowView.findViewById(R.id.contributor_pic);
            
            ImageUpdater updater = Others.getImageFetcher();
            updater.setImageAndImageProcessor(ownerPic, Others.ImageProcessor.ROUND_FOR_MINI_USER_PROFILE);
            updater.execute(rankedComment.userPicURL);
        }
        
        if(rankedComment.isOwnedByCurrentUser()) {
    	    ((TextView) rowView.findViewById(R.id.delete_invitation_text)).setTypeface(AppBase.getTypefaceStrong());
    	    rowView.findViewById(R.id.delete_invitation_container).setVisibility(View.VISIBLE);
    	    rowView.findViewById(R.id.delete_invitation_container).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					
				}
    	    	
    	    });
        }
        
	    return rowView;
	}
	
	@Override
	public boolean isEnabled(int position) {
	    return false;
	}
}
