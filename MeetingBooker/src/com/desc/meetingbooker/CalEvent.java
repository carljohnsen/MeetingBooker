package com.desc.meetingbooker;

import java.text.Format;

/**
 * A Class that resembles an event in the calendar
 * 
 * @author Carl Johnsen, Daniel Pedersen, Emil Pedersen and Sune Bartels
 * @version 0.9
 * @since 04-04-2013
 */
public final class CalEvent {

	protected final Long startTime;
	protected final Long endTime;
	protected final String title;
	public String description;
	protected String organizer;
	protected Format datF;
	protected boolean isUnderway;
	protected long id;

	/**
	 * The constructor for making a new CalEvent
	 * 
	 * @param sT
	 *            The start time of the event
	 * @param eT
	 *            The end time of the event
	 * @param tit
	 *            The title of the event
	 * @param desc
	 *            The description of the event
	 * @param tf
	 *            The date format of the event
	 * @param iU
	 *            boolean value isUnderway
	 * @param id
	 *            The id of the event
	 * @param organizer
	 *            The organizer of the event
	 */
	public CalEvent(final long sT, final long eT, final String tit,
			final String desc, final Format tf, final boolean iU,
			final long id, final String organizer) {
		startTime = sT;
		endTime = eT;
		title = tit;
		if (desc != null && !desc.equals("")) {
			description = desc;
		} else {
			description = "(no description)";
		}
		datF = tf;
		isUnderway = iU;
		this.id = id;
		this.organizer = organizer;
	}

	/**
	 * The constructor to make a temporary event (used by XX)
	 * 
	 * @param sT
	 *            The start time of the event
	 * @param eT
	 *            The end time of the event
	 * @param tit
	 *            The title of the event
	 * @param desc
	 *            The description of the event
	 */
	public CalEvent(final long sT, final long eT, final String tit, final String desc) {
		startTime = sT;
		endTime = eT;
		title = tit;
		description = desc;
	}

	/**
	 * The constructor to make a check event ??
	 * 
	 * @param sT
	 *            The start time of the event
	 * @param eT
	 *            The end time of the event
	 * @param tit
	 *            The title of the event
	 * @param desc
	 *            The description of the event
	 * @param id
	 *            The id of the event
	 */
	public CalEvent(final long sT, final long eT, final String tit, final String desc, final long id) {
		startTime = sT;
		endTime = eT;
		title = tit;
		description = desc;
		this.id = id;
	}

	/**
	 * Method for getting a String representation of the event
	 * 
	 * @return A String representation of the event
	 */
	public final String toString() {
		return "Title : " + title + "\n" + "Start : " + datF.format(startTime)
				+ "\n" + "End : " + datF.format(endTime) + "\n"
				+ "Description : " + description + "\n" + "isUnderway : "
				+ isUnderway + "\n" + "Organizer : " + organizer;
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
	 * Method for getting a String representation of the events start time
	 * 
	 * @return A String representation of the events start time
	 */
	public final String getStartTime() {
		return datF.format(startTime);
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
	 * Returns a TimeWindow representation of the event
	 * 
	 * @return The time window of the event
	 */
	public final TimeWindow getTimeWindow() {
		return new TimeWindow(this.startTime, this.endTime);
	}
	
}
