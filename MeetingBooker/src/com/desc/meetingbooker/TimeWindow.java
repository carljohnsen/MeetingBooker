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
public class TimeWindow {
	
	private long start;
	private long end;
	
	/**
	 * Creates a new TimeWindow
	 * 
	 * @param start The start time of the window
	 * @param end The end time of the window
	 */
	public TimeWindow(final long start, final long end) {
		this.start = start;
		this.end = end;
		long interval = this.end - this.start;
		if (interval > (60 * 60000)) {
			this.end = this.start + (60 * 60000);
		}
	}
	
	/**
	 * Start time getter
	 * 
	 * @return The start time of the window
	 */
	public long getStart() {
		return this.start;
	}
	
	/**
	 * End time getter
	 * 
	 * @return The end time of the window
	 */
	public long getEnd() {
		return this.end;
	}
	
	/**
	 * Get a String representation of the TimeWindow
	 * 
	 * @return A String representation of the TimeWindow
	 */
	public String toString() {
		String st = DateFormat.format("kk:mm", new Date(this.start)).toString();
		String en = DateFormat.format("kk:mm", new Date(this.end)).toString();
		return "Start " + st + " :  End " + en;
	}

}
