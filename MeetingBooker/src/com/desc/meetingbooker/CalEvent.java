package com.desc.meetingbooker;

import java.text.Format;

/**
 * A Class that resembles an event in the calendar
 * 
 * @author Carl Johnsen, Daniel Pedersen, Emil Pedersen and Sune Bartels
 * @version 0.9
 * @since 04-04-2013
 */
public class CalEvent {

	private Long startTime;
	private Long endTime;
	private String title;
	private String description;
	private String organizer;
	private Format datF;
	private boolean isUnderway;
	private long id;
	
	/**
	 * The constructor for making a new CalEvent
	 * 
	 * @param sT The start time of the event
	 * @param eT The end time of the event
	 * @param tit The title of the event
	 * @param desc The description of the event
	 * @param tf The date format of the event
	 * @param iU boolean value isUnderway
	 * @param id The id of the event
	 * @param organizer The organizer of the event
	 */
	public CalEvent(long sT, long eT, String tit, String desc, Format tf, boolean iU, long id, String organizer) {
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
	 * @param sT The start time of the event
	 * @param eT The end time of the event
	 * @param tit The title of the event
	 * @param desc The description of the event
	 */
	public CalEvent(long sT, long eT, String tit, String desc) {
		startTime = sT;
		endTime = eT;
		title = tit;
		description = desc;
	}
	
	/**
	 * The constructor to make a check event ??
	 * 
	 * @param sT The start time of the event
	 * @param eT The end time of the event
	 * @param tit The title of the event
	 * @param desc The description of the event
	 * @param id The id of the event
	 */
	public CalEvent(long sT, long eT, String tit, String desc, long id) {
		startTime = sT;
		endTime = eT;
		title = tit;
		description = desc;
		this.id = id;
	}
	
	/**
	 * Start time getter
	 * 
	 * @return The start time of the event
	 */
	public Long getStart() {
		return startTime;
	}
	
	/**
	 * End time getter
	 * 
	 * @return The end time of the event
	 */
	public Long getEnd() {
		return endTime;
	}
	
	/**
	 * Title getter
	 * 
	 * @return The title of the event
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Description getter
	 * 
	 * @return The description of the event
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Organizer getter
	 * 
	 * @return The organizer of the event
	 */
	public String getOrganizer() {
		return organizer;
	}
	
	/**
	 * Method for getting a String representation of the event
	 * 
	 * @return A String representation of the event
	 */
	public String toString() {
		return "Title : " + title + "\n" +
			   "Start : " + datF.format(startTime) + "\n" +
			   "End : " + datF.format(endTime) + "\n" + 
			   "Description : " + description + "\n" +
			   "isUnderway : " + isUnderway + "\n" + 
			   "Organizer : " + organizer;
	}
	
	/**
	 * boolean isUnderway getter
	 * 
	 * @return The isUnderway state of the event
	 */
	public boolean isUnderway() {
		return isUnderway;
	}
	
	/**
	 * Id getter
	 * 
	 * @return The id of the event
	 */
	public long getId() {
		return this.id;
	}
	
	/**
	 * Event comparer, compares this event, to a given event
	 * 
	 * @return true if equal
	 */
	public boolean equals(CalEvent e) {
		return this.id == e.getId();	
	}
	
	/**
	 * Method for getting a String representation of the events start time
	 * 
	 * @return A String representation of the events start time
	 */
	public String getStartTime() {
		return datF.format(startTime);
	}
	
	/**
	 * Method for getting a String representation of the events end time
	 * 
	 * @return A String representation of the events end time
	 */
	public String getEndTime() {
		return datF.format(endTime);
	}
	
	/**
	 * Returns a TimeWindow representation of the event
	 * 
	 * @return The time window of the event
	 */
	public TimeWindow getTimeWindow() {
		return new TimeWindow(this.startTime, this.endTime);
	}
	
	/**
	 * Description setter
	 * 
	 * @param desc The new description
	 */
	public void setDescription(String desc) {
		this.description = desc;
	}
	
}
