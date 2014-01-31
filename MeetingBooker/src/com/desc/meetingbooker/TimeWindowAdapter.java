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
 * An custom ArrayAdapter for NewEditActivity
 * 
 * @author carljohnsen
 * @version 1.0
 * @since 03-07-2013
 */
public final class TimeWindowAdapter extends ArrayAdapter<TimeWindow> {

	private final ArrayList<TimeWindow> entries;
	private final Activity activity;

	/**
	 * The constructor for an TimeWindowAdapter
	 * 
	 * @param a 				 The activity it is used
	 * @param textViewResourceId The layout it uses
	 * @param entries 			 The ArrayList that will be set up in the list
	 */
	public TimeWindowAdapter(final Activity a, 
			final int textViewResourceId,
			final ArrayList<TimeWindow> entries) {
		super(a, textViewResourceId, entries);
		this.entries = entries;
		this.activity = a;
	}

	/**
	 * The ViewHolder for an item in the list
	 * 
	 * @author carljohnsen
	 * @version 1.0
	 * @since 03-07-2013
	 */
	public final static class ViewHold {
		/** R.id.windowItem */
		public TextView item1;
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
			v = vi.inflate(R.layout.item_timewindow, null);
			
			// Make a new ViewHolder and find the Views
			holder = new ViewHold();
			holder.item1 = (TextView) v.findViewById(R.id.timewindow_textview);
			v.setTag(holder);
		} else {
			holder = (ViewHold) v.getTag();
		}
		
		// Take the TimeWindow and fill its information into the View
		final TimeWindow window = entries.get(position);
		if (window != null) {
			String time = window.getStartString() + " | "
					+ window.getEndString();
			holder.item1.setText(time);
		}
		return v;
	}

}
