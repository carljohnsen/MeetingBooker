package com.desc.meetingbooker;


import java.util.ArrayList;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.AdapterView.OnItemClickListener;

/**
 * The Activity used when an event is being edited
 * 
 * @author Carl Johnsen
 * @version 1.3
 * @since 14-05-2013
 */
public final class NewEditActivity extends Activity {
	
	// All of the Views
	private static TextView	activityTitle;
	private static TextView	addButton;
	private static ImageView  deleteButton;
	private static EditText   descriptionTextEdit;
	private static ListView   intervalPicker;
	private static TimePicker timeEnd;
	private static TimePicker timeStart;
	private static EditText   titleTextEdit;
	private static TextView	updateButton;

	// All of the data fields
	private static 		TimeWindowAdapter 	   adapter;
	private static 		Context 			   context;
	private static 		CalEvent 			   event;
	private static 		int 				   index;
	private static final String 				   TAG = NewEditActivity
														 .class.getSimpleName();
	private static 		 ArrayList<TimeWindow> windowList;

	// All of the config fields
	protected static boolean candelete;
	protected static int 		windowSize;

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		Log.d(TAG, "called onCreate()");
		super.onCreate(savedInstanceState);
		
		// Hide Status bar and App title bar (before setContentView())
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		// Set the content view
		setContentView(R.layout.activity_new_edit);
		
		context = getApplicationContext();

		// Cast all the views
		activityTitle 		= (TextView)	findViewById(R.id.new_edit_title);
		titleTextEdit 		= (EditText)	findViewById(R.id.new_edit_left_title_value);
		descriptionTextEdit = (EditText)	findViewById(R.id.new_edit_left_description_value);
		addButton 			= (TextView)	findViewById(R.id.new_edit_right_add_button);
		updateButton 		= (TextView)	findViewById(R.id.new_edit_right_update_button);
		deleteButton 		= (ImageView)	findViewById(R.id.new_edit_left_delete_button);
		timeStart 			= (TimePicker) 	findViewById(R.id.new_edit_left_time_picker_start);
		timeEnd 			= (TimePicker) 	findViewById(R.id.new_edit_left_time_picker_end);
		intervalPicker 		= (ListView)	findViewById(R.id.new_edit_right_interval_list);
		
		// Sets the TimePickers to use 24 hour
		timeStart	.setIs24HourView(true);
		timeEnd		.setIs24HourView(true);
		
		// Find TimeWindows, make a new adapter and add it to the ListView
		windowList = findTimeWindow();
		adapter = new TimeWindowAdapter(this, R.layout.item_timewindow,
				windowList);
		intervalPicker.setAdapter(adapter);
		Log.d(TAG, "found " + windowList.size() + " TimeWindows");

