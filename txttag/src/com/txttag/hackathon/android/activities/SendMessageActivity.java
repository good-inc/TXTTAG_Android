package com.txttag.hackathon.android.activities;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.txttag.hackathon.android.R;
import com.txttag.hackathon.android.app.AppUtils;
import com.txttag.hackathon.android.models.MessageSentConfirmation;
import com.txttag.hackathon.android.net.JsonResponse;
import com.txttag.hackathon.android.net.TxtTagService;

public class SendMessageActivity extends BaseActivity 
{
	private static final String TAG = "SendMessageActivity";

	private Spinner stateSpinner;
	private ArrayAdapter<CharSequence> spinnerAdapter;
	private EditText plateInput;
	private EditText messageInput;
	
	private DialogFragment successDialog = new SuccessDialogFragment();
	private DialogFragment noSuccessDialog = new NoSuccessDialogFragment();
	private DialogFragment errorDialog = new ErrorDialogFragment();
	
	private LocationManager locationManager;
	private Geocoder geocoder;
	
	private String sendError = "";
	
	public SendMessageActivity() {
		super("Txt That Tag!");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_txt_that_tag);
		
		stateSpinner = (Spinner)findViewById(R.id.state_spinner);
		plateInput = (EditText)findViewById(R.id.tag);
		messageInput = (EditText)findViewById(R.id.message);
		
		spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.states, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		stateSpinner.setAdapter(spinnerAdapter);
		
		locationManager = (LocationManager)this.getSystemService(LOCATION_SERVICE);
		geocoder = new Geocoder(this);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		try {
			List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			//Log.d(TAG, "Address: " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());
			//Log.d(TAG, "State index: " + AppUtils.getStateIndexFromName(addresses.get(0).getAdminArea()));
			stateSpinner.setSelection(AppUtils.getStateIndexFromName(addresses.get(0).getAdminArea()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendTxt(View view)
	{
		Log.d(TAG, "Sending Txt...");
		
		final String state = AppUtils.getStateCodeFromName( stateSpinner.getSelectedItem().toString() );
		final String plate = plateInput.getText().toString();
		final String message = messageInput.getText().toString();
		
		Log.d(TAG, "State: " + state);
		
		hideKeyboard();
		
		this.showProgressDialog("Sending Message...");
		
		(new Thread(new Runnable() {

			@Override
			public void run() {
				TxtTagService service = new TxtTagService();
				
				final JsonResponse<MessageSentConfirmation> response = service.sendMessage(state, plate, message);
				
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						hideProgressDialog();
						
						if(response != null && response.success)
						{
							// successful
							stateSpinner.setSelection(0);
							plateInput.setText("");
							messageInput.setText("");
							showSuccessMessage();
						}
						else if(response != null)
						{
							// error processing message
							if(response.message != null && response.message.length() > 0)
								sendError = response.message;
							else
								sendError = "Unknown Issue.";
							showUnsuccessfulMessage();
						}
						else
						{
							// communication error
							showCommunicationErrorMessage();
						}
					}
					
				});
			}
			
		})).start();
	}
	
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
	
	public class SuccessDialogFragment extends DialogFragment
	{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(SendMessageActivity.this);
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
	
	public class NoSuccessDialogFragment extends DialogFragment
	{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(SendMessageActivity.this);
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
	
	public class ErrorDialogFragment extends DialogFragment
	{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(SendMessageActivity.this);
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
}
