package org.wikicleta.activities.access;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;
import com.actionbarsherlock.app.SherlockActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AccessActivity extends SherlockActivity {
	
	protected void showGreetMessage() {
		LayoutInflater inflater = this.getLayoutInflater();
		View layout = inflater.inflate(R.layout.message,
		                               (ViewGroup) this.findViewById(R.id.toast_layout_root));
		
		ImageView icon = (ImageView) layout.findViewById(R.id.message_icon);
		icon.setImageDrawable(this.getResources().getDrawable(R.drawable.hand_icon));
		TextView text = (TextView) layout.findViewById(R.id.message_text);
		text.setTypeface(AppBase.getTypefaceLight());
		text.setText(R.string.profile_welcome);
		Toast toast = new Toast(this);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
		toast.setView(layout);
		toast.show();
	}
}
