package org.wikicleta.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

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
	
	public static String getJSONExpectingString(String path, boolean prefixed) {
		HttpClient client = new DefaultHttpClient();
		
		HttpGet request = null;
		if(prefixed)
			request = new HttpGet(serverHost.concat(path));
		else
			request = new HttpGet(path);
	    request.setHeader("Accept", "application/json");
	    request.setHeader("Content-type", "application/json");
	    
		try {
			return EntityUtils.toString(client.execute(request).getEntity());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new String();
	}
	
	protected static HttpResponse postJSON(String path, String jsonValue)  {
	    HttpClient client = new DefaultHttpClient();
	    HttpPost request = new HttpPost(serverHost.concat(path));	
	   
	    StringEntity se;
		try {
			se = new StringEntity(jsonValue);
			request.setEntity(se);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");
	    //Handles what is returned from the page 
	    
    	HttpResponse response = null;
		try {
			response = client.execute(request);
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
