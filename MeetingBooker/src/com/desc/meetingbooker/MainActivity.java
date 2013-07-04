package com.desc.meetingbooker;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * An Activity that displays todays agenda, current/next meeting and all the
 * links to the other activities
 * 
 * @author Carl Johnsen, Daniel Pedersen, Emil Pedersen and Sune Bartels
 * @version 0.9
 * @since 04-04-2013
 */
public final class MainActivity extends Activity {

	private static ListView listView;
	private static TextView calendarName;
	private static TextView currentAvail;
	private static View curNextLay;
	private static TextView currentUpcom;
	private static TextView currentTitle;
	private static TextView currentOrganizer;
	private static TextView currentDesc;
	private static TextView currentStart;
	private static TextView currentEnd;
	private static View mainView;
	private static Context context;
	private static final String TAG = MainActivity.class.getSimpleName();
	protected static ArrayList<CalEvent> eventlist = new ArrayList<CalEvent>();
	private static ArrayAdapter<CalEvent> adapter;
	protected static CalEvent current = null;
	private static Button nextMeeting;
	private static Button endMeeting;
	private static boolean isDelayed = false;
	private static boolean isOverTime = false;

	protected static boolean extendEnd;
	protected static int endExtend;
	protected static boolean extendStart;
	protected static int startExtend;
	protected static boolean canEnd;
	protected static boolean endDelete;
	protected static String roomName;

	@Override
	protected final void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate() called");

