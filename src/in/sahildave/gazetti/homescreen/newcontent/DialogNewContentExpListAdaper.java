package in.sahildave.gazetti.homescreen.newcontent;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.util.NewsCatFileUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogNewContentExpListAdaper extends BaseExpandableListAdapter {

    private static final String LOG_TAG = DialogNewContentExpListAdaper.class.getName();

    private Context mContext;
    private List<String> mListDataHeader;
    private HashMap<String, List<String>> mListDataChild;
    public static Map<String, Object> mUserSelection;
    public static Map<Integer, boolean[]> mChildCheckStates;

    private int lastExpandedGroupPosition = -1;

    public static ExpandableListView expandableList;
    TypedArray explist_np_images;

    public DialogNewContentExpListAdaper(Context context, List<String> listDataHeader,
                                         HashMap<String, List<String>> listChildData) {
        this.mContext = context;
        this.mListDataHeader = listDataHeader;
        this.mListDataChild = listChildData;

        mUserSelection = NewsCatFileUtil.getInstance(mContext).getFullJsonMap();
        explist_np_images = mContext.getResources().obtainTypedArray(R.array.explist_newspaper_images);
        setupChildCheckedStates();
    }

    private void setupChildCheckedStates() {
        mChildCheckStates = new HashMap<Integer, boolean[]>();
        int groupNumbers = mListDataHeader.size();

        for(int i=0; i<groupNumbers; i++){
            String newspaper = mListDataHeader.get(i);
//            Log.d(LOG_TAG, "newspaper - " + newspaper);
            List<String> allCategories = mListDataChild.get(newspaper);
//            Log.d(LOG_TAG, "allCategories - "+allCategories);
            int categoriesLength = allCategories.size();
//            Log.d(LOG_TAG, "categoriesLength - "+categoriesLength);
            boolean[] checkStates = new boolean[categoriesLength];

            mChildCheckStates.put(i, checkStates);
        }
//        Log.d(LOG_TAG, "Expandable List Adapter -- "+mChildCheckStates.toString());
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String groupText = getGroup(groupPosition);

        GroupViewHolder groupViewHolder;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.welcome_feed_select_explist_group, parent, false);

            // Initialize the GroupViewHolder defined at the bottom of this
            // document
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.mGroupText = (TextView) convertView
                    .findViewById(R.id.welcome_feed_select_explist_group_textview);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        groupViewHolder.mGroupText.setText(groupText);
        groupViewHolder.mGroupText.setTypeface(null, Typeface.BOLD);

        int imageResId = explist_np_images.getResourceId(groupPosition, -1);
        Drawable image = mContext.getResources().getDrawable(imageResId);
        image.setBounds(0, 0, image.getMinimumWidth(), image.getMinimumHeight());
        groupViewHolder.mGroupText.setCompoundDrawables(null, null, image, null);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {

        final int mGroupPosition = groupPosition;
        final int mChildPosition = childPosition;

        String childText = getChild(groupPosition, childPosition);

        final ChildViewHolder childViewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.welcome_feed_select_explist_rowdetail,  parent, false);

            childViewHolder = new ChildViewHolder();

            childViewHolder.mChildText = (TextView) convertView
                    .findViewById(R.id.welcome_feed_select_explist_childTextView);

            childViewHolder.mCheckBox = (CheckBox) convertView
                    .findViewById(R.id.welcome_feed_select_explist_childCheckBox);

            convertView.setTag(R.layout.welcome_feed_select_explist_rowdetail, childViewHolder);
        } else {

            childViewHolder = (ChildViewHolder) convertView.getTag(R.layout.welcome_feed_select_explist_rowdetail);
        }

        childViewHolder.mChildText.setText(childText);
        childViewHolder.mCheckBox.setOnCheckedChangeListener(null);

        if (mChildCheckStates.containsKey(mGroupPosition)) {
            boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
            childViewHolder.mCheckBox.setChecked(getChecked[mChildPosition]);

        } else {
            boolean getChecked[] = new boolean[getChildrenCount(mGroupPosition)];
            mChildCheckStates.put(mGroupPosition, getChecked);
            childViewHolder.mCheckBox.setChecked(false);
        }

        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                childViewHolder.mCheckBox.toggle();
            }
        });

        childViewHolder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                getChecked[mChildPosition] = isChecked;
                mChildCheckStates.put(mGroupPosition, getChecked);
            }
        });

        return convertView;
    }

    public void setExpList(ExpandableListView explist) {
        expandableList = explist;
    }

    public Map<String, Object> getClickedStates() {
        try {

            int headerLength = mListDataHeader.size();
            Map<String, Object> returnMap = new HashMap<String, Object>();

            for(int i=0; i<headerLength; i++){

                Map<String, Boolean> children = new HashMap<String, Boolean>();
                String newspaper = mListDataHeader.get(i);
                boolean[] checkedState = mChildCheckStates.get(i);

                int categoriesLength = checkedState.length;
                for(int j=0; j<categoriesLength; j++){
                    String category = mListDataChild.get(newspaper).get(j);
                    children.put(category, checkedState[j]);
                }

                returnMap.put(newspaper, children);
            }
            return returnMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isAnythingSelected(){
        try {
            int headerLength = mListDataHeader.size();
            for(int i=0; i<headerLength; i++){
                boolean[] checkedState = mChildCheckStates.get(i);
                if(isBooleanArrayTrue(checkedState)){
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    private boolean isBooleanArrayTrue(boolean[] checkedState) {
        for (boolean aCheckedState : checkedState) {
            if (aCheckedState) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getChild(int groupPosition, int childPosititon) {
        return mListDataChild.get(getGroup(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mListDataChild.get(getGroup(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return mListDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mListDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {

        if (groupPosition != lastExpandedGroupPosition) {
            expandableList.collapseGroup(lastExpandedGroupPosition);
        }
        expandableList.smoothScrollToPosition(groupPosition);
        lastExpandedGroupPosition = groupPosition;
        super.onGroupExpanded(groupPosition);

    }

    public final class GroupViewHolder {
        TextView mGroupText;
        ImageView mGroupIcon;
    }

    public final class ChildViewHolder {

        TextView mChildText;
        CheckBox mCheckBox;
    }

    @Override
    public String toString() {
        int headerLength = mListDataHeader.size();

        String returnString = "";
        for(int i=0; i<headerLength; i++){
            returnString += i+" -- ";
            boolean[] checkedState = mChildCheckStates.get(i);
            int categoriesLength = checkedState.length;
            for(int j=0; j<categoriesLength; j++){
                boolean state = mChildCheckStates.get(1)[j];
                returnString += state + ", " ;
            }
            returnString += "; ";
        }
        return returnString;
    }
}