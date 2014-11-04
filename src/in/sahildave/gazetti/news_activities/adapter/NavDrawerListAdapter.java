package in.sahildave.gazetti.news_activities.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import in.sahildave.gazetti.R;

public class NavDrawerListAdapter extends BaseAdapter {

    private Context context;
    private String[] navDrawerItems;

    public NavDrawerListAdapter(Context context, String[] navMenuTitles) {
        this.context = context;
        this.navDrawerItems = navMenuTitles;
    }

    @Override
    public int getCount() {
        return navDrawerItems.length;
    }

    @Override
    public String getItem(int position) {
        return navDrawerItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.nav_drawer_list_item, parent, false);
        }
        TextView textTitle = (TextView) convertView.findViewById(R.id.nav_list_item_title);
        textTitle.setText(getItem(position));
        textTitle.setTypeface(RobotoLight.getInstance(context).getTypeFace());
        return convertView;
    }

}