package com.desc.meetingbooker;

/**
 * A class that represents a setting in the settingsactivity
 * 
 * @author carljohnsen
 * @since 28-06-2013
 * @version 0.1
 */
public final class Setting {
	
	protected final String desc;
	protected final String name;
	protected String value;
	protected final String valueType;
	
	/**
	 * Constructs a new Setting object
	 * 
	 * @param name The name of the setting
	 * @param value The value linked to the name
	 * @param valueType The type of the value
	 */
	public Setting(final String name, 
			final String value, 
			final String valueType,
			final String desc) {
		this.desc = desc;
		this.name = name;
		this.value = value;
		this.valueType = valueType;
	}

}
