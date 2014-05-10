package com.example.try_masterdetail;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.try_masterdetail.adapter.CustomAdapter;
import com.example.try_masterdetailflow.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

public class WebsiteDetailFragment extends Fragment {
	private static final String TAG = "MasterDetail";
	public static final String articleLink = "articleLink_key";

	private static final int SECOND_MILLIS = 1000;
	private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
	private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
	private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

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

	public WebsiteDetailFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "in Detail onCreateView");

		if (getArguments().containsKey(articleLink)) {
			mArticleURL = CustomAdapter.linkMap.get(getArguments().getString(articleLink));
		}

		if (getArguments().containsKey(articleLink)) {
			mArticlePubDate = CustomAdapter.pubDateMap.get(getArguments().getString(articleLink));
			Log.d(TAG, "Got date " + mArticlePubDate);
		}
		View rootView = inflater.inflate(R.layout.fragment_website_detail, container, false);
		mArticleTextView = (TextView) rootView.findViewById(R.id.body);
		mArticlePubDateView = (TextView) rootView.findViewById(R.id.pubDateView);
		mSubtitleLayout = (RelativeLayout) rootView.findViewById(R.id.subtitleLayout);

		mNewspaperTile = (ImageButton) rootView.findViewById(R.id.newspaperTile);
		mViewInBrowser = (TextView) rootView.findViewById(R.id.viewInBrowser);

		mNewspaperTile.setOnTouchListener(webViewCalled);
		mViewInBrowser.setOnTouchListener(webViewCalled);

		return rootView;
	}

	private OnTouchListener webViewCalled = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			Log.d(TAG, "Touched Item " + v);

			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mArticleURL)));

			return true;
		}
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "in Detail onActivityCreated");

		new Myasynctask().execute();

	}

	public void viewInBrowser(View v) {
		Log.d(TAG, "you clicked!");
	}

	private class Myasynctask extends AsyncTask<Void, String, Void> {

		String bodyText = "";
		String titleText;
		String datelineText;

		@Override
		protected Void doInBackground(Void... params) {
			Document doc;

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

				// get dateline
				Elements datelineElements = bodyElement.select(".dateline");
				datelineText = datelineElements.text();

			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		public String getTimeAgo(long time) {
			if (time < 1000000000000L) {
				// if timestamp given in seconds, convert to millis
				time *= 1000;
			}

			long now = System.currentTimeMillis();
			if (time > now || time <= 0) {
				return null;
			}

			// TODO: localize
			final long diff = now - time;
			if (diff < MINUTE_MILLIS) {
				return "just now";
			} else if (diff < 2 * MINUTE_MILLIS) {
				return "a minute ago";
			} else if (diff < 50 * MINUTE_MILLIS) {
				return diff / MINUTE_MILLIS + " minutes ago";
			} else if (diff < 90 * MINUTE_MILLIS) {
				return "an hour ago";
			} else if (diff < 24 * HOUR_MILLIS) {
				return diff / HOUR_MILLIS + " hours ago";
			} else if (diff < 48 * HOUR_MILLIS) {
				return "yesterday";
			} else {
				return diff / DAY_MILLIS + " days ago";
			}
		}

		private String getImageURL(Element bodyElement) {

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
			super.onProgressUpdate(values);

			if (values[0].length() > 0) {
				bodyText = values[0] + "\n\n";
				mArticleTextView.append(bodyText);
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			mSubtitleLayout.setVisibility(View.VISIBLE);
			mArticlePubDateView.setText(mArticlePubDate);

			if (bodyText.equals("")) {
				mArticleTextView
						.setText("Sorry, this story is not supported for mobile view. Try to read in the browser");

			}

			if (mImageURL == null) {
				Log.d(TAG, "Image not, Title : " + titleText);

				View articleTitleStub = ((ViewStub) getActivity().findViewById(R.id.article_title_stub_import))
						.inflate();
				mTitleTextView = (TextView) articleTitleStub.findViewById(R.id.article_title);
				mTitleTextView.setText(titleText);
			} else {
				Log.d(TAG, "Loading Image...");

				View articleHeaderStub = ((ViewStub) getActivity().findViewById(R.id.article_header_stub_import))
						.inflate();
				mTitleTextView = (TextView) articleHeaderStub.findViewById(R.id.article_header_title);
				mTitleTextView.setText(titleText);

				mMainImageView = (ImageView) articleHeaderStub.findViewById(R.id.mainImage);
				Picasso picassoInstance = Picasso.with(getActivity().getApplicationContext());
				picassoInstance.setDebugging(true);
				picassoInstance.load(mImageURL).into(mMainImageView);
			}
		}
	}

}
