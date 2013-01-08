package org.wikicleta.activities;

import org.wikicleta.R;
import org.wikicleta.helpers.SlidingMenuAndActionBarHelper;

import android.app.Activity;
import android.os.Bundle;

public class HighlightsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_highlights);
        SlidingMenuAndActionBarHelper.load(this);
	}
}
