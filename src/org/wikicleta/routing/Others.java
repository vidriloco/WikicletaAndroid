package org.wikicleta.routing;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.wikicleta.helpers.Graphics;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class Others {

	public enum ImageProcessor {
		ROUND_FOR_USER_PROFILE,
		ROUND_FOR_MINI_USER_PROFILE,
		ROUND_AT_10,
		ROUND_AT_30
	}
	
	public enum Cruds {
		MODIFY,
		CREATE
	}
	
	protected static Others defaultSyncer;

	public static ImageUpdater getImageFetcher() {
		if(defaultSyncer == null)
			defaultSyncer = new Others();
		return defaultSyncer.new ImageUpdater();
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
					this.roundAt(10, result);
				else if(processorMode == ImageProcessor.ROUND_AT_30)
					this.roundAt(30, result);
			}
	    }
		
		protected void roundAt(int number, Bitmap result) {
			imagePlaceHolder.setImageBitmap(Graphics.getRoundedCornerBitmap(result, number));
		}
		
		protected void roundAtDefaultNoScaling(Bitmap result) {
			Bitmap ownerPicBitmap = Bitmap.createScaledBitmap(result, 230, 230, true);
        	imagePlaceHolder.setImageBitmap(Graphics.getRoundedCornerBitmap(ownerPicBitmap, 115));
		}
		
		protected void scaleAndRoundForMiniPic(Bitmap result) {
			Bitmap ownerPicBitmap = Bitmap.createScaledBitmap(result, 70, 70, true);
        	imagePlaceHolder.setImageBitmap(Graphics.getRoundedCornerBitmap(ownerPicBitmap, 35));
		}

	}
}
