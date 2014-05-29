package com.example.try_masterdetail.preference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.try_masterdetail.R;
import com.example.try_masterdetail.homescreen.adapter.CellListObjects;
import com.google.gson.Gson;

public class FeedSelectViewpager extends ActionBarActivity implements NewspaperSelectFragment.NewspaperCallback,
		FeedSelectFragment.FeedSelectCallback {

	private static final String TAG = "FEED";

	private static final int NUM_ITEMS = 2;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private boolean backFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.feed_select_viewpager_activity);

		List<Fragment> fragmentList = new ArrayList<Fragment>();
		fragmentList.add(NewspaperSelectFragment.create(0));
		fragmentList.add(FeedSelectFragment.create(1));

		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.feedSelectPager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragmentList);
		mPager.setAdapter(mPagerAdapter);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Select Feeds");
	}

	@Override
	public void npsFragBackButton() {
		onBackPressed();
	}

	@Override
	public void npsFragNextButton() {
		mPager.setCurrentItem(1);
	}

	@Override
	public void fsFragBackButton() {
		mPager.setCurrentItem(0);
	}

	@Override
	public void fsFragDoneButton(HashMap<Integer, boolean[]> mChildCheckStates) {
		Toast.makeText(this, "DONE!", Toast.LENGTH_SHORT).show();
		backFlag = true;
		onBackPressed();

		// Update feedPrefs
		// Gson gson = new Gson();
		// String feedsChecked = gson.toJson(mChildCheckStates);
		// SharedPreferences feedPrefs = this.getSharedPreferences("FeedPrefs",
		// Context.MODE_PRIVATE);
		// SharedPreferences.Editor prefEditor = feedPrefs.edit();
		// prefEditor.putString("feedPreference", feedsChecked);
		// prefEditor.commit();

		FeedPrefObject feedPrefObject = new FeedPrefObject(this);
		feedPrefObject.saveFeedPrefs(mChildCheckStates);

		// Update cellList
		CellListObjects cellListObject = new CellListObjects(this);
		cellListObject.updateCellListByFeedPrefs();

	}

	@Override
	public void onBackPressed() {

		if (mPager.getCurrentItem() == 0) {
			Log.d(TAG, "1");
			super.onBackPressed();
		} else if (mPager.getCurrentItem() == 1 && backFlag) {
			Log.d(TAG, "2");
			super.onBackPressed();
		} else if (mPager.getCurrentItem() == 1 && !backFlag) {
			Log.d(TAG, "3");
			mPager.setCurrentItem(0);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

		private List<Fragment> fragmentList;

		public ScreenSlidePagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
			super(fm);
			this.fragmentList = fragmentList;
		}

		@Override
		public Fragment getItem(int position) {

			return fragmentList.get(position);
		}

		@Override
		public int getCount() {
			return NUM_ITEMS;
		}
	}

}
