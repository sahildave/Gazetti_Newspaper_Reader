package in.sahildave.gazetti.homescreen.adapter;

import in.sahildave.gazetti.util.GazettiEnums.Category;
import in.sahildave.gazetti.util.GazettiEnums.Newspapers;

public class NewsCatModel {

    String newspaperId;
    String newspaperTitle;
    String categoryId;
    String categoryTitle;
    String newspaperImage;

    public NewsCatModel(Newspapers newspaper, Category category){
        setNewspaperId(newspaper.getNewspaperId());
        setNewspaperImage(newspaper.getNewspaperImage());
        setNewspaperTitle(newspaper.getTitle());
        setCategoryId(category.getCategoryId());
        setCategoryTitle(category.getTitle());
    }

    public String getNewspaperId() {
        return newspaperId;
    }

    public void setNewspaperId(String newspaperId) {
        this.newspaperId = newspaperId;
    }

    public String getNewspaperTitle() {
        return newspaperTitle;
    }

    public void setNewspaperTitle(String newspaperTitle) {
        this.newspaperTitle = newspaperTitle;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getNewspaperImage() {
        return newspaperImage;
    }

    public void setNewspaperImage(String newspaperImage) {
        this.newspaperImage = newspaperImage;
    }

    @Override
    public String toString() {
        return "CSVObject [newspaperId=" + newspaperId + ", newspaperTitle=" + newspaperTitle + ", categoryId=" + categoryId + ", categoryTitle=" + categoryTitle
                + ", newspaperImage=" + newspaperImage + "]";
    }


}
