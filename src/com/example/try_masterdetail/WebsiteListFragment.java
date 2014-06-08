package com.example.try_masterdetail;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.try_masterdetail.adapter.CustomAdapter;
import com.example.try_masterdetailflow.R;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;

public class WebsiteListFragment extends ListFragment {

	private static final String TAG = "MasterDetail";

	// For HeadlinesList
	private CustomAdapter customAdapter;
	private ListView listView;

	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	private Callbacks mCallbacks = sDummyCallbacks;
	private int mActivatedPosition = ListView.INVALID_POSITION;

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "in onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "in onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_website_list, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.d(TAG, "in onViewCreated");

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "in onActivityCreated");

		ParseQueryAdapter.QueryFactory<ParseObject> factory = new ParseQueryAdapter.QueryFactory<ParseObject>() {

			public ParseQuery create() {

				ParseQuery query = new ParseQuery("freshNewsArticle");
				query.setLimit(15);
				query.whereEqualTo("cat_id", "1");
				query.orderByDescending("createdAt");
				return query;
			}

		};

		customAdapter = new CustomAdapter(getActivity().getApplicationContext(), factory);
		customAdapter.addOnQueryLoadListener(new OnQueryLoadListener<ParseObject>() {
			public void onLoading() {
				Log.d(TAG, "ON LOADING");
			}

			public void onLoaded(List<ParseObject> arg0, Exception arg1) {
				Log.d(TAG, "ON LOADED");

				HashMap<String, String> mMap = customAdapter.mMap;

				// Iterator iter = mMap.entrySet().iterator();
				// Log.d(TAG, "onLoaded " + iter.hasNext());
				// Log.d(TAG, "onLoaded " + mMap.size());
				// while (iter.hasNext()) {
				// Map.Entry mEntry = (Map.Entry) iter.next();
				// Log.d(TAG, mEntry.getKey() + " : " + mEntry.getValue());
				// }

			}
		});

		listView = getListView();
		listView.setAdapter(customAdapter);
		customAdapter.loadObjects();
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
	public void onDetach() {
		super.onDetach();
		Log.d(TAG, "in onDetach");

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		TextView headlineTextView = (TextView) view.findViewById(R.id.headline);
		String headlineText = (String) headlineTextView.getText();
		mCallbacks.onItemSelected(headlineText, customAdapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
}
