package com.desc.meetingbooker;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.Format;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;

/**
 * A Class that holds all the static methods
 * 
 * @author carljohnsen
 * @version 1.0
 * @since 24-06-2013
 */
public final class StatMeth {

	// The query used to get the events from the Android calendar
	private static final String[] COLS = new String[] {
			CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND,
			CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION,
			CalendarContract.Events._ID, CalendarContract.Events.ORGANIZER };
	private static Cursor cursor;
	private static ArrayList<Setting> settings;

	/**
	 * Creates a new config.cfg file
	 * 
	 * @param context The context of the application
	 */
	private final static void configMake(final Context context) {
		Log.d("Config", "configMake()!");
		try {
			// Open the file
			final FileOutputStream out = context.openFileOutput("config.cfg",
					Context.MODE_PRIVATE);
			final OutputStreamWriter outputStream = new OutputStreamWriter(out);
			
			// Write all the config lines
			String line;
			line = "extendstarttime true";
			interpret(line);
			line += "\n";
			outputStream.write(line, 0, line.length());
			line = "starttime 15";
			interpret(line);
			line += "\n";
			outputStream.write(line, 0, line.length());
			line = "extendendtime true";
			interpret(line);
			line += "\n";
			outputStream.write(line, 0, line.length());
			line = "endtime 15";
			interpret(line);
			line += "\n";
			outputStream.write(line, 0, line.length());
			line = "candelete true";
			interpret(line);
			line += "\n";
			outputStream.write(line, 0, line.length());
			line = "canend true";
			interpret(line);
			line += "\n";
			outputStream.write(line, 0, line.length());
			line = "enddelete true";
			interpret(line);
			line += "\n";
			outputStream.write(line, 0, line.length());
			line = "windowsize 60";
			interpret(line);
			line += "\n";
			outputStream.write(line, 0, line.length());
			line = "calendarname " + getCalendarName(context);
			interpret(line);
			line += "\n";
			outputStream.write(line, 0, line.length());
			
			// Close the file
			outputStream.close();
			out.close();
		} catch (IOException e) {
			Log.d("ConfigReader", e.getMessage());
		}
	}

	/**
	 * "Deletes" the given event. The method for deletion in this application,
	 * is to change the start time of the event, to now, and the end time to one
	 * millisecond later 
	 * 
	 * @param event The given event
	 * @param context The context of the application
	 */
	public final static void delete(final CalEvent event, 
			final Context context) {
		// Get the ContentResolver and the URI
		final ContentResolver cr = context.getContentResolver();
		final ContentValues cv = new ContentValues();
		Uri uri = null;
		
		// Change the start and end time of the event
		final long time = new Date().getTime();
		cv.put(Events.DTSTART, time);
		cv.put(Events.DTEND, time + 1);
		uri = ContentUris.withAppendedId(Events.CONTENT_URI, event.id);
		
		// Update the calendar
		cr.update(uri, cv, null, null);
	}

	/**
	 * The method to get the name of the calendar
	 * 
	 * @param context The context of the app, used to extract the CONTENT_URI 
	 * 				  and the ContentResolver
	 * @return The name of the calendar
	 */
	public final static String getCalendarName(final Context context) {
		// The query
		final String[] que = { 
			CalendarContract.Calendars.CALENDAR_DISPLAY_NAME 
		};
		
		// Get the ContentResolver, and extract the Cursor
		final ContentResolver cr = context.getContentResolver();
		final Cursor cursor = cr.query(CalendarContract.Calendars.CONTENT_URI,
				que, null, null, null);
		
		// Take the information from the first result, and return it
		cursor.moveToFirst();
		final String result = cursor.getString(0);
		cursor.close();
		return result;
	}

	/**
	 * Used for retrieving the password from private file pwd
	 * 
	 * @param context The context of the application
	 * @return The password needed to unlock the settings menu
	 */
	public final static String getPassword(final Context context) {
		try {
			// Open the file
			final FileInputStream in = context.openFileInput("pwd");
			final InputStreamReader reader = new InputStreamReader(in);
			final BufferedReader bufferedReader = new BufferedReader(reader);
			
			// Read the file
			final String password = bufferedReader.readLine();
			
			// Close the file
			reader.close();
			in.close();
			return password;
		} catch (FileNotFoundException e) {
			return newPassword(context);
		} catch (IOException e) {
			return "ERROR";
		}
	}

