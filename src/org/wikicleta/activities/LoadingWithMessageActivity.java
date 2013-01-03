package org.wikicleta.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

public class LoadingWithMessageActivity extends Activity {
	
	protected View previousContainerView;
	protected View messageContainerView;
	protected TextView viewMessage;

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	protected void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			messageContainerView.setVisibility(View.VISIBLE);
			messageContainerView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							previousContainerView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			messageContainerView.setVisibility(View.VISIBLE);
			messageContainerView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							previousContainerView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			messageContainerView.setVisibility(show ? View.VISIBLE : View.GONE);
			previousContainerView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
}
