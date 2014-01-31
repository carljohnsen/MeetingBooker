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
 * An custom ArrayAdapter for SettingsActivity
 * 
 * @author Carl Johnsen
 * @version 1.0
 * @since 28-06-2013
 */
public final class SettingsAdapter extends ArrayAdapter<Setting> {

	private final ArrayList<Setting> entries;
	private final Activity activity;

	/**
	 * The constructor for an SettingsAdapter
	 * 
	 * @param a 				 The activity it is used
	 * @param textViewResourceId The layout it uses
	 * @param entries 			 The ArrayList that will be set up in the list
	 */
	public SettingsAdapter(final Activity a, 
			final int textViewResourceId,
			final ArrayList<Setting> entries) {
		super(a, textViewResourceId, entries);
		this.entries = entries;
		this.activity = a;
	}

	/**
	 * The ViewHolder for an item in the list
	 * 
	 * @author Carl Johnsen
	 * @version 1.0
	 * @since 28-06-2013
	 */
	public final static class ViewHolder {
		/** R.id.SettingName */
		public TextView item1;
		/** R.id.settingVal */
		public TextView item2;
		/** R.id.settingCheck */
		public CheckBox item3;
	}

	@Override
	public final View getView(final int position, 
			final View convertView,
			final ViewGroup parent) {
		
		View v = convertView;
		final ViewHolder holder;
		
		if (v == null) {
			// Inflate the View
			final LayoutInflater vi = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.item_setting, null);
			
			// Make a new ViewHolder and find the Views
			holder = new ViewHolder();
			holder.item1 = (TextView) v.findViewById(R.id.setting_name);
			holder.item2 = (TextView) v.findViewById(R.id.setting_value);
			holder.item3 = (CheckBox) v.findViewById(R.id.setting_check);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		
		// Take the Setting, and fill its information into the Views
		final Setting setting = entries.get(position);
		if (setting != null) {
			holder.item1.setText(setting.desc);
			if (setting.valueType.equals("boolean")) {
				holder.item2.setVisibility(TextView.GONE);
				holder.item3.setChecked(Boolean.parseBoolean(setting.value));
			} else {
				holder.item2.setText(setting.value);
				holder.item3.setVisibility(CheckBox.GONE);
			}
		}
		return v;
	}

}
