package com.desc.meetingbooker;

import java.util.ArrayList;

/**
 * A Class that holds all the methods needed to check the calendar
 * 
 * @author Carl Johnsen, Daniel Pedersen, Emil Pedersen and Sune Bartels
 * @version 0.9
 * @since 27-05-2013
 */
public class CalendarChecker {
	
	/**
	 * Checks whether or not the selected time will overlap with existing events
	 * 
	 * @param event The selected time
	 * @return true, if it does not overlap
	 */
	public static boolean isFree(CalEvent event) {
		ArrayList<CalEvent> eventlist = MainActivity.eventlist;
		if (MainActivity.current != null) {
			eventlist.add(MainActivity.current);
		}
		if (!eventlist.isEmpty()) {
			for (CalEvent ev : eventlist) {
				if (ev.getStart() > event.getStart() && ev.getStart() < event.getEnd()) {
					return false;
				}
				if (ev.getEnd() > event.getStart() && ev.getEnd() < event.getEnd()) {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Checks whether or not there is free time to extend the end time of a selected event
	 * 
	 * @param event The selected event
	 * @param index The index of the selected event, so that it wont check with its own time
	 * @return true, if there is free time to extend
	 */
	public static boolean isUpdatable(CalEvent event, int index) {
		ArrayList<CalEvent> eventlist = MainActivity.eventlist;
		if (!(index == -1)) {
			eventlist.add(MainActivity.current);
			eventlist.remove(index);
		} 
		if (eventlist.isEmpty()) {
			return true;
		}
		for (CalEvent ev : eventlist) {
			if (ev.getStart() > event.getStart() && ev.getStart() < event.getEnd()) {
				return false;
			}
			if (ev.getEnd() > event.getStart() && ev.getEnd() < event.getEnd()) {
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

}
