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
import org.mobility.wikicleta.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Display;

@SuppressLint("NewApi")
public class AppBase {
	
	public static Activity currentActivity;
	
	public static Display getScreenSize() {
		return currentActivity.getWindowManager().getDefaultDisplay();
	}
	
	public static String urlFor(String string) throws BadURLResourceException {
		if(string.equalsIgnoreCase("routes"))
			return "";
		else
			throw new AppBase.BadURLResourceException();
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
	         
	     } catch(Exception e){}
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