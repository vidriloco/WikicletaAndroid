package org.wikicleta.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.wikicleta.helpers.Strings;

public class NetworkOperations {
	//public static String serverHost = "http://wikicleta.com";
	public static String serverHost = "http://50.56.30.227:3000/"; //solo para pruebas
	
	public static int postJSONTo(String path, String jsonValue) {
		HttpResponse response = NetworkOperations.postJSON(path, jsonValue);
		
		if(response == null)
			return 404;
		return response.getStatusLine().getStatusCode();
	}
	
	
	public static int putJSONTo(String path, String jsonValue) {
		HttpResponse response = NetworkOperations.putJSON(path, jsonValue);
		
		if(response == null)
			return 404;
		return response.getStatusLine().getStatusCode();
	}
	
	public static String getJSONExpectingStringGzipped(String path, boolean prefixed) {
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Accept-Encoding", "gzip");
		
		HttpResponse response = NetworkOperations.getJSON(path, prefixed, headers);
		Reader reader = null;
		StringWriter writer = null;
		String charset = "UTF-8"; // You should determine it based on response header.

		try {
		    InputStream gzippedResponse = response.getEntity().getContent();
		    InputStream ungzippedResponse = new GZIPInputStream(gzippedResponse);
		    reader = new InputStreamReader(ungzippedResponse, charset);
		    writer = new StringWriter();

		    char[] buffer = new char[10240];
		    for (int length = 0; (length = reader.read(buffer)) > 0;) {
		        writer.write(buffer, 0, length);
		    }
		} catch(Exception exception) {
			
		} finally {
			try {
				if(writer != null)
					writer.close();
				if(reader != null)
					reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if(writer != null)
			return writer.toString();
		return null;
	}
	
	public static String postJSONExpectingStringTo(String path, String jsonValue) {
		HttpResponse response = NetworkOperations.postJSON(path, jsonValue);
		try {
			if(response!=null)
				return Strings.inputStreamToString(response.getEntity().getContent());
			else
				return null;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new String();
	}
	
	public static String getJSONExpectingString(String path, boolean prefixed) {
		HttpResponse response = NetworkOperations.getJSON(path, prefixed);
		try {
			if(response!=null)
				return Strings.inputStreamToString(response.getEntity().getContent());
			else
				return null;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new String();
	}
	
	protected static HttpResponse getJSON(String path, boolean prefixed)  {
		return getJSON(path, prefixed, null);
	}
	
	protected static HttpResponse getJSON(String path, boolean prefixed, HashMap<String, String> extraHeaders)  {
	    HttpClient client = new DefaultHttpClient();
	    HttpGet request = null;
	    if(prefixed)
	    	request = new HttpGet(path);
	    else
	    	request = new HttpGet(serverHost.concat(path));
	    
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");
		if(extraHeaders != null) {
			for(String key : extraHeaders.keySet()) {
				String value = extraHeaders.get(key);
				request.setHeader(key, value);
			}
		}
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
	
	protected static HttpResponse postJSON(String path, String jsonValue)  {
	    HttpClient client = new DefaultHttpClient();
	    HttpPost request = new HttpPost(serverHost.concat(path));	
	    
	    StringEntity se;
		try {
			se = new StringEntity(jsonValue, HTTP.UTF_8);
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
	
	protected static HttpResponse putJSON(String path, String jsonValue)  {
	    HttpClient client = new DefaultHttpClient();
	    HttpPut request = new HttpPut(serverHost.concat(path));	
	    
	    StringEntity se;
		try {
			se = new StringEntity(jsonValue, HTTP.UTF_8);
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
