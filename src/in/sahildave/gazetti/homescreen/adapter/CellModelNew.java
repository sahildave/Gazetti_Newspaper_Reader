package in.sahildave.gazetti.homescreen.adapter;

import in.sahildave.gazetti.util.Enums.Newspapers;

public class CellModelNew {

    private String newspaperImage;
    private String newspaperTitle;
    private String titleCategory;

    public CellModelNew(Newspapers newspapers, String categoryTitle) {
        setNewspaperImage(newspapers.getNewspaperImage());
        setTitleCategory(categoryTitle);
    }

    public String getNewspaperImage() {
        return newspaperImage;
    }

    public void setNewspaperImage(String newspaperImage) {
        this.newspaperImage = newspaperImage;
    }

    public String getTitleCategory() {
        return titleCategory;
    }

    public void setTitleCategory(String titleCategory) {
        this.titleCategory = titleCategory;
    }

    public String getNewspaperTitle() {
        return newspaperTitle;
    }

    public void setNewspaperTitle(String newspaperTitle) {
        this.newspaperTitle = newspaperTitle;
    }

    @Override
    public String toString() {
        return newspaperImage;
    }

}
