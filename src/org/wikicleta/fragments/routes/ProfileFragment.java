package org.wikicleta.fragments.routes;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.wikicleta.R;
import org.wikicleta.activities.access.LandingActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.common.NetworkOperations;
import org.wikicleta.models.Bike;
import org.wikicleta.models.User;
import org.wikicleta.routing.Others;
import org.wikicleta.routing.Others.ImageUpdater;

import com.nineoldandroids.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProfileFragment extends Fragment {
	
	protected AlertDialog.Builder alertDialog;
	protected LinearLayout profileSyncer;
	protected ObjectAnimator syncAnimator;
	protected TextView syncingText;
	protected UserProfileDataTask profileFetcher;
	
	protected ImageView userPicture;
	protected TextView profileCity;
	protected TextView profileBio;
	
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
		alertDialog = new AlertDialog.Builder(this.getActivity());
        // Inflate the layout for this fragment
		View fragment =  inflater.inflate(R.layout.fragment_profile, container, false);
		
		userPicture = (ImageView) fragment.findViewById(R.id.avatar_pic);
		
		TextView profileUsername = (TextView) fragment.findViewById(R.id.profile_username);
		profileUsername.setText(User.username());
		profileUsername.setTypeface(AppBase.getTypefaceStrong());
		
		profileCity = (TextView) fragment.findViewById(R.id.profile_city);
		profileCity.setTypeface(AppBase.getTypefaceLight());

		profileBio = (TextView) fragment.findViewById(R.id.profile_bio);
		profileBio.setTypeface(AppBase.getTypefaceLight());
		
		TextView profileEraseAccountText = (TextView) fragment.findViewById(R.id.profile_erase_account_text);
		profileEraseAccountText.setTypeface(AppBase.getTypefaceStrong());
		
		Button eraseButton = (Button) fragment.findViewById(R.id.profile_erase_acount_button);
		eraseButton.setTypeface(AppBase.getTypefaceStrong());
		
		syncingText = (TextView) fragment.findViewById(R.id.syncing_text);
		syncingText.setTypeface(AppBase.getTypefaceLight());

		profileSyncer = (LinearLayout) fragment.findViewById(R.id.profile_sync_container);
		
		syncAnimator = ObjectAnimator.ofFloat(profileSyncer, "alpha", 1, 0.2f, 1);
		syncAnimator.setDuration(2000);
		syncAnimator.setRepeatCount(ObjectAnimator.INFINITE);
		
        profileFetcher = new UserProfileDataTask();
        profileFetcher.execute();
        
		profileSyncer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(profileFetcher == null) {
			        profileFetcher = new UserProfileDataTask();
			        profileFetcher.execute();
				}
			}
			
		});
		
		fragment.findViewById(R.id.profile_erase_acount_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				alertDialog.setTitle("Pregunta").
				setMessage("¿Deseas eliminar tu cuenta de este teléfono?").
				setNeutralButton(null, null).
				setNegativeButton("Cancelar", null).
				setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						User.destroy();
						AppBase.launchActivity(LandingActivity.class);
						getActivity().finish();
					}
				});
				
				alertDialog.show();
			}
			
		});
		
		return fragment;
    }
	
	public void updateWithProfileFragments(JSONObject object) throws IOException  {
		final RelativeLayout replaceableView = (RelativeLayout) getActivity().findViewById(R.id.white_container);
		
		final String city = (String) object.get("city_name");
		final String bio = (String) object.get("bio");

		final JSONArray bikesArray = (JSONArray) object.get("bikes");
		@SuppressWarnings("unchecked")
		Iterator<JSONObject> bikesIterator = (Iterator<JSONObject>) bikesArray.iterator();
		
		final Vector<Bike> bikesList = new Vector<Bike>(bikesArray.size());
		while(bikesIterator.hasNext()) {
			JSONObject bikeObj = bikesIterator.next();
			/**
			 * Registers or updates a bike from the fetched parameters
			 */
			Bike bike = Bike.find((Long) bikeObj.get("id"));
			if(bike != null) {
				bike.updateAttrsFromJSON(bikeObj);
			} else {
				bike = Bike.newFormJSON(bikeObj);
			}
			bikesList.add(bike);
			bike.save();
		}
		
		replaceableView.removeAllViews();
		if(bikesArray.size() == 0)
			insertViewForNoBikesToShow(replaceableView);
		else
			insertViewsForBikes(replaceableView, bikesList);
		if(city == null) 
			profileCity.setText(getActivity().getResources().getString(R.string.profile_city_not_set));
		 else 
			profileCity.setText(city);
		
		if(bio != null) 
			profileBio.setText(bio);
		
		String userPicURL = (String) object.get("user_pic"); 

		if(userPicURL != null) {
			ImageUpdater picFetcher = Others.getImageFetcher();
			picFetcher.setImageAndImageProcessor(userPicture, Others.ImageProcessor.ROUND_FOR_USER_PROFILE);
			
			picFetcher.execute(NetworkOperations.serverHost.concat(userPicURL));
		}
	}
	
	private void insertViewsForBikes(RelativeLayout parentView, Vector<Bike> bikesList) {
		LayoutInflater li = getActivity().getLayoutInflater();
		
		LinearLayout bikesLayout = (LinearLayout) li.inflate(R.layout.bikes_layout, null);
		parentView.addView(bikesLayout.findViewById(R.id.bikes_layout));
		LinearLayout listBikesView = (LinearLayout) parentView.findViewById(R.id.bike_list_container);
		
		TextView bikeListTitle = (TextView) bikesLayout.findViewById(R.id.bike_list_text);
		bikeListTitle.setTypeface(AppBase.getTypefaceStrong());
		
		for(Bike bike : bikesList) {
			View view = li.inflate(R.layout.bike_layout, null);
			
			ImageView bikePicPlaceholder = (ImageView) view.findViewById(R.id.bike_icon);
			
			ImageUpdater bikePicFetcher = Others.getImageFetcher();
			bikePicFetcher.setImageAndImageProcessor(bikePicPlaceholder, Others.ImageProcessor.ROUND_AT_10);
			bikePicFetcher.execute(NetworkOperations.serverHost.concat(bike.imageURL));
			
			TextView bikeName = (TextView) view.findViewById(R.id.bike_name_text);
			bikeName.setTypeface(AppBase.getTypefaceLight());
			bikeName.setText(bike.name);
			
			TextView bikeBrand = (TextView) view.findViewById(R.id.bike_brand_text);
			bikeBrand.setTypeface(AppBase.getTypefaceLight());
			bikeBrand.setText(bike.brand);
			
			TextView bikeLikes = (TextView) view.findViewById(R.id.bike_likes_count_text);
			bikeLikes.setTypeface(AppBase.getTypefaceLight());
			
			if(bike.likesCount == 1) {
				bikeLikes.setText(String.valueOf(bike.likesCount).concat(" ").concat(getActivity().getString(R.string.fav)));
			} else {
				bikeLikes.setText(String.valueOf(bike.likesCount).concat(" ").concat(getActivity().getString(R.string.favs)));
			}
			
			listBikesView.addView(view.findViewById(R.id.bike_layout));
		}
	}
	
	public void insertViewForNoBikesToShow(RelativeLayout parentView) {
		View view = getActivity().getLayoutInflater().inflate(R.layout.no_bikes_layout, null);
		parentView.addView(view.findViewById(R.id.no_bikes_to_show_layout));
		TextView noBikesTextView = (TextView) view.findViewById(R.id.no_bikes_text);
		noBikesTextView.setTypeface(AppBase.getTypefaceLight());
	}
	
	/**
	 *  Fetch user profile data
	 */
	public class UserProfileDataTask extends AsyncTask<Void, Void, Boolean> {
		JSONObject responseObject;
		
		@Override
		protected Boolean doInBackground(Void... params) {
			String result = NetworkOperations.getJSONExpectingString("/api/profiles/".concat(User.username()), false);
			if(result == null)
				return false;
			
			responseObject = (JSONObject) JSONValue.parse(result);
			if(responseObject.containsKey("success") && !((Boolean) responseObject.get("success"))) {
				return false;
			} else {
				return true;
			}
		}

		@Override
		protected void onPreExecute() {
			syncAnimator.start();
			syncingText.setText(getActivity().getResources().getString(R.string.syncing_profile_text_value));
			syncingText.setTextColor(getActivity().getResources().getColor(R.color.wikicleta_blue));
		}
		
		@Override
		protected void onPostExecute(final Boolean success) {
			syncAnimator.cancel();

			if(success) {
				try {
					updateWithProfileFragments(responseObject);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				syncingText.setText(getActivity().getResources().getString(R.string.syncing_profile_failed_text_value));
				syncingText.setTextColor(getActivity().getResources().getColor(R.color.wikicleta_orange));

				ObjectAnimator animatorBack = ObjectAnimator.ofFloat(profileSyncer, "alpha", 1, 0.8f, 1);
				animatorBack.setDuration(500);
				animatorBack.start();
			}
			profileFetcher = null;
		}

		@Override
		protected void onCancelled() {
			
		}
	}
}
