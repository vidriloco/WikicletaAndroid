package org.wikicleta.activities;

import org.wikicleta.R;
import org.wikicleta.helpers.SlidingMenuAndActionBarHelper;

import android.app.Activity;
import android.os.Bundle;

public class PlacesActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_places);
        SlidingMenuAndActionBarHelper.load(this);
	}
}
