package com.desc.meetingbooker;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
public final class SettingsActivity extends Activity {

	private final String TAG = SettingsActivity.class.getSimpleName();
	private ArrayList<Setting> config;
	private static ListView settingList;
	private SettingsAdapter adapter;
	private static Context context;

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
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
			public final void onItemClick(final AdapterView<?> arg0,
					final View arg1, final int position, final long arg3) {
				Log.d(TAG, "List was clicked!");
				if (config.get(position).valueType.equals("boolean")) {
					final CheckBox box = (CheckBox) arg1
							.findViewById(R.id.settingCheck);
					box.setChecked(!box.isChecked());
					if (position == 0 || position == 2) {
						final View v = settingList.getChildAt(position + 1);
						final TextView com = (TextView) v
								.findViewById(R.id.settingName);
						final TextView val = (TextView) v
								.findViewById(R.id.settingVal);
						if (!box.isChecked()) {
							com.setTextColor(Color.GRAY);
							val.setTextColor(Color.GRAY);
						} else {
							com.setTextColor(Color.BLACK);
							val.setTextColor(Color.BLACK);
						}
					}
					return;
				}
				if (config.get(position).valueType.equals("int")) {
					if (position == 1 || position == 3) {
						final View v = settingList.getChildAt(position - 1);
						final CheckBox box = (CheckBox) v
								.findViewById(R.id.settingCheck);
						if (!box.isChecked()) {
							return;
						}
					}
					NumberFragment.index = position;
					final NumberFragment fragment = new NumberFragment();
					fragment.show(getFragmentManager(), "BLA");
					return;
				}
				if (config.get(position).valueType.equals("String")) {
					StringFragment.index = position;
					final StringFragment fragment = new StringFragment();
					fragment.show(getFragmentManager(), "BLA");
					return;
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
	public final void newPassword(final View view) {
		new PasswordFragment().show(getFragmentManager(), "BLA");
	}

	/**
	 * Reads the formula, and then writes it to the config file
	 * 
	 * @param view
	 *            The View from the button
	 */
	public final void save(final View view) {
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
	public final ArrayList<Setting> readList() {
		final ArrayList<Setting> temp = new ArrayList<Setting>();
		for (int i = 0; i < config.size(); i++) {
			final View v = settingList.getChildAt(i);
			final Setting set = config.get(i);
			final String value;
			if (set.valueType.equals("boolean")) {
				CheckBox box = (CheckBox) v.findViewById(R.id.settingCheck);
				value = "" + box.isChecked();
			} else {
				TextView tv = (TextView) v.findViewById(R.id.settingVal);
				value = "" + tv.getText();
			}
			Log.d("SettingsActivity", "Field value : " + value);
			set.value = value;
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
	public final void cancel(final View view) {
		Log.d(TAG, "cancel()");
		finish();
	}

	public final static class StringFragment extends DialogFragment {
		private static int index;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					getActivity());
			final LayoutInflater inflater = getActivity().getLayoutInflater();
			final View v = inflater.inflate(R.layout.string_edit_layout, null);
			final TextView layTV = (TextView) v.findViewById(R.id.editLayTV);
			final EditText edit = (EditText) v.findViewById(R.id.editLayET);
			final View vi = settingList.getChildAt(index);
			final TextView tv1 = (TextView) vi.findViewById(R.id.settingVal);
			final TextView tv2 = (TextView) vi.findViewById(R.id.settingName);
			edit.setHint(tv1.getText());
			layTV.setText(tv2.getText());

			builder.setView(v)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public final void onClick(
										final DialogInterface dialog,
										final int which) {
									final EditText edit = (EditText) v
											.findViewById(R.id.editLayET);
									final String name = edit.getText()
											.toString();
									final View vi = settingList
											.getChildAt(index);
									final TextView tv = (TextView) vi
											.findViewById(R.id.settingVal);
									tv.setText(name);
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public final void onClick(
										final DialogInterface dialog,
										final int which) {
									// NOTHING

								}
							});
			return builder.create();
		}
	}

	public final static class NumberFragment extends DialogFragment {

		private static int index;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					getActivity());
			final LayoutInflater inflater = getActivity().getLayoutInflater();
			final View v = inflater
					.inflate(R.layout.number_picker_layout, null);
			final NumberPicker n = (NumberPicker) v
					.findViewById(R.id.numberPicker);
			final View vi = settingList.getChildAt(index);
			final TextView tv = (TextView) vi.findViewById(R.id.settingName);
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
						public void onClick(final DialogInterface arg0,
								final int arg1) {
							final NumberPicker picker = (NumberPicker) v
									.findViewById(R.id.numberPicker);
							final int number = picker.getValue();
							final View vi = settingList.getChildAt(index);
							final TextView tv = (TextView) vi
									.findViewById(R.id.settingVal);
							tv.setText("" + number);
						}

					});
			return builder.create();
		}

	}

	public final static class PasswordFragment extends DialogFragment {

		private static int error = 0;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					getActivity());
			final LayoutInflater inflater = getActivity().getLayoutInflater();
			final View v = inflater.inflate(R.layout.change_password_layout,
					null);
			final TextView prompt = (TextView) v
					.findViewById(R.id.changePrompt);
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
								public void onClick(final DialogInterface arg0,
										final int arg1) {
									final EditText oldText = (EditText) v
											.findViewById(R.id.pwOld);
									final EditText newText = (EditText) v
											.findViewById(R.id.pwNew1);
									final EditText confText = (EditText) v
											.findViewById(R.id.pwNew2);

									final String old = oldText.getText()
											.toString();
									final String new1 = newText.getText()
											.toString();
									final String new2 = confText.getText()
											.toString();
									final String storedpw = StatMeth
											.getPassword(context);

									if (new1.equals(new2)
											&& old.equals(storedpw)) {
										error = 0;
										StatMeth.savePassword(new1, context);
										return;
									}
									if (!new1.equals(new2)) {
										error = 1;
										final PasswordFragment fragment = new PasswordFragment();
										fragment.show(getFragmentManager(),
												"BLA");
										return;
									}
									if (!old.equals(storedpw)) {
										error = 2;
										final PasswordFragment fragment = new PasswordFragment();
										fragment.show(getFragmentManager(),
												"BLA");
										return;
									}
								}

							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public final void onClick(
										final DialogInterface dialog,
										final int which) {
									error = 0;
								}

							});
			return builder.create();
		}

	}

}