package com.txttag.hackathon.android.activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;
import com.txttag.hackathon.android.R;
import com.txttag.hackathon.android.adapters.TagMessageAdapter;
import com.txttag.hackathon.android.app.UserInfo;
import com.txttag.hackathon.android.models.TagMessage;
import com.txttag.hackathon.android.net.JsonResponse;
import com.txttag.hackathon.android.net.TxtTagService;

public class ViewMyMessagesActivity extends BaseActivity 
{
	private static final String TAG = "SendMessageActivity";
	
//	private DialogFragment successDialog = new SuccessDialogFragment();
//	private DialogFragment noSuccessDialog = new NoSuccessDialogFragment();
//	private DialogFragment errorDialog = new ErrorDialogFragment();
	
	private String responseMessage = "";
	
	private PullToRefreshListView pullList;
	private View emptyDisplay;
	
	private ArrayList<TagMessage> messages = new ArrayList<TagMessage>();
	private TagMessageAdapter adapter;
	
	public ViewMyMessagesActivity() {
		super("My Messages");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_view_messages);
		
		emptyDisplay = findViewById(android.R.id.empty);
		
		pullList = (PullToRefreshListView) findViewById(R.id.pull_list);

		// Set a listener to be invoked when the list should be refreshed.
		pullList.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				refreshData();
			}
		});

		ListView actualListView = pullList.getRefreshableView();
		
		// Need to use the Actual ListView when registering for Context Menu
		//registerForContextMenu(actualListView);

		/**
		 * Add Sound Event Listener
		 */
		SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<ListView>(this);
		soundListener.addSoundEvent(State.PULL_TO_REFRESH, R.raw.pull_event);
		soundListener.addSoundEvent(State.RESET, R.raw.reset_sound);
		soundListener.addSoundEvent(State.REFRESHING, R.raw.refreshing_sound);
		pullList.setOnPullEventListener(soundListener);
		
		adapter = new TagMessageAdapter(this, messages);
		pullList.setAdapter(adapter);
		
//		pullList.setVisibility(View.INVISIBLE);
		emptyDisplay.setVisibility(View.INVISIBLE);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		refreshData();
	}
	
	private void refreshData()
	{
		(new Thread(new Runnable() {

			@Override
			public void run() {
				TxtTagService service = new TxtTagService();
				
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ViewMyMessagesActivity.this);
				
				//String state = prefs.getString("state", null);
				//String plate = prefs.getString("plate", null);
				
				if(UserInfo.activeTag == null)
				{
					runOnUiThread(new Runnable() {
						public void run() {
							AlertDialog.Builder builder = new AlertDialog.Builder(ViewMyMessagesActivity.this);
							builder.setMessage("We don't have a state and plate on file for you.  Please be sure to claim a tag so that you can view messages.")
							.setNeutralButton("Ok", new OnClickListener() {
		
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
				
				//messages.clear();
				//messages.addAll( service.getMessages() );
				Log.d(TAG, "Gettings messages for " + UserInfo.activeTag.state + ": " + UserInfo.activeTag.plate);
				final List<TagMessage> allMessages = service.getMessages(UserInfo.activeTag.state, UserInfo.activeTag.plate);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						messages.clear();
						messages.addAll(allMessages);
						adapter.notifyDataSetChanged();
						
						pullList.onRefreshComplete();
						
//						if(messages.size() > 0)
//						{
//							pullList.setVisibility(View.VISIBLE);
//							emptyDisplay.setVisibility(View.INVISIBLE);
//						}
//						else
//						{
//							pullList.setVisibility(View.INVISIBLE);
//							emptyDisplay.setVisibility(View.VISIBLE);
//						}
					}
					
				});
				
			}
			
		})).start();
	}
	
	/*
	public void showSuccessMessage()
	{
		successDialog.show(getSupportFragmentManager(), "SuccessFragment");
	}

	public void showUnsuccessfulMessage()
	{
		noSuccessDialog.show(getSupportFragmentManager(), "NoSuccessFragment");
	}
	
	public void showCommunicationErrorMessage()
	{
		errorDialog.show(getSupportFragmentManager(), "ErrorFragment");
	}
	
	private class SuccessDialogFragment extends DialogFragment
	{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(ViewMyMessages.this);
			builder.setMessage("Txt Sent Successfully!")
				.setNeutralButton("Cool", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.d(TAG, "Done sending message. Going home.");
					}
				});
			
			return builder.create();
		}
	}
	
	private class NoSuccessDialogFragment extends DialogFragment
	{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(ViewMyMessages.this);
			builder.setMessage("Error Sending Txt: " + '\n' + sendError)
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.d(TAG, "Error happened, so sending back to screen.");
					}
				});
			
			return builder.create();
		}
	}
	
	private class ErrorDialogFragment extends DialogFragment
	{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(ViewMyMessages.this);
			builder.setMessage("There was an error communicating with the server.  Please try again.")
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.d(TAG, "Error happened, so sending back to screen.");
					}
				});
			
			return builder.create();
		}
	}
	*/
}