		// Set the OnItemClickListener
		intervalPicker.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, 
					View arg1,
					int position, 
					long arg3) {
				Log.d(TAG, "pressed " + windowList.get(position).toString());
				// Set the TimePickers to the selected TimeWindow
				setTimePickers(windowList.get(position));
			}
			
		});

		// Checks which formula it should show
		if (this.getIntent().getIntExtra("type", 0) == 0) {
			setNew();
		} else {
			setEdit();
		}
		Log.d(TAG, "onCreate() is done");
	}

	/**
	 * The method called by the "Add" button. Reads all of the fields in the UI,
	 * inserts them into a new CalEvent and then sends it to EventCreate
	 * 
	 * @param view The View of the button
	 */
	public final void add(final View view) {
		// Set the add button to not clickable, to ensure no double bookings
		addButton.setClickable(false);

		Log.d(TAG, "pressed Add button");

		// Read the fields
		String title = titleTextEdit.getText().toString();
		if (title.equals("")) {
			title = titleTextEdit.getHint().toString();
		}
		final String desc 		= descriptionTextEdit.getText().toString();
		final int startHour 	= timeStart.getCurrentHour();
		final int startMin 	= timeStart.getCurrentMinute();
		final int endHour 		= timeEnd.getCurrentHour();
		final int endMin 		= timeEnd.getCurrentMinute();

		// Set the time to now
		Time time = new Time();
		time.setToNow();

		// Set the start time to the information read from TimePicker
		time.set(0, startMin, startHour, time.monthDay, time.month, time.year);
		final long start = time.toMillis(false);

		// Set the end time to the information read from TimePicker
		time.set(0, endMin, endHour, time.monthDay, time.month, time.year);
		final long end = time.toMillis(false);

		// Create a new CalEvent
		final CalEvent event = new CalEvent(start, end, title, desc);
		
		// If the events end time is before start time, notify the user
		if (StatMeth.eventStartIsBeforeEnd(event)) {
			final AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("Error");
			dialog.setMessage("End time is before start time");
			dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(final DialogInterface dialog,
								final int which) {
							// DO NOTHING
						}

					});
			Log.d(TAG, "showed isBefore error dialog");
			dialog.show();
			addButton.setClickable(true);
			return;
		}
		
		// If the selected time is available, insert the event
		// If not, notify the user
		if (StatMeth.isFree(event, context)) {
			StatMeth.setNewEvent(event, context);
			StatMeth.remoteLog("Added new meeting: " + event.title);
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
							// DO NOTHING
						}

					});
			Log.d(TAG, "showed overlapping dialog");
			dialog.show();
			addButton.setClickable(true);
		}
	}

	/**
	 * The method called by the "Cancel" button. Returns the user to the
	 * MainActivity
	 * 
	 * @param view The View of the button
	 */
	public final void cancel(final View view) {
		Log.d(TAG, "Cancel button pressed");
		finish();
	}

	/**
	 * The method called by the delete button. Inflates a "are you sure you
	 * want to delete" dialog
	 * 
	 * @param view The View of the button
	 */
	public final void delete(final View view) {
		final AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle("Delete");
		dialog.setMessage("Are you sure you want to delete this event?");
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						Log.d(TAG, "pressed OK button");
						StatMeth.delete(event, context);
						MainActivity.sync();
						StatMeth.remoteLog("Deleted event: " + event.title);
						finish();
					}

				});
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(final DialogInterface dialog,
							final int which) {
						Log.d(TAG, "pressed Cancel button");
					}
				});
		Log.d(TAG, "showed delete dialog");
		dialog.show();
	}

	/**
	 * Help method for findTimeWindow. Finds all TimeWindows between start and 
	 * end. The size of the windows depends on the windowSize config 
	 * 
	 * @param list The ArrayList that the windows will be added to
	 * @param start The start time it should find windows from
	 * @param end The end time it should find windows to
	 * @return an ArrayList of available TimeWindows between start and end
	 */
	private static final ArrayList<TimeWindow> findHelp(
			final ArrayList<TimeWindow> list, 
			long start, 
			final long end) {
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

	/**
	 * Finds all available TimeWindows from now and until 23:59
	 * 
	 * @return An ArrayList of available TimeWindows
	 */
	private static final ArrayList<TimeWindow> findTimeWindow() {
		Log.d(TAG, "called findTimeWindow()");
		// Make a new ArrayList and find current time
		ArrayList<TimeWindow> returnList = new ArrayList<TimeWindow>();
		
		// Set the time to now
		Time time = new Time();
		time.setToNow();
		
		// Set the time to the next quarter
		if (time.minute > 45) {
			time.set(0, 0, time.hour+1, time.monthDay, time.month, time.year);
		} else if (time.minute > 30) {
			time.set(0, 45, time.hour, time.monthDay, time.month, time.year);
		} else if (time.minute > 15) {
			time.set(0, 30, time.hour, time.monthDay, time.month, time.year);
		} else {
			time.set(0, 15, time.hour, time.monthDay, time.month, time.year);
		}
		final long start = time.toMillis(false);
		
		// Set the time 1 minute before midnight
		time.set(0, 0, 23, time.monthDay, time.month, time.year);
		final long midnight = time.toMillis(false);

		if (MainActivity.current == null) {
			// Find windows from now and the rest of the day
			returnList = findHelp(returnList, start, midnight);
			return returnList;
		}
		if (!MainActivity.current.isUnderway) {
			// Find windows from now until current starts
			returnList = findHelp(returnList, start,
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

	/**
	 * Called by onCreate(). Used when it should show Edit formula
	 */
	private final void setEdit() {
		Log.d(TAG, "called setEdit()");
		
		// Change the title
		activityTitle.setText(R.string.text_edit_meeting);
		
		// Gets the index of the selected event
		index = this.getIntent().getIntExtra("event", -2);
		if (index == -1) {
			event = MainActivity.current;
		} else {
			event = MainActivity.eventlist.get(index);
		}
		
		// If the config allows it, show the delete button
		if (candelete) {
			deleteButton.setVisibility(ImageView.VISIBLE);
		} else {
			deleteButton.setVisibility(ImageView.GONE);
		}

		// Set the views to information from the event
		titleTextEdit.setText(event.title);
		descriptionTextEdit.setText(event.description);
		setTimePickers(event.getTimeWindow());
		
		// Hide add button and show update button
		addButton.setVisibility(Button.GONE);
		updateButton.setVisibility(Button.VISIBLE);
		
	}

	/**
	 * Called by onCreate. Used when it should show new meeting formula
	 */
	private final void setNew() {
		Log.d(TAG, "called setNew()");
		
		// Change the title
		activityTitle.setText(R.string.text_new_meeting);
		
		// Hide the delete button
		deleteButton.setVisibility(Button.GONE);
		
		// Set the titleTexts hint to the default value 
		titleTextEdit.setHint(titleTextEdit.getText().toString());
		titleTextEdit.setText("");
		
		// Set the TimePickers to the first found TimeWindow, if there is any
		if (windowList.isEmpty()) {
			final AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("Error");
			dialog.setMessage("There are no more available times today");
			dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(final DialogInterface dialog,
								final int which) {
							finish();
						}

					});
			dialog.show();
		} else {
			setTimePickers(windowList.get(0));
		}
		
		// Show the add button and hide the update button
		addButton.setVisibility(Button.VISIBLE);
		updateButton.setVisibility(Button.GONE);
	}

	// Sets time pickers to a possible interval
	@SuppressLint("SimpleDateFormat")
	private final void setTimePickers(final TimeWindow window) {
		Log.d(TAG, "called setTimePickers()");
		
		Time time = new Time();
		
		// Set the start TimePicker to the windows start time
		time.set(window.start);
		timeStart.setCurrentHour(time.hour);
		timeStart.setCurrentMinute(time.minute);

		// Set the end TimePicker to the windows end time
		time.set(window.end);
		timeEnd.setCurrentHour(time.hour);
		timeEnd.setCurrentMinute(time.minute);

	}

	/**
	 * The method called by the "Add" button. Reads all of the fields in the UI,
	 * inserts them into a new CalEvent and then sends it to EventCreate
	 * 
	 * @param view The View of the button
	 */
	public final void update(final View view) {
		Log.d(TAG, "pressed update button");

		// Read the fields
		final String title 		= titleTextEdit.getText().toString();
		final String desc 		= descriptionTextEdit.getText().toString();
		final int startHour 	= timeStart.getCurrentHour();
		final int startMin 	= timeStart.getCurrentMinute();
		final int endHour 		= timeEnd.getCurrentHour();
		final int endMin 		= timeEnd.getCurrentMinute();

		// Set the time to now
		Time time = new Time();
		time.setToNow();
		
		// Set the start time to the information read from TimePicker
		time.set(0, startMin, startHour, time.monthDay, time.month, time.year);
		final long start = time.toMillis(false);

		// Set the end time to the information read from TimePicker
		time.set(0, endMin, endHour, time.monthDay, time.month, time.year);
		final long end = time.toMillis(false);

		// Create a new CalEvent
		final CalEvent newEvent = new CalEvent(start, end, title, desc,
				event.id);
		
		// If the end time is before the start time, notify the user
		if (StatMeth.eventStartIsBeforeEnd(newEvent)) {
			final AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("Error");
			dialog.setMessage("End time is before start time");
			dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(final DialogInterface dialog,
								final int which) {
							// DO NOTHING
						}

					});
			Log.d(TAG, "showed isBefore dialog");
			dialog.show();
			return;
		}
		
		// If the selected time is available, update the event
		// If not, notify the user
		if (StatMeth.isUpdatable(newEvent, context)) {
			StatMeth.update(newEvent, context);
			StatMeth.remoteLog("Updated event: " + newEvent.title);
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
							// DO NOTHING
						}

					});
			Log.d(TAG, "showed overlapping dialog");
			dialog.show();
		}
	}

}
