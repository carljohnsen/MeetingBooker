package com.desc.meetingbooker;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * An custom ArrayAdapter for the ListView in MainActivity
 * 
 * @author Carl Johnsen
 * @version 1.0
 * @since 02-06-2013
 */
public final class CalEventAdapter extends ArrayAdapter<CalEvent> {

	private final ArrayList<CalEvent> entries;
	private final Activity activity;

	/**
	 * The constructor for an CalEventAdapter
	 * 
	 * @param a 				 The activity it is used
	 * @param textViewResourceId The layout it uses
	 * @param entries 			 The ArrayList that will be set up in the list
	 */
	public CalEventAdapter(Activity a, 
			int textViewResourceId,
			ArrayList<CalEvent> entries) {
		super(a, textViewResourceId, entries);
		this.entries = entries;
		this.activity = a;
	}

	/**
	 * The ViewHolder for an item in the list
	 * 
	 * @author Carl Johnsen
	 * @version 1.0
	 * @since 02-06-2013
	 */
	public final static class ViewHold {
		/** R.id.evTitle */
		public TextView item1;
		/** R.id.evOrganizer */
		public TextView item2;
		/** R.id.evDescription */
		public TextView item3;
		/** R.id.evStEn */
		public TextView item4;
	}

	@Override
	public final View getView(final int position, 
			final View convertView,
			final ViewGroup parent) {
		
		View v = convertView;
		final ViewHold holder;
		
		if (v == null) {
			// Inflate the View
			final LayoutInflater vi = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.item_calevent, null);
			
			// Make a new ViewHold, and find the Views
			holder = new ViewHold();
			holder.item1 = (TextView) v.findViewById(R.id.calevent_title_value);
			holder.item2 = (TextView) v.findViewById(R.id.calevent_organizer_value);
			holder.item3 = (TextView) v.findViewById(R.id.calevent_description_value);
			holder.item4 = (TextView) v.findViewById(R.id.calevent_time_value);
			v.setTag(holder);
		} else {
			holder = (ViewHold) v.getTag();
		}
		
		// Take the event, and fill its information into the Views
		final CalEvent event = entries.get(position);
		if (event != null) {
			holder.item1.setText(event.title);
			holder.item2.setText(event.organizer);
			holder.item3.setText(event.description);
			holder.item4.setText(event.getTimeWindow().toString());
		}
		
		return v;
	}

}
