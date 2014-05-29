package com.example.try_masterdetail.preference;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.example.try_masterdetail.R;

public class NewspaperSelectFragment extends Fragment {

	public static final String ARG_PAGE = "page";
	private int mPageNumber;
	private NewspaperCallback callback;

	public interface NewspaperCallback {
		public void npsFragBackButton();

		public void npsFragNextButton();
	}

	public static NewspaperSelectFragment create(int pageNumber) {
		NewspaperSelectFragment fragment = new NewspaperSelectFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		if (!(activity instanceof NewspaperCallback)) {
			throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
		}
		callback = (NewspaperCallback) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.feed_select_fragment_gridview, container, false);

		// Watch for button clicks.
		Button button = (Button) rootView.findViewById(R.id.feed_select_gridview_back_button);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View back) {
				callback.npsFragBackButton();
			}
		});

		button = (Button) rootView.findViewById(R.id.feed_select_gridview_next_button);
		button.setOnClickListener(new OnClickListener() {
			public void onClick(View next) {
				callback.npsFragNextButton();
			}
		});

		return rootView;
	}

	public int getPageNumber() {
		return mPageNumber;
	}
}
