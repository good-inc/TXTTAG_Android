package com.txttag.hackathon.android.activities;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.txttag.hackathon.android.R;
import com.txttag.hackathon.android.app.UserInfo;
import com.txttag.hackathon.android.models.Tag;
import com.txttag.hackathon.android.net.TxtTagService;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class SplashActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_splash);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		final TxtTagService service = new TxtTagService();
		(new Thread(new Runnable() {

			@Override
			public void run() {
				String email = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).getString("email", null);
				
				if(email != null)
				{
					UserInfo.allTags = service.getTags(email); 
					
					String activeState = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).getString("activeState", null);
					String activePlate = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).getString("activePlate", null);
					
					if(activePlate != null && activeState != null)
					{
						for(Tag tag : UserInfo.allTags)
						{
							if(tag.state.equals(activeState) && tag.plate.equals(activePlate))
							{
								UserInfo.activeTag = tag;
								break;
							}
						}
					}
					
					
					if(UserInfo.activeTag == null && UserInfo.allTags.size() > 0)
					{
						UserInfo.activeTag = UserInfo.allTags.get(0);
						SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this).edit();
						editor.putString("activeState", UserInfo.activeTag.state).putString("activePlate", UserInfo.activeTag.plate).commit();
					}
					
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Intent intent = new Intent(SplashActivity.this, SendMessageActivity.class);
					startActivity(intent);
				}
				else
				{
				
					//boolean connected = service.verifyConnection();
					
					//if(connected)
					//{
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Intent intent = new Intent(SplashActivity.this, SendMessageActivity.class);
						//Intent intent = new Intent(SplashActivity.this, ViewMyMessagesActivity.class);
						startActivity(intent);
					//}
				}
			}
			
		})).start();
	}
}
