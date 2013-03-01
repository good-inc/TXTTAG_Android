package com.txttag.hackathon.android.activities;

import java.util.ArrayList;
import java.util.List;

import com.txttag.hackathon.android.R;
import com.txttag.hackathon.android.app.UserInfo;
import com.txttag.hackathon.android.models.Tag;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class FeedbackActivity extends BaseActivity 
{
	private static final String TAG = "FeedbackActivity";

	private EditText nameInput;
	private EditText emailInput;
	private EditText commentInput;
	private Button submitButton;
	
	public FeedbackActivity() {
		super("Feedback");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_feedback);
		
		nameInput = (EditText)findViewById(R.id.name);
		emailInput = (EditText)findViewById(R.id.email);
		commentInput = (EditText)findViewById(R.id.comment);
		submitButton = (Button)findViewById(R.id.submit);
	}
	
	private void sendComment() 
	{
		
	}
	
}
