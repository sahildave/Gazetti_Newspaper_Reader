package com.example.try_masterdetail.news_activities;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.example.try_masterdetail.MainActivity;
import com.example.try_masterdetail.R;
import com.example.try_masterdetail.news_activities.adapter.CustomAdapter;
import com.example.try_masterdetail.news_activities.adapter.NavDrawerListAdapter;
import com.example.try_masterdetail.news_activities.model.NavDrawerItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

@SuppressLint("NewApi")
// TODO: Check this
public class WebsiteListActivity extends ActionBarActivity implements WebsiteListFragment.Callbacks,
		WebsiteDetailFragment.TaskCallbacks {

	private static final String TAG = "MasterDetail";
	private static final String TAG_ASYNC = "ASYNC";

	public boolean mTwoPane;

	// For NavDrawer
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	WebsiteListFragment mlistFragment;

	// CallBacks for handling asynctask for twoPane mode

	// ScrollView from DetailFragment
	View rootView;
	View headerStub;

	// Header
	ProgressBar detailViewProgress;
	TextView mTitleTextView;
	ImageView mMainImageView;
	ImageLoader mImageLoader;
	String mImageURL;
	String mArticleURL;
	String titleText = "";

	// Subtitle
	RelativeLayout mSubtitleLayout;
	TextView mArticlePubDateView;
	String mArticlePubDate;

	// Body
	TextView mArticleTextView;
	String bodyText = "";
	ScrollView mScrollView;

	// Scroll To Read Bubble
	LinearLayout mScrollToReadLayout;
	private boolean displayScrollToRead = false;
	private Animation slide_up;

	// Intent variables from Home Screen
	String npId = "0";
	String catId = "1";
	String npName = "";
	String catName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		// Log.d(TAG, "Activity onCreate");
		setContentView(R.layout.activity_website_list);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			npId = extras.getString("npId");
			catId = extras.getString("catId");
			npName = extras.getString("npName");
			catName = extras.getString("catName");
		}
		getSupportActionBar().setTitle(npName + " - " + catName);

		if (findViewById(R.id.website_detail_container) != null) {
			// Log.d(TAG, "Activity twoPane true");
			mTwoPane = true;
		}

		mlistFragment = (WebsiteListFragment) getSupportFragmentManager().findFragmentByTag("listContent");

		Log.d(TAG, "mlistFragment==null - " + (mlistFragment == null));

		if (mlistFragment == null) {

			mlistFragment = new WebsiteListFragment();
			Bundle layoutBundle = new Bundle();

			layoutBundle.putBoolean("mTwoPane", mTwoPane);
			layoutBundle.putString("npId", npId);
			layoutBundle.putString("catId", catId);

			mlistFragment.setArguments(layoutBundle);

			getSupportFragmentManager().beginTransaction()
					.add(R.id.website_list_container, mlistFragment, "listContent").commit();

		}

		slide_up = AnimationUtils.loadAnimation(this, R.animator.slide_up);

		// Make Navigation Drawer
		makeNavDrawer();

	}

	@Override
	public void onItemSelected(String headlineText, CustomAdapter customAdapter) {

		Bundle arguments = new Bundle();
		// Log.d(TAG, "ListActivity headlineText " + headlineText);
		arguments.putString(WebsiteDetailFragment.articleLinkKey, headlineText);
		WebsiteDetailFragment detailFragment = new WebsiteDetailFragment();
		detailFragment.setArguments(arguments);

		if (mTwoPane) {
			// Log.d(TAG, "replacing detailFragment");
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.website_detail_container, detailFragment, "detail").commit();
		} else {
			Intent detailIntent = new Intent(this, WebsiteDetailActivity.class);
			detailIntent.putExtra(WebsiteDetailFragment.articleLinkKey, headlineText);
			startActivity(detailIntent);

		}
	}

	/****************************/
	/***** CALLBACK METHODS *****/
	/****************************/

	@Override
	public void onPreExecute(View rootView) {

		this.rootView = rootView;

		// initialize article views
		mArticleTextView = (TextView) rootView.findViewById(R.id.body);
		mArticlePubDateView = (TextView) rootView.findViewById(R.id.pubDateView);
		mSubtitleLayout = (RelativeLayout) rootView.findViewById(R.id.subtitleLayout);
		mScrollView = (ScrollView) rootView.findViewById(R.id.scroller);
		mScrollToReadLayout = (LinearLayout) rootView.findViewById(R.id.scrollToReadLayout);

		// Progress Bar
		detailViewProgress = (ProgressBar) rootView.findViewById(R.id.detailViewProgressBar);
		detailViewProgress.setVisibility(View.VISIBLE);
	}

	@Override
	public void onProgressUpdate(String values) {
		bodyText = values + "\n\n";
		// Log.d(TAG_ASYNC, "Adding Body Text");
		mArticleTextView.append(bodyText);
	}

	@Override
	public void getHeaderStub(View headerStub) {
		this.headerStub = headerStub;
	}

	@Override
	public void onPostExecute(String[] result) {
		Log.d(TAG_ASYNC, "Activity onPostExecute");

		titleText = result[0];
		mImageURL = result[1];
		mArticlePubDate = result[2];

		if (bodyText == null || bodyText.isEmpty()) {
			mArticleTextView.setText("Sorry, this story is not supported for mobile view. Try to read in the browser");

		}
		mSubtitleLayout.setVisibility(View.VISIBLE);
		mArticlePubDateView.setText(mArticlePubDate);
		mArticleTextView.setVisibility(View.VISIBLE);

		if (mImageURL == null) {
			// Log.d(TAG, "Image not, Title : " + titleText);

			mTitleTextView = (TextView) headerStub.findViewById(R.id.article_title);
			mTitleTextView.setText(titleText);
			detailViewProgress.setVisibility(View.GONE);
		} else {
			// Log.d(TAG, "Loading Image...");

			mTitleTextView = (TextView) headerStub.findViewById(R.id.article_header_title);
			mTitleTextView.setText(titleText);

			mMainImageView = (ImageView) headerStub.findViewById(R.id.mainImage);
			Picasso picassoInstance = Picasso.with(this);
			picassoInstance.setDebugging(true);
			picassoInstance.load(mImageURL).into(mMainImageView, new Callback() {

				@Override
				public void onSuccess() {
					Log.d(TAG_ASYNC, "Image Loaded");

					mMainImageView.getViewTreeObserver().addOnGlobalLayoutListener(
							new ViewTreeObserver.OnGlobalLayoutListener() {

								@SuppressWarnings("deprecation")
								@SuppressLint("NewApi")
								@Override
								public void onGlobalLayout() {

									// Get Display metrics according to the SDK
									Display display = getWindowManager().getDefaultDisplay();
									Point screen = new Point();
									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
										display.getSize(screen);
									} else {
										screen.x = display.getWidth();
										screen.y = display.getHeight();
									}

									// StatusBar Height
									int statusBarHeight = 0;
									int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
									if (resId > 0) {
										statusBarHeight = getResources().getDimensionPixelSize(resId);
									}

									// ActionBar Height
									TypedValue tv = new TypedValue();
									int actionBarHeight = 0;
									if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
										actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
												getResources().getDisplayMetrics());
									}

									// Check
									// Log.d(TAG_ASYNC, "mSubtitleLayout " +
									// mSubtitleLayout.getHeight());
									// Log.d(TAG_ASYNC, "mMainImageView " +
									// mMainImageView.getHeight());
									// Log.d(TAG_ASYNC, "mArticleTextView " +
									// mArticleTextView.getTop());
									// Log.d(TAG_ASYNC, "total height " +
									// screen.y);
									// Log.d(TAG_ASYNC, "status bar " +
									// statusBarHeight);
									// Log.d(TAG_ASYNC, "action bar " +
									// actionBarHeight);

									// Boolean to check if image+subtitle is
									// large enough.
									// If yes, then display "Scroll To Read"
									displayScrollToRead = (screen.y - statusBarHeight - actionBarHeight) < (mArticleTextView
											.getTop()) * 1.08;
									Log.d(TAG_ASYNC, "displayScrollToRead " + displayScrollToRead);

									if (displayScrollToRead) {
										mScrollToReadLayout.startAnimation(slide_up);
										mScrollToReadLayout.setVisibility(View.VISIBLE);
										Log.d(TAG_ASYNC, "Visiblity - " + mScrollToReadLayout.getVisibility());
									}

									// remove GlobalLayoutListener according to
									// SDK
									if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
										mMainImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
									} else {
										mMainImageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
									}

								}

							});
					detailViewProgress.setVisibility(View.GONE);
				}

				@Override
				public void onError() {

					Log.d(TAG_ASYNC, "IMAGE WAS NOT LOADED!!!");

				}
			});
		}

		bodyText = null;
		titleText = null;
		mImageURL = null;
	}

	/******************************************/

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

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
		return super.onPrepareOptionsMenu(menu);
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

	private void makeNavDrawer() {
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
		adapter = new NavDrawerListAdapter(this, navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// New NavBar _ OVER

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};

		/*
		 * Making NavBar - END
		 */

		mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (position == 0) {
					Log.d(TAG, "Home Called");
					onBackPressed();
				} else {

					mlistFragment = new WebsiteListFragment();
					Bundle layoutBundle = new Bundle();

					catId = String.valueOf(position);

					layoutBundle.putBoolean("mTwoPane", mTwoPane);
					layoutBundle.putString("npId", npId);
					layoutBundle.putString("catId", catId);

					Log.d(TAG, "mlistFragment==null - " + (mlistFragment == null));

					mlistFragment.setArguments(layoutBundle);

					getSupportFragmentManager().beginTransaction()
							.replace(R.id.website_list_container, mlistFragment, "listContent").commit();

					// update selected item and title, then close the drawer
					mDrawerList.setItemChecked(position, true);
					mDrawerList.setSelection(position);
					setTitle(npName + " - " + navMenuTitles[position]);
					mDrawerLayout.closeDrawer(mDrawerList);
				}
			}
		});

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

}
