package org.wikicleta.activities;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import org.wikicleta.helpers.SimpleAnimatorListener;
import org.wikicleta.models.Route;
import org.wikicleta.models.User;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class LandingActivity extends Activity {
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppBase.currentActivity = this;
		this.setContentView(R.layout.activity_landing); 

		Route.build();
		AnimatorSet set = new AnimatorSet();
    	set.playTogether(
    	    ObjectAnimator.ofFloat(findViewById(R.id.logo), "scaleX", 1, 1.2f),
    	    ObjectAnimator.ofFloat(findViewById(R.id.logo), "scaleY", 1, 1.2f),
    	    ObjectAnimator.ofFloat(findViewById(R.id.logo), "alpha", 0, 1, 1)
    	);
		set.setDuration(800).start();

		if(User.isSignedIn()) {
			
			findViewById(R.id.container).setVisibility(View.GONE);
	    	set.addListener(new SimpleAnimatorListener() {
	    		@Override
	    		public void onAnimationEnd(Animator animation) {
	    			// TODO Auto-generated method stub
	    			AppBase.launchActivity(MainMapActivity.class);
	    			finish();
	    		}
	    	});
			return;
		}
		
		findViewById(R.id.explore).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AppBase.launchActivity(MainMapActivity.class);
			}
		});
		
		findViewById(R.id.join).setOnClickListener(
			new View.OnClickListener() {
				@Override
			public void onClick(View view) {
				AppBase.launchActivity(LoginActivity.class);
			}
		});
	}
}
