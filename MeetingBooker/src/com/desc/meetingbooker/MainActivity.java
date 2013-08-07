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
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * An Activity that displays todays agenda, current/next meeting and all the
 * links to the other activities
 * 
 * @author Carl Johnsen
 * @version 1.0
 * @since 04-04-2013
 */
public final class MainActivity extends Activity {
	
	// All of the Views
	private static TextView 	  black_box;
	private static TextView 	  calendarName;
	private static View 		  curNextLay;
	private static TextView 	  currentAvail;
	private static TextView 	  currentDesc;
	private static TextView 	  currentEnd;
	private static TextView 	  currentOrganizer;
	private static TextView 	  currentStart;
	private static TextView 	  currentTitle;
	private static TextView 	  currentUpcom;
	private static TextView		  endMeeting;
	private static RelativeLayout line2;
	private static ListView 	  listView;
	private static View 		  mainView;
	private static View			  mainWrap;
	private static TextView		  nextMeeting;
	
	// All of the data fields
	private   static ArrayAdapter<CalEvent> adapter;
	private   static Context 				context;
	protected static CalEvent 				current = null;
	protected static ArrayList<CalEvent> 	eventlist = 
												new ArrayList<CalEvent>();
	private   static		boolean			hasPressed = false;
	private   static 		boolean			isDelayed = false;
	private   static 		boolean			isOverTime = false;
	private   static final  String 			TAG = MainActivity
												.class.getSimpleName();
	private	  		 		Timer			timer;
	private					TimerTask		timerTask;
	private					TimerTask		touchTask;
	private					Timer			touchTimer;
	private   static 		Window 			window;

	// All of the config fields
	protected static boolean canEnd;
	protected static boolean endDelete;
	protected static int 	 endExtend;
	protected static boolean extendEnd;
	protected static boolean extendStart;
	protected static String  roomName;
	protected static int 	 startExtend;

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
		setContentView(R.layout.activity_main);
		
		// Get window
		window = getWindow();

		// Get the context
		context = getApplicationContext();
		StatMeth.readConfig(context);

		// Cast all the Views
		black_box		 = (TextView)		findViewById(R.id.black_box);
		calendarName 	 = (TextView) 		findViewById(R.id.calendarName);
		curNextLay 		 = (View) 			findViewById(R.id.curnextLay);
		currentAvail 	 = (TextView) 		findViewById(R.id.currentAvail);
		currentDesc 	 = (TextView) 		findViewById(R.id.currentDesc);
		currentEnd 		 = (TextView) 		findViewById(R.id.currentEnd);
		currentOrganizer = (TextView) 		findViewById(R.id.currentOrganizer);
		currentStart 	 = (TextView) 		findViewById(R.id.currentStart);
		currentTitle 	 = (TextView) 		findViewById(R.id.currentTitle);
		currentUpcom 	 = (TextView) 		findViewById(R.id.currentUpcom);
		endMeeting 		 = (TextView) 		findViewById(R.id.endMeetingButton);
		line2 			 = (RelativeLayout) findViewById(R.id.line2);
		listView 		 = (ListView) 		findViewById(R.id.listView1);
		mainView 		 = (View) 			findViewById(R.id.mainLay);
		mainWrap		 = (View)			findViewById(R.id.mainLayWrap);
		nextMeeting 	 = (TextView) 		findViewById(R.id.nextMeetingButton);

		// Set the name of the Calendar
		calendarName.setText(roomName);

		// Make a new CalEventAdapter and give it to the ListView
		adapter = new CalEventAdapter(this, R.layout.calevent_item, eventlist);
		listView.setAdapter(adapter);

