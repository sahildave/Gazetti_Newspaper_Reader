package com.example.try_masterdetail.news_activities.fetch;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

public class toi {
	final String TAG_ASYNC = "ASYNC";

	String mArticleURL;
	String titleText;
	String mImageURL = null;
	String bodyText = "";
	String mArticlePubDate;

	public toi(String mArticleURL, String mArticlePubDate) {
		this.mArticleURL = mArticleURL;
		this.mArticlePubDate = mArticlePubDate;
		Log.d("ASYNC", "JSoup toi " + mArticleURL);
	}

	public String[] getToiArticle() {

		Document doc;
		String[] result = new String[4];
		String url = mArticleURL;

		try {

			long connecting = System.currentTimeMillis();
			doc = Jsoup.connect(url) //
					.userAgent("Mozilla") //
					.timeout(10 * 1000) //
					.get(); //
			long connected = System.currentTimeMillis();

			// get Title
			String ToiTitleXPath = "span.arttle > h1"; // ()
			titleText = doc.select(ToiTitleXPath).text();

			// get HeaderImageUrl
			mImageURL = getImageURL(doc);

			// get p elements with class = Normal
			String ToiArticleXPath = ".Normal"; // (//*[@id="article-block"]/div/p[1])
			Element bodyArticleElements = doc.select(ToiArticleXPath).first();
			String temp = bodyArticleElements.text();
			// try {
			temp = bodyArticleElements.html().replace("<br />", "$$$");
			Document bodyNewLine = Jsoup.parse(temp);
			bodyText = bodyNewLine.body().text().replace("$$$", "\n").toString();
			// } catch (NullPointerException npe) {
			// bodyText = bodyArticleElements.text();
			// }

			Log.d(TAG_ASYNC, "bodyText is NULL? " + (bodyText == null));

			long done = System.currentTimeMillis();
			Log.d(TAG_ASYNC, "Connected IN  - " + (connected - connecting));
			Log.d(TAG_ASYNC, "DONE IN  - " + (done - connected));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException npe) {
			bodyText = null;
		}
		result[0] = titleText;
		result[1] = mImageURL;
		result[2] = bodyText;
		result[3] = mArticlePubDate;

		return result;

	}

	private String getImageURL(Element doc) {
		if (doc.select("div.flL_pos > img").size() != 0) {

			// get image elements with class = main-image
			// (//*[@id="hcenter"]/img)
			Elements mainImageElement = doc.select("div.flL_pos > img");
			mImageURL = mainImageElement.first().attr("src");
			// Log.d(TAG, "ImageUrl - " + mImageURL);
		} else if (doc.select("#articleimg1").size() != 0) {

			// get image elements on right
			// (//*[@id="articleimg1"])
			Elements carouselElements = doc.select("#articleimg1");
			mImageURL = carouselElements.first().attr("src");
			// Log.d(TAG, mImageURL);
		} else {
			mImageURL = null;
			// Log.d(TAG, "IMAGE NOT FOUND!");

		}

		return mImageURL;

	}
}
