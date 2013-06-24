package com.desc.meetingbooker;

import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

/**
 * A Class that inserts an CalEvent into the calendar
 * 
 * @author Carl Johnsen, Daniel Pedersen, Emil Pedersen and Sune Bartels
 * @version 0.9
 * @since 09-05-2013
 */
public class EventCreate {
	
	/**
	 * The method for inserting into the calendar
	 * 
	 * @param event The event that should be inserted
	 * @param context The context of this application, used to extract the CONTENT_URI and the ContentResolver
	 */
	public static void setNewEvent(CalEvent event, Context context) {
		
		Uri EVENTS_URI = Uri.parse(CalendarContract.Events.CONTENT_URI.toString());
		ContentResolver cr = context.getContentResolver();
		
		ContentValues values = new ContentValues();
		values.put("calendar_id", 1);
		values.put("title", event.getTitle());
		values.put("allDay", 0);
		values.put("dtstart", event.getStart());
		values.put("dtend", event.getEnd());
		values.put("description", event.getDescription());
		values.put("availability", 0);
		values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().toString());
		cr.insert(EVENTS_URI, values);
		
	}

}
