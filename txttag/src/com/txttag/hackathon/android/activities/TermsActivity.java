package com.txttag.hackathon.android.activities;

import android.os.Bundle;
import android.webkit.WebView;

import com.txttag.hackathon.android.R;

public class TermsActivity extends BaseActivity 
{

	public TermsActivity() 
	{
		super("TxtTag Terms");
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview_page);
		
		((WebView)findViewById(R.id.webview)).loadUrl("file:///android_asset/pages/terms.html");
	}

}
