package com.desc.meetingbooker;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

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
	private ArrayList<Setting> config;
	private static ListView settingList;
	private SettingsAdapter adapter;
	private static Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Hide system UI
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_settings);

		context = getApplicationContext();
		config = StatMeth.readConfig(context);

		settingList = (ListView) findViewById(R.id.settingList);
		adapter = new SettingsAdapter(this, R.id.settingList, config);
		settingList.setAdapter(adapter);

		settingList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Log.d(TAG, "List was clicked!");
				if (config.get(position).getValueType().equals("boolean")) {
					CheckBox box = (CheckBox) arg1
							.findViewById(R.id.settingCheck);
					box.setChecked(!box.isChecked());
					if (position == 0 || position == 2) {
						View v = settingList.getChildAt(position + 1);
						TextView com = (TextView) v
								.findViewById(R.id.settingName);
						TextView val = (TextView) v
								.findViewById(R.id.settingVal);
						if (!box.isChecked()) {
							com.setTextColor(Color.GRAY);
							val.setTextColor(Color.GRAY);
						} else {
							com.setTextColor(Color.BLACK);
							val.setTextColor(Color.BLACK);
						}
					}
				}
				if (config.get(position).getValueType().equals("int")) {
					if (position == 1 || position == 3) {
						View v = settingList.getChildAt(position - 1);
						CheckBox box = (CheckBox) v
								.findViewById(R.id.settingCheck);
						if (!box.isChecked()) {
							return;
						}
					}
					NumberFragment.index = position;
					NumberFragment fragment = new NumberFragment();
					fragment.show(getFragmentManager(), "BLA");
				}
				if (config.get(position).getValueType().equals("String")) {
					StringFragment.index = position;
					StringFragment fragment = new StringFragment();
					fragment.show(getFragmentManager(), "BLA");
				}
			}
		});

		Log.d(TAG, "onCreate()");
	}

	/**
	 * The method called by the "Change password" button
	 * 
	 * @param view
	 *            The View of the button
	 */
	public void newPassword(View view) {
		PasswordFragment fragment = new PasswordFragment();
		fragment.show(getFragmentManager(), "BLA");
	}

	/**
	 * Reads the formula, and then writes it to the config file
	 * 
	 * @param view
	 *            The View from the button
	 */
	public void save(View view) {
		config = readList();
		StatMeth.write(config, getApplicationContext());
		Log.d(TAG, "Save the new configuration");
		finish();
	}

	/**
	 * Reads the fields in the ListView, and returns the new list of settings
	 * 
	 * @return The new list of settings
	 */
	public ArrayList<Setting> readList() {
		ArrayList<Setting> temp = new ArrayList<Setting>();
		for (int i = 0; i < config.size(); i++) {
			View v = settingList.getChildAt(i);
			Setting set = config.get(i);
			String value;
			if (set.getValueType().equals("boolean")) {
				CheckBox box = (CheckBox) v.findViewById(R.id.settingCheck);
				value = "" + box.isChecked();
			} else {
				TextView tv = (TextView) v.findViewById(R.id.settingVal);
				value = "" + tv.getText();
			}
			Log.d("SettingsActivity", "Field value : " + value);
			set.setValue(value);
			temp.add(set);
		}
		return temp;
	}

	/**
	 * Exits the SettingsActivity
	 * 
	 * @param view
	 *            The View of the button
	 */
	public void cancel(View view) {
		Log.d(TAG, "cancel()");
		finish();
	}

	public static class StringFragment extends DialogFragment {
		private static int index;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();
			final View v = inflater
					.inflate(R.layout.string_edit_layout, null);
			TextView layTV = (TextView) v.findViewById(R.id.editLayTV);
			EditText edit = (EditText) v.findViewById(R.id.editLayET);
			View vi = settingList.getChildAt(index);
			TextView tv1 = (TextView) vi.findViewById(R.id.settingVal);
			TextView tv2 = (TextView) vi.findViewById(R.id.settingName);
			edit.setHint(tv1.getText());
			layTV.setText(tv2.getText());
			
			builder.setView(v).setPositiveButton("OK", 
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							EditText edit = (EditText) v.findViewById(R.id.editLayET);
							String name = edit.getText().toString();
							View vi = settingList.getChildAt(index);
							TextView tv = (TextView) vi.findViewById(R.id.settingVal);
							tv.setText(name);
						}
					}).setNegativeButton("Cancel", 
							new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// NOTHING
							
						}
					});
			return builder.create();
		}
	}

	public static class NumberFragment extends DialogFragment {

		private static int index;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();
			final View v = inflater
					.inflate(R.layout.number_picker_layout, null);
			NumberPicker n = (NumberPicker) v.findViewById(R.id.numberPicker);
			View vi = settingList.getChildAt(index);
			TextView tv = (TextView) vi.findViewById(R.id.settingName);
			if ((tv.getText() + "").startsWith("Length")) {
				n.setMinValue(15);
				n.setMaxValue(60);
			} else {
				n.setMinValue(0);
				n.setMaxValue(30);
			}
			n.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

			builder.setView(v).setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							NumberPicker picker = (NumberPicker) v
									.findViewById(R.id.numberPicker);
							int number = picker.getValue();
							View vi = settingList.getChildAt(index);
							TextView tv = (TextView) vi
									.findViewById(R.id.settingVal);
							tv.setText("" + number);
						}

					});
			return builder.create();
		}

	}

	public static class PasswordFragment extends DialogFragment {

		private static int error = 0;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			LayoutInflater inflater = getActivity().getLayoutInflater();
			final View v = inflater.inflate(R.layout.change_password_layout,
					null);
			TextView prompt = (TextView) v.findViewById(R.id.changePrompt);
			switch (error) {
			case 0:
				prompt.setVisibility(TextView.GONE);
				break;
			case 1:
				prompt.setVisibility(TextView.VISIBLE);
				prompt.setText("The new passwords didn't match");
				break;
			case 2:
				prompt.setVisibility(TextView.VISIBLE);
				prompt.setText("The old password was wrong");
				break;
			}
			builder.setView(v)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									EditText oldText = (EditText) v
											.findViewById(R.id.pwOld);
									EditText newText = (EditText) v
											.findViewById(R.id.pwNew1);
									EditText confText = (EditText) v
											.findViewById(R.id.pwNew2);

									String old = oldText.getText().toString();
									String new1 = newText.getText().toString();
									String new2 = confText.getText().toString();
									String storedpw = StatMeth
											.getPassword(context);

									if (new1.equals(new2)
											&& old.equals(storedpw)) {
										error = 0;
										StatMeth.savePassword(new1, context);
										return;
									}
									if (!new1.equals(new2)) {
										error = 1;
										PasswordFragment fragment = new PasswordFragment();
										fragment.show(getFragmentManager(),
												"BLA");
										return;
									}
									if (!old.equals(storedpw)) {
										error = 2;
										PasswordFragment fragment = new PasswordFragment();
										fragment.show(getFragmentManager(),
												"BLA");
										return;
									}
								}

							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									error = 0;
								}

							});
			return builder.create();
		}

	}

}