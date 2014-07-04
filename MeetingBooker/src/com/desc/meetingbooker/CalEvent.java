package com.desc.meetingbooker;

import android.text.format.Time;

/**
 * A Class that resembles an event in the calendar
 * 
 * @author Carl Johnsen
 * @version 1.6
 * @since 04-04-2013
 */
public final class CalEvent {

	protected 			boolean 	isUnderway;
	protected 			long 		id;
	protected 	final 	Long 		endTime;
	protected 	final 	Long 		startTime;
	protected			String 		description;
	protected 			String 		organizer;
	protected 	final 	String 		title;
	
	/**
	 * The constructor for making a new CalEvent
	 * 
	 * @param startTime 	The start time of the event
	 * @param endTime 		The end time of the event
	 * @param title			The title of the event
	 * @param description 	The description of the event
	 * @param timeFormat 	The date format of the event
	 * @param isUnderway 	The boolean value isUnderway
	 * @param id 			The id of the event
	 * @param organizer 	The organizer of the event
	 */
	public CalEvent(final long startTime, 
			final long 	endTime, 
			final String 	title,
			final String 	description,
			final boolean isUnderway,
			final long 	id, 
			final String 	organizer) {
		
		// If the description is empty, change it to "(no description)"
		if (description != null && !description.equals("")) {
			this.description = description;
		} else {
			this.description = "(no description)";
		}
		
		this.startTime 		= startTime;
		this.endTime 		= endTime;
		this.title 			= title;
		this.isUnderway 	= isUnderway;
		this.id 			= id;
		this.organizer 		= organizer;
	}

	/**
	 * The constructor to make a temporary event (used by NewEditActivity.add())
	 * 
	 * @param startTime   	The start time of the event
	 * @param endTime   	The end time of the event
	 * @param title  		The title of the event
	 * @param description 	The description of the event
	 */
	public CalEvent(final long startTime, 
			final long 	endTime, 
			final String 	title, 
			final String 	description) {
		this.startTime 		= startTime;
		this.endTime 		= endTime;
		this.title 			= title;
		this.description 	= description;
	}

	/**
	 * The constructor to make a temporary event 
	 * (used by NewEditActivity.update())
	 * 
	 * @param startTime   	The start time of the event
	 * @param endTime   	The end time of the event
	 * @param title  		The title of the event
	 * @param description 	The description of the event
	 * @param id   			The id of the event
	 */
	public CalEvent(final long startTime, 
			final long 	endTime, 
			final String 	title, 
			final String 	description, 
			final long 	id) {
		this.startTime 		= startTime;
		this.endTime 		= endTime;
		this.title 			= title;
		this.description 	= description;
		this.id 			= id;
	}

	/**
	 * Event comparer, compares this event, to a given event
	 * 
	 * @param event The event that this should be compared to
	 * @return true if equal
	 */
	public final boolean equals(CalEvent event) {
		return this.id == event.id;
	}

	/**
	 * Method for getting a String representation of the events end time
	 * 
	 * @return A String representation of the events end time
	 */
	public final String getEndTime() {
		Time time = new Time();
		time.set(this.endTime);
		return time.format("HH:mm");
	}

	/**
	 * Method for getting a String representation of the events start time
	 * 
	 * @return A String representation of the events start time
	 */
	public final String getStartTime() {
		Time time = new Time();
		time.set(this.startTime);
		return time.format("HH:mm");
	}

	/**
	 * Method for getting a TimeWindow representation of the event
	 * 
	 * @return The time window of the event
	 */
	public final TimeWindow getTimeWindow() {
		return new TimeWindow(this.startTime, this.endTime);
	}

	/**
	 * Method for getting a String representation of the event
	 * 
	 * @return A String representation of the event
	 */
	public final String toString(String asdf) {
		return this.title + " - " + this.getTimeWindow().toString();
	}
	
}
