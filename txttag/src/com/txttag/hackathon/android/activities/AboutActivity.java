package com.txttag.hackathon.android.activities;

import android.os.Bundle;
import android.webkit.WebView;

import com.txttag.hackathon.android.R;

public class AboutActivity extends BaseActivity
{

	public AboutActivity() 
	{
		super("About TxtTag");
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview_page);
	}

}
