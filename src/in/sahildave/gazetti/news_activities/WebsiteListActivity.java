package in.sahildave.gazetti.news_activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.crashlytics.android.Crashlytics;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.news_activities.WebsiteDetailFragment.LoadArticleCallback;
import in.sahildave.gazetti.news_activities.WebsiteListFragment.ItemSelectedCallback;
import in.sahildave.gazetti.news_activities.adapter.CustomAdapter;
import in.sahildave.gazetti.news_activities.adapter.NavDrawerListAdapter;
import in.sahildave.gazetti.preference.SettingsActivity;

@SuppressLint("NewApi")
public class WebsiteListActivity extends ActionBarActivity implements ItemSelectedCallback,
        LoadArticleCallback {

    private static final String TAG = "MasterDetail";
    private static final String TAG_ASYNC = "ASYNC";

    public boolean mTwoPane;

    // For NavDrawer
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private LinearLayout mLeftDrawer;
    private String[] mDrawerItems;
    private int[] mActionBarColors;
    private int currentColor;

    WebsiteListFragment mlistFragment;

    // Intent variables from Home Screen
    String npId;
    String catId;
    String npName;
    String catName;
    private ArticleLoadingCallback articleLoadingCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        Log.d(TAG, "Activity onCreate");
        setContentView(R.layout.activity_website_list);

        mActionBarColors = getResources().getIntArray(R.array.action_bar_colors);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.d(TAG, "Activity retaining extras");
            npId = extras.getString("npId");
            catId = extras.getString("catId");
            npName = extras.getString("npName");
            catName = extras.getString("catName");

            currentColor = mActionBarColors[Integer.parseInt(catId) - 1];
        }

        if (savedInstanceState != null) {
            Log.d(TAG, "Activity retaining savedInstanceState");
            npName = savedInstanceState.getString("npName");
            catId = savedInstanceState.getString("catId");
            currentColor = savedInstanceState.getInt("color");
            catName = savedInstanceState.getString("catName");

        }

        // mActionBarColors =
        // getResources().getIntArray(R.array.action_bar_colors);
        setTitle(npName + " - " + catName);
        setColor(currentColor);

        if (findViewById(R.id.website_detail_container) != null) {
            // Log.d(TAG, "Activity twoPane true");
            mTwoPane = true;
        }

        mlistFragment = (WebsiteListFragment) getSupportFragmentManager().findFragmentByTag("listContent");

        if (mlistFragment == null) {

            mlistFragment = new WebsiteListFragment();
            Bundle layoutBundle = new Bundle();

            layoutBundle.putBoolean("mTwoPane", mTwoPane);
            layoutBundle.putString("npId", npId);
            layoutBundle.putString("npName", npName);
            layoutBundle.putString("catId", catId);
            layoutBundle.putInt("color", currentColor);

            mlistFragment.setArguments(layoutBundle);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.website_list_container, mlistFragment, "listContent").commit();

        }

        articleLoadingCallback = new ArticleLoadingCallback(this);

        // Make Navigation Drawer
        makeNavDrawer();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Activity saving savedInstanceState");
        outState.putString("npName", npName);
        outState.putString("catId", catId);
        outState.putInt("color", currentColor);
        outState.putString("catName", catName);

    }

    @Override
    public void onItemSelected(String headlineText, CustomAdapter customAdapter) {


        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString("npName", npName);
            arguments.putString("catName", catName);
            arguments.putString(WebsiteDetailFragment.HEADLINE_CLICKED, headlineText);
            WebsiteDetailFragment detailFragment = new WebsiteDetailFragment();
            detailFragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.website_detail_container, detailFragment, "detail").commit();
        } else {
            Intent detailIntent = new Intent(this, WebsiteDetailActivity.class);
            detailIntent.putExtra("npName", npName);
            detailIntent.putExtra("catName", catName);
            detailIntent.putExtra(WebsiteDetailFragment.HEADLINE_CLICKED, headlineText);
            detailIntent.putExtra("ActionBarColor", currentColor);
            detailIntent.putExtra("ActionBarTitle", npName + " - " + catName);
            startActivity(detailIntent);

        }
    }
    /****************************/
    /***** CALLBACK METHODS *****/
    /**
     * ************************
     */

    @Override
    public void onPreExecute(View rootView) {
        articleLoadingCallback.onPreExecute(rootView);
    }

    @Override
    public void setHeaderStub(View headerStub) {
        articleLoadingCallback.setHeaderStub(headerStub);
    }

    @Override
    public void onPostExecute(String[] result, String mArticlePubDate) {
        articleLoadingCallback.onPostExecute(result, mArticlePubDate);
    }

    @Override
    public void articleNotFound(String mArticleUrl) {

//        WebViewFragment webViewFragment = (WebViewFragment) getSupportFragmentManager().findFragmentByTag("webViewFragment");
//        if(webViewFragment==null){
            articleLoadingCallback.articleNotFound(mArticleUrl);
//        }
    }


    /**
     * **************************************
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or closeUtilObject the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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
		 * http://www.androidhive.info/2013/11/android-sliding
		 * -menu-using-navigation-drawer/
		 */

        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        mLeftDrawer = (LinearLayout) findViewById(R.id.left_drawer);
        mDrawerList = (ListView) findViewById(R.id.nav_list_slidermenu);
        mDrawerItems = getResources().getStringArray(R.array.nav_drawer_items);

        // set a custom shadow that overlays the main content when the drawer
        // opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        NavDrawerListAdapter navAdapter = new NavDrawerListAdapter(this, mDrawerItems);
        mDrawerList.setAdapter(navAdapter);

        // New NavBar _ OVER

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.drawable.ic_navigation_drawer, /*
										 * nav drawer image to replace 'Up'
										 * caret
										 */
                R.string.drawer_open, /* "open drawer" description for accessibility */
                R.string.drawer_close /* "closeUtilObject drawer" description for accessibility */
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

        // Nav Drawer Home Button listener
        TextView navBarHeaderView = (TextView) findViewById(R.id.nav_bar_header);
        navBarHeaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Nav List listener
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "Drawer onItemClick - " + position + " - " + mDrawerItems[position]);

                mlistFragment = new WebsiteListFragment();
                Bundle layoutBundle = new Bundle();

                catId = String.valueOf(position + 1);
                catName = mDrawerItems[position];
                layoutBundle.putBoolean("mTwoPane", mTwoPane);
                layoutBundle.putString("npId", npId);
                layoutBundle.putString("catId", catId);
                layoutBundle.putInt("color", mActionBarColors[position]);

                mlistFragment.setArguments(layoutBundle);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.website_list_container, mlistFragment, "listContent").commit();

                // update selected item and title, then closeUtilObject the drawer
                mDrawerList.setItemChecked(position, true);
                mDrawerList.setSelection(position);
                setTitle(npName + " - " + mDrawerItems[position]);
                setColor(mActionBarColors[position]);

                Log.d(TAG, position + " - " + mActionBarColors[position]);

                mDrawerLayout.closeDrawer(mLeftDrawer);
            }

        });

        // Nav List Footer options listeners
        LinearLayout navBarSettingsView = (LinearLayout) findViewById(R.id.settings);
        LinearLayout navBarHelpView = (LinearLayout) findViewById(R.id.help);
        LinearLayout navBarSendFeedbackView = (LinearLayout) findViewById(R.id.send_feedback);

        navBarSettingsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "You selected Settings", Toast.LENGTH_SHORT).show();

                Intent settingIntent = new Intent(WebsiteListActivity.this, SettingsActivity.class);
                settingIntent.putExtra("ActionBarColor", currentColor);
                startActivity(settingIntent);
                mDrawerLayout.closeDrawer(mLeftDrawer);
            }
        });

        navBarHelpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "You selected Help", Toast.LENGTH_SHORT).show();
                mDrawerLayout.closeDrawer(mLeftDrawer);
            }
        });

        navBarSendFeedbackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "You selected Send Feedback", Toast.LENGTH_SHORT).show();
                mDrawerLayout.closeDrawer(mLeftDrawer);
            }
        });

    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    public void setColor(int colorId) {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorId));
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        currentColor = colorId;
    }
}
