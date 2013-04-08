package com.txttag.hackathon.android.activities;

import java.util.ArrayList;
import java.util.List;

import com.txttag.hackathon.android.R;
import com.txttag.hackathon.android.app.UserInfo;
import com.txttag.hackathon.android.models.Tag;
import com.txttag.hackathon.android.models.TagMessage;
import com.txttag.hackathon.android.net.TxtTagService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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
		
		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sendComment();
			}
			
		});
		
		emailInput.setText(UserInfo.getEmail());
	}
	
	private void clearFields()
	{
		nameInput.setText("");
		emailInput.setText("");
		commentInput.setText("");
	}
	
	private void sendComment() 
	{
		final String name = nameInput.getText().toString();
		final String email = emailInput.getText().toString();
		final String comment = commentInput.getText().toString();
		
		(new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				TxtTagService service = new TxtTagService();
				
				showProgressDialog("Sending Message...");
				
				boolean success = service.sendFeedback(email, name, comment);
				
				hideProgressDialog();
				
				if(success)
				{
					runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackActivity.this);
							builder.setMessage("Thanks for the input!")
							.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
		
								@Override
								public void onClick(DialogInterface dialog, int which) {
									clearFields();
									dialog.dismiss();
								}
								
							});
							
							builder.create().show();
						}
					});
					return;
				}
				else
				{
					runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackActivity.this);
							builder.setMessage("Something went wrong.  Could not send message.")
							.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
		
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
								
							});
							
							builder.create().show();
						}
					});
					return;
				}
			}
			
		})).start();
	}
	
}