		// Hide Status bar and App title bar (before setContentView())
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_main);

		// Get the context
		context = getApplicationContext();
		StatMeth.readConfig(context);

		// Casting all the Views
		calendarName = (TextView) findViewById(R.id.calendarName);
		currentAvail = (TextView) findViewById(R.id.currentAvail);
		curNextLay = (View) findViewById(R.id.curnextLay);
		currentUpcom = (TextView) findViewById(R.id.currentUpcom);
		currentTitle = (TextView) findViewById(R.id.currentTitle);
		currentOrganizer = (TextView) findViewById(R.id.currentOrganizer);
		currentDesc = (TextView) findViewById(R.id.currentDesc);
		currentStart = (TextView) findViewById(R.id.currentStart);
		currentEnd = (TextView) findViewById(R.id.currentEnd);
		mainView = (View) findViewById(R.id.mainLay);
		nextMeeting = (Button) findViewById(R.id.nextMeetingButton);
		endMeeting = (Button) findViewById(R.id.endMeetingButton);
		listView = (ListView) findViewById(R.id.listView1);

		// Set the name of the Calendar
		calendarName.setText(roomName);

		// ArrayAdapter for the ListView of events
		adapter = new CalEventAdapter(this, R.layout.calevent_item, eventlist);

		// Setting the ListView
		listView.setAdapter(adapter);

		// Setting a custom ItemClickListener
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public final void onItemClick(final AdapterView<?> arg0,
					final View arg1, final int position, final long arg3) {
				final Intent intent = new Intent(MainActivity.this,
						NewEditActivity.class);
				intent.putExtra("event", position);
				intent.putExtra("type", 1);
				startActivityForResult(intent, 1);
			}
		});

		// Timer for continuous update of calendar
		final Timer timer = new Timer();
		Log.d(TAG, "Timer started");
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public final void run() {
				runOnUiThread(new Runnable() {
					@Override
					public final void run() {
						MainActivity.sync();
					}
				});
			}
		}, 10, 5000);
		Log.d(TAG, "onCreate() done");
	}

	@Override
	protected final void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();
		MainActivity.sync();
		calendarName.setText(roomName);
	}

	@Override
	protected final void onStop() {
		super.onStop();
	}

	@Override
	protected final void onActivityResult(final int requestcode,
			final int resultcode, final Intent data) {
		// startActivity(new Intent(this,MainActivity.class));
	}

	public final void editCurrent(View view) {
		final Intent intent = new Intent(this, NewEditActivity.class);
		intent.putExtra("event", -1);
		intent.putExtra("type", 1);
		startActivityForResult(intent, 1);
	}

	/**
	 * The method called by the "StartNextMeeting button". Changes the start
	 * time of the next event, to the current time.
	 * 
	 * @param view
	 *            The View from the button
	 */
	public final void startNextMeeting(final View view) {
		StatMeth.updateStart(current, context);
	}

	/**
	 * The method called by the "EndMeeting" button. Changes the end time of the
	 * current event, to the current time
	 * 
	 * @param view
	 *            The View from the button
	 */
	public final void endMeeting(final View view) {
		if (endDelete) {
			StatMeth.updateEnd(current, context);
		} else {
			current.description = current.description + " ended";
			StatMeth.update(current, context);
			sync();
		}
	}

	public final void settings(final View view) {
		final DialogFragment fragment = new SettingsFragment();
		fragment.show(getFragmentManager(), "BLA");
	}

	/**
	 * The method called by the "NewMeeting" button. Starts the
	 * NewMeetingActivity
	 * 
	 * @param view
	 *            The View from the button
	 */
	public final void startNewMeeting(final View view) {
		Log.d(TAG, "New Meeting button pressed");
		// Creates NewMeetingActivity, for user input in booking a new meeting
		final Intent intent = new Intent(this, NewEditActivity.class);
		intent.putExtra("type", 0);
		startActivityForResult(intent, 1);
	}

	/**
	 * Changes the current event, to the given event
	 * 
	 * @param event
	 *            The event which should be set as current
	 */
	private final static void setCurrent(final CalEvent event) {
		currentTitle.setText(event.title);
		currentOrganizer.setText(event.organizer);
		currentDesc.setText(event.description);
		currentStart.setText("" + event.getStartTime() + " | ");
		currentEnd.setText("" + event.getEndTime());
	}

	// Shows and hides the TextViews for current event
	private final static void curShow(final boolean val) {
		if (val) {
			curNextLay.setClickable(true);
			curNextLay.setVisibility(TextView.VISIBLE);
		} else {
			curNextLay.setClickable(false);
			curNextLay.setVisibility(TextView.GONE);
			nextMeeting.setVisibility(Button.GONE);
			endMeeting.setVisibility(Button.GONE);
		}
	}

	private final static void deleteCurrent() {
		StatMeth.delete(current, context);
	}

	// Pushes the current event forward by, up to 15 minutes if nobody pressed
	// End Meeting
	private final static void currentOvertime() {
		if (current != null && current.description.endsWith("ended")) {
			return;
		}
		final Long currentTime = new Date().getTime() + 60000;
		if (current != null && !isOverTime && current.endTime <= currentTime) {
			isOverTime = true;
			StatMeth.updateEnd(current, context, findExtendedTimeWindow());
		}
	}

	private final static void currentDelayed() {
		final Long currentTime = new Date().getTime() + 10000;
		if (current != null && !current.isUnderway && !isDelayed
				&& current.startTime <= currentTime) {
			isDelayed = true;
			if ((current.endTime - current.startTime) > (startExtend * 60000)) {
				StatMeth.updateStart(current, context, current.startTime
						+ (startExtend * 60000));
			} else {
				StatMeth.updateStart(current, context, current.endTime - 60000);
			}
		}
		if (current != null && !current.isUnderway && isDelayed
				&& current.startTime <= currentTime) {
			deleteCurrent();
		}
	}

	// Gives up to 15 minutes to extend current event
	private final static long findExtendedTimeWindow() {
		if (!eventlist.isEmpty()) {
			final long interval = eventlist.get(0).startTime - current.endTime;
			if (interval < (60000 * endExtend)) {
				return eventlist.get(0).startTime;
			}
		}
		return current.endTime + (60000 * endExtend);
	}

	/**
	 * The method called by the Timer every 5 seconds. It reads the calendar,
	 * and updates the UI if changes have been made
	 */
	protected final static void sync() {
		// The event that is currently underway
		current = null;

		// Reads all events from the calendar on the present day into an
		// ArrayList
		eventlist = StatMeth.readCalendar(MainActivity.context);

		// Checks if any of the event in the ArrayList is underway,
		// and sets it as current event and removes it from the list
		if (!eventlist.isEmpty()) {
			current = eventlist.get(0);
			eventlist.remove(0);
		}

		if (extendEnd) {
			currentOvertime();
		}
		if (extendStart) {
			currentDelayed();
		}

		// Sets the background color(Red if any event is underway, green if not)
		if (current != null && current.isUnderway) {
			mainView.setBackgroundColor(Color.RED);
			currentAvail.setText("Unavailable");
			currentUpcom.setText("Current\nMeeting");
			nextMeeting.setVisibility(Button.GONE);
			if (canEnd) {
				endMeeting.setVisibility(Button.VISIBLE);
			} else {
				endMeeting.setVisibility(Button.GONE);
			}
			setCurrent(current);
			isDelayed = false;
			curShow(true);
		} else {
			mainView.setBackgroundColor(Color.GREEN);
			currentAvail.setText("Available");
			currentUpcom.setText("Upcoming\nMeeting");
			isOverTime = false;
			if (current != null) {
				nextMeeting.setVisibility(Button.VISIBLE);
				endMeeting.setVisibility(Button.GONE);
				setCurrent(current);
				curShow(true);
			} else {
				curShow(false);
			}
		}

		// Check if the end meeting has been checked
		// if it is, and it has ended, it changes background to yellow
		if (!endDelete && current != null
				&& current.description.endsWith("ended")) {
			mainView.setBackgroundColor(Color.YELLOW);
			currentAvail.setText("Available");
			currentUpcom.setText("Last Meeting:");
			endMeeting.setVisibility(Button.GONE);
		}

		// Creates the listView
		adapter.clear();
		adapter.addAll(eventlist);

	}

	public final static class SettingsFragment extends DialogFragment {

		private static boolean wasWrong = false;

		@Override
		public final Dialog onCreateDialog(final Bundle savedInstanceState) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					getActivity());
			final LayoutInflater inflater = getActivity().getLayoutInflater();
			final View v = inflater.inflate(R.layout.password_prompt_layout,
					null);
			if (wasWrong) {
				v.findViewById(R.id.pwPrompt).setVisibility(TextView.VISIBLE);
			} else {
				v.findViewById(R.id.pwPrompt).setVisibility(TextView.GONE);
			}
			builder.setView(v)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public final void onClick(
										final DialogInterface arg0,
										final int arg1) {
									final EditText pwtext = (EditText) v
											.findViewById(R.id.pwEdit);
									final String typedpw = pwtext.getText()
											.toString();
									final String storedpw = StatMeth
											.getPassword(context);
									if (typedpw.equals(storedpw)) {
										wasWrong = false;
										final Intent intent = new Intent(
												MainActivity.context,
												SettingsActivity.class);
										startActivityForResult(intent, 1);
										return;
									} else {
										wasWrong = true;
										final DialogFragment fragment = new SettingsFragment();
										fragment.show(getFragmentManager(),
												"BLA");
									}

								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								@Override
								public final void onClick(
										final DialogInterface dialog,
										final int which) {
									wasWrong = false;
								}
							});
			return builder.create();
		}

	}

}
