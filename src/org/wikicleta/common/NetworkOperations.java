package org.wikicleta.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

public class NetworkOperations {
	static String serverHost = "http://wikicleta.com";
	
	public static int postJSONTo(String path, String jsonValue) {
		HttpResponse response = NetworkOperations.postJSON(path, jsonValue);
		
		if(response == null)
			return 404;
		
		return response.getStatusLine().getStatusCode();
	}
	
	public static String postJSONExpectingStringTo(String path, String jsonValue) {
		HttpResponse response = NetworkOperations.postJSON(path, jsonValue);
		if(response==null)
			return new String();
		try {
			return response.getEntity().getContent().toString();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String();
	}
	
	protected static HttpResponse postJSON(String path, String jsonValue)  {
	    HttpClient client = new DefaultHttpClient();
	    HttpPost httpost = new HttpPost(serverHost.concat(path));	
	   
	    StringEntity se;
		try {
			se = new StringEntity(jsonValue);
		    httpost.setEntity(se);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    httpost.setHeader("Accept", "application/json");
	    httpost.setHeader("Content-type", "application/json");
	    //Handles what is returned from the page 
	    
	   
    	HttpResponse response = null;
		try {
			response = client.execute(httpost);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	return response;
	}
}
