package com.example.try_masterdetail.preference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.try_masterdetail.R;
import com.example.try_masterdetail.preference.NewspaperSelectFragment.NewspaperCallback;

public class FeedSelectFragment extends Fragment {

	/**
	 * The argument key for the page number this fragment represents.
	 */
	public static final String ARG_PAGE = "page";

	private int mPageNumber;
	private MyExpandableListAdapter expListAdapter;
	private List<String> listDataHeader;
	private HashMap<String, List<String>> listDataChild;

	private FeedSelectCallback callback;

	public interface FeedSelectCallback {
		public void fsFragBackButton();

		public void fsFragDoneButton(HashMap<Integer, boolean[]> mChildCheckStates);
	}

	public static FeedSelectFragment create(int pageNumber) {
		FeedSelectFragment fragment = new FeedSelectFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public FeedSelectFragment() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (!(activity instanceof NewspaperCallback)) {
			throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
		}
		callback = (FeedSelectCallback) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout containing a title and body text.
		Toast.makeText(getActivity(), mPageNumber + "", Toast.LENGTH_SHORT).show();
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.feed_select_fragment_explist, container, false);
		ExpandableListView expListView = (ExpandableListView) rootView.findViewById(R.id.feed_select_expandable_list);

		prepareListData();
		expListAdapter = new MyExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
		expListView.setAdapter(expListAdapter);
		expListAdapter.setExpList(expListView);

		// Watch for button clicks.
		Button button = (Button) rootView.findViewById(R.id.feed_select_explist_back_button);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View back) {
				callback.fsFragBackButton();
			}
		});

		button = (Button) rootView.findViewById(R.id.feed_select_explist_done_button);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View next) {
				HashMap<Integer, boolean[]> mChildCheckStates = expListAdapter.getClickedStates();
				callback.fsFragDoneButton(mChildCheckStates);
			}
		});

		return rootView;
	}

	/**
	 * Returns the page number represented by this fragment object.
	 */
	public int getPageNumber() {
		return mPageNumber;
	}

	private void prepareListData() {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();

		// Adding child data
		listDataHeader.add(0, "The Hindu");
		listDataHeader.add(1, "The Times of India");

		// Adding child data
		List<String> th = new ArrayList<String>();
		th.add("TH - National");
		th.add("TH - International");
		th.add("TH - Politics");
		th.add("TH - Sports");
		th.add("TH - Entertainment");

		List<String> toi = new ArrayList<String>();
		toi.add("TOI - National");
		toi.add("TOI - International");
		toi.add("TOI - Politics");
		toi.add("TOI - Sports");
		toi.add("TOI - Entertainment");

		// Header, Child data
		listDataChild.put(listDataHeader.get(0), th);
		listDataChild.put(listDataHeader.get(1), toi);
	}

}
