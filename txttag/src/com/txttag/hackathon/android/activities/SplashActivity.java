package com.txttag.hackathon.android.activities;
import com.txttag.hackathon.android.R;
import com.txttag.hackathon.android.app.UserInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_splash);
		
		// Initialize UserInfo singleton, it represents this user throughout the application
		UserInfo.init(this);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		(new InitializeAppThread()).start();
	}
	
	
	/**
	 * Attempts to grab some initial user information from server.  Whether that
	 * attempt succeeds or fails, the thread then waits a few seconds and starts
	 * the first interactive activity in the app.
	 * 
	 * @author Matt
	 *
	 */
	private class InitializeAppThread extends Thread
	{
		@Override
		public void run() 
		{
			if(UserInfo.getEmail() != null)
			{
				UserInfo.refreshUserData();
			}
			
			waitAndStartApp();
		}
		
		private void waitAndStartApp()
		{
			// Wait a few seconds to leave splash screen up
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Intent intent = new Intent(SplashActivity.this, SendMessageActivity.class);
			startActivity(intent);
		}
	}
	
	
}
