package org.wikicleta.common;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.R;
import org.wikicleta.activities.MainMapActivity;
import org.wikicleta.helpers.DialogBuilder;
import org.wikicleta.helpers.Graphics;
import org.wikicleta.layers.TipsOverlay;
import org.wikicleta.layers.components.TipOverlayItem;
import org.wikicleta.models.Tip;
import org.wikicleta.models.User;
import org.wikicleta.tips.activities.NewTipActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.ImageView;

public class Syncers {

	public enum ImageProcessor {
		ROUND_FOR_USER_PROFILE,
		ROUND_FOR_MINI_USER_PROFILE,
		ROUND_AT_10
	}
	
	public enum Cruds {
		MODIFY,
		CREATE
	}
	
	protected static Syncers defaultSyncer;
	protected String listingsPath="/api/tips?";
	protected String postPath="/api/tips";
	protected String changePath="/api/tips/:id";
	
	public static TipsFetching getTipsFetcher() {
		if(defaultSyncer == null)
			defaultSyncer = new Syncers();
		return defaultSyncer.new TipsFetching();
	}
	
	public static ImageUpdater getImageFetcher() {
		if(defaultSyncer == null)
			defaultSyncer = new Syncers();
		return defaultSyncer.new ImageUpdater();
	}
	
	public static TipsDelete getTipsDelete() {
		if(defaultSyncer == null)
			defaultSyncer = new Syncers();
		return defaultSyncer.new TipsDelete();
	}
	
	public static TipsChange getTipsChange() {
		if(defaultSyncer == null)
			defaultSyncer = new Syncers();
		return defaultSyncer.new TipsChange();
	}
	
	public class ImageUpdater extends AsyncTask<String, Void, Bitmap> {
		
		protected ImageView imagePlaceHolder;
		protected ImageProcessor processorMode;
		
		public void setImageAndImageProcessor(ImageView image, ImageProcessor processor) {
			this.imagePlaceHolder = image;
			this.processorMode = processor;
		}
		
