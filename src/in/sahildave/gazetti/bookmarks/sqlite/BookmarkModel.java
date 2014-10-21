package in.sahildave.gazetti.bookmarks.sqlite;

/**
 * Created by sahil on 21/10/14.
 */
public class BookmarkModel {

    private String newspaperName;
    private String categoryName;
    private String mArticleHeadline;
    private String mArticleBody;
    private String mArticleImageURL;
    private String mArticlePubDate;
    private String mArticleURL;

    public String getNewspaperName() {
        return newspaperName;
    }

    public void setNewspaperName(String newspaperName) {
        this.newspaperName = newspaperName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getmArticleHeadline() {
        return mArticleHeadline;
    }

    public void setmArticleHeadline(String mArticleHeadline) {
        this.mArticleHeadline = mArticleHeadline;
    }

    public String getmArticleBody() {
        return mArticleBody;
    }

    public void setmArticleBody(String mArticleBody) {
        this.mArticleBody = mArticleBody;
    }

    public String getmArticleURL() {
        return mArticleURL;
    }

    public void setmArticleURL(String mArticleURL) {
        this.mArticleURL = mArticleURL;
    }

    public String getmArticleImageURL() {
        if(mArticleImageURL==null){
            return "";
        }
        return mArticleImageURL;
    }

    public void setmArticleImageURL(String mArticleImageURL) {
        this.mArticleImageURL = mArticleImageURL;
    }

    public String getmArticlePubDate() {
        if(mArticlePubDate==null){
            return "";
        }
        return mArticlePubDate;
    }

    public void setmArticlePubDate(String mArticlePubDate) {
        this.mArticlePubDate = mArticlePubDate;
    }

    @Override
    public String toString() {
        return getmArticleHeadline();
    }
}
