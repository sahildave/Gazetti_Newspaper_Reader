package in.sahildave.gazetti.homescreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.crashlytics.android.Crashlytics;
import com.parse.ConfigCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseConfig;
import com.parse.ParseException;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.bookmarks.BookmarkListActivity;
import in.sahildave.gazetti.homescreen.adapter.*;
import in.sahildave.gazetti.homescreen.adapter.AddCellDialogFragment.AddCellDialogListener;
import in.sahildave.gazetti.homescreen.adapter.EditCellDialogFragment.EditCellDialogListener;
import in.sahildave.gazetti.preference.SettingsActivity;
import in.sahildave.gazetti.util.*;
import in.sahildave.gazetti.welcomescreen.WelcomeScreenViewPagerActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenActivity extends ActionBarActivity implements HomeScreenFragment.Callbacks,
        AddCellDialogListener, EditCellDialogListener {

    private static final String TAG = "HomeScreen";

    private FragmentManager fragmentManager;
    private List<CellModel> cellList;
    private GridAdapter adapter;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.d(TAG, "onCreate - " + (null == savedInstanceState));
        super.onCreate(savedInstanceState);
        ParseAnalytics.trackAppOpened(getIntent());

        setupCustomActionBar();

        setContentView(R.layout.homescreen_activity);

        // When coming from WelcomeScreen but without completing the task, the intent would have "Exit Me"
        if (getIntent().getBooleanExtra("Exit me", false)) {
            this.finish();
            return; // add this to prevent from doing unnecessary stuffs
        }

        checkCurrentConfig();

        fragmentManager = getSupportFragmentManager();
        Fragment homeScreenFragment = fragmentManager.findFragmentByTag("homeScreen");

        if (homeScreenFragment == null) {
            homeScreenFragment = new HomeScreenFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.homescreen_container, homeScreenFragment, "homeScreen").commit();
        }

        if (isFirstRun()) {
            //Show welcomeActivity if first time user
            Intent welcomeIntent = new Intent(this, WelcomeScreenViewPagerActivity.class);
            startActivity(welcomeIntent);

        }
    }

    private void checkCurrentConfig() {
        //Log.d("TAG", "Getting the latest config...");
        ParseConfig.getInBackground(new ConfigCallback() {
            @Override
            public void done(ParseConfig config, ParseException e) {
                new ConfigService();
            }
        });
    }

    private void setupCustomActionBar() {
        View actionBarCustomView = LayoutInflater.from(this).inflate(R.layout.homescreen_actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(actionBarCustomView, params);

        setUpPopupWindow();
        ImageButton overflow = (ImageButton) actionBarCustomView.findViewById(R.id.overflow);
        overflow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.showAsDropDown(v);
            }
        });
    }

    private void setUpPopupWindow() {
        popupWindow = new PopupWindow(this);
        // some other visual settings
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
        popupWindow.setWidth(getPixelForDp(120));
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        // the drop down list is a list view
        final ListView popUpDropDownList = new ListView(this);
        List<String> optionList = new ArrayList<String>();
        optionList.add(0, "Bookmarks");
        optionList.add(1, "Edit Feed");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, optionList){


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // setting the ID and text for every items in the list
                String text = getItem(position);

                // visual settings for the list item
                TextView listItem = new TextView(HomeScreenActivity.this);

                listItem.setText(text);
                listItem.setTag(position);
                listItem.setTextSize(16);
                listItem.setPadding(24, 16, 16, 16);
                listItem.setBackgroundColor(Color.parseColor("#333333"));
                listItem.setTextColor(Color.parseColor("#FFFFFF"));
                listItem.setMinHeight(getPixelForDp(36));
                listItem.setGravity(Gravity.CENTER_VERTICAL);

                return listItem;
            }
        };

        popUpDropDownList.setAdapter(arrayAdapter);
        popupWindow.setContentView(popUpDropDownList);

        popUpDropDownList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) popUpDropDownList.getAdapter().getItem(position);
                popupWindow.dismiss();

                if (item.equalsIgnoreCase("Edit Feed")) {
                    Intent settingIntent = new Intent(HomeScreenActivity.this, SettingsActivity.class);
                    startActivity(settingIntent);
                } else if (item.equalsIgnoreCase("Bookmarks")) {
                    Intent bookmarkIntent = new Intent(HomeScreenActivity.this, BookmarkListActivity.class);
                    startActivity(bookmarkIntent);
                }
            }
        });
    }

    private int getPixelForDp(int dp) {
        return (int) (dp * this.getResources().getDisplayMetrics().density);
    }


    private boolean isFirstRun() {
        SharedPreferences preferences = getSharedPreferences(Constants.IS_FIRST_RUN, MODE_PRIVATE);
        return preferences.getBoolean(Constants.IS_FIRST_RUN, true);
    }

    @Override
    public void showAddNewCellDialog(List<CellModel> cellList, GridAdapter adapter) {
        this.cellList = cellList;
        this.adapter = adapter;

        AddCellDialogFragment addCellDialog = new AddCellDialogFragment();
        addCellDialog.show(fragmentManager, "addCell");

    }

    @Override
    public void showEditCellDialog(int position, String newspaper, String category, List<CellModel> cellList,
                                   GridAdapter adapter) {
        this.cellList = cellList;
        this.adapter = adapter;

        //Remove "custom" tag if on newspaper page
        if (newspaper.contains("_custom")) {
            newspaper = newspaper.substring(0, newspaper.length() - 7);
        }
        int newspaperId = cellList.get(position).getDefaultNewspaperId(newspaper);

        EditCellDialogFragment editCellDialog = EditCellDialogFragment.newInstance(position, newspaperId, category);
        editCellDialog.show(fragmentManager, "editCell");

    }

    @Override
    public void onFinishEditingListener(int editPosition, String npName, String cat, boolean edited) {

        if (edited) {

            CsvFileUtil csvFile = new CsvFileUtil(this);
            NewsCatModel csvObject = csvFile.getObjectByNPName(npName, cat);

            if (!isCellPresent(csvObject)) {
                CellModel newCell = new CellModel(csvObject.getNpImage(), csvObject.getCatName());
                cellList.set(editPosition, newCell);
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Category Already Present.", Toast.LENGTH_LONG).show();
            }

            csvFile.closeUtilObject();

            CellListUtil.saveCellListToSharedPrefs(this, cellList);
            UserSelectionUtil.updateUserSelectionSharedPrefs(this);
        }

    }

    @Override
    public void onFinishAddingListener(String npName, String cat) {

        try {
            CsvFileUtil csvFile = new CsvFileUtil(this);
            NewsCatModel csvObject = csvFile.getObjectByNPName(npName, cat);

            if (!isCellPresent(csvObject)) {
                CellModel newCell = new CellModel(csvObject.getNpImage(), csvObject.getCatName());
                cellList.add(cellList.size()-1, newCell);
                adapter.notifyDataSetChanged();
            }
            Log.d(TAG, cellList.toString());

            csvFile.closeUtilObject();

            CellListUtil.saveCellListToSharedPrefs(this, cellList);
            UserSelectionUtil.updateUserSelectionSharedPrefs(this);
        } catch (Exception e) {
            Crashlytics.log(npName + ", " + cat);
            Crashlytics.log(cellList.toString());
            Crashlytics.logException(e);
        }

    }

    private boolean isCellPresent(NewsCatModel csvObject) {
        boolean isCellPresent = false;
        for (CellModel cellObj : cellList) {
            boolean catMatch = (cellObj.getTitleCategory().equalsIgnoreCase(csvObject.getCatName()));
            boolean npMatch = (cellObj.getNewspaperImage().equalsIgnoreCase(csvObject.getNpImage()));
            if ((catMatch && npMatch)) {
                isCellPresent = true;
                break;
            }
        }
        return isCellPresent;
    }

}
