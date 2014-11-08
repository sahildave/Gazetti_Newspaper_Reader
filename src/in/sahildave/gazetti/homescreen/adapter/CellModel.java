package in.sahildave.gazetti.homescreen.adapter;

public class CellModel {

    private String newspaperImage;
    private String titleCategory;

    public CellModel(String newspaperImage, String categoryTitle) {
        setNewspaperImage(newspaperImage);
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

    public int getDefaultNewspaperId(String newspaperImage) {
        if (newspaperImage.equals("th")) {
            return 0;
        } else if (newspaperImage.equals("toi")) {
            return 1;
        } else if (newspaperImage.equals("fp")) {
            return 2;
        } else if (newspaperImage.equals("ht")) {
            return 3;
        } else if (newspaperImage.equals("tie")) {
            return 4;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return newspaperImage;
    }

}
