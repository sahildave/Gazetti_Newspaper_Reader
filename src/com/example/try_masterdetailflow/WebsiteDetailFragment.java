package com.example.try_masterdetailflow;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.try_masterdetailflow.adapter.CustomAdapter;

public class WebsiteDetailFragment extends Fragment {
	private static final String TAG = "MasterDetail";
	public static final String ARG_ITEM_ID = "item_id";

	private String mItem;

	public WebsiteDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "in " + getClass().getSimpleName());
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			mItem = CustomAdapter.mMap.get(getArguments().getString(ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_website_detail, container, false);

		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.detail)).setText(mItem);
		}

		return rootView;
	}
}
