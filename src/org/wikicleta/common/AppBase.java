package org.wikicleta.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.wikicleta.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;

@SuppressLint("NewApi")
public class AppBase {
	
	public static Activity currentActivity;
	protected static Typeface font;
	
	public static Display getScreenSize() {
		return currentActivity.getWindowManager().getDefaultDisplay();
	}

	public static Typeface getDefaultTypeface(String type) {
		if(font == null)
			font = Typeface.createFromAsset(currentActivity.getAssets(), "GothamRnd-"+type+".ttf");  
		return font;
	}
	
	public static String urlFor(String string) throws BadURLResourceException {
		if(string.equalsIgnoreCase("routes"))
			return "";
		else
			throw new AppBase.BadURLResourceException();
	}
	
	public static void launchActivity(Class<?> activity) {
		launchActivityWithBundle(activity, null);
	}
	
	public static void launchActivityWithBundle(Class<?> activity, Bundle bundle) {
		Intent intentActivity = new Intent(AppBase.currentActivity, activity);
		if(bundle != null)
			intentActivity.putExtras(bundle);
		AppBase.currentActivity.startActivity(intentActivity);
	}
	
	public static void launchActivityAnimated(Class<?> activity) {
		Intent intentActivity = new Intent(AppBase.currentActivity, activity);
		//intentActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		AppBase.currentActivity.startActivity(intentActivity);
    	AppBase.currentActivity.overridePendingTransition(R.anim.slide_up,
                R.anim.slide_down);
	}
	
	private static String fetchResource(String resource) {
		InputStream is = null;
	    String result = new String();
	    try{
	         HttpClient httpclient = new DefaultHttpClient();
	         HttpGet get = new HttpGet(urlFor(resource));
	         HttpResponse response = httpclient.execute(get);
	         HttpEntity entity = response.getEntity();
	         is = entity.getContent();
	     }catch(Exception e){ }

	      try{
	         BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
	         StringBuilder sb = new StringBuilder();
	         String line = null;
	         while ((line = reader.readLine()) != null) {
	             sb.append(line + "\n");
	         }
	         is.close();
	         result=sb.toString();
	         
	     } catch(Exception e){  }
	     return result;
	}
	
	public static JSONArray fetchResourceAsArray(String resource) {
		String result = fetchResource(resource);
	    JSONArray json = null;
	    try{
	    	 // Handle empty results arising from errors on network comunication
	    	 if(result.isEmpty()) {
	    		 if(resource.equalsIgnoreCase("routes"))
	    			 result = readRawTextFile(R.raw.routes);
	    	 }
	         json = new JSONArray(result);
	     }catch(JSONException e){}
	     return json;
	}
	
	public static String fetchResourceAsString(String resource) {
		return fetchResource(resource).replaceAll("[^0-9]","");
	}
	
	public static InputStream rawTextStream(int resId) {
		return currentActivity.getResources().openRawResource(resId);
	}
	
	public static String readRawTextFile(int resId)
    {
		InputStreamReader inputreader = new InputStreamReader(rawTextStream(resId));
		BufferedReader buffreader = new BufferedReader(inputreader);
		String line;
		StringBuilder text = new StringBuilder();
		try {
			while (( line = buffreader.readLine()) != null) {
                   text.append(line);
                   text.append('\n');
                 }
           } catch (IOException e) {
               return null;
           }
		return text.toString();
    }
	
	public static class BadURLResourceException extends Exception
	{

		private static final long serialVersionUID = 1L;
		
	}
}