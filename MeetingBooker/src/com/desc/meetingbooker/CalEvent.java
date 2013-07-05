package com.desc.meetingbooker;

import java.text.Format;

/**
 * A Class that resembles an event in the calendar
 * 
 * @author Carl Johnsen
 * @version 1.0
 * @since 04-04-2013
 */
public final class CalEvent {

	protected 		boolean isUnderway;
	protected 		Format 	datF;
	protected 		long 	id;
	protected final Long 	endTime;
	protected final Long 	startTime;
	protected		String 	description;
	protected 		String 	organizer;
	protected final String 	title;
	
	/**
	 * The constructor for making a new CalEvent
	 * 
	 * @param sT 		The start time of the event
	 * @param eT 		The end time of the event
	 * @param tit 		The title of the event
	 * @param desc 		The description of the event
	 * @param tf 		The date format of the event
	 * @param iU 		The boolean value isUnderway
	 * @param id 		The id of the event
	 * @param organizer The organizer of the event
	 */
	public CalEvent(final long sT, 
			final long eT, 
			final String tit,
			final String desc, 
			final Format tf, 
			final boolean iU,
			final long id, 
			final String organizer) {
		
		// If the description is empty, change it to "(no description)"
		if (desc != null && !desc.equals("")) {
			this.description = desc;
		} else {
			this.description = "(no description)";
		}
		
		this.startTime = sT;
		this.endTime = eT;
		this.title = tit;
		this.datF = tf;
		this.isUnderway = iU;
		this.id = id;
		this.organizer = organizer;
	}

	/**
	 * The constructor to make a temporary event (used by NewEditActivity.add())
	 * 
	 * @param sT   The start time of the event
	 * @param eT   The end time of the event
	 * @param tit  The title of the event
	 * @param desc The description of the event
	 */
	public CalEvent(final long sT, 
			final long eT, 
			final String tit, 
			final String desc) {
		this.startTime = sT;
		this.endTime = eT;
		this.title = tit;
		this.description = desc;
	}

	/**
	 * The constructor to make a temporary event 
	 * (used by NewEditActivity.update())
	 * 
	 * @param sT   The start time of the event
	 * @param eT   The end time of the event
	 * @param tit  The title of the event
	 * @param desc The description of the event
	 * @param id   The id of the event
	 */
	public CalEvent(final long sT, 
			final long eT, 
			final String tit, 
			final String desc, 
			final long id) {
		this.startTime = sT;
		this.endTime = eT;
		this.title = tit;
		this.description = desc;
		this.id = id;
	}

	/**
	 * Event comparer, compares this event, to a given event
	 * 
	 * @return true if equal
	 */
	public final boolean equals(CalEvent e) {
		return this.id == e.id;
	}

	/**
	 * Method for getting a String representation of the events end time
	 * 
	 * @return A String representation of the events end time
	 */
	public final String getEndTime() {
		return datF.format(endTime);
	}

	/**
	 * Method for getting a String representation of the events start time
	 * 
	 * @return A String representation of the events start time
	 */
	public final String getStartTime() {
		return datF.format(startTime);
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
	public final String toString() {
		return "Title : "    + title				  + "\n" + 
			"Start : "       + datF.format(startTime) + "\n" + 
			"End : "         + datF.format(endTime)   + "\n" + 
			"Description : " + description 			  + "\n" + 
			"isUnderway : "  + isUnderway 			  + "\n" + 
			"Organizer : "   + organizer;
	}
	
}
