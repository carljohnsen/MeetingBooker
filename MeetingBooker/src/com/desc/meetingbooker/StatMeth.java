package com.desc.meetingbooker;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import android.text.format.Time;
import android.util.Log;

/**
 * A Class that holds all the static methods
 * 
 * @author Carl Johnsen
 * @version 1.7
 * @since 24-06-2013
 */
public final class StatMeth {
	
	private static String TAG = StatMeth.class.getSimpleName();

	// The query used to get the events from the Android calendar
	private static final String[] COLUMNS = new String[] {
			CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND,
			CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION,
			CalendarContract.Events._ID, CalendarContract.Events.ORGANIZER };
	private static Cursor cursor;
	private static ArrayList<Setting> settings;
	private static String calendarId; 
	//private static long configTimestamp = 0L;
	protected static String manServer = null;
	protected static Context context;

	/**
	 * "Deletes" the given event. The method for deletion in this application,
	 * is to change the start time of the event, to now, and the end time to one
	 * millisecond later 
	 * 
	 * @param event The given event
	 */
	public final static void delete(final CalEvent event) {
		Log.d(TAG, "called delete()");
		// Get the ContentResolver and the URI
		final ContentResolver cr = context.getContentResolver();
		final ContentValues cv = new ContentValues();
		Uri uri = null;
		
		// Change the start and end time of the event
		final long time = new Date().getTime() - 1000;
		cv.put(Events.DTSTART, time);
		cv.put(Events.DTEND, time + 1);
		uri = ContentUris.withAppendedId(Events.CONTENT_URI, event.id);
		
		// Update the calendar
		cr.update(uri, cv, null, null);
	}
	
	/**
	 * Checks the end time of the given event is before the start time
	 * 
	 * @param event
	 *            The given event
	 * @return true, if the end is before the start
	 */
	public final static boolean eventStartIsBeforeEnd(final CalEvent event) {
		Log.d(TAG, "called isBefore()");
		return event.endTime < event.startTime;
	}

	/**
	 * The method to get the name of the calendar
	 * 
	 * @return The name of the calendar
	 */
	public final static String getCalendarName() {
		Log.d(TAG, "called getCalendarName()");
		// The query
		final String[] que = { 
			CalendarContract.Calendars.CALENDAR_DISPLAY_NAME 
		};
		// Make sure only the selcted calendar id name is fetched
		final String id = "_ID = " + calendarId;
		
		// Get the ContentResolver, and extract the Cursor
		final ContentResolver cr = context.getContentResolver();
		final Cursor cursor = cr.query(CalendarContract.Calendars.CONTENT_URI,
				que, id, null, null);
		
		// Take the information from the first result, and return it
		cursor.moveToFirst();
		final String result = cursor.getString(0);
		cursor.close();
		return result;
	}
	
	/**
	 * Used for retrieving possible Calendar names, and ID's
	 * 
	 * @return An ArrayList of CalName
	 */
	public final static ArrayList<CalName> getCalendarNames() {
		Log.d(TAG, "called getCalendarNames()");
		
		ArrayList<CalName> result = new ArrayList<CalName>();
		
		// The query
		final String[] query = {
				CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
				CalendarContract.Calendars._ID
		};
		
		// Get the ContentResolver, and extract the cursor
		final ContentResolver cr = context.getContentResolver();
		final Cursor cursor = cr.query(CalendarContract.Calendars.CONTENT_URI, 
				query, null, null, null);
		// Move the cursor to the first result
		cursor.moveToFirst();
		// Loop over the results
		while(!cursor.isAfterLast()) {
			// Get the name and the id of the results
			String name = cursor.getString(0);
			String id = cursor.getString(1);
			// Put them in the HashMap
			result.add(new CalName(name,id));
			// Move to next result
			cursor.moveToNext();
		}
		
		return result;
	}

