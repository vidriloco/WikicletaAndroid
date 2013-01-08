package org.wikicleta.adapters;

import org.wikicleta.R;
import org.wikicleta.common.AppBase;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MenuEntryAdapter extends ArrayAdapter<MenuEntry> {

	public MenuEntryAdapter(Context context) {
		super(context, 0);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.sliding_menu, null);
		}
		
		final MenuEntry menuEntry = (MenuEntry) this.getItem(position);
		
		ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
		icon.setImageResource(menuEntry.iconResource);
		final TextView title = (TextView) convertView.findViewById(R.id.row_title);
		title.setText(menuEntry.textResource);
		
		convertView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intentActivity = new Intent(AppBase.currentActivity, menuEntry.activity);
				AppBase.currentActivity.startActivity(intentActivity);

				TransitionDrawable transition = (TransitionDrawable) v.getBackground();
				transition.startTransition(200);
				transition.setCrossFadeEnabled(true);
			}
			
		});
		
		return convertView;
	}

}