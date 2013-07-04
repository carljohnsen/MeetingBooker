package com.desc.meetingbooker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemClickListener;

/**
 * The Activity used when an event is being edited
 * 
 * @author Carl Johnsen, Daniel Pedersen, Emil Pedersen and Sune Bartels
 * @version 0.9
 * @since 14-05-2013
 */
public final class NewEditActivity extends Activity {

	private static final String TAG = NewEditActivity.class.getSimpleName();
	private static TimePicker timeStart;
	private static TimePicker timeEnd;
	private static ListView intervalPicker;
	private static Date date = new Date();
	private static final Calendar cal = Calendar.getInstance();
	private static ArrayList<TimeWindow> windowList;
	private static TimeWindowAdapter adapter;
	private static int index;
	private static EditText titleText;
	private static EditText descText;
	private static CalEvent event;
	private static Context context;

	private static Button add;
	private static Button update;
	private static ImageView delete;

	protected static boolean candelete;
	protected static int windowSize;

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		Log.d(TAG, "called onCreate()");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_new_edit);

		titleText = (EditText) findViewById(R.id.editTitle);
		descText = (EditText) findViewById(R.id.editDesc);
		add = (Button) findViewById(R.id.addButton);
		update = (Button) findViewById(R.id.updateButton);
		delete = (ImageView) findViewById(R.id.deleteButton);
		context = getApplicationContext();

		// Finds the TimePickers
		timeStart = (TimePicker) findViewById(R.id.timePickerStart);
		timeEnd = (TimePicker) findViewById(R.id.timePickerEnd);

		intervalPicker = (ListView) findViewById(R.id.intervalView);
		windowList = findTimeWindow();

		adapter = new TimeWindowAdapter(this, R.layout.timewindow_item,
				windowList);

		// Setting the ListView
		intervalPicker.setAdapter(adapter);

		intervalPicker.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				setTimePickers(windowList.get(position));
			}
		});

		if (this.getIntent().getIntExtra("type", 0) == 0) {
			setNew();
		} else {
			setEdit();
		}
	}

	/**
	 * Called by onCreate(). Used when it should show Edit formula
	 */
	private final void setEdit() {
		index = this.getIntent().getIntExtra("event", -2);
		if (index == -1) {
			event = MainActivity.current;
		} else {
			event = MainActivity.eventlist.get(index);
		}
		if (candelete) {
			delete.setVisibility(ImageView.VISIBLE);
		} else {
			delete.setVisibility(ImageView.GONE);
		}

		titleText.setText(event.title);
		descText.setText(event.description);
		setTimePickers(event.getTimeWindow());
		add.setVisibility(Button.GONE);
		update.setVisibility(Button.VISIBLE);
	}

	/**
	 * Called by onCreate. Used when it should show new meeting formula
	 */
	private final void setNew() {
		delete.setVisibility(Button.GONE);
		titleText.setHint(titleText.getText().toString());
		titleText.setText("");
		setTimePickers(windowList.get(0));
		add.setVisibility(Button.VISIBLE);
		update.setVisibility(Button.GONE);
	}

	/**
	 * The method called by the "Add" button. Reads all of the fields in the UI,
	 * inserts them into a new CalEvent and then sends it to EventCreate
	 * 
	 * @param view
	 *            The View of the button
	 */
	public final void add(final View view) {
		add.setClickable(false);

		Log.d(TAG, "Adding event to calendar");

		// Read the fields
		String title = titleText.getText().toString();
		if (title.equals("")) {
			title = titleText.getHint().toString();
		}
		final String desc = descText.getText().toString();
		final int startHour = timeStart.getCurrentHour();
		final int startMin = timeStart.getCurrentMinute();
		final int endHour = timeEnd.getCurrentHour();
		final int endMin = timeEnd.getCurrentMinute();

		// Convert timePicker readings to long
		final String startTime = cal.get(Calendar.DAY_OF_MONTH) + "-"
				+ (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.YEAR)
				+ " " + startHour + ":" + startMin;
		final SimpleDateFormat formatter = new SimpleDateFormat(
				"dd-MM-yyyy HH:mm", Locale.getDefault());
		try {
			date = formatter.parse(startTime);
			Log.d(TAG, startTime);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		final long start = date.getTime();
		final String endTime = cal.get(Calendar.DAY_OF_MONTH) + "-"
				+ (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.YEAR)
				+ " " + endHour + ":" + endMin;
		try {
			date = formatter.parse(endTime);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		final long end = date.getTime();

		// Create a new CalEvent
		final CalEvent event = new CalEvent(start, end, title, desc);
		if (StatMeth.isBefore(event)) {
			final AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("Error");
			dialog.setMessage("End time is before start time");
			dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(final DialogInterface dialog,
								final int which) {
						}

					});
			dialog.show();
			add.setClickable(true);
			return;
		}
		Log.d(TAG, "" + StatMeth.isFree(event));
		if (StatMeth.isFree(event)) {
			StatMeth.setNewEvent(event, context);
			Log.d(TAG, "event inserted");
			finish();
		} else {
			final AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("Error");
			dialog.setMessage("Meeting is overlapping");
			dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(final DialogInterface dialog,
								final int which) {
						}

					});
			dialog.show();
			add.setClickable(true);
		}
	}

	public final void delete(final View view) {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle("Delete");
		dialog.setMessage("Are you sure you want to delete this event?");
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						StatMeth.updateStart(event, context);
						StatMeth.updateEnd(event, context);
						finish();
					}

				});
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
					}
				});
		dialog.show();
	}

	/**
	 * The method called by the "Add" button. Reads all of the fields in the UI,
	 * inserts them into a new CalEvent and then sends it to EventCreate
	 * 
	 * @param view
	 *            The View of the button
	 */
	public final void update(final View view) {
		Log.d(TAG, "Adding event to calendar");

		// Read the fields
		final String title = titleText.getText().toString();
		final String desc = descText.getText().toString();
		final int startHour = timeStart.getCurrentHour();
		final int startMin = timeStart.getCurrentMinute();
		final int endHour = timeEnd.getCurrentHour();
		final int endMin = timeEnd.getCurrentMinute();

		// Convert timePicker readings to long
		final String startTime = cal.get(Calendar.DAY_OF_MONTH) + "-"
				+ (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.YEAR)
				+ " " + startHour + ":" + startMin;
		final SimpleDateFormat formatter = new SimpleDateFormat(
				"dd-MM-yyyy HH:mm", Locale.getDefault());
		try {
			date = formatter.parse(startTime);
			Log.d(TAG, startTime);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		final long start = date.getTime();
		final String endTime = cal.get(Calendar.DAY_OF_MONTH) + "-"
				+ (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.YEAR)
				+ " " + endHour + ":" + endMin;
		try {
			date = formatter.parse(endTime);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		final long end = date.getTime();

		// Create a new CalEvent
		final CalEvent newEvent = new CalEvent(start, end, title, desc,
				event.id);
		if (StatMeth.isBefore(newEvent)) {
			final AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("Error");
			dialog.setMessage("End time is before start time");
			dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(final DialogInterface dialog,
								final int which) {
						}

					});
			dialog.show();
			return;
		}
		if (StatMeth.isUpdatable(newEvent, index)) {
			StatMeth.update(newEvent, context);
			Log.d(TAG, "event inserted");
			finish();
		} else {
			final AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("Error");
			dialog.setMessage("Meeting is overlapping");
			dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(final DialogInterface dialog,
								final int which) {
						}

					});
			dialog.show();
		}
	}

	/**
	 * The method called by the "Cancel" button. Returns the user to the
	 * MainActivity
	 * 
	 * @param view
	 *            The View of the button
	 */
	public final void cancel(final View view) {
		Log.d(TAG, "Cancel button pressed");
		finish();
	}

	// Sets time pickers to a possible interval
	@SuppressLint("SimpleDateFormat")
	private final void setTimePickers(final TimeWindow window) {

		// Sets the TimePickers to use 24 hour
		timeStart.setIs24HourView(true);
		timeEnd.setIs24HourView(true);

		int hour = Integer.parseInt(new SimpleDateFormat("HH").format(new Date(
				window.start)));
		int minute = Integer.parseInt(new SimpleDateFormat("mm")
				.format(new Date(window.start)));

		timeStart.setCurrentHour(hour);
		timeStart.setCurrentMinute(minute);

		hour = Integer.parseInt(new SimpleDateFormat("HH").format(new Date(
				window.end)));
		minute = Integer.parseInt(new SimpleDateFormat("mm").format(new Date(
				window.end)));

		timeEnd.setCurrentHour(hour);
		timeEnd.setCurrentMinute(minute);

	}

	// Help method for findTimeWindow
	// Finds all TimeWindows between the given start and end time, that is
	// > 5 min and < 1 hour
	private static final ArrayList<TimeWindow> findHelp(
			final ArrayList<TimeWindow> list, long start, final long end) {
		final long size = 60000 * windowSize;
		long interval = end - start;
		while (interval > 0) {
			if (interval < 60000 * 5) {
				return list;
			}
			if (interval > size) {
				list.add(new TimeWindow(start, start + size));
				start = start + size;
				interval = interval - size;
			} else {
				list.add(new TimeWindow(start, end));
				interval = 0;
			}
		}
		return list;
	}

	// Finds the window to set TimePickers to
	private static final ArrayList<TimeWindow> findTimeWindow() {
		ArrayList<TimeWindow> returnList = new ArrayList<TimeWindow>();
		final long time = new Date().getTime();

		// Find next midnight
		final Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		final long midnight = calendar.getTimeInMillis();

		if (MainActivity.current == null) {
			// Find windows from now and the rest of the day
			returnList = findHelp(returnList, time, midnight);
			return returnList;
		}
		if (!MainActivity.current.isUnderway) {
			// Find windows from now until current starts
			returnList = findHelp(returnList, time,
					MainActivity.current.startTime);
		}
		if (MainActivity.eventlist.isEmpty()) {
			// Find windows from the end of current and the rest of the day
			returnList = findHelp(returnList, MainActivity.current.endTime,
					midnight);
		} else {
			// Find windows between current and first event
			returnList = findHelp(returnList, MainActivity.current.endTime,
					MainActivity.eventlist.get(0).startTime);
			final int size = MainActivity.eventlist.size() - 1;
			for (int i = 0; i < size; i++) {
				// Find windows between individual events
				final CalEvent first = MainActivity.eventlist.get(i);
				final CalEvent second = MainActivity.eventlist.get(i + 1);
				returnList = findHelp(returnList, first.endTime,
						second.startTime);
			}
			// Find windows from the last event until midnight
			final CalEvent last = MainActivity.eventlist
					.get(MainActivity.eventlist.size() - 1);
			returnList = findHelp(returnList, last.endTime, midnight);
		}
		return returnList;
	}

}
