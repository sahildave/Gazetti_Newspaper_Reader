package in.sahildave.gazetti.homescreen;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import com.crashlytics.android.Crashlytics;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.homescreen.adapter.CellModel;
import in.sahildave.gazetti.homescreen.adapter.GridAdapter;
import in.sahildave.gazetti.news_activities.WebsiteListActivity;
import in.sahildave.gazetti.util.GazettiEnums.Category;
import in.sahildave.gazetti.util.GazettiEnums.Newspapers;
import in.sahildave.gazetti.util.UserPrefUtil;

import java.util.List;
import java.util.Random;

public class HomeScreenFragment extends Fragment {
    private Callbacks activityCallback;
    private GridView gridview;
    private List<CellModel> cellList;
    private GridAdapter adapter;

    private String LOG_TAG = HomeScreenFragment.class.getName();

    private boolean phoneMode;
    private View actionBarCustomView;
    private ImageView phoneBackgroundImage;
    private KenBurnsView kenBurnsView;
    private Activity activity;

    public HomeScreenFragment() {

    }

    public interface Callbacks {
        public void showAddNewCellDialog(List<CellModel> cellList, GridAdapter adapter);

        public void showEditCellDialog(int position, String newspaper, String category, List<CellModel> cellList,
                                       GridAdapter adapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            activityCallback = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ToolbarListener");
        }

        this.activity = activity;

    }

    @Override
    public void onResume() {
        super.onResume();

        if (UserPrefUtil.isUserPrefChanged()) {
            UserPrefUtil.setUserPrefChanged(false);
            cellList.clear();
            setupCellGrid();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.homescreen_fragment, container, false);

        ActionBar actionBar = ((ActionBarActivity) activity).getSupportActionBar();
        actionBarCustomView = actionBar.getCustomView();

        gridview = (GridView) rootView.findViewById(R.id.gridview);
        phoneBackgroundImage = (ImageView) rootView.findViewById(R.id.phone_homescreen_background);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupImageBackground(view);
        setupCellGrid();
        registerForContextMenu(gridview);
        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if (position == (cellList.size() - 1)) {
                    activityCallback.showAddNewCellDialog(cellList, adapter);
                } else {
                    CellModel clickedObject = cellList.get(position);

                    String npId = clickedObject.getNewspaperId();
                    String catId = clickedObject.getCategoryId();
                    String npName = clickedObject.getNewspaperTitle();
                    String catName = clickedObject.getCategoryTitle();

                    npId = String.valueOf(Integer.parseInt(npId) + 1);

                    Intent headlinesIntent = new Intent(getActivity(), WebsiteListActivity.class);
                    headlinesIntent.putExtra("npId", npId);
                    headlinesIntent.putExtra("catId", catId);
                    headlinesIntent.putExtra("npName", npName);
                    headlinesIntent.putExtra("catName", catName);
                    startActivity(headlinesIntent);
                }

            }
        });

        if (phoneMode) {

            gridview.setOnScrollListener(new OnScrollListener() {

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (firstVisibleItem != 0) {
                        return;
                    }
                    if (null != gridview.getChildAt(0)) {

                        int actionBarTopMargin = gridview.getChildAt(0).getTop() - actionBarCustomView.getHeight();

                        if (actionBarTopMargin < ((-1) * actionBarCustomView.getHeight())) {
                            actionBarTopMargin = ((-1) * actionBarCustomView.getHeight());
                        }
                        ViewHelper.setTranslationY(actionBarCustomView, actionBarTopMargin);

                    }
                }

            });
        }

    }

    private void setupCellGrid() {
        cellList = UserPrefUtil.getUserPrefCellList();
        putAddNewCellInList();

        adapter = new GridAdapter(getActivity(), cellList);

        SwingBottomInAnimationAdapter animAdapter = new SwingBottomInAnimationAdapter(adapter);
        AlphaInAnimationAdapter animAdapterMultiple = new AlphaInAnimationAdapter(animAdapter);
        animAdapterMultiple.setAbsListView(gridview);

        gridview.setAdapter(animAdapterMultiple);
    }

    private void putAddNewCellInList() {
        if (cellList.size() > 0) {
            CellModel modelObject = cellList.get(cellList.size() - 1);
            if (!modelObject.getNewspaperImage().equals("add_new")) {
                cellList.add(new CellModel(Newspapers.ADD_NEW, Category.ADD_NEW));
            }
        } else {
            cellList.add(new CellModel(Newspapers.ADD_NEW, Category.ADD_NEW));
        }
    }

    private void setupImageBackground(View view) {

        kenBurnsView = (KenBurnsView) view.findViewById(R.id.kenBurnsView_Background);
        phoneMode = (kenBurnsView == null);

        try {
            new AsyncTask<Void, Void, Integer>(){
                @Override
                protected Integer doInBackground(Void... params) {
                    if (phoneMode) {
                        return getPhoneBackground();
                    } else {
                        return getTabletBackground();
                    }
                }

                @Override
                protected void onPostExecute(Integer resID) {
                    if(phoneMode){
                        Picasso.with(getActivity()).load(resID).into(phoneBackgroundImage);
                    } else {
                        Picasso.with(getActivity()).load(resID).into(kenBurnsView);
                    }

                }
            }.execute();
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }

    private int getTabletBackground() {
        Random rand = new Random();
        int n = rand.nextInt(3);
        String backgroundImageUri = "land_" + n;

        int resID = getResources().getIdentifier(backgroundImageUri, "drawable", getActivity().getPackageName());
        if (resID == 0) {
            resID = getResources().getIdentifier("land_0", "drawable", getActivity().getPackageName());
        }
        Log.d(LOG_TAG, "returning "+resID+" for "+n);
        return resID;
    }

    private int getPhoneBackground() {
        // get a random image, if null then get image_0
        Random rand = new Random();
        int n = rand.nextInt(3);
        String backgroundImageUri = "port_" + n;
        int resID = getResources().getIdentifier(backgroundImageUri, "drawable", getActivity().getPackageName());
        if (resID == 0) {
            resID = getResources().getIdentifier("port_0", "drawable", getActivity().getPackageName());
        }
        Log.d(LOG_TAG, "returning "+resID+" for "+n);
        return resID;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.gridview_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        String newspaper = cellList.get(position).getNewspaperImage();
        String category = cellList.get(position).getCategoryTitle();

        switch (item.getItemId()) {
            case R.id.edit:
                if (position == (cellList.size() - 1)) {
                    Toast.makeText(getActivity(), "Cannot Edit", Toast.LENGTH_SHORT).show();
                    return true;
                }
                activityCallback.showEditCellDialog(position, newspaper, category, cellList, adapter);
                return true;
            case R.id.delete:
                if (position == (cellList.size() - 1)) {
                    Toast.makeText(getActivity(), "Cannot Delete", Toast.LENGTH_SHORT).show();
                    return true;
                }
                UserPrefUtil.deleteUserPref(cellList.get(position));
                cellList.remove(position);
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
