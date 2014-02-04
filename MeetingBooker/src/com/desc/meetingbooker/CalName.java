package com.desc.meetingbooker;

/**
 * A calendar name object. Used, to associate a name with an id
 * 
 * @author Carl Johnsen
 * @version 1.0
 * @since 28-01-2014
 */
public class CalName {
	
	protected String name;
	protected String id;
	
	/**
	 * Constructor for CalName, makes a new CalName object
	 * 
	 * @param name The calendar name it should hold
	 * @param id The associated id
	 */
	public CalName(final String name, final String id) {
		this.name = name;
		this.id = id;
	}
	
	/**
	 * Get a String representation of the object
	 * 
	 * @return The String representation of the object
	 */
	public String toString() {
		return name + " " + id;
	}
	
}
