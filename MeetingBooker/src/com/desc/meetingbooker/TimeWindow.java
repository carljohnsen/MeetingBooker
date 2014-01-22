package com.desc.meetingbooker;

import java.util.Date;
import android.text.format.DateFormat;

/**
 * A class which is used to represent a time window, i.e. A window of time
 * where there the calendar is not booked
 * 
 * @version 1.0
 * @author Carl Johnsen
 * @since 14-05-2013
 */
public final class TimeWindow {
	
	protected final long start;
	protected final long end;
	
	/**
	 * Creates a new TimeWindow
	 * 
	 * @param start The start time of the window
	 * @param end The end time of the window
	 */
	public TimeWindow(final long start, final long end) {
		this.start = start;
		this.end = end;
	}
	
	/**
	 * Get a String representation of the TimeWindows start time
	 * 
	 * @return A String representation of the TimeWindows start time
	 */
	public final String getStartString() {
		return DateFormat.format("kk:mm", new Date(this.start)).toString();
	}
	
	/**
	 * Get a String representation of the TimeWindows end time
	 * 
	 * @return A String representation of the TimeWindows end time
	 */
	public final String getEndString() {
		return DateFormat.format("kk:mm", new Date(this.end)).toString();
	}
	
	/**
	 * Get a String representation of the TimeWindow
	 * 
	 * @return A String representation of the TimeWindow
	 */
	public final String toString() {
		return DateFormat.format("kk:mm", new Date(this.start)).toString() + 
				" - " + 
				DateFormat.format("kk:mm", new Date(this.end)).toString();
	}

}
