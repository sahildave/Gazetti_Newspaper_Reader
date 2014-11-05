package in.sahildave.gazetti.news_activities.fetch;

import android.util.Log;
import com.crashlytics.android.Crashlytics;
import in.sahildave.gazetti.util.ConfigService;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class toi {
    final String LOG_TAG = this.getClass().getName();

    String mArticleURL;
    String titleText;
    String mImageURL = null;
    String bodyText = "";

    public toi(String mArticleURL) {
        this.mArticleURL = mArticleURL;
    }

    public String[] getToiArticleContent() {

        Document doc;
        String[] result = new String[3];
        String url = mArticleURL;

        try {

            Connection connection = Jsoup.connect(url).userAgent("Mozilla").timeout(10 * 1000);
            Response response = connection.execute();

            if(response==null){
                Crashlytics.log(Log.ERROR, LOG_TAG, "Is response null ? "+(null==response));
                return null;
            } else if(response.statusCode() !=200){
                Crashlytics.log(Log.INFO, LOG_TAG, "Received response - "+response.statusCode()+" -- "+response.statusMessage());
                Crashlytics.log(Log.INFO, LOG_TAG, "Received response - "+response.body());
                return null;
            }

            doc = connection.get();

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

        } catch (IOException e) {
            Crashlytics.logException(e);
        } catch (NullPointerException npe) {
            bodyText = null;
            Crashlytics.logException(npe);
        } catch (Exception e) {
            Crashlytics.logException(e);
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
