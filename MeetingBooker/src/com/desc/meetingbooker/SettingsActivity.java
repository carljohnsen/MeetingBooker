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
 * @author Carl Johnsen
 * @version 1.0
 * @since 27-05-2013
 */
public final class SettingsActivity extends Activity {

	private final String TAG = SettingsActivity.class.getSimpleName();
	private static ArrayList<Setting> config;
	private static ListView settingList;
	private SettingsAdapter adapter;
	private static Context context;

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "called onCreate()");

		// Hide Status bar and App title bar (before setContentView())
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// Set content view
		setContentView(R.layout.activity_settings);

		// Read the config file
		context = getApplicationContext();
		config = StatMeth.readConfig(context);

		// Set up the ListView
		settingList = (ListView) findViewById(R.id.settingList);
		adapter = new SettingsAdapter(this, R.id.settingList, config);
		settingList.setAdapter(adapter);

		// Set OnItemClickListener
		settingList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public final void onItemClick(final AdapterView<?> arg0,
					final View arg1, final int position, final long arg3) {

				Log.d(TAG, "List was clicked on position " + position);

				// Check if it is a boolean, so that it should check/uncheck
				if (config.get(position).valueType.equals("boolean")) {
					// Find the box
					final CheckBox box = (CheckBox) arg1
							.findViewById(R.id.settingCheck);
					// check / uncheck
					box.setChecked(!box.isChecked());

					// If its either extendend or extendstart, gray out the
					// time selectors
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

				// Check if it is an int, so that it should use numberfragment
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

				// Check if it is a String, so that it should show stringfrag.
				if (config.get(position).valueType.equals("String")) {
					StringFragment.index = position;
					final StringFragment fragment = new StringFragment();
					fragment.show(getFragmentManager(), "BLA");
					return;
				}

				// TODO Check if it is a hashmap, so that it should show
				// hashmaofrag.
				if (config.get(position).valueType.equals("hashmap")) {
					ListFragment.index = position;
					ListFragment.setting = config.get(position);
					final ListFragment fragment = new ListFragment();
					fragment.show(getFragmentManager(), "BLA");
					return;
				}

			}
		});

		Log.d(TAG, "onCreate() is done");
	}

	/**
	 * Exits the SettingsActivity
	 * 
	 * @param view
	 *            The View of the button
	 */
	public final void cancel(final View view) {
		Log.d(TAG, "pressed Cancel button");
		finish();
	}

	/**
	 * The method called by the "Change password" button. Inflates change
	 * password fragment
	 * 
	 * @param view
	 *            The View of the button
	 */
	public final void newPassword(final View view) {
		Log.d(TAG, "pressed NewPassword button");
		new PasswordFragment().show(getFragmentManager(), "BLA");
	}

	/**
	 * Reads the fields in the ListView, and returns the new list of settings
	 * 
	 * @return The new list of settings
	 */
	public final ArrayList<Setting> readList() {
		Log.d(TAG, "called readList()");
		// Make a new ArrayList
		final ArrayList<Setting> temp = new ArrayList<Setting>();

		// Add all the settings to the ArrayList
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
			set.value = value;
			temp.add(set);
		}

		// Return the AraryList
		return temp;
	}

	/**
	 * Reads the formula, and then writes it to the config file
	 * 
	 * @param view
	 *            The View from the button
	 */
	public final void save(final View view) {
		Log.d(TAG, "pressed Save button");
		config = readList();
		StatMeth.write(config, getApplicationContext());
		finish();
	}

	/**
	 * A DialogFragment, that shows a list of possible Calendar names
	 * 
	 * @author Carl Johnsen
	 * @version 1.0
	 * @since 28-01-2014 TODO !!!
	 */
	public final static class ListFragment extends DialogFragment {
		// TODO !!!
		private static String TAG = ListFragment.class.getSimpleName();
		private static int index;
		private static Setting setting;

		// TODO !!!
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Log.d(TAG, "called onCreateDialog()");

			// Inflate the View
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					getActivity());
			final LayoutInflater inflater = getActivity().getLayoutInflater();
			final View v = inflater.inflate(R.layout.fragment_list, null);

			// Find the ListView in the fragment
			final ArrayList<CalName> list = StatMeth.getCalendarNames(context);
			final ListView listView = (ListView) v
					.findViewById(R.id.fragment_list_list);
			final TextView name = (TextView) v
					.findViewById(R.id.fragment_list_name);
			final TextView id = (TextView) v
					.findViewById(R.id.fragment_list_id);
			
			name.setText(StatMeth.getCalendarName(context));
			id.setText(setting.value);
			
			listView.setAdapter(new CalNameAdapter(this.getActivity(),
					R.layout.item_calname, list));
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public final void onItemClick(final AdapterView<?> arg0,
						final View arg1, final int position, final long arg3) {
					CalName selected = list.get(position);
					name.setText(selected.name);
					id.setText(selected.id);
					setting.value = selected.id;
				}
			});
			
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(final DialogInterface arg0,
								final int arg1) {
							Log.d(TAG, "pressed OK button");
							// Save the selected CalName, and save it in a
							// Setting
							config.set(index, setting);

							final View vi = settingList.getChildAt(index);
							TextView tv = (TextView) vi
									.findViewById(R.id.settingVal);
							tv.setText(setting.value);
							tv = (TextView) vi.findViewById(R.id.settingName);
							tv.setText("Calendar ID : " + name.getText());
						}

					})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public final void onClick(
										final DialogInterface dialog,
										final int which) {
									Log.d(TAG, "pressed Cancel button");
									// DO NOTHING
								}
							});
			return builder.create();
		}

	}

	/**
	 * A DialogFragment with a NumberPicker. Used for editing int settings
	 * 
	 * @author Carl Johnsen
	 * @version 1.0
	 * @since 30-06-2013
	 */
	public final static class NumberFragment extends DialogFragment {

		private static String TAG = NumberFragment.class.getSimpleName();
		private static int index;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Log.d(TAG, "called onCreateDialog()");
			// Inflate the View
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					getActivity());
			final LayoutInflater inflater = getActivity().getLayoutInflater();
			final View v = inflater.inflate(R.layout.fragment_number_picker,
					null);

			// Find the fragments View
			final NumberPicker n = (NumberPicker) v
					.findViewById(R.id.numberPicker);

			// Find the ListViews child
			final View vi = settingList.getChildAt(index);
			final TextView tv = (TextView) vi.findViewById(R.id.settingName);

			// Set the boundries of the NumberPicker
			if ((tv.getText() + "").startsWith("Length")) {
				n.setMinValue(15);
				n.setMaxValue(60);
			} else {
				n.setMinValue(0);
				n.setMaxValue(30);
			}

			// Remove focus from the NumberPicker
			n.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

			builder.setView(v).setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(final DialogInterface arg0,
								final int arg1) {
							Log.d(TAG, "pressed OK button");
							// Read the NumberPicker and save the information
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

	/**
	 * A DialogFragment with 3 EditText fields. Used when changing password
	 * 
	 * @author carljohnsen
	 * @version 1.0
	 * @since 26-06-2013
	 */
	public final static class PasswordFragment extends DialogFragment {

		private static String TAG = PasswordFragment.class.getSimpleName();
		private static int error = 0;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Log.d(TAG, "called onCreateDialog()");
			// Inflate the View
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					getActivity());
			final LayoutInflater inflater = getActivity().getLayoutInflater();
			final View v = inflater.inflate(R.layout.fragment_change_password,
					null);

			// Find the prompt View for displaying reason for error
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

									// Find the fragments views
									final EditText oldText = (EditText) v
											.findViewById(R.id.pwOld);
									final EditText newText = (EditText) v
											.findViewById(R.id.pwNew1);
									final EditText confText = (EditText) v
											.findViewById(R.id.pwNew2);

									// Save the data in the Views
									final String old = oldText.getText()
											.toString();
									final String new1 = newText.getText()
											.toString();
									final String new2 = confText.getText()
											.toString();
									final String storedpw = StatMeth
											.getPassword(context);

									// Check if the typed old and the stored old
									// are the same, and if the two new typed
									// are the same
									if (new1.equals(new2)
											&& old.equals(storedpw)) {
										Log.d(TAG, "new password: all ok");
										error = 0;
										StatMeth.savePassword(new1, context);
										return;
									}

									// If the two new differ from each other
									if (!new1.equals(new2)) {
										Log.d(TAG, "new password: two new dont"
												+ " match");
										error = 1;
										final PasswordFragment fragment = new PasswordFragment();
										fragment.show(getFragmentManager(),
												"BLA");
										return;
									}

									// If the typed old differs from the stored
									// old
									if (!old.equals(storedpw)) {
										Log.d(TAG, "new password: old was"
												+ " wrong");
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
									Log.d(TAG, "pressed Cancel button");
									error = 0;
								}

							});
			return builder.create();
		}

	}

	/**
	 * A DialogFragment with a EditText field in. Used for editing String
	 * settings-
	 * 
	 * @author Carl Johnsen
	 * @version 1.0
	 * @since 03-07-2013
	 */
	public final static class StringFragment extends DialogFragment {

		private static String TAG = StringFragment.class.getSimpleName();
		private static int index;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Log.d(TAG, "called onCreateDialog()");
			// Inflate the View
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					getActivity());
			final LayoutInflater inflater = getActivity().getLayoutInflater();
			final View v = inflater
					.inflate(R.layout.fragment_string_edit, null);

			// Find the fragments Views
			final TextView layTV = (TextView) v.findViewById(R.id.editLayTV);
			final EditText edit = (EditText) v.findViewById(R.id.editLayET);

			// Find the ListViews child
			final View vi = settingList.getChildAt(index);
			final TextView tv1 = (TextView) vi.findViewById(R.id.settingVal);
			final TextView tv2 = (TextView) vi.findViewById(R.id.settingName);

			// Fill the fragments Views with information from the ListView
			edit.setHint(tv1.getText());
			layTV.setText(tv2.getText());

			builder.setView(v)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public final void onClick(
										final DialogInterface dialog,
										final int which) {
									Log.d(TAG, "pressed OK button");
									// Read the fragment Views, and save them
									// in the ListView
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
									Log.d(TAG, "pressed Cancel button");
									// DO NOTHING
								}
							});
			return builder.create();
		}
	}

}