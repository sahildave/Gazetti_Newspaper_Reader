package in.sahildave.gazetti.bookmarks;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.bookmarks.sqlite.BookmarkModel;

import java.util.HashMap;
import java.util.List;

public class BookmarkAdapter extends ArrayAdapter<BookmarkModel> {

    private static final String TAG = "MasterDetail";
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    private Activity ctx;

    public static HashMap<String, String> linkMap = new HashMap<String, String>();
    public static HashMap<String, String> catNameMap = new HashMap<String, String>();
    public static HashMap<String, String> npNameMap = new HashMap<String, String>();
    public static HashMap<String, String> articleImage = new HashMap<String, String>();
    public static HashMap<String, String> articleBody = new HashMap<String, String>();
    public static HashMap<String, String> pubDateMap = new HashMap<String, String>();

    private List<BookmarkModel> articleObjectList;

    public BookmarkAdapter(Activity context, List<BookmarkModel> articleObjectList) {
        super(context, R.layout.headline_list_row, articleObjectList);
        ctx = context;
        this.articleObjectList = articleObjectList;
    }

    static class ViewHolder {
        TextView textViewItem;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = ctx.getLayoutInflater();
            convertView = inflater.inflate(R.layout.headline_list_row, parent, false);

            holder = new ViewHolder();
            holder.textViewItem = (TextView) convertView.findViewById(R.id.headline);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BookmarkModel object = articleObjectList.get(position);
        // Add the title view
        holder.textViewItem.setText(object.getmArticleHeadline());

        // Add title, link to a Map
        linkMap.put(object.getmArticleHeadline(), object.getmArticleURL());
        pubDateMap.put(object.getmArticleHeadline(), object.getmArticlePubDate());
        catNameMap.put(object.getmArticleHeadline(), object.getCategoryName());
        npNameMap.put(object.getmArticleHeadline(), object.getNewspaperName());
        articleImage.put(object.getmArticleHeadline(), object.getmArticleImageURL());
        articleBody.put(object.getmArticleHeadline(), object.getmArticleBody());
        return convertView;

    }
}
