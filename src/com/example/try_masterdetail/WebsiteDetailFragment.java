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
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.try_masterdetail.adapter.CustomAdapter;
import com.example.try_masterdetailflow.R;

public class WebsiteDetailFragment extends Fragment {
	private static final String TAG = "DFRAGMENT";
	private static final String TAG_ASYNC = "ASYNC";
	public static final String articleLinkKey = "articleLink_key";

	private ImageButton mNewspaperTile;
	private TextView mViewInBrowser;
	private String mImageURL;
	private String mArticleURL;
	private String mArticlePubDate;
	private String titleText = "";

	private TaskCallbacks mCallbacks;
	private MyAsyncTask mTask;

	private boolean firstRun = false;
	private LinearLayout mScrollToReadLayout;

//	private GestureDetectorCompat mDetector;
	private ScrollView mScrollView;
	private Animation slide_down;

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

		if (getArguments().containsKey(articleLinkKey)) {
			mArticleURL = CustomAdapter.linkMap.get(getArguments().getString(articleLinkKey));
		}

		if (getArguments().containsKey(articleLinkKey)) {
			mArticlePubDate = CustomAdapter.pubDateMap.get(getArguments().getString(articleLinkKey));
		}
//		mDetector = new GestureDetectorCompat(getActivity(), new MyGestureListener());
		slide_down = AnimationUtils.loadAnimation(getActivity(), R.animator.slide_down);
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

		mNewspaperTile = (ImageButton) rootView.findViewById(R.id.newspaperTile);
		mViewInBrowser = (TextView) rootView.findViewById(R.id.viewInBrowser);

		mNewspaperTile.setOnClickListener(webViewCalled);
		mViewInBrowser.setOnClickListener(webViewCalled);

		mScrollToReadLayout = (LinearLayout) rootView.findViewById(R.id.scrollToReadLayout);
		mScrollToReadLayout.setVisibility(View.INVISIBLE);
		Log.d(TAG_ASYNC, "Fragment Visiblity - " + mScrollToReadLayout.getVisibility());

		mScrollView = (ScrollView) rootView.findViewById(R.id.scroller);

		mScrollView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					Log.d(TAG_ASYNC, "ScrollView ACTION_UP");
					if (mScrollToReadLayout.getVisibility() == View.VISIBLE) {
						mScrollToReadLayout.startAnimation(slide_down);
						mScrollToReadLayout.setVisibility(View.INVISIBLE);
					}
					return true;
				}
				return false;
			}
		});

		return rootView;
	}

	@Override
	public void onDetach() {
		Log.d(TAG, "DetailFragment onDetach");
		super.onDetach();
		mCallbacks = null;
	}

	private OnClickListener webViewCalled = new OnClickListener() {

		@Override
		public void onClick(View v) {
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mArticleURL)));

		}
	};

	public class MyAsyncTask extends AsyncTask<Void, String, String[]> {

		View rootView;

		public MyAsyncTask(View rootView) {
			Log.d(TAG, "Async Constructor");
			this.rootView = rootView;
		}

		@Override
		protected void onPreExecute() {
			Log.d(TAG, "Async onPreExecute");

			if (mCallbacks != null) {
				mCallbacks.onPreExecute(rootView);
			}

		}

		@Override
		protected String[] doInBackground(Void... params) {
			Log.d(TAG, "Async doInBackground");
			Document doc;
			String[] result = new String[3];
			try {

				String url = mArticleURL;

				doc = Jsoup.connect(url).timeout(10 * 1000).get();
				long connected = System.currentTimeMillis();
				Log.d("JSOUP", "Jsoup connected - " + System.currentTimeMillis());
				// get Body
				Element bodyElement = doc.body();
				Log.d("JSOUP", "Jsoup bodyElement - " + System.currentTimeMillis());
				// get page title
				Elements titleElements = bodyElement.select(".detail-title");
				titleText = titleElements.text();
				Log.d("JSOUP", "Jsoup titleElements - " + System.currentTimeMillis());

				// get HeaderImageUrl
				mImageURL = getImageURL(bodyElement);
				Log.d("JSOUP", "Jsoup getImageURL - " + System.currentTimeMillis());

				// get p elements with class = body
				Elements bodyArticleElements = bodyElement.select("p[class=body]");
				for (Element textArticleElement : bodyArticleElements) {
					publishProgress(textArticleElement.text());
				}
				Log.d("JSOUP", "Jsoup bodyArticleElements - " + System.currentTimeMillis());
				long done = System.currentTimeMillis();
				Log.d("JSOUP", "DONE IN  - " + (done - connected));

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
			Log.d(TAG, "Async onProgressUpdate");
			if (mCallbacks != null && values[0].length() > 0) {
				mCallbacks.onProgressUpdate(values[0]);
			}

		}

		@Override
		protected void onPostExecute(String[] result) {
			Log.d(TAG, "Async onPostExecute");

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

	// private class MyGestureListener extends
	// GestureDetector.SimpleOnGestureListener {
	//
	// @Override
	// public boolean onDown(MotionEvent event) {
	// Log.d(TAG_ASYNC, "ONDOWN");
	// if (mScrollToReadLayout.getVisibility() == View.VISIBLE) {
	// mScrollToReadLayout.startAnimation(slide_down);
	// mScrollToReadLayout.setVisibility(View.INVISIBLE);
	// }
	// return true;
	// }
	//
	// @Override
	// public boolean onFling(MotionEvent event1, MotionEvent event2, float
	// velocityX, float velocityY) {
	// Log.d(TAG_ASYNC, "ONFLING");
	// if (mScrollToReadLayout.getVisibility() == View.VISIBLE) {
	// mScrollToReadLayout.startAnimation(slide_down);
	// mScrollToReadLayout.setVisibility(View.INVISIBLE);
	// }
	// return super.onFling(event1, event2, velocityX, velocityY);
	// }
	// }
}
