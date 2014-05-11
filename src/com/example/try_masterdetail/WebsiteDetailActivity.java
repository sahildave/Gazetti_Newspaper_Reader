package com.example.try_masterdetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.try_masterdetailflow.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

public class WebsiteDetailActivity extends FragmentActivity implements WebsiteDetailFragment.TaskCallbacks {
	private static final String TAG = "DFRAGMENT";
	private static final String TAG_ASYNC = "ASYNC";

	private static final String STATE_BODY_TEXT_ = "body_text";
	private static final String STATE_TITLE_TEXT_ = "title_text";
	private static final String STATE_DATE_TEXT_ = "date_text";

	WebsiteDetailFragment mDetailFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "in detail activity class " + getLocalClassName().toString());
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

		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_BODY_TEXT_)) {
			Log.d(TAG, "body text present...");
			mArticleTextView.setText(savedInstanceState.getString(STATE_BODY_TEXT_));
		}
		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_TITLE_TEXT_)) {
			Log.d(TAG, "title text present...");
			mTitleTextView.setText(savedInstanceState.getString(STATE_TITLE_TEXT_));
		}
		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_DATE_TEXT_)) {
			Log.d(TAG, "date text present...");
			mArticlePubDateView.setText(savedInstanceState.getString(STATE_DATE_TEXT_));
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (!bodyText.equals("")) {
			Log.d(TAG, "saving bodyText...");
			outState.putString(STATE_BODY_TEXT_, bodyText);
		}
		if (!titleText.equals("")) {
			Log.d(TAG, "saving titleText...");
			outState.putString(STATE_TITLE_TEXT_, titleText);
		}
		if (!datelineText.equals("")) {
			Log.d(TAG, "saving datelineText...");
			outState.putString(STATE_DATE_TEXT_, datelineText);
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

	/****************************/
	/***** CALLBACK METHODS *****/
	/****************************/

	FrameLayout mHeaderLayout;
	TextView mTitleTextView;
	ImageView mMainImageView;

	RelativeLayout mSubtitleLayout;
	TextView mArticlePubDateView;

	TextView mArticleTextView;
	ImageButton mNewspaperTile;
	TextView mViewInBrowser;

	ImageLoader mImageLoader;

	String mImageURL;
	String mArticleURL;
	String mArticlePubDate;

	String bodyText = "";
	String titleText = "";
	String datelineText = "";

	@Override
	public void onProgressUpdate(String values) {
		Log.d(TAG_ASYNC, "Activity onProgressUpdate");
		bodyText = values + "\n\n";
		mArticleTextView.append(bodyText);
	}

	@Override
	public void onPostExecute(String[] result) {
		Log.d(TAG_ASYNC, "Activity onPostExecute");
		mSubtitleLayout.setVisibility(View.VISIBLE);
		mArticlePubDateView.setText(mArticlePubDate);

		if (bodyText.equals("")) {
			mArticleTextView.setText("Sorry, this story is not supported for mobile view. Try to read in the browser");

		}

		if (mImageURL == null) {
			Log.d(TAG, "Image not, Title : " + titleText);

			View articleTitleStub = ((ViewStub) findViewById(R.id.article_title_stub_import)).inflate();
			mTitleTextView = (TextView) articleTitleStub.findViewById(R.id.article_title);
			mTitleTextView.setText(titleText);
		} else {
			Log.d(TAG, "Loading Image...");

			View articleHeaderStub = ((ViewStub) findViewById(R.id.article_header_stub_import)).inflate();
			mTitleTextView = (TextView) articleHeaderStub.findViewById(R.id.article_header_title);
			mTitleTextView.setText(titleText);

			mMainImageView = (ImageView) articleHeaderStub.findViewById(R.id.mainImage);
			Picasso picassoInstance = Picasso.with(getApplicationContext());
			picassoInstance.setDebugging(true);
			picassoInstance.load(mImageURL).into(mMainImageView);
		}

	}

	@Override
	public void onPreExecute(View rootView) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getHeaderStub(View headerStub) {
		// TODO Auto-generated method stub
		
	}

}
