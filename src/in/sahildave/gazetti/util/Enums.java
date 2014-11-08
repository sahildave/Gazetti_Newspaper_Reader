package in.sahildave.gazetti.util;

/**
 * Created by sahil on 9/11/14.
 */
public class Enums {

    public enum Newspapers {
        THE_HINDU ("The Hindu", "th", "0", "hindu_data"),
        TOI ("The Times of India", "toi", "1", "toi_data"),
        FIRST_POST ("First Post", "fp", "2", "fp_data"),
        INDIAN_EXP ("The Indian Express", "tie", "3", "tie_data"),
        ADD_NEW ("Add New", "add_new", "-1", "null");

        private final String title;
        private final String newspaperImage;
        private final String newspaperId;
        private final String dbToSearch;

        Newspapers(String title, String newspaperImage, String npId, String dbToSearch) {
            this.title = title;
            this.newspaperImage = newspaperImage;
            this.newspaperId = npId;
            this.dbToSearch = dbToSearch;
        }

        public String getTitle() {
            return title;
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
}
