package in.sahildave.gazetti.bookmarks;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.parse.ParseObject;
import in.sahildave.gazetti.R;

import java.util.HashMap;
import java.util.List;

public class BookmarkAdapter extends ArrayAdapter<ParseObject> {

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

    private List<ParseObject> articleObjectList;

    public BookmarkAdapter(Activity context, List<ParseObject> articleObjectList) {
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

        ParseObject object = articleObjectList.get(position);
        // Add the title view
        holder.textViewItem.setText(object.getString("title"));

        // Add title, link to a Map
        linkMap.put(object.getString("title"), object.getString("link"));
        pubDateMap.put(object.getString("title"), object.getString("pubDate"));
        catNameMap.put(object.getString("title"), object.getString("catName"));
        npNameMap.put(object.getString("title"), object.getString("npName"));
        articleImage.put(object.getString("title"), object.getString("image"));
        articleBody.put(object.getString("title"), object.getString("body"));
        return convertView;

    }
}
