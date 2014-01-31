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
		settingList = (ListView) findViewById(R.id.settings_list);
		adapter = new SettingsAdapter(this, R.id.settings_list, config);
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
							.findViewById(R.id.setting_check);
					// check / uncheck
					box.setChecked(!box.isChecked());

					// If its either extendend or extendstart, gray out the
					// time selectors
					if (position == 0 || position == 2) {
						final View v = settingList.getChildAt(position + 1);
						final TextView com = (TextView) v
								.findViewById(R.id.setting_name);
						final TextView val = (TextView) v
								.findViewById(R.id.setting_value);
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
								.findViewById(R.id.setting_check);
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

				if (config.get(position).valueType.equals("hashmap")) {
					CustomListFragment.index = position;
					CustomListFragment.setting = config.get(position);
					final CustomListFragment fragment = new CustomListFragment();
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
				CheckBox box = (CheckBox) v.findViewById(R.id.setting_check);
				value = "" + box.isChecked();
			} else {
				TextView tv = (TextView) v.findViewById(R.id.setting_value);
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
	 * Named Custom*, because of name clash with android.content.ListFragment
	 * 
	 * @author Carl Johnsen
	 * @version 1.0
	 * @since 28-01-2014 
	 */
	public final static class CustomListFragment extends DialogFragment {
		private static String TAG = CustomListFragment.class.getSimpleName();
		private static int index;
		private static Setting setting;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			Log.d(TAG, "called onCreateDialog()");

			// Inflate the View
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					getActivity());
			final LayoutInflater inflater = getActivity().getLayoutInflater();
			final View v = inflater.inflate(R.layout.fragment_list_picker, null);

			// Find the ListView in the fragment
			final ListView listView = (ListView) v
					.findViewById(R.id.list_picker_list);
			final TextView name = (TextView) v
					.findViewById(R.id.list_picker_name);
			final TextView id = (TextView) v
					.findViewById(R.id.list_picker_id_value);
			
			name.setText(StatMeth.getCalendarName(context));
			id.setText(setting.value);
			
			final ArrayList<CalName> list = StatMeth.getCalendarNames(context);
			CalNameAdapter adapter = new CalNameAdapter(this.getActivity(), R.layout.item_calname, list);
			listView.setAdapter(adapter);
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
			// TODO Ændre UI på dette fragment, så selected ikke ser så malplaceret ud
			builder.setView(v)
				.setTitle(R.string.text_choose_calendar)
				.setPositiveButton("OK",
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
									.findViewById(R.id.setting_value);
							tv.setText(setting.value);
							tv = (TextView) vi.findViewById(R.id.setting_name);
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
					.findViewById(R.id.number_picker_picker);

			// Find the ListViews child
			final View vi = settingList.getChildAt(index);
			final TextView tv = (TextView) vi.findViewById(R.id.setting_name);

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

			builder.setView(v)
				.setTitle(R.string.text_pick_a_number)
				.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(final DialogInterface arg0,
								final int arg1) {
							Log.d(TAG, "pressed OK button");
							// Read the NumberPicker and save the information
							final NumberPicker picker = (NumberPicker) v
									.findViewById(R.id.number_picker_picker);
							final int number = picker.getValue();
							final View vi = settingList.getChildAt(index);
							final TextView tv = (TextView) vi
									.findViewById(R.id.setting_value);
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
					.findViewById(R.id.change_pass_prompt);
			switch (error) {
			case 0:
				prompt.setVisibility(TextView.GONE);
				break;
			case 1:
				prompt.setVisibility(TextView.VISIBLE);
				prompt.setText(R.string.text_passwords_didnt_match);
				break;
			case 2:
				prompt.setVisibility(TextView.VISIBLE);
				prompt.setText(R.string.text_wrong_old_password);
				break;
			}
			builder.setView(v)
					.setTitle(R.string.text_change_password_colon)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(final DialogInterface arg0,
										final int arg1) {

									// Find the fragments views
									final EditText oldText = (EditText) v
											.findViewById(R.id.change_pass_old);
									final EditText newText = (EditText) v
											.findViewById(R.id.change_pass_new1);
									final EditText confText = (EditText) v
											.findViewById(R.id.change_pass_new2);

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
			final EditText edit = (EditText) v.findViewById(R.id.string_edit_edit);

			// Find the ListViews child
			final View vi = settingList.getChildAt(index);
			final TextView tv1 = (TextView) vi.findViewById(R.id.setting_value);
			final TextView tv2 = (TextView) vi.findViewById(R.id.setting_name);

			// Fill the fragments Views with information from the ListView
			edit.setHint(tv1.getText());

			builder.setView(v)
					.setTitle(tv2.getText() + ":")
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
											.findViewById(R.id.string_edit_edit);
									final String name = edit.getText()
											.toString();
									final View vi = settingList
											.getChildAt(index);
									final TextView tv = (TextView) vi
											.findViewById(R.id.setting_value);
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