package org.wikicleta.helpers;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;


public class TypefaceSpan extends MetricAffectingSpan {
	private static LruCache<String, Typeface> sTypefaceCache =new LruCache<String, Typeface>(12);
 
	private Typeface mTypeface;
 

	public TypefaceSpan(String fontName, Typeface typeface) {
		mTypeface = sTypefaceCache.get(fontName);
 
		if (mTypeface == null) {
			mTypeface = typeface;
 			sTypefaceCache.put(fontName, mTypeface);
		}
	}
 
	@Override
	public void updateMeasureState(TextPaint p) {
		p.setTypeface(mTypeface);
		p.setFlags(p.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
	}
 
	@Override
	public void updateDrawState(TextPaint tp) {
		tp.setTypeface(mTypeface);
		tp.setFlags(tp.getFlags() | Paint.SUBPIXEL_TEXT_FLAG);
	}
}
