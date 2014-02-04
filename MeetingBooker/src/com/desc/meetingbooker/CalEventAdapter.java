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
	 * @param activity			 	The activity it is used
	 * @param textViewResourceId 	The layout it uses
	 * @param entries 			 	The ArrayList that will be set up in the list
	 */
	public CalEventAdapter(Activity activity, 
			int textViewResourceId,
			ArrayList<CalEvent> entries) {
		super(activity, textViewResourceId, entries);
		this.entries = entries;
		this.activity = activity;
	}

	/**
	 * The ViewHolder for an item in the list
	 * 
	 * @author Carl Johnsen
	 * @version 1.0
	 * @since 02-06-2013
	 */
	public final static class ViewHold {
		/** R.id.calevent_title_value */
		public TextView title;
		/** R.id.calevent_organizer_value */
		public TextView organizer;
		/** R.id.calevent_description_value */
		public TextView description;
		/** R.id.calevent_time_value */
		public TextView time;
	}

	@Override
	public final View getView(final int position, 
			final View convertView,
			final ViewGroup parent) {
		
		View view = convertView;
		final ViewHold holder;
		
		if (view == null) {
			// Inflate the View
			final LayoutInflater viewInflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = viewInflater.inflate(R.layout.item_calevent, null);
			
			// Make a new ViewHold, and find the Views
			holder = new ViewHold();
			holder.title 		= (TextView) view.findViewById(R.id.calevent_title_value);
			holder.organizer 	= (TextView) view.findViewById(R.id.calevent_organizer_value);
			holder.description 	= (TextView) view.findViewById(R.id.calevent_description_value);
			holder.time 		= (TextView) view.findViewById(R.id.calevent_time_value);
			view.setTag(holder);
		} else {
			holder = (ViewHold) view.getTag();
		}
		
		// Take the event, and fill its information into the Views
		final CalEvent event = entries.get(position);
		if (event != null) {
			holder.title		.setText(event.title);
			holder.organizer	.setText(event.organizer);
			holder.description	.setText(event.description);
			holder.time			.setText(event.getTimeWindow().toString());
		}
		
		return view;
	}

}
