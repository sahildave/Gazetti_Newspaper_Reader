package in.sahildave.gazetti.news_activities.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.parse.ParseObject;
import in.sahildave.gazetti.R;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<ParseObject> {

    private static final String TAG = "MasterDetail";
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    private Activity ctx;

    public static HashMap<String, String> linkMap = new HashMap<String, String>();
    public static HashMap<String, String> pubDateMap = new HashMap<String, String>();

    private List<ParseObject> articleObjectList;

    public NewsAdapter(Activity context, List<ParseObject> articleObjectList) {
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

        // get PubDate
        Date createdAtDate = object.getCreatedAt();
        long createdAtDateInMillis = createdAtDate.getTime();
        //Log.d(TAG, "createdAtDateInMillis " + createdAtDateInMillis);

        String diff = getTimeAgo(createdAtDateInMillis);
        //Log.d(TAG, "diff " + diff);

        // Add title, link to Map
        linkMap.put(object.getString("title"), object.getString("link"));
        pubDateMap.put(object.getString("title"), diff);
        //Log.d(TAG, "Added " + pubDateMap.get(object.getString("title")));

        // PubDate

        return convertView;

    }

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return "Just Now";
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Just Now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "A Minute Ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " Minutes Ago";
        } else if (diff < 120 * MINUTE_MILLIS) {
            return "An Hour Ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " Hours Ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday";
        } else {
            return diff / DAY_MILLIS + " Days Ago";
        }
    }

}
