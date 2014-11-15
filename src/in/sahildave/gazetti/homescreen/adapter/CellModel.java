package in.sahildave.gazetti.homescreen.adapter;

import in.sahildave.gazetti.util.GazettiEnums.Category;
import in.sahildave.gazetti.util.GazettiEnums.Newspapers;

public class CellModel {

    private String newspaperImage;
    private String newspaperTitle;
    private String newspaperId;
    private String categoryTitle;
    private String categoryId;

    public CellModel(Newspapers newspapers, Category category) {
        setNewspaperImage(newspapers.getNewspaperImage());
        setNewspaperTitle(newspapers.getTitle());
        setNewspaperId(newspapers.getNewspaperId());

        setCategoryTitle(category.getTitle());
        setCategoryId(category.getCategoryId());
    }

    public CellModel (NewsCatModel newsCatModel) {
        setNewspaperImage(newsCatModel.getNewspaperImage());
        setNewspaperTitle(newsCatModel.getNewspaperTitle());
        setNewspaperId(newsCatModel.getNewspaperId());

        setCategoryTitle(newsCatModel.getCategoryTitle());
        setCategoryId(newsCatModel.getCategoryId());
    }

    public String getNewspaperImage() {
        return newspaperImage;
    }

    public void setNewspaperImage(String newspaperImage) {
        this.newspaperImage = newspaperImage;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getNewspaperTitle() {
        return newspaperTitle;
    }

    public void setNewspaperTitle(String newspaperTitle) {
        this.newspaperTitle = newspaperTitle;
    }

    public String getNewspaperId() {
        return newspaperId;
    }

    public void setNewspaperId(String newspaperId) {
        this.newspaperId = newspaperId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return newspaperImage;
    }

}
