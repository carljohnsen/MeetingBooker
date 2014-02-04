package com.desc.meetingbooker;

import java.util.Comparator;

/**
 * A Class used when sorting the array of events
 * 
 * @author Carl Johnsen
 * @version 1.0
 * @since 04-04-2013
 */
public final class CustomComparator implements Comparator<CalEvent> {
	
	@Override
	public final int compare(final CalEvent event1, final CalEvent event2) {
		return event1.startTime.compareTo(event2.startTime);
	}
	
}