	/**
	 * Used for retrieving the password from private file pwd
	 * 
	 * @return The password needed to unlock the settings menu
	 */
	public final static String getPassword() {
		Log.d(TAG, "called getPassword()");
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
			return newPassword();
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
	public final static Setting interpretSetting(final String str) {
		// Find the whitespace in the String, and split it into two
		final int index = str.indexOf(' ');
		final String command = str.substring(0, index);
		final String value = str.substring(index + 1, str.length());
		
		// Make the new Setting, and change it according to its information
		final Setting setting;
		if (command.equals("extendend")) {
			MainActivity.canExtendEnd = parseBool(value);
			setting = new Setting(command, value, "boolean", "Extend end time");
			return setting;
		}
		if (command.equals("endtime")) {
			MainActivity.endExtendAmount = Integer.parseInt(value);
			setting = new Setting(command, value, "int", "Minutes to extend by");
			return setting;
		}
		if (command.equals("extendstart")) {
			MainActivity.canExtendStart = parseBool(value);
			setting = new Setting(command, value, "boolean", "Extend start time");
			return setting;
		}
		if (command.equals("starttime")) {
			MainActivity.startExtendAmount = Integer.parseInt(value);
			setting = new Setting(command, value, "int",
					"Minutes to extend with");
			return setting;
		}
		if (command.equals("candelete")) {
			NewEditActivity.candelete = parseBool(value);
			setting = new Setting(command, value, "boolean",
					"Show the delete button");
			return setting;
		}
		if (command.equals("canend")) {
			MainActivity.canEnd = parseBool(value);
			setting = new Setting(command, value, "boolean",
					"Show the End Meeting button");
			return setting;
		}
		if (command.equals("enddelete")) {
			MainActivity.canEndDelete = parseBool(value);
			setting = new Setting(command, value, "boolean", "End delete");
			return setting;
		}
		if (command.equals("windowsize")) {
			NewEditActivity.windowSize = Integer.parseInt(value);
			setting = new Setting(command, value, "int",
					"Length of TimeWindows");
			return setting;
		}
		if (command.equals("calendarname")) {
			MainActivity.roomName = value;
			setting = new Setting(command, value, "String", "Calendar name");
			return setting;
		}
		if (command.equals("calendarid")) {
			StatMeth.calendarId = value;
			setting = new Setting(command, value, "hashmap", "Calendar ID : " + getCalendarName());
			return setting;
		}
		if (command.equals("delaydelete")) {
			MainActivity.canDelayDelete = parseBool(value);
			setting = new Setting(command, value, "boolean", "Delay delete");
			return setting;
		}
		if (command.equals("remotelog")) {
			StatMeth.manServer = value;
			setting = new Setting(command, value, "String", "Remote logging server");
			return setting;
		}
		if (command.equals("canstart")) {
			MainActivity.canStart = parseBool(value);
			setting = new Setting(command, value, "boolean", "Show the start meeting button");
			return setting;
		}
		// If it was interpreted wrong, return null to indicate error
		return null;
	}
	
	/**
	 * Checks if time is < 08:00 or > 20:00
	 * 
	 * @return true, if the time is before 08:00 or after 20:00
	 */
	public final static boolean isEvening() {
		Time t = new Time();
		t.setToNow();
		t.set(0, 0, 8, t.monthDay, t.month, t.year);
		long eight = t.toMillis(false);
		t.set(0, 0, 20, t.monthDay, t.month, t.year);
		long twenty = t.toMillis(false);
		long now = new Date().getTime();
		return now <= eight || now >= twenty;
	}

	/**
	 * Checks whether or not the selected time will overlap with existing events
	 * 
	 * @param event The selected time
	 * @return true, if it does not overlap
	 */
	public final static boolean isFree(final CalEvent event) {
		Log.d(TAG, "called isFree()");
		ArrayList<CalEvent> eventlist = readCalendar();
		if (!eventlist.isEmpty()) {
			// Check against all other events today
			final int len = eventlist.size();
			for (int i = 0; i < len; i++) {
				final CalEvent ev = eventlist.get(i);
				if ((
						// If new event is between start & end time
						event.startTime >= ev.startTime && 
						event.endTime <= ev.endTime)
						||
						// If new event overlaps the start time
						(event.startTime <= ev.startTime && 
						ev.startTime < event.endTime)
						||
						// If new event overlaps the end time
						(event.startTime < ev.endTime && 
						event.endTime >= ev.endTime)
						||
						// If start time is before and end time is after
						(event.startTime <= ev.startTime &&
						event.endTime >= ev.endTime)) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Reads the global config times timestamp
	 * 
	 * @return true, if the global config file is newer than the current
	 */
	public final static boolean isGlobalConfigNewer() {
		return false;
		/*Log.d(TAG, "Called isGlobalConfigNewer()");
		try {
			// Define and open the url
			URL url = new URL("");
			BufferedReader in = new BufferedReader(
					new InputStreamReader(url.openStream()));
			
			// Read one line
			String line = in.readLine();
			
			// Close the stream
			in.close();
			
			// Parse the read line, and return
			long timestamp = Long.parseLong(line);
			return timestamp > configTimestamp;
		} catch (Exception e) {
			Log.e(TAG, "ERR! " + e.getMessage());
		}
		return false;*/
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
	public final static boolean isUpdatable(final CalEvent event) {
		Log.d(TAG, "called isUpdatable");
		
		ArrayList<CalEvent> eventlist = readCalendar();
		
		// Return true, if the only event, is the one that is being updated
		if (eventlist.isEmpty()) {
			return true;
		}
		// Check against all events today
		final int len = eventlist.size();
		for (int i = 0; i < len; i++) {
			final CalEvent ev = eventlist.get(i);
			if (ev.id == event.id) {
				continue;
			}
			if ((
					// If new event is between start & end time
					event.startTime >= ev.startTime && 
					event.endTime <= ev.endTime)
					||
					// If new event overlaps the start time
					(event.startTime <= ev.startTime && 
					ev.startTime < event.endTime)
					||
					// If new event overlaps the end time
					(event.startTime < ev.endTime && 
					event.endTime >= ev.endTime)
					||
					// If new event starts before and ends after
					(event.startTime <= ev.startTime &&
					event.endTime >= ev.endTime)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Creates a new config.cfg file
	 */
	private final static void makeNewConfig() {
		Log.d(TAG, "called configMake()");
		try {
			// Open the file
			final FileOutputStream out = context.openFileOutput("config.cfg",
					Context.MODE_PRIVATE);
			final OutputStreamWriter outputStream = new OutputStreamWriter(out);
			
			// Write all the config lines
			String line;
			line = "extendstart true";
			settings.add(interpretSetting(line));
			line += "\n";
			outputStream.write(line, 0, line.length());
			
			line = "starttime 15";
			settings.add(interpretSetting(line));
			line += "\n";
			outputStream.write(line, 0, line.length());
			
			line = "extendend true";
			settings.add(interpretSetting(line));
			line += "\n";
			outputStream.write(line, 0, line.length());
			
			line = "endtime 15";
			settings.add(interpretSetting(line));
			line += "\n";
			outputStream.write(line, 0, line.length());
			
			line = "candelete true";
			settings.add(interpretSetting(line));
			line += "\n";
			outputStream.write(line, 0, line.length());
			
			line = "canend true";
			settings.add(interpretSetting(line));
			line += "\n";
			outputStream.write(line, 0, line.length());
			
			line = "enddelete true";
			settings.add(interpretSetting(line));
			line += "\n";
			outputStream.write(line, 0, line.length());
			
			line = "windowsize 60";
			settings.add(interpretSetting(line));
			line += "\n";
			outputStream.write(line, 0, line.length());
			
			line = "calendarid 2";
			settings.add(interpretSetting(line));
			line += "\n";
			outputStream.write(line, 0, line.length());
			
			line = "calendarname " + getCalendarName();
			settings.add(interpretSetting(line));
			line += "\n";
			outputStream.write(line, 0, line.length());
			
			line = "delaydelete false";
			settings.add(interpretSetting(line));
			line += "\n";
			outputStream.write(line, 0, line.length());
			
			line = "remotelog not_set";
			settings.add(interpretSetting(line));
			line += "\n";
			outputStream.write(line, 0, line.length());
			
			line = "canstart true";
			outputStream.write(line, 0, line.length());
			line += "\n";
			settings.add(interpretSetting(line));
			
			// Close the file
			outputStream.close();
			out.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}
	
	/**
	 * Hashes the given String with the md5 hashing algorithm
	 * 
	 * @param data The data that should be hashed
	 * @return The hashed data
	 */
	public final static String md5(final String data) {
		try {
			// Create the hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(data.getBytes());
			byte[] message = digest.digest();
			
			// Create the String
			StringBuffer str = new StringBuffer();
			for (int i = 0; i < message.length; i++) {
				str.append(Integer.toHexString(0xFF & message[i]));
			}
			
			// return result
			return str.toString();
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "No such algorithm: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Used to generate a new default password
	 * 
	 * @return The default password, if everything went well
	 */
	public final static String newPassword() {
		Log.d(TAG, "called newPassword()");
		final String stdPwd = StatMeth.md5("a");
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
	 * Parses a String into a boolean. Looks for "true", "false", "1" or "0"
	 * 
	 * @param str The String that will be parsed
	 * @return True if the String is either "true" or "1", otherwise false
	 */
	public static final boolean parseBool(final String str) {
		return str.equals("true") || str.equals("1");
	}
	
	/**
	 * The method that reads the calendar
	 * 
	 * @return An ArrayList of CalEvents, that either is started, or is in the
	 *         future
	 */
	public final static ArrayList<CalEvent> readCalendar() {
		Log.d(TAG, "called readCalendar()");
		
		// The ArrayList to hold the events
		final ArrayList<CalEvent> eventlist = new ArrayList<CalEvent>();

		// Get the ContentResolver
		final ContentResolver contentResolver = context.getContentResolver();

		// Calling the query
		String query = "CALENDAR_ID = " + calendarId + " AND DTSTART <= ? AND DTEND > ?";
		Time t = new Time();
		t.setToNow();
		String dtEnd = "" + t.toMillis(false);
		t.set(59, 59, 23, t.monthDay, t.month, t.year);
		String dtStart = "" + t.toMillis(false);
		String[] selectionArgs = { dtStart, dtEnd };
		cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI,
				COLUMNS, query, selectionArgs, null);
		cursor.moveToFirst();

		Long start = 0L;

		// Writing all the events to the eventlist
		while (!cursor.isAfterLast()) {
			start = cursor.getLong(0);
			boolean isUnderway = false;
			if (start < new Date().getTime()) {
				isUnderway = true;
			}
			eventlist.add(new CalEvent(
					cursor.getLong(0), 	 // Start time
					cursor.getLong(1), 	 // End Time
					cursor.getString(2), // Title
					cursor.getString(3), // Description
					isUnderway, 		 // Is underway
					cursor.getLong(4), 	 // Event ID
					cursor.getString(5)  // Organizer
					));

			cursor.moveToNext();

		}
		cursor.close();

		// Sorts the event list by start time
		Collections.sort(eventlist, new CustomComparator());

		Log.d(TAG, "readCalendar() is done");
		
		return eventlist;
	}

	/**
	 * Reads the configuration file, and then it interprets it
	 * 
	 * @return A HashMap of (command, value) pairs
	 */
	public final static ArrayList<Setting> readConfig() {
		Log.d(TAG, "called readConfig()");
		
		if (isGlobalConfigNewer()) {
			return readGlobalConfig();
		}
		
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
				settings.add(interpretSetting(line));
			}
			Log.d(TAG, "called interpret() " + settings.size() + " times");
			
			// Close the file
			inputStreamReader.close();
			in.close();
		} catch (FileNotFoundException e) {
			makeNewConfig();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		return settings;
	}
	
	/**
	 * Reads the global configuration file from HTTP
	 * 
	 * @return A HashMap of (command, value) pairs
	 */
	public final static ArrayList<Setting> readGlobalConfig() {
		Log.d(TAG, "calledReadGlobalConfig()");
		
		// Make a new ArrayList
		settings = new ArrayList<Setting>();
		try {
			// Define the URL and open the stream
			URL url = new URL("");
			BufferedReader in = new BufferedReader(
					new InputStreamReader(url.openStream()));
			
			// Read lines, and interpret them
			String line;
			while ((line = in.readLine()) != null) {
				settings.add(interpretSetting(line));
			}
			
			// Close the stream
			in.close();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
		
		// Write the newly read config file
		writeConfig(settings);
		return settings;
	}
	
	/**
	 * Writes message to remote logging server
	 * Assumes that server does not require any other information, than the message
	 * Executes the writing process in a new thread, so that it ensured not to run on the UI thread
	 * 
	 * @param message The message to be written
	 */
	public final static void remoteLog(final String message) {
		// If the server have not been defined, don't try to connect
		if (manServer == null || manServer.equals("not_set")) {
			Log.e(TAG, "Remote logging server not set");
			return;
		}
		new Thread(new Runnable() {
			public void run() {
				try {
					// Create the socket, and define the in and out streams
					Socket socket = new Socket(manServer, 5000);
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					
					out.write("LOG\r\n");
					out.write(MainActivity.roomName + "\r\n");
					out.write(message + "\r\n");
					out.write("\r\n");
					out.flush();
					
					String response = in.readLine();
					if (response.equals("OK") && in.readLine().equals("")) {
						Log.d(TAG, "Wrote to remote logging server");
					} else if (response.equals("ERR") && in.readLine().equals("")) {
						Log.e(TAG, "Error when writing to remote logging server");
					} else if (response.equals("NOREG") && in.readLine().equals("")) {
						Log.e(TAG, "Remote log server claims that this tablet hasn't registered");
					} else {
						Log.e(TAG, "Unknown response from log server");
					}
					
					// Close the streams and the socket
					in.close();
					out.close();
					socket.close();
				} catch (IOException ioe) {
					Log.e(TAG, "Error writing to logging server: " + ioe.getMessage());
				}
			}
		}).start();
	}

	/**
	 * Used for changing the password for the settings menu
	 * 
	 * @param password The new password
	 */
	public final static void savePassword(final String password) {
		Log.d(TAG, "called savePassword()");
		try {
			// Open the file
			final FileOutputStream out = context.openFileOutput("pwd",
					Context.MODE_PRIVATE);
			
			// Write the line
			out.write(password.getBytes());
			
			// Close the file
			out.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

	/**
	 * The method for inserting into the calendar
	 * 
	 * @param event The event that should be inserted
	 */
	public final static void setNewEvent(final CalEvent event) {
		Log.d(TAG, "called setNewEvent()");

		// Get the URI and the ContentResolver
		final Uri EVENTS_URI = Uri.parse(CalendarContract.Events.CONTENT_URI
				.toString());
		final ContentResolver cr = context.getContentResolver();

		// Insert all the required information
		final ContentValues values = new ContentValues();
		values.put("calendar_id", calendarId);
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
	 */
	public final static void update(final CalEvent event) {
		Log.d(TAG, "called update()");
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
	 */
	public final static void updateEnd(final CalEvent event) {
		Log.d(TAG, "called updateEnd(CalEvent)");
		// Get the ContentResolver and the URI
		final ContentResolver cr = context.getContentResolver();
		final ContentValues cv = new ContentValues();
		Uri uri = null;
		
		// Set the events start time to one second ago
		cv.put(Events.DTEND, new Date().getTime() - 1000);
		uri = ContentUris.withAppendedId(Events.CONTENT_URI, event.id);
		
		// Update the calendar, and call sync()
		cr.update(uri, cv, null, null);
		MainActivity.sync();
	}

	/**
	 * Changes the end time of the given event, to the given time
	 * 
	 * @param event The event that should be updated
	 * @param time The time the event should now end on
	 */
	public final static void updateEnd(final CalEvent event, final long time) {
		Log.d(TAG, "called updateEnd(CalEvent, long)");
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
	 */
	public final static void updateStart(final CalEvent event) {
		Log.d(TAG, "called updateStart(CalEvent)");
		// Get the ContentResolver and the uri
		final ContentResolver cr = context.getContentResolver();
		final ContentValues cv = new ContentValues();
		Uri uri = null;
		
		// Set the new start time to one second ago
		cv.put(Events.DTSTART, new Date().getTime() - 1000);
		cv.put(Events.DESCRIPTION, event.description);
		uri = ContentUris.withAppendedId(Events.CONTENT_URI, event.id);
		
		// Update, and call sync()
		cr.update(uri, cv, null, null);
		MainActivity.sync();
	}

	/**
	 * Changes the start time of the given event, to the given time
	 * 
	 * @param event The event that should be updated
	 * @param time The time that the start should be set to
	 */
	public final static void updateStart(final CalEvent event, final long time) {
		Log.d(TAG, "called updateStart(CalEvent, long)");
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
	 * Writes the given ArrayList of Settings to the config file
	 * 
	 * @param sett The given ArrayList
	 */
	public final static void writeConfig(final ArrayList<Setting> sett) {
		Log.d(TAG, "called write()");
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
			Log.e(TAG, e.getMessage());
		}
		// Read the to make sure that save went OK
		readConfig();
	}

}
