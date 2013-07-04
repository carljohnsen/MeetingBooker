package com.desc.meetingbooker;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public final class TimeWindowAdapter extends ArrayAdapter<TimeWindow> {

	private final ArrayList<TimeWindow> entries;
	private final Activity activity;

	public TimeWindowAdapter(final Activity a, final int textViewResourceId,
			final ArrayList<TimeWindow> entries) {
		super(a, textViewResourceId, entries);
		this.entries = entries;
		this.activity = a;
	}

	public final static class ViewHold {
		public TextView item1;
	}

	@Override
	public final View getView(final int position, final View convertView,
			final ViewGroup parent) {
		View v = convertView;
		final ViewHold holder;
		if (v == null) {
			final LayoutInflater vi = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.timewindow_item, null);
			holder = new ViewHold();
			holder.item1 = (TextView) v.findViewById(R.id.windowItem);
			v.setTag(holder);
		} else {
			holder = (ViewHold) v.getTag();
		}
		final TimeWindow window = entries.get(position);
		if (window != null) {
			String time = window.getStartString() + " | "
					+ window.getEndString();
			holder.item1.setText(time);
		}
		return v;
	}

}
