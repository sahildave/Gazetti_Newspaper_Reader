package com.example.try_masterdetail;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.try_masterdetail.adapter.CustomAdapter;
import com.example.try_masterdetailflow.R;

public class WebsiteDetailFragment extends Fragment {
	private static final String TAG = "DFRAGMENT";
	private static final String TAG_ASYNC = "ASYNC";
	public static final String articleLink = "articleLink_key";

	private static final int SECOND_MILLIS = 1000;
	private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
	private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
	private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

	// FrameLayout mHeaderLayout;
	// TextView mTitleTextView;
	// ImageView mMainImageView;
	//
	RelativeLayout mSubtitleLayout;
	TextView mArticlePubDateView;
	//
	TextView mArticleTextView;

	ImageButton mNewspaperTile;
	TextView mViewInBrowser;
	//
	// ImageLoader mImageLoader;
	//
	String mImageURL;
	String mArticleURL;
	String mArticlePubDate;
	//
	// String bodyText = "";
	String titleText = "";
	String datelineText = "";

	private TaskCallbacks mCallbacks;
	private MyAsyncTask mTask;

	private boolean firstRun = false;

	static interface TaskCallbacks {
		void onPreExecute(View rootView);

		void onProgressUpdate(String values);

		void getHeaderStub(View headerStub);

		void onPostExecute(String[] result);
	}

	public WebsiteDetailFragment() {
		Log.d(TAG, "DetailFragment constructor");
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof TaskCallbacks)) {
			throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
		}
		Log.d(TAG, "DetailFragment onAttach");
		mCallbacks = (TaskCallbacks) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true);

		if (getArguments().containsKey(articleLink)) {
			mArticleURL = CustomAdapter.linkMap.get(getArguments().getString(articleLink));
		}

		if (getArguments().containsKey(articleLink)) {
			mArticlePubDate = CustomAdapter.pubDateMap.get(getArguments().getString(articleLink));
		}

		firstRun = true;

	}

	private void callAsync(View rootView) {
		Log.d(TAG_ASYNC, "Async called");
		mTask = new MyAsyncTask(rootView);
		mTask.execute();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "DetailFragment onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_website_detail, container, false);

		if (firstRun) {
			callAsync(rootView);
		}

		mArticleTextView = (TextView) rootView.findViewById(R.id.body);
		mArticlePubDateView = (TextView) rootView.findViewById(R.id.pubDateView);
		mSubtitleLayout = (RelativeLayout) rootView.findViewById(R.id.subtitleLayout);

		mNewspaperTile = (ImageButton) rootView.findViewById(R.id.newspaperTile);
		mViewInBrowser = (TextView) rootView.findViewById(R.id.viewInBrowser);

		mNewspaperTile.setOnTouchListener(webViewCalled); // TODO: add onClick
															// in xml
		mViewInBrowser.setOnTouchListener(webViewCalled);
		return rootView;
	}

	@Override
	public void onDetach() {
		Log.d(TAG, "DetailFragment onDetach");
		super.onDetach();
		mCallbacks = null;
	}

	private OnTouchListener webViewCalled = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Log.d(TAG, "Touched Item " + v);

			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mArticleURL)));
			return true;
		}
	};

	public class MyAsyncTask extends AsyncTask<Void, String, String[]> {

		View rootView;

		public MyAsyncTask(View rootView) {
			this.rootView = rootView;
		}

		@Override
		protected void onPreExecute() {

			if (mCallbacks != null) {
				mCallbacks.onPreExecute(rootView);
			}
		}

		@Override
		protected String[] doInBackground(Void... params) {
			Document doc;
			String[] result = new String[3];
			try {

				String url = mArticleURL;

				doc = Jsoup.connect(url).get();

				// get Body
				Element bodyElement = doc.body();

				// get page title
				Elements titleElements = bodyElement.select(".detail-title");
				titleText = titleElements.text();

				// get HeaderImageUrl
				mImageURL = getImageURL(bodyElement);

				// get p elements with class = body
				Elements bodyArticleElements = bodyElement.select("p[class=body]");
				for (Element textArticleElement : bodyArticleElements) {
					publishProgress(textArticleElement.text());
				}

				// // get dateline
				// Elements datelineElements = bodyElement.select(".dateline");
				// datelineText = datelineElements.text();

				result[0] = titleText;
				result[1] = mImageURL;
				result[2] = mArticlePubDate;

			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}

		private String getImageURL(Element bodyElement) {
			Log.d(TAG, "in getImageURL");
			if (bodyElement.select(".main-image").size() != 0) {
				// get image elements with class = main-image
				// Log.d(TAG, "in main-image");
				Elements mainImageElement = bodyElement.select(".main-image");
				mImageURL = mainImageElement.attr("src");
				// Log.d(TAG, "ImageUrl - " + mImageURL);
			} else if (bodyElement.select("div#contartcarousel").size() != 0) {
				// get image elements with carousel
				// Log.d(TAG, "in contartcarousel ");
				Elements carouselElements = bodyElement.select("div#contartcarousel");
				Elements carouselImage = carouselElements.select("div#pic").first().select("img");
				mImageURL = carouselImage.attr("src");
				// Log.d(TAG, mImageURL);
			} else {
				// Log.d(TAG, "IMAGE NOT FOUND!");

			}

			return mImageURL;

		}

		@Override
		protected void onProgressUpdate(String... values) {
			if (mCallbacks != null && values[0].length() > 0) {
				mCallbacks.onProgressUpdate(values[0]);
			}

		}

		@Override
		protected void onPostExecute(String[] result) {

			if (mCallbacks != null) {
				View headerStub;
				if (mImageURL == null) {
					headerStub = ((ViewStub) rootView.findViewById(R.id.article_title_stub_import)).inflate();
				} else {
					headerStub = ((ViewStub) rootView.findViewById(R.id.article_header_stub_import)).inflate();
				}
				mCallbacks.getHeaderStub(headerStub);
				mCallbacks.onPostExecute(result);
			}
		}
	}

}
