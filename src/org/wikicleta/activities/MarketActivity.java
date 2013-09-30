package org.wikicleta.activities;

import org.wikicleta.R;
import org.wikicleta.helpers.SlidingMenuBuilder;

import android.app.Activity;
import android.os.Bundle;

public class MarketActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_highlights);
        SlidingMenuBuilder.loadOnLeft(this);
	}
}
