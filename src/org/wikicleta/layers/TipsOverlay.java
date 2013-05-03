package org.wikicleta.layers;

import java.util.ArrayList;
import java.util.Date;

import org.wikicleta.R;
import org.wikicleta.activities.MainMapActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.Constants;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.common.Syncers;
import org.wikicleta.common.Syncers.ImageUpdater;
import org.wikicleta.common.Syncers.TipsDelete;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.layers.components.TipOverlayItem;
import org.wikicleta.models.Tip;
import org.wikicleta.models.User;
import org.wikicleta.tips.activities.NewTipActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.ocpsoft.pretty.time.PrettyTime;

public class TipsOverlay extends ItemizedOverlay<OverlayItem> implements IdentifiableOverlay {
    private ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();

	public LayersConnectorListener listener;
	
    public TipsOverlay(Drawable marker, LayersConnectorListener overlayListener) {
        super(boundCenterBottom(marker));
        this.populate();
        this.listener = overlayListener;
        this.fetch();
    }
    
    public void fetch() {
    	Syncers.TipsFetching tipsFetcher = Syncers.getTipsFetcher();
    	tipsFetcher.execute(this);
    }

    
    public void addItem(OverlayItem item) {
    	overlayItems.add(item);
        populate();
    }
 
    @Override
    protected OverlayItem createItem(int i) {
        return overlayItems.get(i);
    }
 
    @Override
    public int size() {
        return overlayItems.size();
    }
 
    @Override
    protected boolean onTap(int i) {
        OverlayItem item = overlayItems.get(i);
        this.buildViewForOverlayItem(((TipOverlayItem) item));
        return true;
    }
    
    public void buildViewForOverlayItem(TipOverlayItem item) {
    	final Tip tip = item.associatedTip;
    	final Activity activity = listener.getActivity();
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View view = inflater.inflate(R.layout.tip_details, null);
        
        String type = activity.getResources().getString(
        		activity.getResources().getIdentifier(
        				"tips.categories.".concat(tip.categoryString()), "string", activity.getPackageName()));
        
        TextView title = (TextView) view.findViewById(R.id.tip_category_title);
        title.setText(type);
        title.setTypeface(AppBase.getTypefaceStrong());
        
        TextView content = (TextView) view.findViewById(R.id.tip_contents);
        content.setText(tip.content);
        content.setTypeface(AppBase.getTypefaceLight());
        
        TextView creationLegend = (TextView) view.findViewById(R.id.tip_created_date);
        
        PrettyTime ptime = new PrettyTime();
        creationLegend.setText(activity.getResources().getString(R.string.updated_on).concat(" ").concat(ptime.format(new Date(tip.updatedAt))));
        creationLegend.setTypeface(AppBase.getTypefaceLight());
        
        LinearLayout modifyContainer = (LinearLayout) view.findViewById(R.id.modify_button_container);
        LinearLayout destroyContainer = (LinearLayout) view.findViewById(R.id.delete_button_container);
        
        TextView creatorName = (TextView) view.findViewById(R.id.tip_creator);
        
        String username = tip.userId == User.id() ? activity.getResources().getString(R.string.you) : tip.username;
        
        creatorName.setText(activity.getResources().getString(R.string.created_by).concat(" ").concat(username));
        creatorName.setTypeface(AppBase.getTypefaceStrong());
        
        if(tip.hasPic()) {
            ImageView ownerPic = (ImageView) view.findViewById(R.id.tip_creator_pic);
            
            ImageUpdater updater = Syncers.getImageFetcher();
            updater.setImageAndImageProcessor(ownerPic, Syncers.ImageProcessor.ROUND_FOR_MINI_USER_PROFILE);
            updater.execute(NetworkOperations.serverHost.concat(tip.userPicURL));
        }

        ImageView iconImage = (ImageView) view.findViewById(R.id.tip_category_icon);
        iconImage.setImageDrawable(activity.getResources().getDrawable(TipOverlayItem.getDrawable(tip)));
        
        builder.setView(view);
        final AlertDialog tipDialog = builder.create();
        view.findViewById(R.id.dialog_close).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				tipDialog.dismiss();
			}
        	
        });
        
        if(tip.isOwnedByCurrentUser()) {
        	TextView modifyButton = (TextView) view.findViewById(R.id.button_modify);
            modifyButton.setTypeface(AppBase.getTypefaceStrong());
            
            modifyContainer.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				tipDialog.dismiss();
    				Bundle bundle = new Bundle();
    				bundle.putSerializable("tip", tip);
    				AppBase.launchActivityWithBundle(NewTipActivity.class, bundle);
    			}
            	
            });
            
            TextView destroyButton = (TextView) view.findViewById(R.id.button_delete);
            destroyButton.setTypeface(AppBase.getTypefaceStrong());
            
            destroyContainer.setOnClickListener(new OnClickListener() {

    			@Override
    			public void onClick(View v) {
    				AlertDialog.Builder builder = DialogBuilder.buildAlertWithTitleAndMessage(activity, R.string.question, R.string.tips_question_delete);
    				
    				builder.setNegativeButton(R.string.confirm_no, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							tipDialog.show();
						}

    					
    				}).setPositiveButton(R.string.confirm_yes, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							TipsDelete tipsDelete = Syncers.getTipsDelete();
							tipsDelete.activity = (MainMapActivity) activity;
							tipsDelete.execute(tip);
							tipDialog.dismiss();
						}

    					
    				}).create().show();
    				tipDialog.hide();
    			}
            });
        } else {
        	view.findViewById(R.id.action_buttons_container).setVisibility(View.GONE);
        }
        
        tipDialog.show();
    }
    
    public void notifyOverlayIsReady() {
    	this.populate();
    	this.listener.onOverlayReady();
    }
    
    public void clear() {
    	this.overlayItems.clear();
    }

	@Override
	public int getIdentifier() {
		return Constants.TIPS_OVERLAY;
	}
    
}
