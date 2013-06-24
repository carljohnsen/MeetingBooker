package com.desc.meetingbooker;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

/**
 * A Class that holds the methods to read the config file
 * 
 * @author Carl Johnsen, Daniel Pedersen, Emil Pedersen and Sune Bartels
 * @version 0.9
 * @since 27-05-2013
 */
public class ConfigReader {
	
	private static HashMap<String, String> map;

	/**
	 * Reads the config file, and then it interprets it
	 * 
	 * @param context The context of the application
	 * @return A HashMap of (command, value) pairs
	 */
	public static HashMap<String, String> readConfig(Context context) {
		ArrayList<String> config = new ArrayList<String>();

		boolean hasRead = false;
		while (!hasRead) {
			try {
				FileInputStream in = context.openFileInput("config.cfg");
				InputStreamReader inputStreamReader = new InputStreamReader(in);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					config.add(line);
				}
				inputStreamReader.close();
				in.close();
				hasRead = true;
			} catch (FileNotFoundException e) {
				configMake(context);
			} catch (IOException e) {
			}
		}
		
		map = new HashMap<String, String>();

		for (String st : config) {
			interpret(st);
		}

		return map;
	}

	private static void interpret(String str) {
		int index = str.indexOf(' ');
		String command = str.substring(0, index);
		String value = str.substring(index + 1, str.length());
		Log.d("TAG", command);
		Log.d("TAG", value);
		if (command.equals("extendendtime")) {
			MainActivity.extendEnd = Boolean.parseBoolean(value);
		}
		map.put(command, value);
	}

	private static void configMake(Context context) {
		Log.d("Config", "configMake()!");
		try {
			FileOutputStream out = context.openFileOutput("config.cfg", Context.MODE_PRIVATE);
			OutputStreamWriter outputStream = new OutputStreamWriter(out);
			outputStream.write("extendendtime true", 0, 18);
			outputStream.close();
			out.close();
			MainActivity.extendEnd = true;
		} catch (IOException e) {
			Log.d("ConfigReader", e.getMessage());
		}
	}
	
	/**
	 * Writes the given HashMap to the config file
	 * 
	 * @param map The given HashMap
	 * @param context The context of the application
	 */
	public static void write(HashMap<String, String> map, Context context) {
		try {
			FileOutputStream out = context.openFileOutput("config.cfg", Context.MODE_PRIVATE);
			OutputStreamWriter outputStream = new OutputStreamWriter(out);
			
			String extendEnd = "extendendtime " + map.get("extendendtime");
			MainActivity.extendEnd = Boolean.parseBoolean(map.get("extendendtime"));
			outputStream.write(extendEnd, 0, extendEnd.length());
			
			outputStream.close();
			out.close();
		} catch (IOException e) {
			Log.d("ConfigReader", e.getMessage());
		}
	}

}
