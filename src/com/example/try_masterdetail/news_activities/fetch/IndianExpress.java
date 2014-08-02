package com.example.try_masterdetail.news_activities.fetch;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

public class IndianExpress {

	String mArticleURL;
	String titleText;
	String mImageURL = null;
	String bodyText = "";
	String mArticlePubDate;

	public IndianExpress(String mArticleURL, String mArticlePubDate) {
		this.mArticleURL = mArticleURL;
		this.mArticlePubDate = mArticlePubDate;
		Log.d("ASYNC", "JSoup FirstPost " + mArticleURL);
	}

	public String[] getTIEArticle() {

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

			// get page title
			Elements titleElements = bodyElement.select("h1");
			titleText = titleElements.first().text();

			// get HeaderImageUrl
			mImageURL = getImageURL(bodyElement);

			bodyText = getBody(bodyElement);

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

		if (bodyElement.select("div.img > img").size() != 0) {
			// get image elements with class = main-image
			// Log.d(TAG, "in main-image");
			Elements mainImageElement = bodyElement.select("div.img > img");
			mImageURL = mainImageElement.first().attr("src");
			// Log.d(TAG, "ImageUrl - " + mImageURL);
		} else if (bodyElement.select(".main-gallery").size() != 0) {
			// get image elements with carousel
			// Log.d(TAG, "in contartcarousel ");
			Element carouselImage = bodyElement.select(".main-gallery img").first();
			mImageURL = carouselImage.attr("src");
			// Log.d(TAG, mImageURL);
		}

		return mImageURL;

	}

	private String getBody(Element bodyElement) {

		String tempHtml = "";
		Elements styTxtToSkip = bodyElement.select("div.section-stories img");

		if (bodyElement.select("div.section-stories").size() != 0) {

			for (Element replaceElement : styTxtToSkip) {
				replaceElement.remove();
			}

			String ArticleXPath = "div.section-stories";
			Element bodyArticleElements = bodyElement.select(ArticleXPath).first();
			tempHtml = bodyArticleElements.html();
		}
		return tempHtml;
	}
}
