package in.sahildave.gazetti.news_activities.fetch;

import in.sahildave.gazetti.util.ConfigService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class indianExpress {

    String mArticleURL;
    String titleText;
    String mImageURL = null;
    String articleText = "";

    public indianExpress(String mArticleURL) {
        this.mArticleURL = mArticleURL;
    }

    public String[] getTIEArticleContent() {

        Document doc;
        String[] result = new String[3];
        String url = mArticleURL;

        try {
            doc = Jsoup.connect(url) //
                    .userAgent("Mozilla") //
                    .timeout(10 * 1000) //
                    .get(); //

            // get Body
            Element bodyElement = doc.body();

            // get page title
            Elements titleElements = bodyElement.select(ConfigService.getIndianExpressHead());
            titleText = titleElements.first().text();

            // get HeaderImageUrl
            mImageURL = getImageURL(bodyElement);

            String TIEArticleXPath = ConfigService.getIndianExpressBody();
            Elements articleElements = doc.select(TIEArticleXPath);

            for(Element el: articleElements){
                String temp = el.text();
                if(temp.length()>0){
                    articleText = articleText + temp + "\n\n";
                }
            }

            result[0] = titleText;
            result[1] = mImageURL;
            result[2] = articleText;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            articleText = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    private String getImageURL(Element bodyElement) {

        Elements mainImageElement = bodyElement.select(ConfigService.getIndianExpressImage());
        if (mainImageElement.size() != 0) {
            mImageURL = mainImageElement.first().attr("src");
        }
        return mImageURL;

    }
}
