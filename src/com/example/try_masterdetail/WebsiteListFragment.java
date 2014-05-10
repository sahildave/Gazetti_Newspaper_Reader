package com.example.try_masterdetail;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.try_masterdetail.adapter.CustomAdapter;
import com.example.try_masterdetailflow.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

public class WebsiteListFragment extends ListFragment {

	private static final String TAG = "MasterDetail";
	private static final int SECOND_MILLIS = 1000;
	private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
	private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
	private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

	// For HeadlinesList
	private CustomAdapter customAdapter;
	private ListView listView;

	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	private Callbacks mCallbacks = sDummyCallbacks;
	private int mActivatedPosition = ListView.INVALID_POSITION;

	int old_position = -1;
	String oldLastUpdated = null;

	public interface Callbacks {
		public void onItemSelected(String headlineText, CustomAdapter customAdapter);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String headlineText, CustomAdapter customAdapter) {
		}
	};

	public WebsiteListFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d(TAG, "in onAttach");

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "ListFragment in onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "ListFragment in onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_website_list, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.d(TAG, "ListFragment in onViewCreated");

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "ListFragment in onActivityCreated");

		customAdapter = new CustomAdapter(getActivity(), new ArrayList<ParseObject>());
		listView = getListView();
		listView.setAdapter(customAdapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		final LinearLayout layoutHeaderProgress = (LinearLayout) getActivity().findViewById(R.id.layoutHeaderProgress);

		ConnectivityManager connMgr = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			ParseQuery<ParseObject> query = ParseQuery.getQuery("freshNewsArticle");
			query.whereEqualTo("cat_id", "1");
			query.orderByDescending("createdAt");
			query.setLimit(25);
			// query.fromLocalDatastore();

			query.findInBackground(new FindCallback<ParseObject>() {
				@Override
				public void done(final List<ParseObject> articleObjectList, ParseException arg1) {
					Log.d(TAG, " articleObjectList " + articleObjectList.size());

					customAdapter.addAll(articleObjectList);
					layoutHeaderProgress.setVisibility(View.GONE);
					long nowInMillis = System.currentTimeMillis();
					Log.d(TAG, "Pinning " + nowInMillis);
					final String newLastUpdated = String.valueOf(nowInMillis);

					ParseObject.pinAllInBackground(newLastUpdated, articleObjectList, new SaveCallback() {

						@Override
						public void done(ParseException arg0) {
							// TODO Auto-generated method stub
							oldLastUpdated = newLastUpdated;
							Log.d(TAG, "Pinned " + oldLastUpdated);
						}
					});

				}

			});
		} else {
			Log.d(TAG, "No network connection available.");
			ParseQuery<ParseObject> query = ParseQuery.getQuery("freshNewsArticle");
			query.whereEqualTo("cat_id", "1");
			query.orderByDescending("createdAt");
			query.setLimit(25);
			query.fromLocalDatastore();

			query.findInBackground(new FindCallback<ParseObject>() {
				@Override
				public void done(final List<ParseObject> articleObjectList, ParseException arg1) {
					Log.d(TAG, " articleObjectList " + articleObjectList.size());
					customAdapter.addAll(articleObjectList);
					layoutHeaderProgress.setVisibility(View.GONE);
				}

			});
		}

	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(TAG, "in onDetach");

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		Log.d(TAG, "ListFragment  onListItemClick");
		TextView headlineTextView = (TextView) view.findViewById(R.id.headline);
		String headlineText = (String) headlineTextView.getText();
		view.setActivated(true);

		mCallbacks.onItemSelected(headlineText, customAdapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(TAG, "ListFragment  onSaveInstanceState");
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	private void setActivatedPosition(int position) {
		Log.d(TAG, "ListFragment  setActivatedPosition");
		if (position == ListView.INVALID_POSITION) {
			listView.setItemChecked(mActivatedPosition, false);
		} else {
			listView.setItemChecked(position, true);
		}
		mActivatedPosition = position;
	}
}
