package com.example.try_masterdetailflow;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

public class WebsiteDetailActivity extends FragmentActivity {
	private static final String TAG = "MasterDetail";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "in " + getClass().getSimpleName());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_website_detail);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(WebsiteDetailFragment.ARG_ITEM_ID,
					getIntent().getStringExtra(WebsiteDetailFragment.ARG_ITEM_ID));
			WebsiteDetailFragment fragment = new WebsiteDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().add(R.id.website_detail_container, fragment).commit();
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
