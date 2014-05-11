package com.example.try_masterdetail.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.try_masterdetailflow.R;
import com.parse.ParseObject;

public class CustomAdapter extends ArrayAdapter<ParseObject> {

	private static final String TAG = "MasterDetail";
	private static final int SECOND_MILLIS = 1000;
	private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
	private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
	private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

	private Activity ctx;
	public static ArrayList<String> linkArrayList = new ArrayList<String>();
	public static HashMap<String, String> linkMap = new HashMap<String, String>();
	public static HashMap<String, String> pubDateMap = new HashMap<String, String>();
	private List<ParseObject> articleObjectList;

	public CustomAdapter(Activity context, List<ParseObject> articleObjectList) {
		super(context, R.layout.headline_list_row, articleObjectList);
		Log.d(TAG, "customAdapter big constructor");
		ctx = context;
		this.articleObjectList = articleObjectList;

	}

	static class ViewHolder {
		TextView textViewItem;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			LayoutInflater inflater = ctx.getLayoutInflater();
			convertView = inflater.inflate(R.layout.headline_list_row, parent, false);

			holder = new ViewHolder();
			holder.textViewItem = (TextView) convertView.findViewById(R.id.headline);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ParseObject object = articleObjectList.get(position);

		// Add the title view
		holder.textViewItem.setText(object.getString("title"));

		Date createdAtDate = object.getCreatedAt();
		long createdAtDateInMillis = createdAtDate.getTime();
		// Log.d(TAG, "createdAtDateInMillis " + createdAtDateInMillis);

		String diff = getTimeAgo(createdAtDateInMillis);
		// Log.d(TAG, "diff " + diff);

		// Add title, link to Map
		linkMap.put(object.getString("title"), object.getString("link"));
		pubDateMap.put(object.getString("title"), diff);
		// Log.d(TAG, "CustomAdapter " + mMap.size());

		// PubDate

		return convertView;

	}

	public static String getTimeAgo(long time) {
		if (time < 1000000000000L) {
			// if timestamp given in seconds, convert to millis
			time *= 1000;
		}

		long now = System.currentTimeMillis();
		if (time > now || time <= 0) {
			return null;
		}

		// TODO: localize
		final long diff = now - time;
		if (diff < MINUTE_MILLIS) {
			return "just now";
		} else if (diff < 2 * MINUTE_MILLIS) {
			return "a minute ago";
		} else if (diff < 50 * MINUTE_MILLIS) {
			return diff / MINUTE_MILLIS + " minutes ago";
		} else if (diff < 120 * MINUTE_MILLIS) {
			return "an hour ago";
		} else if (diff < 24 * HOUR_MILLIS) {
			return diff / HOUR_MILLIS + " hours ago";
		} else if (diff < 48 * HOUR_MILLIS) {
			return "yesterday";
		} else {
			return diff / DAY_MILLIS + " days ago";
		}
	}

}
