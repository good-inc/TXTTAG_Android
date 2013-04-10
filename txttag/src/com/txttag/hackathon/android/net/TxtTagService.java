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
import com.txttag.hackathon.android.models.MessageSentConfirmation;
import com.txttag.hackathon.android.models.ServiceStats;
import com.txttag.hackathon.android.models.SuccessMessage;
import com.txttag.hackathon.android.models.Tag;
import com.txttag.hackathon.android.models.TagMessage;
import com.txttag.hackathon.android.models.User;

public class TxtTagService 
{	
	private static final String TAG = "TxtTagService";
	
	private Gson gson = new Gson();
	
	/**
	 * Associate plate represented by [plate] and [state] with [email].  Set [areMessagesPublic] to allow anyone to view messages
	 * sent to given plate.  Set sendToSimilar to true to send duplicate messages to other plate names that alphanumerically appear
	 * similar to [plate].
	 * 
	 * Service API:
	 * 	"action" => "claim"
	 * 	"plate" => [plate]
	 * 	"state" => [2 digit state code]
	 * 	"email" => [email to associate with given plate]
	 * 	"custom" => "0" // to be implemented in service later
	 * 	"public" => [1|0]
	 * 	"share" => [1|0]
	 * 
	 * Service Response:
	 * {
	 *     “success”:[“true”|”false”],
	 *     “message”:[“Your tag is registered, now go send a nice message to someone!”|”That’s your tag!”|”That tag is already registered, bro.  Send us a message.”}],
	 *     “data”:null
	 * }
	 * 
	 * @param state
	 * @param plate
	 * @param email
	 * @param custom
	 * @param areMessagesPublic
	 * @param sendToSimilarTags
	 * @return
	 */
	public JsonResponse<Object> registerTag(String state, String plate, String email, boolean areMessagesPublic, boolean sendToSimilarPlates)
	{
		String uri = String.format("process_json.php");
		
		try {
			Log.d(TAG, "Registering Tag.");
			Map<String, String> params = new HashMap<String, String>();
			params.put("action", "claim");
			params.put("state", state);
			params.put("plate", plate);
			params.put("email", email);
			params.put("custom", "0");
			params.put("public", areMessagesPublic ? "1" : "0");
			params.put("share", sendToSimilarPlates ? "1" : "0");
			String response = ServerCommunicator.getInstance().makePostRequest(uri, params);
			
			Log.d(TAG, response);
			Type responseType = new TypeToken<JsonResponse<Object>>(){}.getType();
			JsonResponse<Object> jsonResponse = gson.fromJson(response, responseType);
			
			Log.d(TAG, "Json Response: " + jsonResponse.toString());
			
			return jsonResponse;
		} catch (Exception e) {
			e.printStackTrace();
			
			return null;
		}
	}
	
	/**
	 * Unregister plate[s] associated with [email].  To delete ALL plates associated with [email], provide empty [plate] and [state].
	 * To unregister a single plate associated with [email] then provide a [state] and [plate].
	 * 
	 * Service API:
	 * 	"action" => "leave"
	 * 	"plate" => [plate|[empty]]
	 * 	"state" => [2 digit state code|[empty]]
	 * 	"email" => [email to associate with given plate]
	 * 
	 * Service Response:
	 * {
	 *     “success”:[“true”|”false”],
	 *     “message”:[english message confirming single deletion or all deletions],
	 *     “data”:null
	 * }
	 * 
	 * @param state
	 * @param plate
	 * @param email
	 * @return
	 */
	public JsonResponse<Object> unregisterTag(String email, String state, String plate)
	{
		String uri = String.format("process_json.php");
		
		try {
			Log.d(TAG, "Registering Tag.");
			Map<String, String> params = new HashMap<String, String>();
			params.put("action", "leave");
			if(state.length() > 0 && plate.length() > 0)
			{
				params.put("state", state);
				params.put("plate", plate);
			}
			params.put("email", email);
			String response = ServerCommunicator.getInstance().makePostRequest(uri, params);
			
			Log.d(TAG, response);
			Type responseType = new TypeToken<JsonResponse<Object>>(){}.getType();
			JsonResponse<Object> jsonResponse = gson.fromJson(response, responseType);
			
			Log.d(TAG, "Json Response: " + jsonResponse.toString());
			
			return jsonResponse;
		} catch (Exception e) {
			e.printStackTrace();
			
			return null;
		}
	}
	
	/**
	 * Retrieves all tags owned by [email].
	 * 
	 * Service API:
	 *  "action" => "list"
	 *  "email" => "email"
	 *  
	 * Service Response:
	 * {
	 * 	"success":["true"|"false"],
	 * 	"message":[readable message if needed|""],
	 *  "data":[
	 *  	{
	 *  		"state":[2 digit state code],
	 *  		"plate":[plate]
	 *  	},
	 *  	...
	 *  ]
	 * }
	 * 
	 * @param email
	 * @return
	 */
	public List<Tag> getTags(String email)
	{
		String uri = String.format("process_json.php");
		
		try {
			Log.d(TAG, "Gettings tags.");
			Map<String, String> params = new HashMap<String, String>();
			params.put("action", "list");
			params.put("email", email);
			String response = ServerCommunicator.getInstance().makePostRequest(uri, params);
			
			Log.d(TAG, response);
			Type responseType = new TypeToken<JsonResponse<List<Tag>>>(){}.getType();
			JsonResponse<List<Tag>> jsonResponse = gson.fromJson(response, responseType);
			
			Log.d(TAG, "Json Response: " + jsonResponse.toString());
			
			if(!jsonResponse.success)
				return new ArrayList<Tag>();
			
			return jsonResponse.data;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return new ArrayList<Tag>();
		}
	}
	