	/**
	 * Interprets a given string, and changes the config fields in the other
	 * classes
	 * 
	 * @param str The string that will be interpretet
	 */
	private final static void interpret(final String str) {
		// Find the whitespace in the String, and split it into two
		final int index = str.indexOf(' ');
		final String command = str.substring(0, index);
		final String value = str.substring(index + 1, str.length());
		
		// Make the new Setting, and change it according to its information
		final Setting setting;
		if (command.equals("extendendtime")) {
			MainActivity.extendEnd = Boolean.parseBoolean(value);
			setting = new Setting(command, value, "boolean", "Extend end time");
			settings.add(setting);
			return;
		}
		if (command.equals("endtime")) {
			MainActivity.endExtend = Integer.parseInt(value);
			setting = 
					new Setting(command, value, "int", "Minutes to extend by");
			settings.add(setting);
			return;
		}
		if (command.equals("extendstarttime")) {
			MainActivity.extendStart = Boolean.parseBoolean(value);
			setting = new Setting(command, value, "boolean",
					"Extend start time");
			settings.add(setting);
			return;
		}
		if (command.equals("starttime")) {
			MainActivity.startExtend = Integer.parseInt(value);
			setting = new Setting(command, value, "int",
					"Minutes to extend with");
			settings.add(setting);
			return;
		}
		if (command.equals("candelete")) {
			NewEditActivity.candelete = Boolean.parseBoolean(value);
			setting = new Setting(command, value, "boolean",
					"Show the delete button");
			settings.add(setting);
			return;
		}
		if (command.equals("canend")) {
			MainActivity.canEnd = Boolean.parseBoolean(value);
			setting = new Setting(command, value, "boolean",
					"Show the End Meeting button");
			settings.add(setting);
			return;
		}
		if (command.equals("enddelete")) {
			MainActivity.endDelete = Boolean.parseBoolean(value);
			setting = new Setting(command, value, "boolean", "End delete");
			settings.add(setting);
			return;
		}
		if (command.equals("windowsize")) {
			NewEditActivity.windowSize = Integer.parseInt(value);
			setting = new Setting(command, value, "int",
					"Length of TimeWindows");
			settings.add(setting);
			return;
		}
		if (command.equals("calendarname")) {
			MainActivity.roomName = value;
			setting = new Setting(command, value, "String", "Calendar name");
			settings.add(setting);
			return;
		}
	}

	/**
	 * Checks the end time of the given event is before the start time
	 * 
	 * @param event
	 *            The given event
	 * @return true, if the end is before the start
	 */
	public final static boolean isBefore(final CalEvent event) {
		return event.endTime < event.startTime;
	}

