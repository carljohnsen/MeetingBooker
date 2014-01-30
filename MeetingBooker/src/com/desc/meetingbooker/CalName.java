package com.desc.meetingbooker;

public class CalName {
	
	protected String name;
	protected String id;
	
	public CalName(final String name, final String id) {
		this.name = name;
		this.id = id;
	}
	
	public String toString() {
		return name + " " + id;
	}
	
}
