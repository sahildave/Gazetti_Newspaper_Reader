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
    String bodyText = "";
    String mArticlePubDate;

    public indianExpress(String mArticleURL, String mArticlePubDate) {
        this.mArticleURL = mArticleURL;
        this.mArticlePubDate = mArticlePubDate;
    }

    public String[] getTIEArticle() {

        Document doc;
        String[] result = new String[4];
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

            bodyText = getBody(bodyElement);

            result[0] = titleText;
            result[1] = mImageURL;
            result[2] = bodyText;
            result[3] = mArticlePubDate;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            bodyText = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    private String getImageURL(Element bodyElement) {

        Elements mainImageElement = bodyElement.select(ConfigService.getIndianExpressImageFirst());
        Elements carouselElements = bodyElement.select(ConfigService.getIndianExpressImageSecond());

        if (mainImageElement.size() != 0) {
            mImageURL = mainImageElement.first().attr("src");
        } else if (carouselElements.size() != 0) {
            Element carouselImage = bodyElement.select(".main-gallery img").first();
            mImageURL = carouselImage.attr("src");
        }

        return mImageURL;

    }

    private String getBody(Element bodyElement) {

        String tempHtml = "";
        Elements styTxtToSkip = bodyElement.select(ConfigService.getIndianExpressSkipBodyElement());
        Elements articleElements = bodyElement.select(ConfigService.getIndianExpressBody());

        if (articleElements.size() != 0) {

            for (Element replaceElement : styTxtToSkip) {
                replaceElement.remove();
            }

            Element bodyArticleElements = articleElements.first();
            tempHtml = bodyArticleElements.html();
        }
        return tempHtml;
    }
}
