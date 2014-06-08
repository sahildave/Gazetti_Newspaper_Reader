package com.example.try_masterdetail.news_activities.fetch;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

public class hindu {

	String mArticleURL;
	String titleText;
	String mImageURL = null;
	String bodyText = "";
	String mArticlePubDate;

	public hindu(String mArticleURL, String mArticlePubDate) {
		this.mArticleURL = mArticleURL;
		this.mArticlePubDate = mArticlePubDate;
		Log.d("ASYNC", "JSoup hindu " + mArticleURL);
	}

	public String[] getHinduArticle() {

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
			String HinduTitleXPath = "h1"; // ("//*[@id="left-column"]/h1")
			Elements titleElements = bodyElement.select(HinduTitleXPath);
			titleText = titleElements.first().text();
			Log.d("ASYNC", "Jsoup title - " + titleText);

			// get HeaderImageUrl
			mImageURL = getImageURL(bodyElement);
			Log.d("ASYNC", "Jsoup imageURL - " + mImageURL);

			// get p elements with class = body
			String HinduArticleXPath = "p.body"; // (//*[@id="article-block"]/div/p[1])
			Elements bodyArticleElements = bodyElement.select(HinduArticleXPath);
			for (Element textArticleElement : bodyArticleElements) {
				bodyText += textArticleElement.text() + "\n\n";
			}
			Log.d("ASYNC", "Jsoup body " + bodyText);

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
		if (bodyElement.select("img.main-image").size() != 0) {

			// get image elements with class = main-image
			// Log.d(TAG, "in main-image");
			// (//*[@id="hcenter"]/img)
			Elements mainImageElement = bodyElement.select("img.main-image");
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
