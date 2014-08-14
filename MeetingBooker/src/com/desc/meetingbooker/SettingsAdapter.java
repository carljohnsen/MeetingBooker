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
 * @version 1.6
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
	public SettingsAdapter(final Activity activity, 
			final int textViewResourceId,
			final ArrayList<Setting> entries) {
		super(activity, textViewResourceId, entries);
		this.entries = entries;
		this.activity = activity;
	}

	/**
	 * The ViewHolder for an item in the list
	 * 
	 * @author Carl Johnsen
	 * @version 1.0
	 * @since 28-06-2013
	 */
	public final static class ViewHolder {
		/** R.id.setting_name */
		public TextView name;
		/** R.id.setting_value */
		public TextView value;
		/** R.id.setting_check */
		public CheckBox checkbox;
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
			holder.name 	= (TextView) v.findViewById(R.id.setting_name);
			holder.value 	= (TextView) v.findViewById(R.id.setting_value);
			holder.checkbox = (CheckBox) v.findViewById(R.id.setting_check);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		
		// Take the Setting, and fill its information into the Views
		final Setting setting = entries.get(position);
		if (setting != null) {
			holder.name.setText(setting.description);
			if (setting.valueType.equals("boolean")) {
				holder.value.setVisibility(TextView.GONE);
				holder.checkbox.setChecked(StatMeth.parseBool(setting.value));
			} else {
				holder.value.setText(setting.value);
				holder.checkbox.setVisibility(CheckBox.GONE);
			}
		}
		return v;
	}

}
