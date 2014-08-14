package com.desc.meetingbooker;

import android.text.format.Time;

/**
 * A class which is used to represent a time window, i.e. A window of time
 * where there the calendar is not booked
 * 
 * @version 1.6
 * @author Carl Johnsen
 * @since 14-05-2013
 */
public final class TimeWindow {
	
	protected final long start;
	protected final long end;
	private final Time time;
	
	/**
	 * Creates a new TimeWindow
	 * 
	 * @param start The start time of the window
	 * @param end The end time of the window
	 */
	public TimeWindow(final long start, final long end) {
		this.start 	= start;
		this.end 	= end;
		this.time 	= new Time();
	}
	
	/**
	 * Get a String representation of the TimeWindows start time
	 * 
	 * @return A String representation of the TimeWindows start time
	 */
	public final String getStartString() {
		time.set(start);
		return time.format("%H:%M");
	}
	
	/**
	 * Get a String representation of the TimeWindows end time
	 * 
	 * @return A String representation of the TimeWindows end time
	 */
	public final String getEndString() {
		time.set(end);
		return time.format("%H:%M");
	}
	
	/**
	 * Get a String representation of the TimeWindow in the format "12:00 - 13:00"
	 * 
	 * @return A String representation of the TimeWindow
	 */
	public final String toString() {
		return getStartString() + " - " + getEndString();
	}
	
	/**
	 * Get a string representation of the TimeWindow in the format "12:00 13:00"
	 * 
	 * @return A String representation of the TimeWindow
	 */
	public final String toString2() {
		return "'" + getStartString() + "' '" + getEndString() + "'";
	}

}
