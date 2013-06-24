package com.desc.meetingbooker;

import java.util.HashMap;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Hide system UI
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_settings);
		
		extendEndCheck = (CheckBox) findViewById(R.id.extendEndCheck);
		
		config = StatMeth.readConfig(getApplicationContext());
		setViews(config);
		Log.d(TAG, "onCreate()");
	}
	
	private void setViews(HashMap<String, String> map) {
		extendEndCheck.setChecked(Boolean.parseBoolean(map.get("extendendtime")));
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

}