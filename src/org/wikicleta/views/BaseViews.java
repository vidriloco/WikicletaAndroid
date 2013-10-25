package org.wikicleta.views;

import org.wikicleta.R;
import org.wikicleta.helpers.SimpleAnimatorListener;
import org.wikicleta.interfaces.FavoritesConnectorInterface;
import org.wikicleta.models.User;
import org.wikicleta.routing.Favorites;

import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

public class BaseViews implements FavoritesConnectorInterface {
	protected ObjectAnimator favoritedAnimator;
	
	protected static BaseViews singleton;
	protected ImageView favoritedIcon;
	protected ImageView nonFavoritedIcon;

	protected static void buildViewForFavoritedResource(Dialog dialog, final long remoteId, final String modelNamed) {
		loadSingleton();
		
		singleton.favoritedIcon = (ImageView) dialog.findViewById(R.id.favorited_image);
        singleton.favoritedIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				singleton.favoritedIcon.setClickable(false);
				Favorites.Post unMarker = new Favorites().new Post(singleton, "unmark");
				unMarker.execute(String.valueOf(remoteId), modelNamed, String.valueOf(User.id()));
				singleton.runAnimator(singleton.favoritedIcon);
			}
        	
        });
        
        singleton.nonFavoritedIcon = (ImageView) dialog.findViewById(R.id.non_favorited_image);
        singleton.nonFavoritedIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				singleton.nonFavoritedIcon.setClickable(false);
				Favorites.Post unMarker = new Favorites().new Post(singleton, "mark");
				unMarker.execute(String.valueOf(remoteId), modelNamed, String.valueOf(User.id()));
				singleton.runAnimator(singleton.nonFavoritedIcon);
			}
        	
        });
        
		Favorites.Marked markedInvestigator = new Favorites().new Marked(singleton);
		markedInvestigator.execute(String.valueOf(remoteId), modelNamed, String.valueOf(User.id()));
		singleton.runAnimator(singleton.nonFavoritedIcon);
	}
	
	@Override
	public void onFavoritedItemChangedState(boolean isFavorite) {
		favoritedAnimator.cancel();
		nonFavoritedIcon.setClickable(true);
		favoritedIcon.setClickable(true);

		if(isFavorite) {
			nonFavoritedIcon.setVisibility(View.GONE);
			favoritedIcon.setVisibility(View.VISIBLE);
		} else {
			nonFavoritedIcon.setVisibility(View.VISIBLE);
			favoritedIcon.setVisibility(View.GONE);
		}
			
	}
	
	protected void runAnimator(final View view) {
		favoritedAnimator = ObjectAnimator.ofFloat(view, "alpha", 1, 0.4f, 1);
    	favoritedAnimator.setDuration(3000);
    	favoritedAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        favoritedAnimator.start();
        favoritedAnimator.addListener(new SimpleAnimatorListener() {
        	@Override
        	public void onAnimationCancel(Animator animation) {
        		ObjectAnimator.ofFloat(view, "alpha", 0.4f, 1, 1).start();      		
        	}
        }); 
	}
	
	protected static void loadSingleton() {
		singleton = new BaseViews();
	}
}
