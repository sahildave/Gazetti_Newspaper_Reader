package in.sahildave.gazetti.homescreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nineoldandroids.view.ViewHelper;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.homescreen.adapter.CellModel;
import in.sahildave.gazetti.homescreen.adapter.GridAdapter;
import in.sahildave.gazetti.homescreen.adapter.NewsCatModel;
import in.sahildave.gazetti.news_activities.WebsiteListActivity;
import in.sahildave.gazetti.util.CellListUtil;
import in.sahildave.gazetti.util.CsvFileUtil;
import in.sahildave.gazetti.util.UserSelectionUtil;

import java.util.List;
import java.util.Random;

public class HomeScreenFragment extends Fragment {
    private Callbacks activityCallback;
    private GridView gridview;
    private List<CellModel> cellList;
    private GridAdapter adapter;
    private int feedVersion;
    private SwingBottomInAnimationAdapter animAdapter;
    private AlphaInAnimationAdapter animAdapterMultiple;

    private String TAG = "HomeScreen";

    private boolean firstRun = false;
    private boolean phoneMode;
    private ActionBar actionBar;
    private View actionBarCustomView;
    private ImageView phoneBackgroundImage;
    private LinearLayout photoCreditLayout;
    private TextView photoCreditText;
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

        SharedPreferences sharedPref = getActivity().getSharedPreferences("CellList", Context.MODE_PRIVATE);
        int newfeedVersion = sharedPref.getInt("feedVersion", 0);

        Log.d(TAG, "HomeScreenFragment in onResume: firstRun - " + firstRun
                + ", newfeedVerision - " + newfeedVersion
                + ", feedVersion - " + feedVersion);
        if ((newfeedVersion > feedVersion)) {

            Log.d(TAG, "RELOADING - " + cellList.size());
            feedVersion = newfeedVersion;
            cellList.clear();
            cellList = CellListUtil.getCellListFromSharedPrefs();

            putAddNewCellInList();

            adapter = new GridAdapter(getActivity(), cellList);

            animAdapter = new SwingBottomInAnimationAdapter(adapter);
            animAdapterMultiple = new AlphaInAnimationAdapter(animAdapter);
            animAdapterMultiple.setAbsListView(gridview);

            gridview.setAdapter(animAdapterMultiple);

            Log.d(TAG, "LOADED - " + cellList.size());

        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "HomeScreenFragment in onCreate ");
        setRetainInstance(true);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("CellList", Context.MODE_PRIVATE);
        feedVersion = sharedPref.getInt("feedVersion", 0);

        firstRun = true;
        Log.d(TAG, "FEEDVERSION onCreate - " + feedVersion);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.homescreen_fragment, container, false);

        actionBar = ((ActionBarActivity) activity).getSupportActionBar();
        actionBarCustomView = actionBar.getCustomView();

        gridview = (GridView) rootView.findViewById(R.id.gridview);
        phoneBackgroundImage = (ImageView) rootView.findViewById(R.id.phone_homescreen_background);
        photoCreditLayout = (LinearLayout) rootView.findViewById(R.id.photoCreditLayout);
        photoCreditText = (TextView) rootView.findViewById(R.id.photoCreditText);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (view.findViewById(R.id.kenBurnsView_Background) == null) {
            // Phone
            phoneMode = true;
            loadPhoneBackground();

        } else {
            phoneMode = false;
            kenBurnsView = (KenBurnsView) view.findViewById(R.id.kenBurnsView_Background);
            Log.d(TAG, "loading for tablet");
            // loadTabletBackground();

        }

        cellList = CellListUtil.getCellListFromSharedPrefs();
        putAddNewCellInList();

        adapter = new GridAdapter(getActivity(), cellList);
        animAdapter = new SwingBottomInAnimationAdapter(adapter);
        animAdapterMultiple = new AlphaInAnimationAdapter(animAdapter);
        animAdapterMultiple.setAbsListView(gridview);

        gridview.setAdapter(animAdapterMultiple);

        registerForContextMenu(gridview);
        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if (position == (cellList.size() - 1)) {
                    activityCallback.showAddNewCellDialog(cellList, adapter);
                } else {
                    CellModel clickedObject = cellList.get(position);
                    String npImage = clickedObject.getNewspaperImage();
                    String catName = clickedObject.getTitleCategory();

                    CsvFileUtil csvFile = new CsvFileUtil(getActivity());
                    NewsCatModel csvObject = csvFile.getObjectByNPImage(npImage, catName);

                    String npId = csvObject.getNpId();
                    String catId = csvObject.getCatId();
                    String npName = csvObject.getNpName();

                    npId = String.valueOf(Integer.parseInt(npId) + 1);
                    catId = String.valueOf(Integer.parseInt(catId) + 1);

                    Intent headlinesIntent = new Intent(getActivity(), WebsiteListActivity.class);
                    headlinesIntent.putExtra("npId", npId);
                    headlinesIntent.putExtra("catId", catId);
                    headlinesIntent.putExtra("npName", npName);
                    headlinesIntent.putExtra("catName", catName);
                    startActivity(headlinesIntent);

                    csvFile.closeUtilObject();
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

    private void putAddNewCellInList() {
        if (cellList.size() > 0) {
            CellModel modelObject = cellList.get(cellList.size() - 1);
            if (!modelObject.getNewspaperImage().equals("add_new")) {
                cellList.add(new CellModel("add_new", "Add New"));
            }
        } else {
            cellList.add(new CellModel("add_new", "Add New"));
        }
    }

    private void loadPhoneBackground() {
        // get a random image, if null then get image_0
        Random rand = new Random();
        int n = rand.nextInt(4) + 1;
        String backgroundImageUri = "port_" + n;

        int resID = getResources().getIdentifier(backgroundImageUri, "drawable", getActivity().getPackageName());

        if (resID == 0) {
            resID = getResources().getIdentifier("port_0", "drawable", getActivity().getPackageName());
        }

        Log.d(TAG, "PHOTO ---- " + n + ", " + resID);

        // Bitmap Options
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resID, options);

        // Raw height and width of image
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        int inSampleSize = 1;

        // height and width of screen
        int reqHeight = getResources().getDisplayMetrics().heightPixels;
        int reqWidth = getResources().getDisplayMetrics().widthPixels;

        // SampleSize Calculations
        if (imageHeight > reqHeight || imageWidth > reqWidth) {

            final int halfHeight = imageHeight / 2;
            final int halfWidth = imageWidth / 2;

            // Calculate the largest inSampleSize value that is a power of 2
            // and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }

            // This offers some additional logic in case the image has a strange
            // aspect ratio. Anything more than 2x the requested pixels we'll
            // sample down
            // further
            long totalPixels = imageWidth * imageHeight / inSampleSize;
            final long totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2;
                totalPixels /= 2;
            }
        }

        options.inJustDecodeBounds = false;
        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), resID, options);

        Log.d(TAG, "bitmap - " + mBitmap.getHeight());

        phoneBackgroundImage.setImageBitmap(mBitmap);

        // gridViewBackground = new BitmapDrawable(getResources(), mBitmap);
        // gridview.setBackgroundDrawable(gridViewBackground);
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
        String category = cellList.get(position).getTitleCategory();

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
                cellList.remove(position);
                adapter.notifyDataSetChanged();

                CellListUtil cellListObject = new CellListUtil(getActivity());
                cellListObject.saveCellListToSharedPrefs(cellList);

                UserSelectionUtil userSelectionUtil = new UserSelectionUtil(getActivity());
                userSelectionUtil.updateUserSelectionSharedPrefs();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
