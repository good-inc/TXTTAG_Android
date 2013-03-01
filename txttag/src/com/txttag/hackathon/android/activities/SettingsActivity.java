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
		sendToSimilarPlatesInput = (CheckBox)findViewById(R.id.send_to_similar_plates);
		saveButton = (Button)findViewById(R.id.save);
		
		tagList = (LinearLayout)findViewById(R.id.tag_list);
		noTags = (TextView)findViewById(R.id.no_tags);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		emailInput.setText(prefs.getString("email", ""));
		publicMessagesInput.setChecked(prefs.getBoolean("public_messages", false));
		
		if(UserInfo.allTags.size() == 0)
		{
			tagList.removeAllViews();
			tagList.addView(noTags);
		}
		else
		{
			tagList.removeAllViews();
			
			tagRadios = new ArrayList<RadioButton>();
			for(Tag tag : UserInfo.allTags)
			{
				View radioLayout = this.getLayoutInflater().inflate(R.layout.tag_radio, null);
				RadioButton radio = (RadioButton)radioLayout.findViewById(R.id.tag_radio);
				
				radio.setText(tag.state + ": " + tag.plate);
				
				if(tag.state == UserInfo.activeTag.state && tag.plate == UserInfo.activeTag.plate)
				{
					radio.setChecked(true);
					activeRadio = radio;
				}
				
				tagRadios.add(radio);
				tagList.addView(radio);
				
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
						
						UserInfo.activeTag = UserInfo.allTags.get(index);
						PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this).edit().putString("activeState", UserInfo.activeTag.state).putString("activePlate", UserInfo.activeTag.plate).commit();
					}
					
				});

			}
			
			if(activeRadio == null)
			{
				activeRadio = tagRadios.get(0);
				tagRadios.get(0).setChecked(true);
				UserInfo.activeTag = UserInfo.allTags.get(0);
				PreferenceManager.getDefaultSharedPreferences(this).edit().putString("activeState", UserInfo.activeTag.state).putString("activePlate", UserInfo.activeTag.plate).commit();
			}
		}
	}
	
	public void saveSettings(View view)
	{
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
		
		if(emailInput.getText().toString().length() > 0)
		{
			editor.putString("email", emailInput.getText().toString());
		}
		
		editor.putBoolean("public_messages", publicMessagesInput.isChecked());
		
		editor.commit();
		
		
	}
	
}
