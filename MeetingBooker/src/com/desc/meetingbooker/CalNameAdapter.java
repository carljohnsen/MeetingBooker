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
 * An custom ArrayAdapter for the ListView in the ListFragment
 *  
 * @author Carl Johnsen
 * @version 1.0
 * @since 28-01-2014
 */
public class CalNameAdapter extends ArrayAdapter<CalName> {

	private final ArrayList<CalName> entries;
	private final Activity activity;
	
	/**
	 * The constructor for an CalNameAdapter
	 * 
	 * @param a 				 The activity it is used
	 * @param textViewResourceId The layout it uses
	 * @param entries 			 The ArrayList that will be set up in the list
	 */
	public CalNameAdapter(Activity a, 
			int textViewResourceId,
			ArrayList<CalName> entries) {
		super(a, textViewResourceId, entries);
		this.entries = entries;
		this.activity = a;
	}
	
	/**
	 * The ViewHolder for an item in the list
	 * 
	 * @author Carl Johnsen
	 * @version 1.0
	 * @since 28-01-2014
	 */
	public final static class ViewHold {
		/** R.id.item_calname_name */
		public TextView item1;
		/** R.id.item_calname_id */
		public TextView item2;
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
			v = vi.inflate(R.layout.item_calname, null);
			
			// Make a new ViewHold, and find the Views
			holder = new ViewHold();
			holder.item1 = (TextView) v.findViewById(R.id.calname_name);
			holder.item2 = (TextView) v.findViewById(R.id.calname_id_value);
			v.setTag(holder);
		} else {
			holder = (ViewHold) v.getTag();
		}
		
		// Take the event, and fill its information into the Views
		final CalName calname = entries.get(position);
		if (calname != null) {
			holder.item1.setText(calname.name);
			holder.item2.setText(calname.id);
		}
		
		return v;
	}
	
}