package com.txttag.hackathon.android.net;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.txttag.hackathon.android.models.SuccessMessage;
import com.txttag.hackathon.android.models.Tag;
import com.txttag.hackathon.android.models.TagMessage;
import com.txttag.hackathon.android.models.User;

public class TxtTagService 
{	
	private static final String TAG = "TxtTagService";
	
	private Gson gson = new Gson();
	
	public boolean verifyConnection()
	{
		String uri = String.format("json_verify_connection.php");
		
		try {
			Log.d(TAG, "Checking Connection With Server.");
			ServerCommunicator.getInstance().makeGetRequest(uri);
			
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return false;
		}
	}
	
	public JsonResponse<Object> registerTag(String state, String plate, String email)
	{
		String uri = String.format("json_register_tag.php");
		
		try {
			Log.d(TAG, "Registering Tag.");
			Map<String, String> params = new HashMap<String, String>();
			params.put("state", state);
			params.put("plate", plate);
			params.put("email", email);
			String response = ServerCommunicator.getInstance().makePostRequest(uri, params);
			
			Log.d(TAG, response);
			Type responseType = new TypeToken<JsonResponse<Object>>(){}.getType();
			JsonResponse<Object> jsonResponse = gson.fromJson(response, responseType);
			
			Log.d(TAG, "Json Response: " + jsonResponse.toString());
			
			return jsonResponse;
			//Type responseType = new TypeToken<JsonResponse<List<JsonCategory>>>() {}.getType();
			//JsonResponse<List<JsonCategory>> response = gson.fromJson(serverResponse, responseType);
			
			//if(response == null)
			//	throw new InvalidObjectException("Gson did not deserialize the provided JSON.  This is sometimes the result of generics issues within the desired POJO.  JSON: " + serverResponse);
			
			//return response;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
	}
	
	public List<Tag> getTags(String email)
	{
		String uri = String.format("json_get_tags.php");
		
		try {
			Log.d(TAG, "Gettings tags.");
			Map<String, String> params = new HashMap<String, String>();
			params.put("email", email);
			String response = ServerCommunicator.getInstance().makePostRequest(uri, params);
			
			Log.d(TAG, response);
			Type responseType = new TypeToken<JsonResponse<List<Tag>>>(){}.getType();
			JsonResponse<List<Tag>> jsonResponse = gson.fromJson(response, responseType);
			
			Log.d(TAG, "Json Response: " + jsonResponse.toString());
			
			return jsonResponse.data;
			//Type responseType = new TypeToken<JsonResponse<List<JsonCategory>>>() {}.getType();
			//JsonResponse<List<JsonCategory>> response = gson.fromJson(serverResponse, responseType);
			
			//if(response == null)
			//	throw new InvalidObjectException("Gson did not deserialize the provided JSON.  This is sometimes the result of generics issues within the desired POJO.  JSON: " + serverResponse);
			
			//return response;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return new ArrayList<Tag>();
		}
	}
	
	public List<TagMessage> getMessages(String state, String plate)
	{
		String uri = String.format("json_retrieve_messages.php?state=%s&plate=%s", state, plate);
		
		try {
			Log.d(TAG, "Retrieving messages.");
			String response = ServerCommunicator.getInstance().makeGetRequest(uri);
			
			Log.d(TAG, response);
			Type responseType = new TypeToken<JsonResponse<List<TagMessage>>>(){}.getType();
			JsonResponse<List<TagMessage>> jsonResponse = gson.fromJson(response, responseType);
			
			Log.d(TAG, "Json Response: " + jsonResponse.toString());
			
			return jsonResponse.data;
			//Type responseType = new TypeToken<JsonResponse<List<JsonCategory>>>() {}.getType();
			//JsonResponse<List<JsonCategory>> response = gson.fromJson(serverResponse, responseType);
			
			//if(response == null)
			//	throw new InvalidObjectException("Gson did not deserialize the provided JSON.  This is sometimes the result of generics issues within the desired POJO.  JSON: " + serverResponse);
			
			//return response;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return new ArrayList<TagMessage>();
		}
		
	}
	
	public JsonResponse<Object> sendMessage(String state, String plate, String message)
	{
		String uri = String.format("json_send_message.php");
		
		try {
			Log.d(TAG, "Sending Message.");
			Map<String, String> params = new HashMap<String, String>();
			params.put("state", state);
			params.put("plate", plate);
			params.put("subject", "Subject");
			params.put("message", message);
			String response = ServerCommunicator.getInstance().makePostRequest(uri, params);
			
			Log.d(TAG, response);
			Type responseType = new TypeToken<JsonResponse<Object>>(){}.getType();
			JsonResponse<Object> jsonResponse = gson.fromJson(response, responseType);
			
			Log.d(TAG, "Json Response: " + jsonResponse.toString());
			
			return jsonResponse;
			//Type responseType = new TypeToken<JsonResponse<List<JsonCategory>>>() {}.getType();
			//JsonResponse<List<JsonCategory>> response = gson.fromJson(serverResponse, responseType);
			
			//if(response == null)
			//	throw new InvalidObjectException("Gson did not deserialize the provided JSON.  This is sometimes the result of generics issues within the desired POJO.  JSON: " + serverResponse);
			
			//return response;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
	}
	
	public void updateSettings(User user)
	{
		
	}
	
}
