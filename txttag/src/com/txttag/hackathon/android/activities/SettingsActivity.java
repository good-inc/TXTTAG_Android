package com.txttag.hackathon.android.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.txttag.hackathon.android.R;
import com.txttag.hackathon.android.app.UserInfo;
import com.txttag.hackathon.android.models.Tag;
import com.txttag.hackathon.android.net.JsonResponse;
import com.txttag.hackathon.android.net.TxtTagService;

public class SettingsActivity extends BaseActivity 
{
	private static final String TAG = "SettingsActivity";

	private EditText emailInput;
	private CheckBox publicMessagesInput;
	private CheckBox sendToSimilarPlatesInput;
	private Button saveButton;
	
	private LinearLayout tagList;
	private TextView noTags;
	private List<RadioButton> tagRadios;
	private RadioButton activeRadio;
	
	public SettingsActivity() {
		super("Settings");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings);
		
		emailInput = (EditText)findViewById(R.id.email);
		publicMessagesInput = (CheckBox)findViewById(R.id.public_messages);
		publicMessagesInput.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d(TAG, "Change to public messages setting: " + isChecked);
				
				saveSettings(null);
			}
			
		});
		sendToSimilarPlatesInput = (CheckBox)findViewById(R.id.send_to_similar_plates);
		sendToSimilarPlatesInput.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d(TAG, "Change to public messages setting: " + isChecked);
				
				saveSettings(null);
			}
			
		});
		saveButton = (Button)findViewById(R.id.save);
		
		tagList = (LinearLayout)findViewById(R.id.tag_list);
		noTags = (TextView)findViewById(R.id.no_tags);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		refreshData();
	}
	
	private void refreshData()
	{
		this.showProgressDialog("Loading Your Tags");
		
		(new Thread(new Runnable() 
		{

			@Override
			public void run() 
			{
				UserInfo.refreshUserData();
				
				runOnUiThread(new Runnable() 
				{

					@Override
					public void run() 
					{
						syncUiStateWithCurrentData();
						SettingsActivity.this.hideProgressDialog();
					}
					
				});
				
			}
			
		})).start();
	}
	
	private void syncUiStateWithCurrentData()
	{
		// Update UI fields with current user preferences
		emailInput.setText(UserInfo.getEmailOrBlank());
		publicMessagesInput.setChecked(UserInfo.getShareMessagesPreference());
		sendToSimilarPlatesInput.setChecked(UserInfo.getSendToSimilarPlatesPreference());
		
		// Update tag selection view with current user data
		if(UserInfo.ownsTags())
			buildTagsView();
		else
			showEmptyTagsView();
		
	}
	
	private RadioButton createRadioButtonView(String text, boolean isSelected)
	{
		View radioLayout = this.getLayoutInflater().inflate(R.layout.tag_radio, null);
		RadioButton radio = (RadioButton)radioLayout.findViewById(R.id.tag_radio);
		
		radio.setText(text);
		radio.setChecked(isSelected);
		
		radio.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				RadioButton radio = (RadioButton) view;
				
				if(radio == activeRadio)
					return;
				
				radio.setChecked(true);
				
				for(RadioButton button : tagRadios)
				{
					if(button != radio)
					{
						button.setChecked(false);
					}
				}
				activeRadio = (RadioButton)radio;
				int index = tagRadios.indexOf(activeRadio);
				
				UserInfo.setActiveTag( UserInfo.getAllTags().get(index) );
			}
			
		});
		
		return radio;
	}
	
	private void buildTagsView()
	{
		List<Tag> allTags = UserInfo.getAllTags();
		
		tagList.removeAllViews();
		
		tagRadios = new ArrayList<RadioButton>();
		for(Tag tag : allTags)
		{
			RadioButton radio = createRadioButtonView(tag.state + ": " + tag.plate, tag.equals(UserInfo.getActiveTag()));
			if(radio.isChecked())
				activeRadio = radio;
			
			tagRadios.add(radio);
			tagList.addView(radio);
		}
		
		if(activeRadio == null)
		{
			activeRadio = tagRadios.get(0);
			tagRadios.get(0).setChecked(true);
			UserInfo.setActiveTag( allTags.get(0) );
		}
	}
	
	private void showEmptyTagsView()
	{
		tagList.removeAllViews();
		tagList.addView(noTags);
	}
	
	public void saveSettings(View view)
	{
		Log.d(TAG,"Saving new settings");
		Log.d(TAG," - email: " + emailInput.getText().toString());
		Log.d(TAG," - public messages: " + publicMessagesInput.isChecked());
		Log.d(TAG," - similar messages: " + sendToSimilarPlatesInput.isChecked());
		
		UserInfo.setEmail(emailInput.getText().toString());
		UserInfo.setShareMessagesPreference(publicMessagesInput.isChecked());
		UserInfo.setSendToSimilarPlatesPreference(sendToSimilarPlatesInput.isChecked());
		
		refreshData();
	}
	
}
