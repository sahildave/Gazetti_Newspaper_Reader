package com.example.try_masterdetail;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.try_masterdetail.adapter.CustomAdapter;
import com.example.try_masterdetailflow.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class WebsiteDetailFragment extends Fragment {
	private static final String TAG = "MasterDetail";
	public static final String ARG_ITEM_ID = "item_id";

	FrameLayout mHeaderLayout;
	TextView mTitleTextView;
	ImageView mMainImageView;
	TextView mArticleTextView;

	ImageLoader mImageLoader;
	String mArticleURL;
	String mImageURL;

	public WebsiteDetailFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "in Detail onCreateView");

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			mArticleURL = CustomAdapter.mMap.get(getArguments().getString(ARG_ITEM_ID));
		}

		View rootView = inflater.inflate(R.layout.fragment_website_detail, container, false);
		mHeaderLayout = (FrameLayout) rootView.findViewById(R.id.headerFrameLayout);
		mTitleTextView = (TextView) rootView.findViewById(R.id.title);
		mArticleTextView = (TextView) rootView.findViewById(R.id.body);
		mMainImageView = (ImageView) rootView.findViewById(R.id.mainImage);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "in Detail onActivityCreated");

		new Myasynctask().execute();

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

		private String getImageURL(Element bodyElement) {

			if (bodyElement.select(".main-image").size() != 0) {
				// get image elements with class = main-image
				Log.d(TAG, "in main-image");
				Elements mainImageElement = bodyElement.select(".main-image");
				mImageURL = mainImageElement.attr("src");
				Log.d(TAG, "ImageUrl - " + mImageURL);
			} else if (bodyElement.select("div#contartcarousel").size() != 0) {
				// get image elements with carousel
				Log.d(TAG, "in contartcarousel ");
				Elements carouselElements = bodyElement.select("div#contartcarousel");
				Elements carouselImage = carouselElements.select("div#pic").first().select("img");
				mImageURL = carouselImage.attr("src");
				Log.d(TAG, mImageURL);
			} else {
				Log.d(TAG, "IMAGE NOT FOUND!");

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

			mTitleTextView.setText(titleText);

			if (bodyText.equals("")) {
				mArticleTextView
						.setText("Sorry, this story is not supported for mobile view. Try to read in the browser");

			}

			if (mImageURL == null) {
				Log.d(TAG, "Image not, Title : " + titleText);
				mMainImageView.setVisibility(View.GONE);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mTitleTextView.getWidth(),
						mTitleTextView.getHeight());
				Log.d(TAG, mTitleTextView.getWidth() + ", " + mTitleTextView.getHeight());
				mHeaderLayout.setLayoutParams(params);

			} else {
				Log.d(TAG, "Loading Image...");
				mImageLoader = ImageLoader.getInstance();
				mImageLoader.displayImage(mImageURL, mMainImageView);
				mMainImageView.setVisibility(0);
			}
		}
	}

}
