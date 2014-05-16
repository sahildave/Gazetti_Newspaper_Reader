package com.example.try_masterdetail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.try_masterdetail.adapter.CustomAdapter;
import com.example.try_masterdetailflow.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class WebsiteListFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener,
		ListView.OnScrollListener {

	private static final String TAG = "MasterDetail";

	private Callbacks mCallbacks = sDummyCallbacks;
	private static final String PREFS_NAME = "QueryPrefs";
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	// Retained objects
	private int mActivatedPosition = 3;

	// For HeadlinesList
	private SwipeRefreshLayout mListViewContainer;
	private CustomAdapter customAdapter;
	private ListView mListView;
	private View headerOnList;
	private View footerOnList;
	private List<ParseObject> retainedList = new ArrayList<ParseObject>();

	private Date dateLastUpdated; // Display as header

	private boolean mTwoPane;
	private boolean firstRun = false;
	private boolean flag_loading_old_data = false;
	private NetworkInfo networkInfo;

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
		// Log.d(TAG, "ListFragment in onAttach ");

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}
		if (getArguments().containsKey("mTwoPane")) {
			mTwoPane = getArguments().getBoolean("mTwoPane");
			// Log.d(TAG, "ListFragment mTwoPane is " + mTwoPane);
		}
		// this.activity = activity;
		mCallbacks = (Callbacks) activity;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Log.d(TAG, "ListFragment in onCreate ");
		firstRun = true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Log.d(TAG, "ListFragment in onCreateView ");
		View rootView = inflater.inflate(R.layout.fragment_website_list, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// Log.d(TAG, "ListFragment in onViewCreated ");
		setRetainInstance(true);

		mListViewContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_container);

		mListViewContainer.setOnRefreshListener(this);
		mListViewContainer.setColorScheme(R.color.holo_blue_bright, R.color.holo_orange_light,
				R.color.holo_green_light, R.color.holo_red_light);

		mListView = getListView();
		mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		mListView.setOnScrollListener(this);

		headerOnList = (View) getActivity().getLayoutInflater().inflate(R.layout.header_view, null);
		mListView.addHeaderView(headerOnList);
		footerOnList = (View) getActivity().getLayoutInflater().inflate(R.layout.footer_view, null);
		mListView.addFooterView(footerOnList);
		mListView.removeFooterView(footerOnList);

		customAdapter = new CustomAdapter(getActivity(), retainedList);

		if (mTwoPane) {
			// Log.d(TAG, "ListFragment selecting first element");
			// Restore the previously serialized activated item position.
			if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
				setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
			}
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Log.d(TAG, "ListFragment in onActivityCreated ");

		ConnectivityManager connMgr = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		networkInfo = connMgr.getActiveNetworkInfo();

		if (networkInfo != null && networkInfo.isConnected()) {
			if (firstRun) {
				getNewListItems();

			}
		} else {
			Toast.makeText(getActivity(), "No network connection available.", Toast.LENGTH_LONG).show();

			ParseQuery<ParseObject> query = ParseQuery.getQuery("freshNewsArticle");
			query.whereEqualTo("cat_id", "1");
			query.orderByDescending("createdAt");
			query.fromLocalDatastore();

			query.findInBackground(new FindCallback<ParseObject>() {
				@Override
				public void done(final List<ParseObject> articleObjectList, ParseException arg1) {
					// Log.d(TAG, " articleObjectList " +
					// articleObjectList.size());
					retainedList.clear();
					retainedList.addAll(0, articleObjectList);
					mListView.setAdapter(customAdapter);
					customAdapter.notifyDataSetChanged();
					((TextView) headerOnList).setText("Data from Local Storage");
				}

			});
		}

	}

	private void getNewListItems() {

		mListViewContainer.setRefreshing(true);

		ParseQuery<ParseObject> queryGetNewItems = ParseQuery.getQuery("freshNewsArticle");
		queryGetNewItems.whereEqualTo("cat_id", "1");
		queryGetNewItems.orderByDescending("createdAt");
		queryGetNewItems.setLimit(25);

		queryGetNewItems.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> articleObjectList, ParseException arg1) {
				Log.d(TAG, "articleObjectList " + articleObjectList.size());
				Log.d(TAG, "adding to old list");

				retainedList.clear();
				retainedList.addAll(0, articleObjectList);

				mListView.setAdapter(customAdapter);
				customAdapter.notifyDataSetChanged();

				Log.d(TAG, "articleObjectList Retained " + retainedList.size());

				dateLastUpdated = Calendar.getInstance().getTime();
				((TextView) headerOnList).setText(dateLastUpdated.toString());
				mListViewContainer.setRefreshing(false);

				ParseObject.pinAllInBackground(articleObjectList);

				Log.d(TAG, "listview top - " + ((ParseObject) mListView.getItemAtPosition(1)).getString("title"));
				Log.d(TAG, "retainList top-" + retainedList.get(0).getString("title"));
				Log.d(TAG, "customAdapter top - " + customAdapter.getItem(0).getString("title"));

				if (mTwoPane && firstRun) {
					Log.d(TAG, "clicking for first time at " + mActivatedPosition);
					mListView.performItemClick(customAdapter.getView(mActivatedPosition - 1, null, null),
							mActivatedPosition, mActivatedPosition);

					firstRun = false;
				}

			}

		});
	}

	private void getOldListItems(Date lastObjectCreatedAtDate) {

		ParseQuery<ParseObject> queryGetOldItems = ParseQuery.getQuery("freshNewsArticle");
		queryGetOldItems.whereEqualTo("cat_id", "1");
		queryGetOldItems.whereLessThan("createdAt", lastObjectCreatedAtDate);
		queryGetOldItems.setLimit(25);
		queryGetOldItems.orderByDescending("createdAt");

		queryGetOldItems.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> articleObjectList, ParseException arg1) {
				Log.d(TAG, "Old Items articleObjectList " + articleObjectList.size());

				retainedList.addAll(articleObjectList);
				customAdapter.notifyDataSetChanged();

				Log.d(TAG, "articleObjectList Retained " + retainedList.size());

				mListView.removeFooterView(footerOnList);
				mListViewContainer.setRefreshing(false);
				flag_loading_old_data = false;
			}

		});

	}

	@Override
	public void onRefresh() {
		if (networkInfo != null && networkInfo.isConnected()) {
			Toast.makeText(getActivity(), "Getting New Headlines", Toast.LENGTH_SHORT).show();
			getNewListItems();
		} else {
			Toast.makeText(getActivity(), "Cannot Refresh. No Connection", Toast.LENGTH_SHORT).show();
			mListViewContainer.setRefreshing(false);
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		Log.d(TAG, "onScrollStateChanged");
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		int topRowVerticalPosition = (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView
				.getChildAt(0).getTop();
		mListViewContainer.setEnabled(topRowVerticalPosition >= 0);
		// Log.d(TAG, firstVisibleItem + ", " + visibleItemCount + ", " +
		// totalItemCount);

		if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
			// Log.d(TAG, visibleItemCount + ", " + totalItemCount);

			// So that short lists dont load
			if (totalItemCount > visibleItemCount && flag_loading_old_data == false) {
				mListView.addFooterView(footerOnList);
				mListViewContainer.setRefreshing(true);
				flag_loading_old_data = true;
				Log.d(TAG, "Last Position - " + retainedList.get(totalItemCount - 2).getString("title"));

				Date lastObjectCreatedAtDate = retainedList.get(totalItemCount - 2).getCreatedAt();
				getOldListItems(lastObjectCreatedAtDate);
			}
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(TAG, "in onDetach");

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks; // TODO: Check with mCallbacks = null;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		Log.d(TAG, "onListItemClick position, id +" + position + ", " + id);

		TextView headlineTextView = (TextView) view.findViewById(R.id.headline);
		String headlineText = (String) headlineTextView.getText();
		Log.d(TAG, "onListItemClick view - " + headlineText);
		// view.setActivated(true);
		setActivatedPosition(position);
		mCallbacks.onItemSelected(headlineText, customAdapter);
	}

	private void setActivatedPosition(int position) {
		// Log.d(TAG, "ListFragment  setActivatedPosition");
		if (position == ListView.INVALID_POSITION) {
			mListView.setItemChecked(mActivatedPosition, false);
		} else {
			mListView.setItemChecked(position, true);
		}
		mActivatedPosition = position;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(TAG, "ListFragment onSaveInstanceState ");
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}
}
