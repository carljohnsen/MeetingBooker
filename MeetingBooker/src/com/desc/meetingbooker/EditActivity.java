package com.desc.meetingbooker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
public class EditActivity extends Activity {

	private static final String TAG = EditActivity.class.getSimpleName();
	private TimePicker timeStart;
	private TimePicker timeEnd;
	private ListView intervalPicker;
	private Date date = new Date();
	private Calendar cal = Calendar.getInstance();
	private ArrayList<CalEvent> eventlist;
	private ArrayList<TimeWindow> windowList;
	private ArrayAdapter<TimeWindow> adapter;
	private int index;
	private EditText titleText;
	private EditText descText;
	private CalEvent event;
	private Context context;
	
	private Button delete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "called onCreate()");
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, 
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_new_meeting);

		eventlist = MainActivity.eventlist;
		index = this.getIntent().getIntExtra("event", -2);
		if (index == -1) {
			event = MainActivity.current;
		} else {
			event = eventlist.get(index);
		}
		delete = (Button) findViewById(R.id.deleteButton);
		delete.setVisibility(Button.VISIBLE);
		context = getApplicationContext();

		// Finds the TimePickers
		timeStart = (TimePicker) findViewById(R.id.timePickerStart);
		timeEnd = (TimePicker) findViewById(R.id.timePickerEnd);
		titleText = (EditText) findViewById(R.id.editTitle);
		descText = (EditText) findViewById(R.id.editDesc);

		intervalPicker = (ListView) findViewById(R.id.intervalView);
		windowList = findTimeWindow();

		adapter = new ArrayAdapter<TimeWindow>(getApplicationContext(),
				R.layout.list_black_text, R.id.list_content, windowList);

		// Setting the ListView
		intervalPicker.setAdapter(adapter);

		intervalPicker.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				setTimePickers(windowList.get(position));
			}
		});

		titleText.setText(event.getTitle());
		descText.setText(event.getDescription());
		setTimePickers(event.getTimeWindow());
		Button add = (Button) findViewById(R.id.addButton);
		add.setText("Update");
	}

	public void delete(View view) {
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle("Delete");
		dialog.setMessage("Are you sure you want to delete this event?");
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						StatMeth.updateStart(event, context);
						StatMeth.updateEnd(event, context);
						finish();
					}

				});
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", 
				new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
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
	public void add(View view) {
		Log.d(TAG, "Adding event to calendar");

		// Read the fields
		String title = titleText.getText().toString();
		String desc = descText.getText().toString();
		int startHour = timeStart.getCurrentHour();
		int startMin = timeStart.getCurrentMinute();
		int endHour = timeEnd.getCurrentHour();
		Log.d(TAG, "END " + endHour);
		int endMin = timeEnd.getCurrentMinute();

		// Convert timePicker readings to long
		String startTime = cal.get(Calendar.DAY_OF_MONTH) + "-"
				+ (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.YEAR)
				+ " " + startHour + ":" + startMin;
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm",
				Locale.getDefault());
		try {
			date = formatter.parse(startTime);
			Log.d(TAG, startTime);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		long start = date.getTime();
		String endTime = cal.get(Calendar.DAY_OF_MONTH) + "-"
				+ (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.YEAR)
				+ " " + endHour + ":" + endMin;
		try {
			date = formatter.parse(endTime);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		long end = date.getTime();

		// Create a new CalEvent
		CalEvent newEvent = new CalEvent(start, end, title, desc, event.getId());
		Context context = getApplicationContext();
		if (StatMeth.isBefore(newEvent)) {
			AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("Error");
			dialog.setMessage("End time is before start time");
			dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
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
			AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("Error");
			dialog.setMessage("Meeting is overlapping");
			dialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
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
	public void cancel(View view) {
		Log.d(TAG, "Cancel button pressed");
		finish();
	}

	// Sets time pickers to a possible interval
	@SuppressLint("SimpleDateFormat")
	private void setTimePickers(TimeWindow window) {

		// Sets the TimePickers to use 24 hour
		timeStart.setIs24HourView(true);
		timeEnd.setIs24HourView(true);

		int hour = Integer.parseInt(new SimpleDateFormat("HH").format(new Date(
				window.getStart())));
		int minute = Integer.parseInt(new SimpleDateFormat("mm")
				.format(new Date(window.getStart())));

		timeStart.setCurrentHour(hour);
		timeStart.setCurrentMinute(minute);

		hour = Integer.parseInt(new SimpleDateFormat("HH").format(new Date(
				window.getEnd())));
		minute = Integer.parseInt(new SimpleDateFormat("mm").format(new Date(
				window.getEnd())));

		timeEnd.setCurrentHour(hour);
		timeEnd.setCurrentMinute(minute);

	}

	// Finds the window to set TimePickers to
	private ArrayList<TimeWindow> findTimeWindow() {
		ArrayList<TimeWindow> returnList = new ArrayList<TimeWindow>();
		CalEvent current = MainActivity.current;
		long time = new Date().getTime();
		long fiveMin = 60000 * 5;
		long oneHour = 60000 * 60;

		if (current == null) {
			returnList.add(new TimeWindow(time, time + oneHour));
			return returnList;
		}
		if (!current.isUnderway()) {
			long interval = current.getStart() - time;
			if (interval >= fiveMin) {
				returnList.add(new TimeWindow(time, current.getStart()));
			}
		}
		returnList.add(new TimeWindow(current.getEnd(), current.getEnd()
				+ oneHour));
		if (!eventlist.isEmpty()) {
			long interval = eventlist.get(0).getStart() - current.getEnd();
			if (interval >= fiveMin) {
				returnList.add(new TimeWindow(current.getEnd(), eventlist
						.get(0).getStart()));
			} else {
				for (int i = 0; i < eventlist.size() - 1; i++) {
					interval = eventlist.get(i + 1).getStart()
							- eventlist.get(i).getEnd();
					if (interval >= fiveMin) {
						returnList.add(new TimeWindow(
								eventlist.get(i).getEnd(), eventlist.get(i + 1)
										.getStart()));
					}
				}
			}
			long eventlistLast = eventlist.get(eventlist.size() - 1).getEnd();
			returnList.add(new TimeWindow(eventlistLast, eventlistLast
					+ oneHour));
		}
		return returnList;
	}

}
