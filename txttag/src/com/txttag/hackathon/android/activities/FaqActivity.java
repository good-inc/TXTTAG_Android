package com.txttag.hackathon.android.activities;

import com.txttag.hackathon.android.R;

import android.os.Bundle;
import android.webkit.WebView;

public class FaqActivity extends BaseActivity
{

	public FaqActivity() 
	{
		super("TxtTag FAQ");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview_page);
		
		((WebView)findViewById(R.id.webview)).loadUrl("file:///android_asset/pages/faq.html");
	}
	
}