		@Override
		protected Bitmap doInBackground(String... params) {
			URL ownerPicURL = null;
			try {
				ownerPicURL = new URL(params[0]);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			try {
				if(ownerPicURL == null)
					return null;
				return BitmapFactory.decodeStream(ownerPicURL.openConnection().getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
	    protected void onPostExecute(Bitmap result) {
			if(result!=null) {
				if(processorMode == ImageProcessor.ROUND_FOR_MINI_USER_PROFILE)
					this.scaleAndRoundForMiniPic(result);
				else if(processorMode == ImageProcessor.ROUND_FOR_USER_PROFILE)
					this.roundAtDefaultNoScaling(result);
				else if(processorMode == ImageProcessor.ROUND_AT_10)
					this.roundAt10(result);
			}
	    }
		
		protected void roundAt10(Bitmap result) {
			imagePlaceHolder.setImageBitmap(Graphics.getRoundedCornerBitmap(result, 10));
		}
		
		protected void roundAtDefaultNoScaling(Bitmap result) {
			imagePlaceHolder.setImageBitmap(Graphics.getRoundedBitmap(result));
		}
		
		protected void scaleAndRoundForMiniPic(Bitmap result) {
			Bitmap ownerPicBitmap = Bitmap.createScaledBitmap(result, 70, 70, true);
        	imagePlaceHolder.setImageBitmap(Graphics.getRoundedCornerBitmap(ownerPicBitmap, 35));
		}

	}
	
	public class TipsDelete extends AsyncTask<Tip, Void, Boolean> {
		
		Tip tip;
		public MainMapActivity activity;
		
		@Override
		protected Boolean doInBackground(Tip... params) {
			tip = params[0];
			
			HashMap<String, Object> auth = new HashMap<String, Object>();
			auth.put("auth_token", User.token());
			HashMap<String, Object> extras = new HashMap<String, Object>();
			extras.put("extras", auth);
			int requestStatus = NetworkOperations.postJSONTo(changePath.replace(":id", String.valueOf(tip.remoteId)), JSONObject.toJSONString(extras));
			return requestStatus == 200;
		}
		
		@Override
		protected void onPreExecute() {
			DialogBuilder.buildLoadingDialogWithMessage(activity, "Eliminando tip");
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			if(success) {
				activity.reloadActiveLayers();
				Toasts.showToastWithMessage(activity, R.string.tips_deleted_successfully, R.drawable.success_icon);
			} else {
				Toasts.showToastWithMessage(activity, R.string.tips_did_not_deleted, R.drawable.failure_icon);
			}
		}
		
	}
	
	public class TipsFetching extends AsyncTask<TipsOverlay, Void, Boolean> {
    	
	   TipsOverlay overlay;
	   JSONArray objectList;
	   
		@Override
		protected Boolean doInBackground(TipsOverlay... args) {
			overlay = args[0];
			HashMap<String, String> viewport = overlay.listener.getCurrentViewport();
			String params = "viewport[sw]=".concat(viewport.get("sw")).concat("&viewport[ne]=").concat(viewport.get("ne"));
			String fetchedString = NetworkOperations.getJSONExpectingString(listingsPath.concat(params), false);
			if(fetchedString == null)
				return false;
			
			JSONObject object = (JSONObject) JSONValue.parse(fetchedString);
			if((Boolean) object.get("success")) {
				objectList = (JSONArray) object.get("tips");
				return true;
			} else {
				return false;
			}
		}

		@Override
		protected void onPreExecute() {
			
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			if(success) {
				overlay.clear();
				@SuppressWarnings("unchecked")
				Iterator<JSONObject> iterator = (Iterator<JSONObject>) objectList.iterator();
				while(iterator.hasNext()) {
					JSONObject tip = iterator.next();
					try {
						overlay.addItem(TipOverlayItem.buildFrom(overlay.listener.getActivity(), tip));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				overlay.notifyOverlayIsReady();
			}
		}

		@Override
		protected void onCancelled() {
			
		}
	}
	
	
	public class TipsChange extends AsyncTask<Tip, Void, Boolean> {
		private ProgressDialog progressDialog;
		private Tip tip;
		public NewTipActivity activity;
		public Cruds mode = Cruds.CREATE;
		
		@Override
		protected Boolean doInBackground(Tip... args) {
			tip = args[0];
			HashMap<String, Object> auth = new HashMap<String, Object>();
			auth.put("auth_token", User.token());
			int requestStatus = 404;
			if(mode == Cruds.CREATE)
				requestStatus = NetworkOperations.postJSONTo(postPath, tip.toJSON(auth));
			else if(mode == Cruds.MODIFY)
				requestStatus = NetworkOperations.putJSONTo(changePath.replace(":id", String.valueOf(tip.remoteId)), tip.toJSON(auth));

			return requestStatus == 200;
		}
		
		@Override
		protected void onPreExecute() {
		    super.onPreExecute();
		    progressDialog = ProgressDialog.show(activity, "", 
		    		activity.getResources().getString(R.string.tips_uploading), true);
		}

	    protected void onPostExecute(Boolean success) {
	    	progressDialog.dismiss();
	    	
	    	if(success) {
	    		Toasts.showToastWithMessage(activity, R.string.tips_uploaded_successfully, R.drawable.success_icon);
	    		// Sends to the layers activity
	    		activity.finish();
	    	} else {
	    		activity.formView.hide();
	    		AlertDialog.Builder builder = DialogBuilder.buildAlertWithTitleAndMessage(activity, R.string.notification, R.string.tips_not_commited);
	    		
	    		// Only allowing to save drafts when creating a new tip
	    		if(mode == Cruds.CREATE) {
	    			builder = builder.setNeutralButton(activity.getResources().getString(R.string.save_as_draft), new DialogInterface.OnClickListener() {
	    				public void onClick(DialogInterface dialog,int id) {
		    				tip.save();
		    				AppBase.launchActivity(MainMapActivity.class);
		    				Toasts.showToastWithMessage(activity, R.string.tips_sent_to_drafts, R.drawable.archive_icon);
		    	    		activity.finish();
	    				}
	    			});
	    		}
	    		
	    		builder.setNegativeButton(activity.getResources().getString(R.string.discard), new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog,int id) {
	    				AppBase.launchActivity(MainMapActivity.class);
	    				activity.finish();
	    			}
	    		}).setPositiveButton(activity.getResources().getString(R.string.retry), new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog,int id) {
	    				dialog.dismiss();
	    				activity.formView.show();
	    			}
	    		});
	    		final AlertDialog alert = builder.create();
	    		alert.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface arg0) {
						activity.formView.show();
					}
	    			
	    		});
	    		
	    		alert.setOnShowListener(new DialogInterface.OnShowListener() {
	    		    @Override
	    		    public void onShow(DialogInterface dialog) {
	    		        Button btnPositive = alert.getButton(Dialog.BUTTON_POSITIVE);
	    		        btnPositive.setTextSize(13);
	    		        btnPositive.setTypeface(AppBase.getTypefaceStrong());
	    		        
	    		        Button btnNegative = alert.getButton(Dialog.BUTTON_NEGATIVE);
	    		        btnNegative.setTextSize(13);
	    		        btnNegative.setTypeface(AppBase.getTypefaceStrong());
	    		        
	    		        Button btnNeutral = alert.getButton(Dialog.BUTTON_NEUTRAL);
	    		        btnNeutral.setTextSize(13);
	    		        btnNeutral.setTypeface(AppBase.getTypefaceStrong());

	    		    }
	    		});
	    		
	    		alert.show();
	    	}
	    }

	     
	 }
}
