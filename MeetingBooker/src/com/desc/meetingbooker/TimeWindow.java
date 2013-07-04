package com.desc.meetingbooker;

import java.util.Date;
import android.text.format.DateFormat;

/**
 * A class which is used to represent a time window, i.e. A window of time
 * where there the calendar is not booked
 * 
 * @version 0.9
 * @author Carl Johnsen, Daniel Pedersen, Emil Pedersen and Sune Bartels
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
	
	public final String getStartString() {
		return DateFormat.format("kk:mm", new Date(this.start)).toString();
	}
	
	public final String getEndString() {
		return DateFormat.format("kk:mm", new Date(this.end)).toString();
	}
	
	/**
	 * Get a String representation of the TimeWindow
	 * 
	 * @return A String representation of the TimeWindow
	 */
	public final String toString() {
		return "Start " + 
				DateFormat.format("kk:mm", new Date(this.start)).toString() + 
				" :  End " + 
				DateFormat.format("kk:mm", new Date(this.end)).toString();
	}

}
