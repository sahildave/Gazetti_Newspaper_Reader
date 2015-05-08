package in.sahildave.gazetti.news_activities.fetch;

import com.crashlytics.android.Crashlytics;
import in.sahildave.gazetti.util.ConfigService;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class hindu {
    final String LOG_TAG = this.getClass().getName();

    String mArticleURL;
    String titleText;
    String mImageURL = null;
    String bodyText = "";

    public hindu(String mArticleURL) {
        this.mArticleURL = mArticleURL;
    }

    public String[] getHinduArticleContent() {

        Document doc;
        String[] result = new String[3];
        String url = mArticleURL;

        try {

            Connection connection = Jsoup.connect(url).userAgent("Mozilla").timeout(10 * 1000);
            Response response = connection.execute();

            if(response==null){
                Crashlytics.log("Is response null ? "+(null==response));
                return null;
            } else if(response.statusCode() !=200){
                Crashlytics.log("Received response - "+response.statusCode()+" -- "+response.statusMessage());
                Crashlytics.log("Received response - "+response.body());
                return null;
            }

            doc = connection.get();

            // get Body
            Element bodyElement = doc.body();

            // get Title
            String HinduTitleXPath = ConfigService.getInstance().getTheHinduHead();
            Elements titleElements = bodyElement.select(HinduTitleXPath);
            titleText = titleElements.first().text();

            // get HeaderImageUrl
            mImageURL = getImageURL(bodyElement);

            String HinduArticleXPath = ConfigService.getInstance().getTheHinduBody();
            Elements bodyArticleElements = bodyElement.select(HinduArticleXPath);
            for (Element textArticleElement : bodyArticleElements) {
                bodyText += textArticleElement.text() + "\n\n";
            }

            result[0] = titleText;
            result[1] = mImageURL;
            result[2] = bodyText;

        } catch (IOException e) {
            Crashlytics.logException(e);
            return null;
        } catch (NullPointerException npe) {
            bodyText = null;
            Crashlytics.logException(npe);
            return null;
        } catch (Exception e) {
            Crashlytics.logException(e);
            return null;
        }

        return result;

    }

    private String getImageURL(Element bodyElement) {
        Elements mainImageElement = bodyElement.select(ConfigService.getInstance().getTheHinduImageFirst());
        Elements carouselElements = bodyElement.select(ConfigService.getInstance().getTheHinduImageSecond());

        if (mainImageElement.size() != 0) {
            mImageURL = mainImageElement.first().attr("src");
        } else if (carouselElements.size() != 0) {
            Elements carouselImage = carouselElements.select("div#pic").first().select("img");
            mImageURL = carouselImage.attr("src");
        }

        return mImageURL;

    }
}
