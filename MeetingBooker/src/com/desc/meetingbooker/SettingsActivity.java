package com.desc.meetingbooker;

import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * An activity that reads the config file, and then displays a formula, to edit
 * the config file.
 * 
 * @author Carl Johnsen, Daniel Pedersen, Emil Pedersen and Sune Bartels
 * @version 0.9
 * @since 27-05-2013
 */
public class SettingsActivity extends Activity {
	
	private final String TAG = SettingsActivity.class.getSimpleName();
	private HashMap<String, String> config;
	private CheckBox extendEndCheck;
	private static Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Hide system UI
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_settings);
		
		extendEndCheck = (CheckBox) findViewById(R.id.extendEndCheck);
		
		context = getApplicationContext();
		
		config = StatMeth.readConfig(getApplicationContext());
		setViews(config);
		Log.d(TAG, "onCreate()");
	}
	
	private void setViews(HashMap<String, String> map) {
		extendEndCheck.setChecked(Boolean.parseBoolean(map.get("extendendtime")));
	}
	
	/**
	 * The method called by the "Change password" button
	 * 
	 * @param view The View of the button
	 */
	public void newPassword(View view) {
		PasswordFragment fragment = new PasswordFragment();
		fragment.show(getFragmentManager(), "BLA");
	}
	
	/**
	 * Reads the formula, and then writes it to the config file
	 * 
	 * @param view The View from the button
	 */
	public void save(View view) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("extendendtime", extendEndCheck.isChecked() + "");
		StatMeth.write(map, getApplicationContext());
		Log.d(TAG, "Save the new configuration");
		finish();
	}
	
	/**
	 * Exits the SettingsActivity
	 * 
	 * @param view The View of the button
	 */
	public void cancel(View view) {
		Log.d(TAG, "cancel()");
		finish();
	}
	
	public static class PasswordFragment extends DialogFragment {
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();
			final View v = inflater.inflate(R.layout.change_password_layout, null);
			builder.setView(v)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						EditText oldText = (EditText) v.findViewById(R.id.pwOld);
						EditText newText = (EditText) v.findViewById(R.id.pwNew1);
						EditText confText = (EditText) v.findViewById(R.id.pwNew2);
						
						String old = oldText.getText().toString();
						String new1 = newText.getText().toString();
						String new2 = confText.getText().toString();
						String storedpw = StatMeth.getPassword(context);
						
						if (new1.equals(new2) && old.equals(storedpw)) {
							StatMeth.savePassword(new1, context);
						}
						
						/* 
						 * TODO if new1 != new2 inflate errormsg
						 */
						
						/* 
						 * TODO if old != storedpw inflate errormsg
						 */
					}
					
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// NOTHING
					}
					
				});
			return builder.create();
		}
		
	}

}