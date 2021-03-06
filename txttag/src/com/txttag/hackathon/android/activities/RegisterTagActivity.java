package com.txttag.hackathon.android.activities;

import java.io.IOException;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.txttag.hackathon.android.R;
import com.txttag.hackathon.android.app.AppUtils;
import com.txttag.hackathon.android.net.JsonResponse;
import com.txttag.hackathon.android.net.TxtTagService;

public class RegisterTagActivity extends BaseActivity 
{
	private static final String TAG = "SendMessageActivity";

	private Spinner stateSpinner;
	private ArrayAdapter<CharSequence> spinnerAdapter;
	private EditText plateInput;
	private String email;
	
	private DialogFragment successDialog = new SuccessDialogFragment();
	private DialogFragment noSuccessDialog = new NoSuccessDialogFragment();
	private DialogFragment errorDialog = new ErrorDialogFragment();
	
	private LocationManager locationManager;
	private Geocoder geocoder;
	
	private String receivedMessage = "";
	
	public RegisterTagActivity() {
		super("Register Your Tag");
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register_tag);
		
		stateSpinner = (Spinner)findViewById(R.id.state_spinner);
		plateInput = (EditText)findViewById(R.id.tag);
		
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
		
		//Log.d(TAG, "Getting location...");
		Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		try {
			List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
			//Log.d(TAG, "Address: " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea());
			//Log.d(TAG, "State index: " + AppUtils.getStateIndexFromName(addresses.get(0).getAdminArea()));
			stateSpinner.setSelection(AppUtils.getStateIndexFromName(addresses.get(0).getAdminArea()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Log.d(TAG, "Done getting location.");
		
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String email = prefs.getString("email", null);
		
		if(email == null)
			promptForEmail();
		else
			this.email = email;
	}
	
	private void promptForEmail()
	{
		// Fix for bad text color in popup (also, note, can't fix by assigning color in layout because platforms alter theme colors on popups)
		ContextThemeWrapper themeWrapper = new ContextThemeWrapper(this, R.style.AboutDialog);
		
		View emailForm = View.inflate(themeWrapper, R.layout.alert_email_required, null);//getLayoutInflater().inflate(R.layout.alert_email_required, null);
		final EditText emailInput = (EditText)emailForm.findViewById(R.id.email);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(themeWrapper);
		builder.setTitle("Email Required")
		.setView(emailForm)
		.setPositiveButton("Ok", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				PreferenceManager.getDefaultSharedPreferences(RegisterTagActivity.this)
					.edit().putString("email", emailInput.getText().toString()).commit();
				
				Log.d(TAG, "New Email: " + PreferenceManager.getDefaultSharedPreferences(RegisterTagActivity.this).getString("email", "-empty-"));
				RegisterTagActivity.this.email = PreferenceManager.getDefaultSharedPreferences(RegisterTagActivity.this).getString("email", "");
			}
			
		})
		.setNegativeButton("Cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				RegisterTagActivity.this.finish();
			}
			
		});
		
		builder.create().show();
	}
	
	public void registerTag(View view)
	{
		Log.d(TAG, "Registering Tag...");
		
		final String state = AppUtils.getStateCodeFromName( stateSpinner.getSelectedItem().toString() );
		final String plate = plateInput.getText().toString();
		
		Log.d(TAG, "tag: " + state);
		
		this.showProgressDialog("Registering Tag...");
		
		(new Thread(new Runnable() {

			@Override
			public void run() {
				TxtTagService service = new TxtTagService();
				
				final JsonResponse<Object> response = service.registerTag(state, plate, email, true, true);
				
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						hideProgressDialog();
						
						if(response != null && response.success)
						{
							// successful
							receivedMessage = response.message;
							if(receivedMessage == null || receivedMessage.length() == 0)
								receivedMessage = "Tag Successfully Registered!";
							
							SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(RegisterTagActivity.this).edit();
							editor.putString("state", state);
							editor.putString("plate", plate);
							editor.commit();
							
							plateInput.setText("");
							
							showSuccessMessage();
						}
						else if(response != null)
						{
							// error processing message
							if(response.message != null && response.message.length() > 0)
								receivedMessage = response.message;
							else
								receivedMessage = "Unknown Issue.";
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
		SuccessDialogFragment dialog = new SuccessDialogFragment();
		Bundle b = new Bundle();
		b.putString("receivedMessage", receivedMessage);
		dialog.setArguments(b);
		dialog.show(getSupportFragmentManager(), "fragment_show_success");
		
		//successDialog.show(getSupportFragmentManager(), "SuccessFragment");
	}

	public void showUnsuccessfulMessage()
	{
		NoSuccessDialogFragment dialog = new NoSuccessDialogFragment();
		Bundle b = new Bundle();
		b.putString("receivedMessage", receivedMessage);
		dialog.setArguments(b);
		dialog.show(getSupportFragmentManager(), "fragment_show_no_success");
		//noSuccessDialog.show(getSupportFragmentManager(), "NoSuccessFragment");
	}
	
	public void showCommunicationErrorMessage()
	{
		ErrorDialogFragment dialog = new ErrorDialogFragment();
		dialog.show(getSupportFragmentManager(), "fragment_show_error");
		//errorDialog.show(getSupportFragmentManager(), "ErrorFragment");
	}
	
	public static class SuccessDialogFragment extends DialogFragment
	{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
			builder.setMessage(this.getArguments().getString("receivedMessage"))
				.setNeutralButton("Cool", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.d(TAG, "Done sending message. Going home.");
						
						Intent settingsIntent = new Intent(SuccessDialogFragment.this.getActivity(), SettingsActivity.class);
						SuccessDialogFragment.this.getActivity().startActivity(settingsIntent);
					}
				});
			
			return builder.create();
		}
	}
	
	public static class NoSuccessDialogFragment extends DialogFragment
	{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
			builder.setMessage("Error Registering Tag: " + '\n' + this.getArguments().getString("receivedMessage"))
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.d(TAG, "Error happened, so sending back to screen.");
					}
				});
			
			return builder.create();
		}
	}
	
	public static class ErrorDialogFragment extends DialogFragment
	{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
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
