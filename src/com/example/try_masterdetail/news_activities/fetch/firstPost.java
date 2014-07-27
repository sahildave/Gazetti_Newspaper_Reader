package com.example.try_masterdetail.news_activities.fetch;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

public class firstPost {

	String mArticleURL;
	String titleText;
	String mImageURL = null;
	String bodyText = "";
	String mArticlePubDate;

	public firstPost(String mArticleURL, String mArticlePubDate) {
		this.mArticleURL = mArticleURL;
		this.mArticlePubDate = mArticlePubDate;
		Log.d("ASYNC", "JSoup FirstPost " + mArticleURL);
	}

	public String[] getFirstPostArticle() {

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

			// NEW!
			// get Body
			Element bodyElement = doc.body();
			Log.d("ASYNC", "Jsoup Body - " + (bodyElement == null));

			// get Title
			String FirstPostTitleXPath = "h1"; // ("//*[@id="single"]/h1")
			Elements titleElements = bodyElement.select(FirstPostTitleXPath);
			titleText = titleElements.first().text();
			Log.d("ASYNC", "Jsoup title - " + titleText);

			// get HeaderImageUrl
			mImageURL = getImageURL(bodyElement);
			Log.d("ASYNC", "Jsoup imageURL - " + mImageURL);

			// get p elements with class = stry_cont
			String FirstPostArticleXPath = ".stry_cont"; // (//*[@id="article-block"]/div/p[1])
			Element bodyArticleElements = doc.select(FirstPostArticleXPath).first();

			Elements mainImageElement = bodyElement.select("div.stry_cont img");
			String imgSubtitleText = mainImageElement.first().attr("alt");

			String temp = bodyArticleElements.text();
			temp = bodyArticleElements.html().replace("</p>", "$$$");
			temp = temp.replace(imgSubtitleText, "&&&");
			Document bodyNewLine = Jsoup.parse(temp);
			String bodyTextString = bodyNewLine.body().text().replace("$$$", "\n\n").toString();
			bodyText = bodyTextString.replace("&&&", "\b\b\b\b");

			long done = System.currentTimeMillis();
			Log.d("ASYNC", "Connected IN  - " + (connected - connecting));
			Log.d("ASYNC", "DONE IN  - " + (done - connected));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result[0] = titleText;
		result[1] = mImageURL;
		result[2] = bodyText;
		result[3] = mArticlePubDate;

		return result;

	}

	private String getImageURL(Element bodyElement) {
		if (bodyElement.select("div.stry_cont img").size() != 0) {
			// get image elements with class = main-image
			// Log.d(TAG, "in main-image");
			Elements mainImageElement = bodyElement.select("div.stry_cont img");
			mImageURL = mainImageElement.first().attr("src");
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
}
