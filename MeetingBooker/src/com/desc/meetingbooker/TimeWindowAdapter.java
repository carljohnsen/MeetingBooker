package com.desc.meetingbooker;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TimeWindowAdapter extends ArrayAdapter<TimeWindow> {

	private ArrayList<TimeWindow> entries;
	private Activity activity;

	public TimeWindowAdapter(Activity a, int textViewResourceId,
			ArrayList<TimeWindow> entries) {
		super(a, textViewResourceId, entries);
		this.entries = entries;
		this.activity = a;
	}

	public static class ViewHold {
		public TextView item1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHold holder;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) activity
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
			String time = window.getStartString() + " | " + window.getEndString();
			holder.item1.setText(time);
		}
		return v;
	}

}