	/**
	 * Retrieves list of messages which have been sent to the person who claimed the plate represented
	 * by [plate] and [state].
	 * 
	 * Service API:
	 *  "action" = "view"
	 *  "plate" = [plate]
	 *  "state" = [state]
	 *  
	 * Service Response:
	 * {
	 * 	"success":["true"|"false"],
	 *  "message":[readable message if needed|""],
	 *  "data":[
	 *  	{
	 *  		"date":[date and time of message sent],
	 *  		"tag":[plate],
	 *  		"state":[2 digit state code],
	 *  		"message":[the message that was sent]
	 *  	},
	 *  	...
	 *  ]
	 * }
	 * 
	 * @param state
	 * @param plate
	 * @return
	 */
	public List<TagMessage> getMessages(String state, String plate, String email)
	{
		String uri = String.format("process_json.php");
		
		try {
			Log.d(TAG, "Retrieving messages.");
			Map<String, String> params = new HashMap<String, String>();
			params.put("action", "view");
			params.put("state", state);
			params.put("plate", plate);
			String response = ServerCommunicator.getInstance().makePostRequest(uri, params);
			
			Log.d(TAG, response);
			Type responseType = new TypeToken<JsonResponse<List<TagMessage>>>(){}.getType();
			JsonResponse<List<TagMessage>> jsonResponse = gson.fromJson(response, responseType);
			
			Log.d(TAG, "Json Response: " + jsonResponse.toString());
			
			if(!jsonResponse.success)
				return new ArrayList<TagMessage>();
			
			return jsonResponse.data;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return new ArrayList<TagMessage>();
		}
		
	}
	
	/**
	 * Sends [message] to the person who has claimed the plate signified by [plate] and [state].
	 * 
	 * Service API:
	 *  "action" => "text"
	 *  "plate" => [plate]
	 *  "state" => [state]
	 *  "text" => [message]
	 * 
	 * Service Response:
	 * {
	 * 	"success":[true|false],
	 *  "message":[english message that can be shown to user],
	 *  "state" : [2 digit state code that message was sent to],
	 *  "plate" : [plate the message was sent to],
	 *  "text" : [message that was sent],
	 * }
	 * 
	 * @param state
	 * @param plate
	 * @param message
	 * @return
	 */
	public JsonResponse<MessageSentConfirmation> sendMessage(String state, String plate, String message)
	{
		String uri = String.format("process_json.php");
		
		try {
			Log.d(TAG, "Sending Message.");
			Map<String, String> params = new HashMap<String, String>();
			params.put("action", "text");
			params.put("state", state);
			params.put("plate", plate);
			params.put("text", message);
			String response = ServerCommunicator.getInstance().makePostRequest(uri, params);
			
			Log.d(TAG, response);
			Type responseType = new TypeToken<JsonResponse<MessageSentConfirmation>>(){}.getType();
			JsonResponse<MessageSentConfirmation> jsonResponse = gson.fromJson(response, responseType);
			
			Log.d(TAG, "Json Response: " + jsonResponse.toString());
			
			return jsonResponse;
		} catch (Exception e) {
			e.printStackTrace();
			
			return null;
		}
	}
	
	
	
	/**
	 * Retrieves list of messages which have been sent to the person who claimed the plate represented
	 * by [plate] and [state].
	 * 
	 * Service API:
	 *  "action" = "contact"
	 *  "email" = [email]
	 *  "name" = [userName]
	 *  "text" = [message]
	 *  
	 * Service Response:
	 * {
	 * 	"success":["true"|"false"],
	 *  "message":[readable message if needed],
	 *  "data":null
	 * }
	 * 
	 * @param state
	 * @param plate
	 * @return
	 */
	public boolean sendFeedback(String email, String fullName, String message)
	{
		String uri = String.format("process_json.php");
		
		try {
			Log.d(TAG, "Sending feedback.");
			Map<String, String> params = new HashMap<String, String>();
			params.put("action", "contact");
			params.put("email", email);
			params.put("name", fullName);
			params.put("text", message);
			String response = ServerCommunicator.getInstance().makePostRequest(uri, params);
			
			Log.d(TAG, response);
			Type responseType = new TypeToken<JsonResponse<List<Object>>>(){}.getType();
			JsonResponse<List<Object>> jsonResponse = gson.fromJson(response, responseType);
			
			Log.d(TAG, "Json Response: " + jsonResponse.toString());
			
			return jsonResponse.success;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return false;
		}
		
	}
	
	
	/**
	 * Retrieves statistics from service.  Specifically, retrieves the total number of tags registered in the
	 * system along with the total number of messages sent through the system.
	 * 
	 * Service API:
	 *  "action" = "stats"
	 *  
	 * Service Response:
	 * {
	 * 	"success":["true"|"false"],
	 *  "tags":[number of registered tags in system],
	 *  "msgs":[number of messages sent through system]
	 * }
	 * 
	 * @param state
	 * @param plate
	 * @return
	 */
	public ServiceStats getServiceStats()
	{
		String uri = String.format("process_json.php");
		
		try {
			Log.d(TAG, "Retrieving Stats.");
			Map<String, String> params = new HashMap<String, String>();
			params.put("action", "stats");
			String response = ServerCommunicator.getInstance().makePostRequest(uri, params);
			
			Log.d(TAG, response);
			Type responseType = new TypeToken<JsonResponse<ServiceStats>>(){}.getType();
			JsonResponse<ServiceStats> jsonResponse = gson.fromJson(response, responseType);
			
			Log.d(TAG, "Json Response: " + jsonResponse.toString());
			
			return jsonResponse.data;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
		
	}
	
}
