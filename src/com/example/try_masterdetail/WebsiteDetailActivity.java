package com.example.try_masterdetail;

import com.example.try_masterdetailflow.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

public class WebsiteDetailActivity extends FragmentActivity {
	private static final String TAG = "MasterDetail";

	WebsiteDetailFragment mDetailFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "in " + getClass().getSimpleName());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_website_detail);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mDetailFragment = (WebsiteDetailFragment) getSupportFragmentManager().findFragmentByTag("articleContent");
		Log.d(TAG, "in DetailActvity, mDetailFragment is null? " + (mDetailFragment == null));
		if (savedInstanceState == null || mDetailFragment == null) {
			Log.d(TAG, "mDetailFragmentt is null");
			mDetailFragment = new WebsiteDetailFragment();

			Bundle arguments = new Bundle();
			arguments.putString(WebsiteDetailFragment.articleLink,
					getIntent().getStringExtra(WebsiteDetailFragment.articleLink));
			mDetailFragment.setArguments(arguments);

			getSupportFragmentManager().beginTransaction()
					.add(R.id.website_detail_container, mDetailFragment, "articleContent").commit();
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			NavUtils.navigateUpTo(this, new Intent(this, WebsiteListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
