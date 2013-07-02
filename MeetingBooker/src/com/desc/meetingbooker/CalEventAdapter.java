package com.desc.meetingbooker;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CalEventAdapter extends ArrayAdapter<CalEvent> {

	private ArrayList<CalEvent> entries;
	private Activity activity;

	public CalEventAdapter(Activity a, int textViewResourceId,
			ArrayList<CalEvent> entries) {
		super(a, textViewResourceId, entries);
		this.entries = entries;
		this.activity = a;
	}

	public static class ViewHold {
		public TextView item1;
		public TextView item2;
		public TextView item3;
		public TextView item4;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHold holder;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.calevent_item, null);
			holder = new ViewHold();
			holder.item1 = (TextView) v.findViewById(R.id.evTitle);
			holder.item2 = (TextView) v.findViewById(R.id.evOrganizer);
			holder.item3 = (TextView) v.findViewById(R.id.evDescription);
			holder.item4 = (TextView) v.findViewById(R.id.evStEn);
			v.setTag(holder);
		} else {
			holder = (ViewHold) v.getTag();
		}
		final CalEvent event = entries.get(position);
		if (event != null) {
			// TODO put text in views
			holder.item1.setText(event.getTitle());
			holder.item2.setText(event.getOrganizer());
			holder.item3.setText(event.getDescription());
			holder.item4.setText(event.getStartTime() + " | " + event.getEndTime());
		}
		return v;
	}

}
