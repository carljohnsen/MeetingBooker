package com.desc.meetingbooker;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.CheckBox;

/**
 * A custom ArrayAdapter for SettingsActivity
 * 
 * @author carljohnsen
 * @since 28-06-2013
 * @version 0.1
 */
public class SettingsAdapter extends ArrayAdapter<Setting>{

	private ArrayList<Setting> entries;
	private Activity activity;
	
	public SettingsAdapter(Activity a, int textViewResourceId, ArrayList<Setting> entries) {
		super(a, textViewResourceId, entries);
		this.entries = entries;
		this.activity = a;
	}
	
	public static class ViewHolder {
		public TextView item1;
		public TextView item2;
		public CheckBox item3;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.setting_item, null);
			holder = new ViewHolder();
			holder.item1 = (TextView) v.findViewById(R.id.settingName);
			holder.item2 = (TextView) v.findViewById(R.id.settingVal);
			holder.item3 = (CheckBox) v.findViewById(R.id.settingCheck);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		final Setting setting = entries.get(position);
		if (setting != null) {
			holder.item1.setText(setting.getDesc());
			if (setting.getValueType().equals("boolean")) {
				holder.item2.setVisibility(TextView.GONE);
				holder.item3.setChecked(Boolean.parseBoolean(setting.getValue()));
			} else {
				holder.item2.setText(setting.getValue());
				holder.item3.setVisibility(CheckBox.GONE);
			}
		}
		return v;
	}
	
}
