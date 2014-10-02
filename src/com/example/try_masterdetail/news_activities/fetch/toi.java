package com.example.try_masterdetail.news_activities.fetch;

import com.example.try_masterdetail.util.ConfigService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

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
	}

	public String[] getToiArticle() {

		Document doc;
		String[] result = new String[4];
		String url = mArticleURL;

		try {
			doc = Jsoup.connect(url) //
					.userAgent("Mozilla") //
					.timeout(10 * 1000) //
					.get(); //

			// get Title
			String ToiTitleXPath = ConfigService.getTOIHead();
			titleText = doc.select(ToiTitleXPath).text();

			// get HeaderImageUrl
			mImageURL = getImageURL(doc);

			String ToiArticleXPath = ConfigService.getTOIBody();
			Element bodyArticleElements = doc.select(ToiArticleXPath).first();
			String temp = bodyArticleElements.html().replace("<br />", "$$$");
			Document bodyNewLine = Jsoup.parse(temp);
			bodyText = bodyNewLine.body().text().replace("$$$", "\n");

            result[0] = titleText;
            result[1] = mImageURL;
            result[2] = bodyText;
            result[3] = mArticlePubDate;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException npe){
            npe.printStackTrace();
            bodyText = null;
        } catch (Exception e){
            e.printStackTrace();
        }

		return result;

	}

	private String getImageURL(Element doc) {

        Elements mainImageElement = doc.select(ConfigService.getTOIImageFirst());
        Elements carouselElements = doc.select(ConfigService.getTOIImageSecond());

        if (mainImageElement.size() != 0) {
			mImageURL = mainImageElement.first().attr("src");
		} else if (carouselElements.size() != 0) {
			mImageURL = carouselElements.first().attr("src");
		}

		return mImageURL;

	}
}