		// Set a custom OnItemClickListener
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public final void onItemClick(final AdapterView<?> arg0,
					final View arg1, 
					final int position, 
					final long arg3) {
				
				// Start NewEditActivity, and send along the selected event
				final Intent intent = new Intent(MainActivity.this,
						NewEditActivity.class);
				intent.putExtra("event", position);
				intent.putExtra("type", 1);
				startActivityForResult(intent, 1);
				
			}
		});

		// Make a new Timer for continuous update of calendar ie. call sync()
		// every 5 seconds
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public final void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public final void run() {
						MainActivity.sync();
					}
					
				});
			}
		};
		timer.scheduleAtFixedRate(timerTask, 10, 5000);
		
		// Initialize the touch timer
		touchTimer = new Timer();
		
		Log.d(TAG, "onCreate() done");
	}

	@Override
	protected final void onResume() {
		Log.d(TAG, "called onResume()");
		super.onResume();
		
		// Check if there are new events in the calendar, and check if there is
		// a new roomName
		tempLighten(mainView);
		sync();
		calendarName.setText(roomName);
	}
	
	/**
	 * Extends the start time of current, if noone have pressed "Start Meeting".
	 * If it have been extended one time, and it is delayed, the event will be
	 * deleted, if the config allows it
	 */
	private final static void currentDelayed() {
		// Check if it has gone overtime, and has'nt been extended
		final Long currentTime = new Date().getTime() + 10000;
		if (current != null && 
			!current.isUnderway && 
			!isDelayed && 
			current.startTime <= currentTime) {
			
			Log.d(TAG, "current is delayed; extend start");
			isDelayed = true;
			if ((current.endTime - current.startTime) > (startExtend * 60000)) {
				StatMeth.updateStart(current, context, current.startTime
						+ (startExtend * 60000));
			} else {
				StatMeth.updateStart(current, context, current.endTime - 60000);
			}
			return;
		}
		
		// Check if current should be deleted
		if (current != null && 
			!current.isUnderway && 
			isDelayed && 
			current.startTime <= currentTime) {
			Log.d(TAG, "current is still delayed; delete current");
			StatMeth.delete(current, context);
		}
	}

	/**
	 * Extends the end time of current, if noone have pressed "End Meeting"
	 */
	private final static void currentOvertime() {
		// Check if the event have ended
		if (current != null && current.description.endsWith("ended")) {
			return;
		}
		
		// Checks whether there is a current event, and whether it already have 
		// been extended, and whether it will go overtime in the next minute
		final Long currentTime = new Date().getTime() + 60000;
		if (current != null && !isOverTime && current.endTime <= currentTime) {
			Log.d(TAG, "current have gone over time; extend end");
			isOverTime = true;
			StatMeth.updateEnd(current, context, findExtendedTimeWindow());
		}
	}

	/**
	 * Shows or hides curNextLay
	 * 
	 * @param val Shows curNextLay if true
	 */
	private final static void curShow(final boolean val) {
		if (val) {
			Log.d(TAG, "show curnextLay");
			curNextLay.setClickable(true);
			curNextLay.setVisibility(View.VISIBLE);
			line2.setVisibility(RelativeLayout.VISIBLE);
		} else {
			Log.d(TAG, "hide curnextLay");
			curNextLay.setClickable(false);
			curNextLay.setVisibility(View.GONE);
			nextMeeting.setVisibility(Button.GONE);
			endMeeting.setVisibility(Button.GONE);
			line2.setVisibility(RelativeLayout.GONE);
		}
	}
	
	/**
	 * Changes the background color to black, and dims the display
	 */
	private static final void dim() {
		Log.d(TAG, "called dim()");
		mainWrap.setVisibility(View.GONE);
		black_box.setVisibility(RelativeLayout.VISIBLE);
		black_box.setBackgroundColor(context.getResources().getColor(R.color.black));
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF;
		window.setAttributes(lp);
		mainView.setClickable(true);
	}

	/**
	 * The method called if the user clicks on curNextLay. Does the same as the 
	 * ListViews OnItemClickListener, except it sends along current as the 
	 * selected event
	 * 
	 * @param view The View of the layout
	 */
	public final void editCurrent(View view) {
		Log.d(TAG, "pressed current; edit current");
		final Intent intent = new Intent(this, NewEditActivity.class);
		intent.putExtra("event", -1);
		intent.putExtra("type", 1);
		startActivityForResult(intent, 1);
	}

	/**
	 * The method called by the "EndMeeting" button. Changes the end time of the
	 * current event, to the current time, if the config allows if. If it 
	 * does'nt, it adds the identifier "ended" to the description, and then
	 * sync() will change the background to yellow
	 * 
	 * @param view The View from the button
	 */
	public final void endMeeting(final View view) {
		Log.d(TAG, "pressed EndMeeting button");
		if (endDelete) {
			StatMeth.updateEnd(current, context);
		} else {
			current.description = current.description + " - ended";
			StatMeth.update(current, context);
			sync();
		}
	}

	/**
	 * Finds the max endExtend minutes to extend with
	 * 
	 * @return The largest available time to extend with
	 */
	private final static long findExtendedTimeWindow() {
		Log.d(TAG, "called findExtendedTimeWindow()");
		// Check if there is a next event
		if (!eventlist.isEmpty()) {
			final long interval = eventlist.get(0).startTime - current.endTime;
			if (interval <= (60000 * endExtend)) {
				return eventlist.get(0).startTime;
			}
		}
		// If the eventlist is empty, give endExtend minutes
		return current.endTime + (60000 * endExtend);
	}
	
	/**
	 * Opposite of dim(). Lights up the display
	 */
	public static final void lighten() {
		Log.d(TAG, "called lighten()");
		if (hasPressed) {
			mainWrap.setVisibility(View.VISIBLE);
			black_box.setVisibility(RelativeLayout.GONE);
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
			window.setAttributes(lp);
		} else {
			mainWrap.setVisibility(View.VISIBLE);
			black_box.setVisibility(RelativeLayout.VISIBLE);
			black_box.setBackgroundColor(context.getResources().getColor(R.color.see_through));
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.screenBrightness = 0.25f;
			window.setAttributes(lp);
		}
	}

	/**
	 * The method called by the "NewMeeting" button. Starts the
	 * NewEditActivity
	 * 
	 * @param view The View from the button
	 */
	public final void newMeeting(final View view) {
		Log.d(TAG, "pressed NewMeeting button");
		final Intent intent = new Intent(this, NewEditActivity.class);
		intent.putExtra("type", 0);
		startActivityForResult(intent, 1);
	}
	
	/**
	 * 
	 */
	@Override
	public void onUserInteraction() {
		tempLighten(curNextLay);
		super.onUserInteraction();
	}

	/**
	 * Fills the curNextLay with information from the given event
	 * 
	 * @param event The event which will be set as current
	 */
	private final static void setCurrent(final CalEvent event) {
		Log.d(TAG, "called setCurrent()");
		currentTitle.setText(event.title);
		currentOrganizer.setText(event.organizer);
		currentDesc.setText(event.description);
		currentStart.setText("" + event.getStartTime() + " | ");
		currentEnd.setText("" + event.getEndTime());
	}

	/**
	 * The method called by the settings button. It inflates a password prompt
	 * 
	 * @param view The View of the button
	 */
	public final void settings(final View view) {
		Log.d(TAG, "pressed settings button");
		final DialogFragment fragment = new SettingsFragment();
		fragment.show(getFragmentManager(), "BLA");
	}

	/**
	 * The method called by the "StartNextMeeting button". Changes the start
	 * time of the next event, to the current time.
	 * 
	 * @param view The View from the button
	 */
	public final void startNextMeeting(final View view) {
		Log.d(TAG, "pressed NextMeeting button");
		StatMeth.updateStart(current, context);
	}

	/**
	 * The method called by the Timer every 5 seconds. It reads the calendar,
	 * and updates the UI if changes have been made
	 */
	protected final static void sync() {
		Log.d(TAG, "called sync()");
		if (!hasPressed && StatMeth.isEvening()) {
			dim();
			return;
		} else {
			lighten();
		}
		
		// The event that is currently underway
		current = null;

		// Reads all events from the calendar on the present day into an
		// ArrayList
		eventlist = StatMeth.readCalendar(context);
		Log.d(TAG, "found " + eventlist.size() + " events today");

		// Checks if any of the event in the ArrayList is underway,
		// and sets it as current event and removes it from the list
		if (!eventlist.isEmpty()) {
			current = eventlist.get(0);
			eventlist.remove(0);
		}

		// If the config allows it, check if current have gone over its end time
		if (extendEnd) {
			currentOvertime();
		}
		
		// If the config allows it, check if current is delayed, and havn't been
		// started
		if (extendStart) {
			currentDelayed();
		}

		// Sets the background color(Red if any event is underway, green if not)
		if (current != null && current.isUnderway) {
			mainView.setBackgroundColor(Color.RED);
			currentAvail.setText("Unavailable");
			currentUpcom.setText("Current\nMeeting");
			nextMeeting.setVisibility(Button.GONE);
			
			// If the config allows it, show the end meeting button
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
			
			// If there is a current event, show the curNextLay
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
			currentUpcom.setText("Last\nMeeting:");
			endMeeting.setVisibility(Button.GONE);
		}

		// Creates the listView
		adapter.clear();
		adapter.addAll(eventlist);
		Log.d(TAG, "sync() is done");

	}
	
	/**
	 * The method called when pressing the main layout
	 * 
	 * @param view The View of the layout
	 */
	public final void tempLighten(View view) {
		Log.d(TAG, "pressed the dimmed screen");
		hasPressed = true;
		lighten();
		sync();
		touchTimer.cancel();
		touchTimer = new Timer();
		touchTask = new TimerTask() {
			@Override
			public void run() {
				hasPressed = false;
			}
		};
		touchTimer.schedule(touchTask, 10000);
	}

	/**
	 * An DialogFragment, that shows a prompt for a password
	 * 
	 * @author Carl Johnsen
	 * @version 1.0
	 * @since 24-06-2013
	 */
	public final static class SettingsFragment extends DialogFragment {

		private static String TAG = SettingsFragment.class.getSimpleName();
		private static boolean wasWrong = false;

		@Override
		public final Dialog onCreateDialog(final Bundle savedInstanceState) {

			Log.d(TAG, "called onCreateDialog()");
			
			// Inflate the view
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					getActivity());
			final LayoutInflater inflater = getActivity().getLayoutInflater();
			final View v = inflater
					.inflate(R.layout.password_prompt_layout, null);
			
			// If the fragment have been started by a wrong typed password,
			// notify the user
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
									
									Log.d(TAG, "pressed OK button");
									// Find the the typed password, and the
									// saved password
									final EditText pwtext = (EditText) 
											v.findViewById(R.id.pwEdit);
									final String typedpw = pwtext.getText()
											.toString();
									final String storedpw = StatMeth
											.getPassword(context);
									
									// If the two passwords are equal, start
									// SettingsActivity.
									// If they weren't, Start this fragment
									// again, and notify the user
									if (typedpw.equals(storedpw)) {
										wasWrong = false;
										final Intent intent = new Intent(
												MainActivity.context,
												SettingsActivity.class);
										startActivityForResult(intent, 1);
										return;
									} else {
										wasWrong = true;
										final DialogFragment fragment = 
												new SettingsFragment();
										fragment.show(getFragmentManager(),
												"BLA");
									}

								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {

								// If cancel is pressed, close the fragment
								@Override
								public final void onClick(
										final DialogInterface dialog,
										final int which) {
									Log.d(TAG, "pressed Cancel button");
									wasWrong = false;
								}
								
							});
			return builder.create();
		}

	}

}