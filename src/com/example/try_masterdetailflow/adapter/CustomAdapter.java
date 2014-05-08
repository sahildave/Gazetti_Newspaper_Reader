package com.example.try_masterdetailflow.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.try_masterdetailflow.R;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

public class CustomAdapter extends ParseQueryAdapter<ParseObject> {
	private static final String TAG = "MasterDetail";

	Context ctx;
	public static ArrayList<String> linkArrayList = new ArrayList<String>();

	public CustomAdapter(Context context, QueryFactory<ParseObject> factory) {
		super(context, factory);

		ctx = context;
	}

	public static HashMap<String, String> mMap = new HashMap<String, String>();

	@Override
	public ParseObject getItem(int index) {
		// TODO Auto-generated method stub
		return super.getItem(index);
	}

	int count = 0;

	@Override
	public View getItemView(ParseObject object, View v, ViewGroup parent) {
		if (v == null) {
			v = View.inflate(getContext(), R.layout.headline_list_row, null);
		}

		super.getItemView(object, v, parent);

		// Add the title view
		TextView headlineTextView = (TextView) v.findViewById(R.id.headline);
		headlineTextView.setText(object.getString("title"));
		// Log.d(TAG, object.getString("title"));

		mMap.put(object.getString("title"), object.getString("link"));
		Log.d(TAG, "CustomAdapter " + mMap.size());
		/*
		 * 
		 * //PubDate
		 * 
		 * TextView pubdateView = (TextView) v.findViewById(R.id.pubdate);
		 * 
		 * Date createdAtInput = object.getCreatedAt(); // Log.d(TAG,
		 * "createdAtInput " + createdAtInput.toString()); DateFormat dfInput =
		 * android.text.format.DateFormat.getMediumDateFormat(ctx);
		 * 
		 * Date now = new Date(); // Log.d(TAG, "now " + now.toString()); long
		 * timeDiffInMS = Math.abs(createdAtInput.getTime() - now.getTime());
		 * long timeDiffInMins = TimeUnit.MILLISECONDS.toMinutes(timeDiffInMS);
		 * 
		 * int finalDiff; if (timeDiffInMins == 0) { finalDiff = (int)
		 * timeDiffInMins; pubdateView.setText("0 minutes ago"); } else if
		 * (timeDiffInMins == 1) { finalDiff = (int) timeDiffInMins;
		 * pubdateView.setText("1 minute ago"); } else if (timeDiffInMins < 60)
		 * { finalDiff = (int) timeDiffInMins; pubdateView.setText(finalDiff +
		 * " minutes ago"); } else if (timeDiffInMins < 120) { finalDiff = (int)
		 * timeDiffInMins; pubdateView.setText("1 hour ago"); } else { finalDiff
		 * = (int) (timeDiffInMins / 60); pubdateView.setText(finalDiff +
		 * " hours ago"); }
		 * 
		 * // if (timeDiffInMins < 30) { // finalDiff = (int) (timeDiffInMins /
		 * 60); // pubdateView.setText("some time ago"); // } else if
		 * (timeDiffInMins < 60) { // finalDiff = (int) (timeDiffInMins / 60);
		 * // pubdateView.setText("half an hour ago"); // } else if
		 * (timeDiffInMins < 120) { // finalDiff = (int) (timeDiffInMins / 60);
		 * // pubdateView.setText("1 hour ago"); // } else { // finalDiff =
		 * (int) (timeDiffInMins / 60); // pubdateView.setText(finalDiff +
		 * " hours ago"); // }
		 * 
		 * // Log.d(TAG, "timeDiffInMS " + timeDiffInMS + ", timeDiffInMins " +
		 * // timeDiffInMins + ", finalDiff " + finalDiff); // String dateOutput
		 * = dateformat.format(createdAtInput); // Log.d(TAG, dateOutput);
		 * 
		 * // // Log.d(TAG, (object.getDate("createdAt")).toString());
		 */
		return v;
	}
}