	/**
	 * Checks whether or not the selected time will overlap with existing events
	 * 
	 * @param event The selected time
	 * @return true, if it does not overlap
	 */
	public final static boolean isFree(final CalEvent event) {
		// Ensure that current is also checked
		if (MainActivity.current != null) {
			MainActivity.eventlist.add(MainActivity.current);
		}
		if (!MainActivity.eventlist.isEmpty()) {
			// Check against all other events today
			final int len = MainActivity.eventlist.size();
			for (int i = 0; i < len; i++) {
				final CalEvent ev = MainActivity.eventlist.get(i);
				if ((// If new event is between start & end time
				event.startTime >= ev.startTime && event.endTime <= ev.endTime)
						||
						// If new event overlaps the start time
						(event.startTime <= ev.startTime && 
						ev.startTime < event.endTime)
						||
						// If new event overlaps the end time
						(event.startTime < ev.endTime && 
						event.endTime >= ev.endTime)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks whether or not there is free time to extend the end time of a
	 * selected event
	 * 
	 * @param event
	 *            The selected event
	 * @param index
	 *            The index of the selected event, so that it wont check with
	 *            its own time
	 * @return true, if there is free time to extend
	 */
	public final static boolean isUpdatable(final CalEvent event,
			final int index) {
		// Ensure that current is checked, and the event that is being updated,
		// is removed from the list
		if (!(index == -1)) {
			MainActivity.eventlist.add(MainActivity.current);
			MainActivity.eventlist.remove(index);
		}
		// Return true, if the only event, is the one that is being updated
		if (MainActivity.eventlist.isEmpty()) {
			return true;
		}
		// Check against all events today
		final int len = MainActivity.eventlist.size();
		for (int i = 0; i < len; i++) {
			final CalEvent ev = MainActivity.eventlist.get(i);
			if ((// If new event is between start & end time
			event.startTime >= ev.startTime && event.endTime <= ev.endTime)
					||
					// If new event overlaps the start time
					(event.startTime <= ev.startTime && 
					ev.startTime < event.endTime)
					||
					// If new event overlaps the end time
					(event.startTime < ev.endTime && 
					event.endTime >= ev.endTime)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Used to generate a new default password
	 * 
	 * @param context The context of the application
	 * @return The default password, if everything went well
	 */
	public final static String newPassword(final Context context) {
		final String stdPwd = "a";
		try {
			// Open the file
			final FileOutputStream out = context.openFileOutput("pwd",
					Context.MODE_PRIVATE);
			
			// Write the line
			out.write(stdPwd.getBytes());
			
			// Close the file
			out.close();
			return stdPwd;
		} catch (IOException e) {
			return "ERROR";
		}
	}

	/**
	 * The method that reads the calendar
	 * 
	 * @param context The context of the app. Used to extract the CONTENT_URI 
	 * 				  and the ContentResolver
	 * @return An ArrayList of CalEvents, that either is started, or is in the
	 *         future
	 */
	public final static ArrayList<CalEvent> readCalendar(
			final Context context) {
		
		// The ArrayList to hold the events
		final ArrayList<CalEvent> eventlist = new ArrayList<CalEvent>();

		// Get the ContentResolver
		final ContentResolver contentResolver = context.getContentResolver();

		// Calling the query
		String query = "CALENDAR_ID = 1 AND DTSTART <= ? AND DTEND > ?";
		Time t = new Time();
		t.setToNow();
		String dtEnd = "" + t.toMillis(false);
		t.set(59, 59, 23, t.monthDay, t.month, t.year);
		String dtStart = "" + t.toMillis(false);
		String[] selectionArgs = { dtStart, dtEnd };
		cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI,
				COLS, query, selectionArgs, null);
		cursor.moveToFirst();

		// Getting the used DateFormat from the Android device
		final Format tf = DateFormat.getTimeFormat(context);

		Long start = 0L;

		// Writing all the events to the eventlist
		while (!cursor.isAfterLast()) {
			start = cursor.getLong(0);
			boolean isUnderway = false;
			if (start < new Date().getTime()) {
				Log.d("TAG", "Check event fra read");
				isUnderway = true;
			}
			eventlist.add(new CalEvent(
					cursor.getLong(0), 	 // Start time
					cursor.getLong(1), 	 // End Time
					cursor.getString(2), // Title
					cursor.getString(3), // Description
					tf, 				 // TimeFormat
					isUnderway, 		 // Is underway
					cursor.getLong(4), 	 // Event ID
					cursor.getString(5)  // Organizer
					));

			cursor.moveToNext();

		}
		cursor.close();

		// Sorts eventlist by start time
		Collections.sort(eventlist, new CustomComparator());

		return eventlist;
	}

	/**
	 * Reads the config file, and then it interprets it
	 * 
	 * @param context
	 *            The context of the application
	 * @return A HashMap of (command, value) pairs
	 */
	public final static ArrayList<Setting> readConfig(final Context context) {
		// Make a new ArrayList
		settings = new ArrayList<Setting>();

		try {
			// Open the File
			final FileInputStream in = context.openFileInput("config.cfg");
			final InputStreamReader inputStreamReader = new InputStreamReader(
					in);
			final BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);
			
			// Read each line
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				interpret(line);
			}
			
			// Close the file
			inputStreamReader.close();
			in.close();
		} catch (FileNotFoundException e) {
			configMake(context);
		} catch (IOException e) {
		}
		return settings;
	}

	/**
	 * Used for changing the password for the settings menu
	 * 
	 * @param password The new password
	 * @param context The context of the application
	 */
	public final static void savePassword(final String password,
			final Context context) {
		try {
			// Open the file
			final FileOutputStream out = context.openFileOutput("pwd",
					Context.MODE_PRIVATE);
			
			// Write the line
			out.write(password.getBytes());
			
			// Close the file
			out.close();
		} catch (IOException e) {
		}
	}

	/**
	 * The method for inserting into the calendar
	 * 
	 * @param event The event that should be inserted
	 * @param context The context of this application, used to extract the
	 *           	  CONTENT_URI and the ContentResolver
	 */
	public final static void setNewEvent(final CalEvent event,
			final Context context) {

		// Get the URI and the ContentResolver
		final Uri EVENTS_URI = Uri.parse(CalendarContract.Events.CONTENT_URI
				.toString());
		final ContentResolver cr = context.getContentResolver();

		// Insert all the required information
		final ContentValues values = new ContentValues();
		values.put("calendar_id", 1);
		values.put("title", event.title);
		values.put("allDay", 0);
		values.put("dtstart", event.startTime);
		values.put("dtend", event.endTime);
		values.put("description", event.description);
		values.put("availability", 0);
		values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().toString());
		
		// Insert the event
		cr.insert(EVENTS_URI, values);

	}

	/**
	 * Used when the application has updated an event, and needs to edit the
	 * event in the calendar
	 * 
	 * @param event The event that has been updated
	 * @param context The context of the application
	 */
	public final static void update(final CalEvent event, 
			final Context context) {
		// Get the ContentResolver and the URI
		final ContentResolver cr = context.getContentResolver();
		final ContentValues cv = new ContentValues();
		Uri uri = null;
		
		// Enter the information from the given event, into the URI
		cv.put(Events.DTSTART, event.startTime);
		cv.put(Events.DTEND, event.endTime);
		cv.put(Events.TITLE, event.title);
		cv.put(Events.DESCRIPTION, event.description);
		uri = ContentUris.withAppendedId(Events.CONTENT_URI, event.id);
		
		// Update the calendar
		cr.update(uri, cv, null, null);
	}

	/**
	 * Changes the end time of the given event, to the current time
	 * 
	 * @param event The event that should be updated
	 * @param context The context of the app, used to extract the CONTENT_URI 
	 * 				  and the ContentResolver
	 */
	public final static void updateEnd(final CalEvent event,
			final Context context) {
		// Get the ContentResolver and the URI
		final ContentResolver cr = context.getContentResolver();
		final ContentValues cv = new ContentValues();
		Uri uri = null;
		
		// Set the events start time to now
		cv.put(Events.DTEND, new Date().getTime());
		uri = ContentUris.withAppendedId(Events.CONTENT_URI, event.id);
		
		// Update the calendar, and call sync()
		cr.update(uri, cv, null, null);
		MainActivity.sync();
	}

	/**
	 * Changes the end time of the given event, to the given time
	 * 
	 * @param event The event that should be updated
	 * @param context The context of the app, used to extract the CONTENT_URI 
	 * 				  and the ContentResolver
	 * @param time The time the event should now end on
	 */
	public final static void updateEnd(final CalEvent event,
			final Context context, final long time) {
		// Get the ContentResolver and the URI
		final ContentResolver cr = context.getContentResolver();
		final ContentValues cv = new ContentValues();
		Uri uri = null;
		
		// Enter the information from the given event, into the URI
		cv.put(Events.DTEND, time);
		uri = ContentUris.withAppendedId(Events.CONTENT_URI, event.id);
		
		// Update the calendar, and call sync()
		cr.update(uri, cv, null, null);
		MainActivity.sync();
	}

	/**
	 * Changes the start time of the given event, to the current time
	 * 
	 * @param event The event that should be updated
	 * @param context The context of the app, used to extract the CONTENT_URI 
	 * 				  and the ContentResolver
	 */
	public final static void updateStart(final CalEvent event,
			final Context context) {
		// Get the ContentResolver and the uri
		final ContentResolver cr = context.getContentResolver();
		final ContentValues cv = new ContentValues();
		Uri uri = null;
		
		// Set the new start time to now
		cv.put(Events.DTSTART, new Date().getTime());
		uri = ContentUris.withAppendedId(Events.CONTENT_URI, event.id);
		
		// Update, and call sync()
		cr.update(uri, cv, null, null);
		MainActivity.sync();
	}

	/**
	 * Changes the start time of the given event, to the given time
	 * 
	 * @param event The event that should be updated
	 * @param context The context of the application
	 * @param time The time that the start should be set to
	 */
	public final static void updateStart(final CalEvent event,
			final Context context, final long time) {
		// Get the ContentResolver and the URI
		final ContentResolver cr = context.getContentResolver();
		final ContentValues cv = new ContentValues();
		Uri uri = null;
		
		// Enter the information from the given event, into the URI
		cv.put(Events.DTSTART, time);
		uri = ContentUris.withAppendedId(Events.CONTENT_URI, event.id);
		
		// Update the calendar, and call sync()
		cr.update(uri, cv, null, null);
		MainActivity.sync();
	}

	/**
	 * Writes the given ArrayList of Setting to the config file
	 * 
	 * @param sett The given ArrayList
	 * @param context The context of the application
	 */
	public final static void write(final ArrayList<Setting> sett,
			final Context context) {
		try {
			// Open the file
			final FileOutputStream out = context.openFileOutput("config.cfg",
					Context.MODE_PRIVATE);
			final OutputStreamWriter outputStream = new OutputStreamWriter(out);

			// Write all of the lines
			final int len = sett.size();
			for (int i = 0; i < len; i++) {
				String setting = sett.get(i).name + " " + sett.get(i).value
						+ "\n";
				outputStream.write(setting, 0, setting.length());
			}

			// Close the file
			outputStream.close();
			out.close();
		} catch (IOException e) {
			Log.d("ConfigReader", e.getMessage());
		}
		// Read the to make sure that save went OK
		readConfig(context);
	}

}
