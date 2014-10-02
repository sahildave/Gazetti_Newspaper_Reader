package in.sahildave.gazetti.homescreen;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageButton;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.parse.ConfigCallback;
import com.parse.ParseConfig;
import com.parse.ParseException;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.homescreen.adapter.*;
import in.sahildave.gazetti.homescreen.adapter.AddCellDialogFragment.AddCellDialogListener;
import in.sahildave.gazetti.homescreen.adapter.EditCellDialogFragment.EditCellDialogListener;
import in.sahildave.gazetti.preference.SettingsActivity;
import in.sahildave.gazetti.util.*;
import in.sahildave.gazetti.welcomescreen.WelcomeScreenViewPagerActivity;

import java.util.List;

public class HomeScreenActivity extends ActionBarActivity implements HomeScreenFragment.Callbacks,
        AddCellDialogListener, EditCellDialogListener {

    private String TAG = "HomeScreen";

    private Fragment homeScreenFragment;
    private FragmentManager fragmentManager;
    private List<CellModel> cellList;
    private ImageAdapter adapter;
    private View actionBarCustomView;
    private ImageButton settingsFromActionbar;
    private UserSelectionUtil userSelectionUtil;
    private CellListUtil cellListUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate - " + (null == savedInstanceState));
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);

        setupCustomActionBar();

        setContentView(R.layout.homescreen_activity);
        settingsFromActionbar = (ImageButton) actionBarCustomView.findViewById(R.id.settingsFromActionBar);
        settingsFromActionbar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeScreenActivity.this, "Touched Settings", Toast.LENGTH_SHORT).show();
                Intent settingIntent = new Intent(HomeScreenActivity.this, SettingsActivity.class);
                startActivity(settingIntent);
            }
        });

        // When coming from WelcomeScreen but without completing the task, the intent would have "Exit Me"
        if (getIntent().getBooleanExtra("Exit me", false)) {
            this.finish();
            return; // add this to prevent from doing unnecessary stuffs
        }

        checkCurrentConfig();

        fragmentManager = getSupportFragmentManager();
        homeScreenFragment = fragmentManager.findFragmentByTag("homeScreen");

        if (homeScreenFragment == null) {
            homeScreenFragment = new HomeScreenFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.homescreen_container, homeScreenFragment, "homeScreen").commit();
        }

        //TODO: Move to Application Class.?
        if (isFirstRun()) {
            //Show welcomeActivity if first time user
            Intent welcomIntent = new Intent(this, WelcomeScreenViewPagerActivity.class);
            startActivity(welcomIntent);

        }

        initUtils();

    }

    private void checkCurrentConfig() {
        Log.d("TAG", "Getting the latest config...");
        ParseConfig.getInBackground(new ConfigCallback() {
            @Override
            public void done(ParseConfig config, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Yay! Config was fetched from the server. - version " + config.getNumber("version"));
                } else {
                    Log.e(TAG, "Failed to fetch. Using Cached Config.");
                    config = ParseConfig.getCurrentConfig();
                }

                new ConfigService();
            }
        });
    }

    private void initUtils() {
        cellListUtil = new CellListUtil(this);
        userSelectionUtil = new UserSelectionUtil(this);
    }

    private void setupCustomActionBar() {
        actionBarCustomView = LayoutInflater.from(this).inflate(R.layout.homescreen_actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(actionBarCustomView, params);
    }

    private boolean isFirstRun() {
        SharedPreferences preferences = getSharedPreferences(Constants.IS_FIRST_RUN, MODE_PRIVATE);
        boolean firstRun = preferences.getBoolean(Constants.IS_FIRST_RUN, true);
        return firstRun;
    }

    @Override
    public void showAddNewCellDialog(List<CellModel> cellList, ImageAdapter adapter) {
        this.cellList = cellList;
        this.adapter = adapter;

        AddCellDialogFragment addCellDialog = new AddCellDialogFragment();
        addCellDialog.show(fragmentManager, "addCell");

    }

    @Override
    public void showEditCellDialog(int position, String newspaper, String category, List<CellModel> cellList,
                                   ImageAdapter adapter) {
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

            cellListUtil.saveCellListToSharedPrefs(cellList);
            userSelectionUtil.updateUserSelectionSharedPrefs();
        }

    }

    @Override
    public void onFinishAddingListener(String npName, String cat) {

        CsvFileUtil csvFile = new CsvFileUtil(this);
        NewsCatModel csvObject = csvFile.getObjectByNPName(npName, cat);

        if (!isCellPresent(csvObject)) {
            CellModel newCell = new CellModel(csvObject.getNpImage(), csvObject.getCatName());
            cellList.add(cellList.size() - 1, newCell);
            adapter.notifyDataSetChanged();
        }

        csvFile.closeUtilObject();

        cellListUtil.saveCellListToSharedPrefs(cellList);
        userSelectionUtil.updateUserSelectionSharedPrefs();

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
