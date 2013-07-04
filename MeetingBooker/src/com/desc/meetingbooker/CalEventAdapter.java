package com.desc.meetingbooker;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public final class CalEventAdapter extends ArrayAdapter<CalEvent> {

	private final ArrayList<CalEvent> entries;
	private final Activity activity;

	public CalEventAdapter(Activity a, int textViewResourceId,
			ArrayList<CalEvent> entries) {
		super(a, textViewResourceId, entries);
		this.entries = entries;
		this.activity = a;
	}

	public final static class ViewHold {
		public TextView item1;
		public TextView item2;
		public TextView item3;
		public TextView item4;
	}

	@Override
	public final View getView(final int position, final View convertView,
			final ViewGroup parent) {
		View v = convertView;
		final ViewHold holder;
		if (v == null) {
			final LayoutInflater vi = (LayoutInflater) activity
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
			holder.item1.setText(event.title);
			holder.item2.setText(event.organizer);
			holder.item3.setText(event.description);
			holder.item4.setText(event.getStartTime() + " | "
					+ event.getEndTime());
		}
		return v;
	}

}
