package com.txttag.hackathon.android.activities;

//import com.slidingmenu.lib.SlidingMenu;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.txttag.hackathon.android.R;
import com.txttag.hackathon.android.R.layout;
import com.txttag.hackathon.android.R.menu;
import com.txttag.hackathon.android.fragments.AppMenuListFragment;
import com.txttag.hackathon.android.net.TxtTagService;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.inputmethod.InputMethodManager;

public class BaseActivity extends SlidingFragmentActivity 
{
	private String title;
	protected ListFragment mFrag;

	private ProgressDialog waitDialog;
	
	public BaseActivity(String title) {
		this.title = title;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle(title);
		
		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
		mFrag = new AppMenuListFragment();
		t.replace(R.id.menu_frame, mFrag);
		t.commit();

		// customize the SlidingMenu
		SlidingMenu sm = getSlidingMenu();
		//sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowWidth(0);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		sm.setSlidingEnabled(true);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		this.getSlidingMenu().showContent();
		
	}
	
	protected void showProgressDialog(final String msg)
	{
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() {
				if(waitDialog == null)
				{
					waitDialog = new ProgressDialog(BaseActivity.this);
					waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					waitDialog.setCancelable(false);
				}
				
				waitDialog.setMessage(msg);
				waitDialog.show();
			}
		
		});
	}
	
	protected void hideProgressDialog()
	{
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() {
				if(waitDialog != null)
					waitDialog.hide();
			}
		
		});
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item)
	{
		switch(item.getItemId())
		{
			case android.R.id.home: this.getSlidingMenu().toggle(true); return true;
		}
		
		return super.onMenuItemSelected(featureId, item);
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getSupportMenuInflater().inflate(R.menu.activity_splash, menu);
//		return true;
//	}

}
