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

public class NetworkOperations {
	static String serverHost = "http://192.168.0.7:3000/";
	
	public static String postTo(String path, Map<?,?> params) throws ClientProtocolException, IOException {
	    DefaultHttpClient httpclient = new DefaultHttpClient();
	    HttpPost httpost = new HttpPost(serverHost.concat(path));	    

	    StringEntity se = new StringEntity(JSONValue.toJSONString(params));

	    httpost.setEntity(se);

	    httpost.setHeader("Accept", "application/json");
	    httpost.setHeader("Content-type", "application/json");
	    
	    //Handles what is returned from the page 
	    
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
	    return httpclient.execute(httpost, responseHandler);
	}
}
