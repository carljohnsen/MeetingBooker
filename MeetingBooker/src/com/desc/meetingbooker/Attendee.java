package com.desc.meetingbooker;

/**
 * A Class that resembels an attendee to an event;
 * 
 * @author Carl Johnsen
 * @since 29-07-2013
 * @version 1.0
 */
public class Attendee {

	protected final String name;
	protected final String email;
	protected final String relationship;
	protected final String type;
	protected final String status;
	
	/**
	 * Constructs a new Attendee
	 * 
	 * @param name The name of the attendee
	 * @param email The email of the attendee
	 * @param relationship The relationship of the attendee
	 * @param type The type of the attendee
	 * @param status The status of the attendee
	 */
	public Attendee(final String name,
			final String email,
			final String relationship,
			final String type,
			final String status) {
		this.name = name;
		this.email = email;
		this.relationship = relationship;
		this.type = type;
		this.status = status;
	}
	
}