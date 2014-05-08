package com.example.try_masterdetail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.try_masterdetail.adapter.CustomAdapter;
import com.example.try_masterdetail.adapter.NavDrawerListAdapter;
import com.example.try_masterdetail.model.NavDrawerItem;
import com.example.try_masterdetailflow.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.parse.Parse;

public class WebsiteListActivity extends FragmentActivity implements WebsiteListFragment.Callbacks {

	private static final String TAG = "MasterDetail";

	private boolean mTwoPane;

	// For NavDrawer
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	// private String[] mPlanetTitles; //not used anymore!

	// new slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_website_list);

		if (findViewById(R.id.website_detail_container) != null) {
			mTwoPane = true;
			((WebsiteListFragment) getSupportFragmentManager().findFragmentById(R.id.website_list))
					.setActivateOnItemClick(true);
		}

		// Initialize Parse
		Parse.initialize(this, "EIBQFrIyVZBHDTwmEZqxaWn6yx10UNPo4gy7kkmR", "Fj96ZYVQziKR132klHkXDSpireivZZRaKZOmB0SK");

		// // Initialize UIL ImageLoader
		// File cacheDir =
		// StorageUtils.getCacheDirectory(getApplicationContext(), true);
		// DisplayImageOptions defaultOptions = new
		// DisplayImageOptions.Builder()
		// .cacheInMemory(true)
		// .cacheOnDisc(true)
		// .build();
		//
		// ImageLoaderConfiguration config = new
		// ImageLoaderConfiguration.Builder(getApplicationContext())
		// .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
		// .discCache(new UnlimitedDiscCache(cacheDir))
		// .defaultDisplayImageOptions(defaultOptions)
		// .build();
		//
		// ImageLoader imageLoader = ImageLoader.getInstance();
		// imageLoader.init(config);

		/*
		 * Making NavBar - START
		 */

		mTitle = mDrawerTitle = getTitle();
		// mPlanetTitles = getResources().getStringArray(R.array.planets_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		// New NavBar _ START
		// http://www.androidhive.info/2013/11/android-sliding-menu-using-navigation-drawer/

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		// nav drawer icons from resources
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Find People
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Photos
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Communities, Will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "22"));
		// Pages
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// What's hot, We will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1), true, "50+"));

		// Recycle the typed array
		navMenuIcons.recycle();

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		// set up the drawer's list view with items and click listener
		// mDrawerList.setAdapter(new ArrayAdapter<String>(this,
		// R.layout.drawer_list_item, mPlanetTitles));
		adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// New NavBar _ OVER

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};

		/*
		 * Making NavBar - END
		 */
	}

	/**
	 * Callback method from {@link WebsiteListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		case R.id.action_websearch:
			// create intent to perform web search for this planet
			Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
			intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
			// catch event that there's no activity to handle intent
			if (intent.resolveActivity(getPackageManager()) != null) {
				startActivity(intent);
			} else {
				Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onItemSelected(String headlineText, CustomAdapter customAdapter) {

		HashMap<String, String> mMap = customAdapter.mMap;
		Iterator iter = mMap.entrySet().iterator();
		// Log.d(TAG, "onItemSelected " + iter.hasNext());
		// Log.d(TAG, "onItemSelected " + mMap.size());
		while (iter.hasNext()) {
			Map.Entry mEntry = (Map.Entry) iter.next();
			// Log.d(TAG, mEntry.getKey() + " : " + mEntry.getValue());
		}

		Bundle arguments = new Bundle();
		arguments.putString(WebsiteDetailFragment.ARG_ITEM_ID, headlineText);
		WebsiteDetailFragment fragment = new WebsiteDetailFragment();
		fragment.setArguments(arguments);

		if (mTwoPane) {
			getSupportFragmentManager().beginTransaction().replace(R.id.website_detail_container, fragment).commit();
		} else {
			Intent detailIntent = new Intent(this, WebsiteDetailActivity.class);
			detailIntent.putExtra(WebsiteDetailFragment.ARG_ITEM_ID, headlineText);
			startActivity(detailIntent);
		}
	}

}
