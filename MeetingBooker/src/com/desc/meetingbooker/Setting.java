package com.desc.meetingbooker;

/**
 * A class that represents a setting in the settingsactivity
 * 
 * @author carljohnsen
 * @since 28-06-2013
 * @version 0.1
 */
public class Setting {
	
	private String name;
	private String value;
	private String valueType;
	
	/**
	 * Constructs a new Setting object
	 * 
	 * @param name The name of the setting
	 * @param value The value linked to the name
	 * @param valueType The type of the value
	 */
	public Setting(final String name, 
			final String value, 
			final String valueType) {
		this.name = name;
		this.value = value;
		this.valueType = valueType;
	}

	/**
	 * Setting name getter
	 * 
	 * @return The name of the Setting
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setting value getter
	 * 
	 * @return The value of the Setting
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Setting valueType getter
	 * 
	 * @return The type of the Setting's value
	 */
	public String getValueType() {
		return valueType;
	}
	
	/**
	 * Setting value setter
	 * 
	 * @param val The value that will be set
	 */
	public void setValue(final String val) {
		this.value = val;
	}

}
