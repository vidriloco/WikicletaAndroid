package com.wikicleta.common;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.*;

import com.wikicleta.helpers.JSONStringMerger;

import android.util.Log;

public class NetworkOperations {
	static String serverHost = "http://192.168.1.65:3000";
	
	public static String postTo(String path, Map<?,?> params) throws ClientProtocolException, IOException {
		return postTo(path, params, null);
	}
	
	public static String postTo(String path, Map<?,?> params, JSONStringMerger jsonMerger) throws ClientProtocolException, IOException {
	    DefaultHttpClient httpclient = new DefaultHttpClient();
	    HttpPost httpost = new HttpPost(serverHost.concat(path));	    

	    String jsonValue = JSONValue.toJSONString(params);
	    StringEntity se = new StringEntity(jsonValue);
    	Log.i("WIKICLETA", "Params " + jsonValue);

	    if(jsonMerger != null)
	    	jsonMerger.mergeJSONString(jsonValue);
	    
	    httpost.setEntity(se);

	    httpost.setHeader("Accept", "application/json");
	    httpost.setHeader("Content-type", "application/json");
	    //Handles what is returned from the page 
	    
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
	    return httpclient.execute(httpost, responseHandler);
	}
}
