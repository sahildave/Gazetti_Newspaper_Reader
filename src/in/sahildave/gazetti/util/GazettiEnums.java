package in.sahildave.gazetti.util;

/**
 * Created by sahil on 9/11/14.
 */
public class GazettiEnums {

    public enum Newspapers {
        THE_HINDU ("The Hindu", "th", "0", "hindu_data"),
        TOI ("The Times of India", "toi", "1", "toi_data"),
        FIRST_POST ("First Post", "fp", "2", "fp_data"),
        INDIAN_EXP ("The Indian Express", "tie", "3", "tie_data"),
        ADD_NEW ("Add New", "add_new", "-1", "null");

        private final String newspaperTitle;
        private final String newspaperImage;
        private final String newspaperId;
        private final String dbToSearch;

        Newspapers(String newspaperTitle, String newspaperImage, String npId, String dbToSearch) {
            this.newspaperTitle = newspaperTitle;
            this.newspaperImage = newspaperImage;
            this.newspaperId = npId;
            this.dbToSearch = dbToSearch;
        }

        public String getTitle() {
            return newspaperTitle;
        }

        public String getNewspaperImage() {
            return newspaperImage;
        }

        public String getNewspaperId() {
            return newspaperId;
        }
    }

    public Newspapers getNewspaperFromImage(String newspaperImage){
        if (newspaperImage.equals("th")) {
            return Newspapers.THE_HINDU;
        } else if (newspaperImage.equals("toi")) {
            return Newspapers.TOI;
        } else if (newspaperImage.equals("fp")) {
            return Newspapers.FIRST_POST;
        } else if (newspaperImage.equals("tie")) {
            return Newspapers.INDIAN_EXP;
        }
        return null;
    }

    public Newspapers getNewspaperFromName(String newspaperName){
        if (newspaperName.equals("The Hindu")) {
            return Newspapers.THE_HINDU;
        } else if (newspaperName.equals("The Times of India")) {
            return Newspapers.TOI;
        } else if (newspaperName.equals("First Post")) {
            return Newspapers.FIRST_POST;
        } else if (newspaperName.equals("The Indian Express")) {
            return Newspapers.INDIAN_EXP;
        }
        return null;
    }

    //// Categories ////

    public enum Category {
        NATIONAL ("National", "1"),
        INTERNATIONAL ("International", "2"),
        SPORTS ("Sports", "3"),
        SCIENCE ("Science", "4"),
        BUSINESS ("Business", "6"),
        OPINIONS_BLOGS ("Opinion/Blogs", "7"),
        ENTERTAINMENT ("Entertainment", "5"),
        ADD_NEW("Add New", "-1");

        private final String categoryTitle;
        private final String categoryId;

        Category(String categoryTitle, String catId) {
            this.categoryTitle = categoryTitle;
            this.categoryId = catId;
        }

        public String getTitle() {
            return categoryTitle;
        }

        public String getCategoryId() {
            return categoryId;
        }
    }

    public Category getCategoryFromId(String catId){
        if (catId.equals("1")) {
            return Category.NATIONAL;
        } else if (catId.equals("2")) {
            return Category.INTERNATIONAL;
        } else if (catId.equals("3")) {
            return Category.SPORTS;
        } else if (catId.equals("4")) {
            return Category.SCIENCE;
        } else if (catId.equals("5")) {
            return Category.ENTERTAINMENT;
        } else if (catId.equals("6")) {
            return Category.BUSINESS;
        } else if (catId.equals("7")) {
            return Category.OPINIONS_BLOGS;
        } else if (catId.equals("-1")) {
            return Category.ADD_NEW;
        }
        return null;
    }

    public Category getCategoryFromName(String categoryName){
        if (categoryName.equalsIgnoreCase("Opinion/Blogs")){
            return Category.OPINIONS_BLOGS;
        } else if (categoryName.equalsIgnoreCase("Add New")){
            return Category.ADD_NEW;
        }else {
            return Category.valueOf(categoryName.toUpperCase());
        }
    }
}
