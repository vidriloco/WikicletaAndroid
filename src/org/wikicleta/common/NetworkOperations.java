package org.wikicleta.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class NetworkOperations {
	static String serverHost = "http://192.168.1.65:3000";
	
	public static int postJSONTo(String path, String jsonValue)  {
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
	    Log.e("WIKICLETA", jsonValue);
	    try {
			return client.execute(httpost).getStatusLine().getStatusCode();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return 404;
	}
}
