package org.wikicleta.views;

import org.wikicleta.common.interfaces.FavoritesConnectorInterface;
import org.wikicleta.helpers.SimpleAnimatorListener;

import android.view.View;
import android.widget.ImageView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

public class BaseViews implements FavoritesConnectorInterface {
	protected ObjectAnimator favoritedAnimator;
	
	protected static ParkingViews singleton;
	protected ImageView favoritedIcon;
	protected ImageView nonFavoritedIcon;
    
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
		if(singleton == null)
			singleton = new ParkingViews();
	}
}
