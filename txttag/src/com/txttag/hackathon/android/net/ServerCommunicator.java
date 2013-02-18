package com.txttag.hackathon.android.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.net.http.AndroidHttpClient;
import android.util.Log;

public class ServerCommunicator 
{
	private static final String TAG = "ServerCommunicator";
	private static final String URL = "http://hack.txttag.me/mattcarrol/";
	private static ServerCommunicator instance;
	public static ServerCommunicator getInstance()
	{
		if(instance == null)
			instance = new ServerCommunicator();
		
		return instance;
	}
	
	private AndroidHttpClient client = AndroidHttpClient.newInstance(null);
	{
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 5000);
		HttpConnectionParams.setSoTimeout(client.getParams(), 5000);
	}
	
	public String makeGetRequest(String uri) throws IOException
	{
		String getMessagesUrl = URL + uri;//String.format("json_retrieve_messages.php?state=%s&plate=%s", "FL", "932VVZ");
		Log.d(TAG, "Sending GET request to: " + getMessagesUrl);
		HttpGet request = new HttpGet(getMessagesUrl);
		
		HttpResponse response;
		try {
			response = client.execute(request);
			return getResponse(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}
	
	public String makePostRequest(String uri, Map<String,String> params) throws IOException
	{
		String getMessagesUrl = URL + uri;
		HttpPost request = new HttpPost(getMessagesUrl);
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(params.size());
		for(Map.Entry<String, String> entry : params.entrySet())
		{
			nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			Log.d(TAG, "Adding parameter: " + entry.getKey() + " => " + entry.getValue());
		}
		request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		HttpResponse response;
		try {
			response = client.execute(request);
			return getResponse(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}
	
	public String getResponse(HttpResponse response) throws IOException
	{
		HttpEntity responseEntity = response.getEntity();
		BufferedReader in = new BufferedReader(new InputStreamReader(responseEntity.getContent()));
		
		StringBuilder responseBuilder = new StringBuilder();// <- important that this is string builder, 99 second dload time down to 1.5 seconds
		String lineIn;
		while( (lineIn = in.readLine()) != null )
		{
			responseBuilder.append(lineIn);
		}
		
		//Log.d(TAG, "Server Response: " + responseBuilder.toString());
		
		return responseBuilder.toString();
	}
}
