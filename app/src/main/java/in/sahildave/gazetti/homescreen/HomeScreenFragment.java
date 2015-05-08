package in.sahildave.gazetti.homescreen;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import com.crashlytics.android.Crashlytics;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.homescreen.adapter.CellModel;
import in.sahildave.gazetti.homescreen.adapter.GridAdapter;
import in.sahildave.gazetti.news_activities.WebsiteListActivity;
import in.sahildave.gazetti.util.BitmapTransform;
import in.sahildave.gazetti.util.NewsCatFileUtil;
import in.sahildave.gazetti.util.UserPrefUtil;
import in.sahildave.gazetti.widget.fab.PlusFloatingActionButton;
import in.sahildave.gazetti.widget.fab.FloatingActionButton;
import in.sahildave.gazetti.widget.fab.FloatingActionsMenu;

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
    private PlusFloatingActionButton fabAddNew;
    private FloatingActionButton fabBookmark;
    private FloatingActionButton fabEditFeeds;
    private FloatingActionsMenu fabMenu;

    public HomeScreenFragment() {

    }

    public void refreshCellGrid() {
        if (NewsCatFileUtil.getInstance(getActivity()).isUserPrefChanged()) {
            NewsCatFileUtil.getInstance(getActivity()).setUserPrefChanged(false);
            if(cellList!=null){
                cellList.clear();
            }
            setupCellGrid();
        }
    }

    public interface Callbacks {
        public void showAddNewCellDialog(List<CellModel> cellList, GridAdapter adapter);

        public void showEditCellDialog(int position, String newspaper, String category, List<CellModel> cellList,
                                       GridAdapter adapter);
        public void openEditFeedSettings();
        public void startBookmarkActivity();
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
        refreshCellGrid();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.homescreen_fragment, container, false);

        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
        actionBarCustomView = actionBar.getCustomView();

        gridview = (GridView) rootView.findViewById(R.id.gridview);
        phoneBackgroundImage = (ImageView) rootView.findViewById(R.id.phone_homescreen_background);

        fabMenu = (FloatingActionsMenu) rootView.findViewById(R.id.floating_action_menu);
        fabAddNew = (PlusFloatingActionButton) rootView.findViewById(R.id.fab_add_new_cell);
        fabBookmark = (FloatingActionButton) rootView.findViewById(R.id.fab_bookmark);
        fabEditFeeds = (FloatingActionButton) rootView.findViewById(R.id.fab_edit_feeds);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupImageBackground(view);
        setupCellGrid();
        registerForContextMenu(gridview);
        setupFab();

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
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
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (phoneMode) {
            gridview.setOnScrollListener(new OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {}
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
                        actionBarCustomView.setTranslationY(actionBarTopMargin);
                    }
                }
            });
        }
    }

    private void setupFab() {
        fabAddNew.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenu.collapse();
                activityCallback.showAddNewCellDialog(cellList, adapter);
            }
        });

        fabBookmark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenu.collapse();
                activityCallback.startBookmarkActivity();
            }
        });

        fabEditFeeds.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fabMenu.collapse();
                activityCallback.openEditFeedSettings();
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fabMenu.setVisibility(View.VISIBLE);
            }
        }, 500);
    }

    private void setupCellGrid() {
//        Log.d(LOG_TAG, "Setting up cell grid");
        cellList = UserPrefUtil.getInstance(getActivity()).getUserPrefCellList();
        adapter = new GridAdapter(getActivity(), cellList);

        SwingBottomInAnimationAdapter animAdapter = new SwingBottomInAnimationAdapter(adapter);
        AlphaInAnimationAdapter animAdapterMultiple = new AlphaInAnimationAdapter(animAdapter);
        animAdapterMultiple.setAbsListView(gridview);

        gridview.setAdapter(animAdapterMultiple);
    }

    private void setupImageBackground(View view) {

        kenBurnsView = (KenBurnsView) view.findViewById(R.id.kenBurnsView_Background);
        phoneMode = (kenBurnsView == null);

        //height and width of screen
        final int MAX_HEIGHT = getResources().getDisplayMetrics().heightPixels;
        final int MAX_WIDTH = getResources().getDisplayMetrics().widthPixels;

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

                    RequestCreator requestCreator = Picasso.with(getActivity())
                            .load(resID)
                            .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT));

                    if(phoneMode){
                        requestCreator.into(phoneBackgroundImage);
                    } else {
                        requestCreator.into(kenBurnsView);
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
//        Log.d(LOG_TAG, "returning "+resID+" for "+n);
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
//        Log.d(LOG_TAG, "returning "+resID+" for "+n);
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
                activityCallback.showEditCellDialog(position, newspaper, category, cellList, adapter);
                return true;
            case R.id.delete:
                UserPrefUtil.getInstance(getActivity()).deleteUserPref(cellList.get(position));
                cellList.remove(position);
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
