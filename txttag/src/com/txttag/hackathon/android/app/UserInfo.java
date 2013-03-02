package com.txttag.hackathon.android.app;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.txttag.hackathon.android.activities.SplashActivity;
import com.txttag.hackathon.android.models.Tag;
import com.txttag.hackathon.android.net.TxtTagService;


public class UserInfo 
{
	private static final String PREF_EMAIL = "email";
	private static final String PREF_PUBLIC_MESSAGES = "public_messages";
	private static final String PREF_SEND_TO_SIMILAR_PLATES = "send_to_similar_plates";
	
	private static Context context;
	private static SharedPreferences prefs;
	
	private static String email;
	private static List<Tag> allTags = new ArrayList<Tag>();
	private static Tag activeTag;
	
	public static void init(Context context)
	{
		UserInfo.context = context;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	/**
	 * Retrieves up-to-date information about this user's set of registered tags.  Based
	 * on the server's data, UserInfo populates a list of all tags that this user owns, verifies
	 * the existence of the currently selected "active tag", and selects an active tag if one
	 * does not exist.
	 * 
	 * This user must already have an email registered in SharedPreferences for refreshUserData() to
	 * execute.  If there is no email on record, refreshUserData() will return without doing anything.
	 * 
	 * Run refreshUserData() in separate thread due to network request.
	 */
	public static void refreshUserData()
	{
		String email = getEmail();
		
		// If we don't have an email, we can't ask for associated tags
		if(email == null)
			return;
		
		TxtTagService service = new TxtTagService();
		
		allTags = service.getTags(email); 
		
		activeTag = new Tag();
		activeTag.state = prefs.getString("activeState", null);
		activeTag.plate = prefs.getString("activePlate", null);
		
		// If there is a pre-existing selection for "active tag", make sure this user actually owns
		// that tag.
		boolean foundTag = false;
		if(activeTag.isValidTag())
		{
			for(Tag tag : UserInfo.allTags)
			{
				if(tag.equals(activeTag))
				{
					foundTag = true;
					break;
				}
			}
		}
		
		// If, for some reason, the listed "active tag" is not in the list of all the user's tags,
		// then go ahead and change the "active tag" to the first tag in the list
		if( !foundTag && UserInfo.allTags.size() > 0 )
		{
			setActiveTagWithNoValidation( allTags.get(0) );	
		}
	}
	
	/**
	 * Returns this user's email address if its valid, otherwise returns null.
	 * 
	 * @return
	 */
	public static String getEmail()
	{
		if(email != null)
			return email;
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("email", null);
	}
	
	/**
	 * If email is null or blank, setEmail() does nothing.
	 * @param email
	 */
	public static void setEmail(String email)
	{
		if(email != null && email.length() > 0)
		{
			prefs.edit().putString(PREF_EMAIL, email).commit();
		}
	}
	
	/**
	 * Convenience method to return a valid email or a blank String.  This is useful when
	 * outputting the email to a textfield, where a null value might cause a problem.
	 * 
	 * @return
	 */
	public static String getEmailOrBlank()
	{
		String email = getEmail();
		
		return email != null ? email : "";
	}
	
	public static Tag getActiveTag()
	{
		return activeTag.clone();
	}
	
	public static void setActiveTag(Tag newTag)
	{
		if(newTag.isValidTag())
		{
			for(Tag tag : UserInfo.allTags)
			{
				if(tag.equals(newTag))
				{
					setActiveTagWithNoValidation(newTag.clone());
					break;
				}
			}
		}
	}
	
	/**
	 * Sets activeTag to newTag without varifying that newTag is in allTags, and also does not clone newTag
	 * 
	 * @param newTag
	 */
	private static void setActiveTagWithNoValidation(Tag newTag)
	{
		activeTag = newTag;
		// Commit changes to "active tag" back to preferences
		prefs.edit().putString("activeState", activeTag.state).putString("activePlate", activeTag.plate).commit();
	}
	
	public static List<Tag> getAllTags()
	{
		List<Tag> cloneList = new ArrayList<Tag>();
		
		for(Tag tag:allTags)
		{
			cloneList.add(tag.clone());
		}
		
		return cloneList;
	}
	
	public static boolean ownsTags()
	{
		return allTags.size() > 0;
	}
	
	public static boolean getShareMessagesPreference()
	{
		return prefs.getBoolean(PREF_PUBLIC_MESSAGES, false);
	}
	
	public static void setShareMessagesPreference(boolean pref)
	{
		prefs.edit().putBoolean(PREF_PUBLIC_MESSAGES, pref).commit();
	}
	
	public static boolean getSendToSimilarPlatesPreference()
	{
		return prefs.getBoolean(PREF_SEND_TO_SIMILAR_PLATES, false);
	}
	
	public static void setSendToSimilarPlatesPreference(boolean pref)
	{
		prefs.edit().putBoolean(PREF_SEND_TO_SIMILAR_PLATES, pref).commit();
	}
}
