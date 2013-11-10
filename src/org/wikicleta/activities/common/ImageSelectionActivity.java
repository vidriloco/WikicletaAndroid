package org.wikicleta.activities.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.wikicleta.R;
import org.wikicleta.analytics.AnalyticsBase;
import org.wikicleta.common.AppBase;
import org.wikicleta.helpers.Graphics;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockActivity;

public class ImageSelectionActivity extends SherlockActivity {

	protected int SELECT_FILE=1;
	protected ImageView pic;
	protected Bitmap bitmap;
	
	protected void onCreate(Bundle savedInstanceState, int layout) {
		super.onCreate(savedInstanceState);
		AppBase.currentActivity = this;

		setContentView(layout);
    	
    	pic = (ImageView) this.findViewById(R.id.user_pic);
    	pic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AnalyticsBase.reportLoggedInEvent("User choose pic", getApplicationContext());

				selectImage();
			}
    		
    	});
	}
	
	protected void selectImage() {
		Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,this.getString(R.string.file_to_upload_profile)), SELECT_FILE);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            
            try {
            	bitmap = Graphics.scaleCenterCrop(MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage), 200, 200) ;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
            
            if(bitmap != null) {
            	pic.setImageBitmap(Graphics.getRoundedImageAtSize(bitmap, 230, 115));
				AnalyticsBase.reportLoggedInEvent("Valid pic choosen", getApplicationContext());
            }
        }

    }
}
