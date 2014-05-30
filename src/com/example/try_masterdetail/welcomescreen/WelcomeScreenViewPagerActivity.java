package com.example.try_masterdetail.welcomescreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.example.try_masterdetail.R;
import com.example.try_masterdetail.homescreen.adapter.CellListObjects;
import com.example.try_masterdetail.preference.FeedPrefObject;

public class WelcomeScreenViewPagerActivity extends FragmentActivity implements
		WelcomeScreenFragmentExpList.WelcomeScreenFeedSelectCallback {

	private static final int NUM_ITEMS = 2;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	private boolean selected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome_screen_view_pager);

		List<Fragment> fragmentList = new ArrayList<Fragment>();
		fragmentList.add(WelcomeScreenFragmentFirst.create(0));
		fragmentList.add(WelcomeScreenFragmentExpList.create(1));

		mPager = (ViewPager) findViewById(R.id.welcome_screen_pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragmentList);
		mPager.setAdapter(mPagerAdapter);
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

	@Override
	public void onBackPressed() {
		if (mPager.getCurrentItem() == 0) {
			Toast.makeText(this, "Please select feeds from the next page", Toast.LENGTH_LONG).show();
		} else if (mPager.getCurrentItem() == 1 && selected) {
			super.onBackPressed();
		} else if (mPager.getCurrentItem() == 1) {
			Toast.makeText(this, "Click \"Let\'s Get Started!\" to start reading", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void fsFragBackButton() {
		onBackPressed();
	}

	@Override
	public void fsFragDoneButton(HashMap<Integer, boolean[]> mChildCheckStates) {
		Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
		selected = true;
		onBackPressed();

		// Update feedPrefs
		FeedPrefObject feedPrefObject = new FeedPrefObject(this);
		feedPrefObject.saveFeedPrefs(mChildCheckStates);

		// Update cellList
		CellListObjects cellListObject = new CellListObjects(this);
		cellListObject.updateCellListByFeedPrefs();

	}
}
