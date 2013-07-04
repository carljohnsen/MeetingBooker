package com.desc.meetingbooker;

import java.util.Comparator;

/**
 * A Class used when sorting the array of events
 * 
 * @author Carl Johnsen, Daniel Pedersen, Emil Pedersen and Sune Bartels
 * @version 0.9
 * @since 04-04-2013
 */
public class CustomComparator implements Comparator<CalEvent> {
	
	@Override
	public final int compare(CalEvent e1, CalEvent e2) {
		return e1.startTime.compareTo(e2.startTime);
	}
	
}