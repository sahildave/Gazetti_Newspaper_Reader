package in.sahildave.gazetti.news_activities.fetch;

import android.util.Log;
import in.sahildave.gazetti.util.ConfigService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class firstPost {

    String mArticleURL;
    String titleText;
    String mImageURL = null;
    String articleText = "";
    String mArticlePubDate;

    public firstPost(String mArticleURL, String mArticlePubDate) {
        this.mArticleURL = mArticleURL;
        this.mArticlePubDate = mArticlePubDate;
        Log.d("Firstpost", "JSoup FirstPost " + mArticleURL);
    }

    public String[] getFirstPostArticle() {

        Document doc;
        String[] result = new String[4];
        String url = mArticleURL;

        try {
            doc = Jsoup.connect(url) //
                    .userAgent("Mozilla") //
                    .timeout(10 * 1000) //
                    .get(); //

            Element bodyElement = doc.body();

            // get Title
            String FirstPostTitleXPath = ConfigService.getFirstPostHead();
            Elements titleElements = bodyElement.select(FirstPostTitleXPath);
            titleText = titleElements.first().text();

            // get HeaderImageUrl
            mImageURL = getImageURL(bodyElement);

            String FirstPostArticleXPath = ConfigService.getFirstPostBody();
            Elements articleElements = doc.select(FirstPostArticleXPath);

            for(Element el: articleElements){
                String temp = el.text();
                if(temp.length()>0){
                    articleText = articleText + temp + "\n\n";
                }
            }

            result[0] = titleText;
            result[1] = mImageURL;
            result[2] = articleText;
            result[3] = mArticlePubDate;

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
        Elements mainImageElement = bodyElement.select(ConfigService.getFirstPostImage());

        if (mainImageElement.size() != 0) {
            mImageURL = mainImageElement.get(1).attr("src");
        }

        return mImageURL;

    }
}
