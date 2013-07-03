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
import android.util.Log;

/**
 * A Class that holds all the static methods
 * 
 * @author carljohnsen
 * @version 0.9
 * @since 24-06-2013
 */
public class StatMeth {

	/**
	 * Checks whether or not the selected time will overlap with existing events
	 * 
	 * @param event The selected time
	 * @return true, if it does not overlap
	 */
	public static boolean isFree(CalEvent event) {
		ArrayList<CalEvent> eventlist = MainActivity.eventlist;
		// Ensure that current is also checked
		if (MainActivity.current != null) {
			eventlist.add(MainActivity.current);
		}
		if (!eventlist.isEmpty()) {
			// Check against all other events today
			for (CalEvent ev : eventlist) {
				if ((// If new event is between start & end time
						event.getStart() >= ev.getStart() && 
						event.getEnd() <= ev.getEnd()) ||
						// If new event overlaps the start time
						(event.getStart() <= ev.getStart() &&
						ev.getStart() < event.getEnd()) ||
						// If new event overlaps the end time
						(event.getStart() < ev.getEnd() &&
						event.getEnd() >= ev.getEnd())) {
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
	 * @param event The selected event
	 * @param index The index of the selected event, so that it wont check with
	 * 		  its own time
	 * @return true, if there is free time to extend
	 */
	public static boolean isUpdatable(CalEvent event, int index) {
		ArrayList<CalEvent> eventlist = MainActivity.eventlist;
		// Ensure that current is checked, and the event that is being updated,
		// is removed from the list
		if (!(index == -1)) {
			eventlist.add(MainActivity.current);
			eventlist.remove(index);
		}
		// Return true, if the only event, is the one that is being updated
		if (eventlist.isEmpty()) {
			return true;
		}
		// Check against all events today
		for (CalEvent ev : eventlist) {
			if ((// If new event is between start & end time
					event.getStart() >= ev.getStart() && 
					event.getEnd() <= ev.getEnd()) ||
					// If new event overlaps the start time
					(event.getStart() <= ev.getStart() &&
					ev.getStart() < event.getEnd()) ||
					// If new event overlaps the end time
					(event.getStart() < ev.getEnd() &&
					event.getEnd() >= ev.getEnd())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks the end time of the given event is before the start time
	 * 
	 * @param event The given event
	 * @return true, if the end is before the start
	 */
	public static boolean isBefore(CalEvent event) {
		return event.getEnd() < event.getStart();
	}

	private static ArrayList<Setting> settings;

	/**
	 * Reads the config file, and then it interprets it
	 * 
	 * @param context The context of the application
	 * @return A HashMap of (command, value) pairs
	 */
	public static ArrayList<Setting> readConfig(Context context) {
		ArrayList<String> config = new ArrayList<String>();

		boolean hasRead = false;
		while (!hasRead) {
			try {
				FileInputStream in = context.openFileInput("config.cfg");
				InputStreamReader inputStreamReader = new InputStreamReader(in);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					config.add(line);
				}
				inputStreamReader.close();
				in.close();
				hasRead = true;
			} catch (FileNotFoundException e) {
				configMake(context);
			} catch (IOException e) {
			}
		}

		settings = new ArrayList<Setting>();

		for (String st : config) {
			interpret(st);
		}

		return settings;
	}

	private static void interpret(String str) {
		int index = str.indexOf(' ');
		String command = str.substring(0, index);
		String value = str.substring(index + 1, str.length());
		Log.d("TAG", command);
		Log.d("TAG", value);
		Setting setting;
		if (command.equals("extendendtime")) {
			MainActivity.extendEnd = Boolean.parseBoolean(value);
			setting = new Setting(command, value, "boolean", "Extend end time");
			settings.add(setting);
		}
		if (command.equals("endtime")) {
			MainActivity.endExtend = Integer.parseInt(value);
			setting = new Setting(command, value, "int", "Minutes to extend by");
			settings.add(setting);
		}
		if (command.equals("extendstarttime")) {
			MainActivity.extendStart = Boolean.parseBoolean(value);
			setting = new Setting(command, value, "boolean", "Extend start time");
			settings.add(setting);
		}
		if (command.equals("starttime")) {
			MainActivity.startExtend = Integer.parseInt(value);
			setting = new Setting(command, value, "int", "Minutes to extend with");
			settings.add(setting);
		}
		if (command.equals("candelete")) {
			NewEditActivity.candelete = Boolean.parseBoolean(value);
			setting = new Setting(command, value, "boolean", "Show the delete button");
			settings.add(setting);
		}
		if (command.equals("canend")) {
			MainActivity.canEnd = Boolean.parseBoolean(value);
			setting = new Setting(command, value, "boolean", "Show the End Meeting button");
			settings.add(setting);
		}
		if (command.equals("enddelete")) {
			MainActivity.endDelete = Boolean.parseBoolean(value);
			setting = new Setting(command, value, "boolean", "End delete");
			settings.add(setting);
		}
		if (command.equals("windowsize")) {
			NewEditActivity.windowSize = Integer.parseInt(value);
			setting = new Setting(command, value, "int", "Length of TimeWindows");
			settings.add(setting);
		}
		if (command.equals("calendarname")) {
			MainActivity.roomName = value;
			setting = new Setting(command, value, "String", "Calendar name");
			settings.add(setting);
		}
	}

	private static void configMake(Context context) {
		Log.d("Config", "configMake()!");
		try {
			FileOutputStream out = context.openFileOutput("config.cfg",
					Context.MODE_PRIVATE);
			OutputStreamWriter outputStream = new OutputStreamWriter(out);
			String line;
			line = "extendstarttime true\n";
			outputStream.write(line, 0, line.length());
			line = "starttime 15\n";
			outputStream.write(line, 0, line.length());
			line = "extendendtime true\n";
			outputStream.write(line, 0, line.length());
			line = "endtime 15\n";
			outputStream.write(line, 0, line.length());
			line = "candelete true\n";
			outputStream.write(line, 0, line.length());
			line = "canend true\n";
			outputStream.write(line, 0, line.length());
			line = "enddelete true\n";
			outputStream.write(line, 0, line.length());
			line = "windowsize 60\n";
			outputStream.write(line, 0, line.length());
			line = "calendarname " + getCalendarName(context) + "\n";
			outputStream.write(line, 0, line.length());
			outputStream.close();
			out.close();
			MainActivity.extendEnd = true;
			MainActivity.extendStart = true;
		} catch (IOException e) {
			Log.d("ConfigReader", e.getMessage());
		}
	}

	/**
	 * Writes the given HashMap to the config file
	 * 
	 * @param map The given HashMap
	 * @param context The context of the application
	 */
	public static void write(ArrayList<Setting> sett, Context context) {
		try {
			FileOutputStream out = context.openFileOutput("config.cfg",
					Context.MODE_PRIVATE);
			OutputStreamWriter outputStream = new OutputStreamWriter(out);

			for (Setting set : sett) {
				String setting = set.getName() + " " + set.getValue() + "\n";
				outputStream.write(setting, 0, setting.length());
			}

			outputStream.close();
			out.close();
		} catch (IOException e) {
			Log.d("ConfigReader", e.getMessage());
		}
		readConfig(context);
	}

	/**
	 * The method for inserting into the calendar
	 * 
	 * @param event The event that should be inserted
	 * @param context The context of this application, used to extract the
	 * 				  CONTENT_URI and the ContentResolver
	 */
	public static void setNewEvent(CalEvent event, Context context) {

		Uri EVENTS_URI = Uri.parse(CalendarContract.Events.CONTENT_URI
				.toString());
		ContentResolver cr = context.getContentResolver();

		ContentValues values = new ContentValues();
		values.put("calendar_id", 1);
		values.put("title", event.getTitle());
		values.put("allDay", 0);
		values.put("dtstart", event.getStart());
		values.put("dtend", event.getEnd());
		values.put("description", event.getDescription());
		values.put("availability", 0);
		values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().toString());
		cr.insert(EVENTS_URI, values);

	}

	// The query used to get the events from the Android calendar
	private static final String[] COLS = new String[] {
			CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND,
			CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION,
			CalendarContract.Events._ID, CalendarContract.Events.ORGANIZER };

	private static Cursor cursor;

	/**
	 * The method that reads the calendar
	 * 
	 * @param context The context of the app. Used to extract the CONTENT_URI 
	 * 		  and the ContentResolver
	 * @return An ArrayList of CalEvents, that either is started, or is in the
	 * 		   future
	 */
	public static ArrayList<CalEvent> readCalendar(Context context) {
		// The ArrayList to hold the events
		ArrayList<CalEvent> eventlist = new ArrayList<CalEvent>();

		ContentResolver contentResolver = context.getContentResolver();

		// Calling the query
		cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI,
				COLS, "CALENDAR_ID = 1", null, null);
		cursor.moveToFirst();

		// Getting the used DateFormat from the Android device
		Format df = DateFormat.getDateFormat(context);
		Format tf = DateFormat.getTimeFormat(context);

		Long start = 0L;

		// Getting the current Date and Time
		Date dat = new Date();
		String today = df.format(dat.getTime());

		// Writing all the events to the eventlist
		while (!cursor.isAfterLast()) {
			start = cursor.getLong(0);
			String st = df.format(start);
			boolean isUnderway = false;
			if (start < new Date().getTime()) {
				isUnderway = true;
			}
			if (today.equals(st) && !(
					cursor.getLong(1) < new Date().getTime())) {
				eventlist.add(new CalEvent(cursor.getLong(0), // Start time
						cursor.getLong(1), // End Time
						cursor.getString(2), // Title
						cursor.getString(3), // Description
						tf, // TimeFormat
						isUnderway, // Is underway
						cursor.getLong(4), // Event ID
						cursor.getString(5) // Organizer
						));
			}
			cursor.moveToNext();

		}
		cursor.close();

		// Sorts eventlist by start time
		Collections.sort(eventlist, new CustomComparator());

		return eventlist;
	}

	/**
	 * The method to get the name of the calendar
	 * 
	 * @param context The context of the app, used to extract the CONTENT_URI 
	 * 				  and the ContentResolver
	 * @return The name of the calendar
	 */
	public static String getCalendarName(Context context) {
		String[] que = { CalendarContract.Calendars.CALENDAR_DISPLAY_NAME };
		ContentResolver cr = context.getContentResolver();
		Cursor cursor = cr.query(CalendarContract.Calendars.CONTENT_URI, que,
				null, null, null);
		cursor.moveToFirst();
		String result = cursor.getString(0);
		cursor.close();
		return result;
	}

	/**
	 * Changes the start time of the given event, to the current time
	 * 
	 * @param event The event that should be updated
	 * @param context The context of the app, used to extract the CONTENT_URI 
	 * 				  and the ContentResolver
	 */
	public static void updateStart(CalEvent event, Context context) {
		// Update events start time
		ContentResolver cr = context.getContentResolver();
		ContentValues cv = new ContentValues();
		Uri uri = null;
		cv.put(Events.DTSTART, new Date().getTime());
		uri = ContentUris.withAppendedId(Events.CONTENT_URI, event.getId());
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
	public static void updateStart(CalEvent event, Context context, long time) {
		// Update events start time
		ContentResolver cr = context.getContentResolver();
		ContentValues cv = new ContentValues();
		Uri uri = null;
		cv.put(Events.DTSTART, time);
		uri = ContentUris.withAppendedId(Events.CONTENT_URI, event.getId());
		cr.update(uri, cv, null, null);
		MainActivity.sync();
	}

	/**
	 * Changes the end time of the given event, to the current time
	 * 
	 * @param event The event that should be updated
	 * @param context The context of the app, used to extract the CONTENT_URI 
	 * 				  and the ContentResolver
	 */
	public static void updateEnd(CalEvent event, Context context) {
		// Update events end time
		ContentResolver cr = context.getContentResolver();
		ContentValues cv = new ContentValues();
		Uri uri = null;
		cv.put(Events.DTEND, new Date().getTime());
		uri = ContentUris.withAppendedId(Events.CONTENT_URI, event.getId());
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
	public static void updateEnd(CalEvent event, Context context, long time) {
		// Update events end time
		ContentResolver cr = context.getContentResolver();
		ContentValues cv = new ContentValues();
		Uri uri = null;
		cv.put(Events.DTEND, time);
		uri = ContentUris.withAppendedId(Events.CONTENT_URI, event.getId());
		cr.update(uri, cv, null, null);
	}

	/**
	 * Used when the application has updated an event, and needs to edit the
	 * event in the calendar
	 * 
	 * @param event The event that has been updated
	 * @param context The context of the application
	 */
	public static void update(CalEvent event, Context context) {
		ContentResolver cr = context.getContentResolver();
		ContentValues cv = new ContentValues();
		Uri uri = null;
		cv.put(Events.DTSTART, event.getStart());
		cv.put(Events.DTEND, event.getEnd());
		cv.put(Events.TITLE, event.getTitle());
		cv.put(Events.DESCRIPTION, event.getDescription());
		uri = ContentUris.withAppendedId(Events.CONTENT_URI, event.getId());
		cr.update(uri, cv, null, null);
	}
	
	/**
	 * Used for changing the password for the settings menu
	 * 
	 * @param password The new password
	 * @param context The context of the application
	 */
	public static void savePassword(String password, Context context) {
		String filename = "pwd";
		try {
			FileOutputStream out = context.openFileOutput(filename, 
					Context.MODE_PRIVATE);
			out.write(password.getBytes());
			out.close();
		} catch (IOException e) {}
	}
	
	/**
	 * Used for retrieving the password from private file pwd
	 * 
	 * @param context The context of the application
	 * @return The password needed to unlock the settings menu
	 */
	public static String getPassword(Context context) {
		String filename = "pwd";
		try {
			FileInputStream in = context.openFileInput(filename);
			InputStreamReader reader = new InputStreamReader(in);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String password = bufferedReader.readLine();
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
	 * Used to generate a new default password
	 * 
	 * @param context The context of the application
	 * @return The default password, if everything went well
	 */
	public static String newPassword(Context context) {
		String filename = "pwd";
		String stdPwd = "a";
		try {
			FileOutputStream out = context.openFileOutput(filename, 
					Context.MODE_PRIVATE);
			out.write(stdPwd.getBytes());
			out.close();
			return stdPwd;
		} catch (IOException e) {
			return "ERROR";
		}
	}

}
