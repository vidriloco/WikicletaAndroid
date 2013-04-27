package org.wikicleta.access.activities;

import org.wikicleta.R;
import org.wikicleta.activities.MainMapActivity;
import org.wikicleta.common.AppBase;
import org.wikicleta.helpers.SimpleAnimatorListener;
import org.wikicleta.models.User;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

public class LandingActivity extends Activity {
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AppBase.currentActivity = this;
		this.setContentView(R.layout.activity_landing); 
		RelativeLayout actionsMenuContainer = (RelativeLayout) findViewById(R.id.container);
		animate(actionsMenuContainer).setDuration(0).translationY(150).start();

		AnimatorSet set = new AnimatorSet();
    	set.playTogether(
    	    ObjectAnimator.ofFloat(findViewById(R.id.logo), "scaleX", 1, 1.2f),
    	    ObjectAnimator.ofFloat(findViewById(R.id.logo), "scaleY", 1, 1.2f),
    	    ObjectAnimator.ofFloat(findViewById(R.id.logo), "alpha", 0, 1, 1)
    	);
		set.setDuration(1500).start();
		animate(actionsMenuContainer).setDuration(800).translationY(-150).start();

		if(User.isRegisteredLocally()) {
			
			findViewById(R.id.container).setVisibility(View.GONE);
			findViewById(R.id.cycle_the_city).setVisibility(View.VISIBLE);
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
		
		TextView exploreText = (TextView) this.findViewById(R.id.explore_text);
		exploreText.setTypeface(AppBase.getTypefaceLight());
		
		TextView joinText = (TextView) this.findViewById(R.id.join_text);
		joinText.setTypeface(AppBase.getTypefaceLight());
		
		TextView loginText = (TextView) this.findViewById(R.id.login_text);
		loginText.setTypeface(AppBase.getTypefaceLight());
		
		findViewById(R.id.explore_container).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AppBase.launchActivity(MainMapActivity.class);
			}
		});
		
		findViewById(R.id.join_container).setOnClickListener(
			new View.OnClickListener() {
				@Override
			public void onClick(View view) {
				AppBase.launchActivity(RegistrationActivity.class);
			}
		});
		
		findViewById(R.id.login_container).setOnClickListener(
				new View.OnClickListener() {
				@Override
			public void onClick(View view) {
				AppBase.launchActivity(LoginActivity.class);
			}
		});
	}
}
