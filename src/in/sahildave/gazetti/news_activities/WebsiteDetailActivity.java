package in.sahildave.gazetti.news_activities;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import in.sahildave.gazetti.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class WebsiteDetailActivity extends ActionBarActivity implements WebsiteDetailFragment.TaskCallbacks {
    private static final String TAG = "DFRAGMENT";
    private static final String TAG_ASYNC = "ASYNC";

    private static final String STATE_BODY_TEXT_ = "body_text";
    private static final String STATE_TITLE_TEXT_ = "title_text";

    WebsiteDetailFragment mDetailFragment;

    // CallBacks for handling asynctask for twoPane mode

    View rootView; // ScrollView from DetailFragment
    View headerStub;

    // Header
    ProgressBar detailViewProgress;
    TextView mTitleTextView;
    ImageView mMainImageView;
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

    LinearLayout mScrollToReadLayout;
    private boolean displayScrollToRead = false;
    private Animation slide_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website_detail);

        int ActionBarColorId = getIntent().getIntExtra("ActionBarColor", -1);
        String ActionBarTitleString = getIntent().getStringExtra("ActionBarTitle");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(ActionBarTitleString);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ActionBarColorId));
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mDetailFragment = (WebsiteDetailFragment) getSupportFragmentManager().findFragmentByTag("detail");

        if (mDetailFragment == null) {
            mDetailFragment = new WebsiteDetailFragment();

            Bundle arguments = new Bundle();
            arguments.putString("npName", getIntent().getStringExtra("npName"));
            arguments.putString(WebsiteDetailFragment.articleLinkKey,
                    getIntent().getStringExtra(WebsiteDetailFragment.articleLinkKey));
            mDetailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.website_detail_container, mDetailFragment, "detail").commit();
        }

        slide_up = AnimationUtils.loadAnimation(this, R.animator.slide_up);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Log.d("TABLE", "Clicked home");

            // TYPE 1
            // NavUtils.navigateUpTo(this, new Intent(this,
            // WebsiteListActivity.class));

            // TYPE 2
            // Intent intent = new Intent(WebsiteDetailActivity.this,
            // WebsiteListActivity.class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            // finish();
            // startActivity(intent);

            // TYPE 3
            // NavUtils.navigateUpFromSameTask(this);
            onBackPressed();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /****************************/
    /***** CALLBACK METHODS *****/
    /**
     * ************************
     */

    @Override
    public void onPreExecute(View rootView) {
        this.rootView = rootView;

        // initialize article views
        mArticleTextView = (TextView) rootView.findViewById(R.id.article_body);
        mArticlePubDateView = (TextView) rootView.findViewById(R.id.pubDateView);
        mSubtitleLayout = (RelativeLayout) rootView.findViewById(R.id.subtitleLayout);
        mScrollToReadLayout = (LinearLayout) rootView.findViewById(R.id.scrollToReadLayout);

        // Progress Bar
        detailViewProgress = (ProgressBar) rootView.findViewById(R.id.detailViewProgressBar);
        detailViewProgress.setVisibility(View.VISIBLE);
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
        bodyText = result[2];
        mArticlePubDate = result[3];

        mSubtitleLayout.setVisibility(View.VISIBLE);
        mArticlePubDateView.setText(mArticlePubDate);
        mArticleTextView.setVisibility(View.VISIBLE);
        mArticleTextView.setText(bodyText);

        if (mImageURL == null) {
            Log.d(TAG, "Image not, Title : " + titleText);

            mTitleTextView = (TextView) headerStub.findViewById(R.id.article_title);
            mTitleTextView.setText(titleText);
            detailViewProgress.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "Loading Image...");

            mTitleTextView = (TextView) headerStub.findViewById(R.id.article_header_title);
            mTitleTextView.setText(titleText);

            mMainImageView = (ImageView) headerStub.findViewById(R.id.article_header_image);
            Picasso picassoInstance = Picasso.with(this);
            // picassoInstance.setDebugging(true); //TODO:Turn on for debugging
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

}
