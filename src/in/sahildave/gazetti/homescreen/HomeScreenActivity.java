package in.sahildave.gazetti.homescreen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.crashlytics.android.Crashlytics;
import com.parse.ParseAnalytics;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.bookmarks.BookmarkListActivity;
import in.sahildave.gazetti.homescreen.adapter.*;
import in.sahildave.gazetti.homescreen.adapter.AddCellDialogFragment.AddCellDialogListener;
import in.sahildave.gazetti.homescreen.adapter.EditCellDialogFragment.EditCellDialogListener;
import in.sahildave.gazetti.homescreen.newcontent.DialogNewContent;
import in.sahildave.gazetti.homescreen.newcontent.DialogNewContent.NewContentCallback;
import in.sahildave.gazetti.preference.FeedSelectSettingsActivity;
import in.sahildave.gazetti.util.*;
import in.sahildave.gazetti.util.GazettiEnums.Category;
import in.sahildave.gazetti.util.GazettiEnums.Newspapers;
import in.sahildave.gazetti.welcomescreen.WelcomeScreenViewPagerActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HomeScreenActivity extends ActionBarActivity implements HomeScreenFragment.Callbacks,
        AddCellDialogListener, EditCellDialogListener, NewContentCallback {

    private static final String LOG_TAG = HomeScreenActivity.class.getName();
    private static final int ENTRY_ANIMATION_LOGO = 750;
    private static final int ENTRY_ANIMATION_TITLE = 1000;

    private FragmentManager fragmentManager;
    private List<CellModel> cellList;
    private GridAdapter adapter;
    private GazettiEnums gazettiEnums;
    private int compiledAssetVersion;
    private SharedPreferences sharedPreferences;
    private HomeScreenFragment homeScreenFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.d(TAG, "onCreate - " + (null == savedInstanceState));
        super.onCreate(savedInstanceState);
        ParseAnalytics.trackAppOpened(getIntent());

        setupCustomActionBar();

        setContentView(R.layout.homescreen_activity);

        // When coming from WelcomeScreen but without completing the task, the intent would have "Exit Me"
        if (getIntent().getBooleanExtra("Exit me", false)) {
            finish();
            return;
        }

        sharedPreferences = getSharedPreferences(Constants.GAZETTI, Context.MODE_PRIVATE);
        compiledAssetVersion = getResources().getInteger(R.integer.assetVersion);

        gazettiEnums = new GazettiEnums();
        fragmentManager = getSupportFragmentManager();
        homeScreenFragment = (HomeScreenFragment) fragmentManager.findFragmentByTag("homeScreen");

        if (homeScreenFragment == null) {
            homeScreenFragment = new HomeScreenFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.homescreen_container, homeScreenFragment, "homeScreen").commit();
        }

        if (isFirstRun()) {
            //Show welcomeActivity if first time user
            Intent welcomeIntent = new Intent(this, WelcomeScreenViewPagerActivity.class);
            startActivity(welcomeIntent);
        } else if(isAssetFileNew()) {
            try {
                InputStream is = getAssets().open("newData.json");
                if(is != null){
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    DialogNewContent dialogFragment = new DialogNewContent();
                    dialogFragment.show(ft, "dialog");
                    NewsCatFileUtil.getInstance(this).updateNewsCatFileWithNewAssets();

                    is.close();
                }
            } catch (IOException e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
        }

        sharedPreferences.edit().putInt(Constants.ASSET_VERSION, compiledAssetVersion).apply();
    }

    private void setupCustomActionBar() {
        View actionBarCustomView = LayoutInflater.from(this).inflate(R.layout.homescreen_actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(actionBarCustomView, params);
        TextView heading = (TextView) actionBarCustomView.findViewById(R.id.homescreen_action_bar_title);
        ImageView gazettiLogo = (ImageView) actionBarCustomView.findViewById(R.id.gazetti_logo);

        heading.setAnimation(getEntryAnimation(ENTRY_ANIMATION_TITLE));
        gazettiLogo.setAnimation(getEntryAnimation(ENTRY_ANIMATION_LOGO));
    }

    private AnimationSet getEntryAnimation(int inAnimationDuration) {
        //In
        AnimationSet mInAnimationSet = new AnimationSet(false);

        TranslateAnimation mSlideInAnimation = new TranslateAnimation(
                TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0.0f,
                TranslateAnimation.RELATIVE_TO_SELF, -1.0f,
                TranslateAnimation.RELATIVE_TO_SELF, 0.0f);
        mSlideInAnimation.setFillAfter(true);

        AlphaAnimation mFadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
        mFadeInAnimation.setFillAfter(true);

        mInAnimationSet.addAnimation(mSlideInAnimation);
        mInAnimationSet.addAnimation(mFadeInAnimation);

        mInAnimationSet.setDuration(inAnimationDuration);

        return mInAnimationSet;

    }

    private boolean isFirstRun() {
        SharedPreferences preferences = getSharedPreferences(Constants.IS_FIRST_RUN, MODE_PRIVATE);
        return preferences.getBoolean(Constants.IS_FIRST_RUN, true);
    }

    public boolean isAssetFileNew(){
        int sharedPrefsAssetVersion = sharedPreferences.getInt(Constants.ASSET_VERSION, 0);
        return compiledAssetVersion > sharedPrefsAssetVersion;
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
        int newspaperId = Integer.parseInt(cellList.get(position).getNewspaperId());

        EditCellDialogFragment editCellDialog = EditCellDialogFragment.newInstance(position, newspaperId, category);
        editCellDialog.show(fragmentManager, "editCell");

    }

    @Override
    public void openEditFeedSettings() {
        Intent settingIntent = new Intent(HomeScreenActivity.this, FeedSelectSettingsActivity.class);
        startActivity(settingIntent);
    }

    @Override
    public void startBookmarkActivity() {
        Intent bookmarkIntent = new Intent(HomeScreenActivity.this, BookmarkListActivity.class);
        startActivity(bookmarkIntent);
    }

    @Override
    public void onFinishEditingListener(int editPosition, String npName, String cat, boolean edited) {

        try {
            if (edited) {
                NewsCatModel newsCatModel = createNewsCatModel(npName, cat);
                if (!isCellPresent(newsCatModel)) {
                    CellModel newCell = new CellModel(newsCatModel);
                    CellModel oldCell = cellList.get(editPosition);
                    cellList.set(editPosition, newCell);
                    adapter.notifyDataSetChanged();

                    UserPrefUtil.getInstance(this).replaceUserPref(oldCell, newCell);
                } else {
                    Toast.makeText(this, "Category Already Present.", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Crashlytics.log(npName + ", " + cat);
            Crashlytics.logException(e);
            if(cellList!=null){
                Crashlytics.log(cellList.toString());
            } else {
                Crashlytics.log("Cell List is null!!");
            }
        }

    }

    private NewsCatModel createNewsCatModel(String npName, String cat) {
        Newspapers npEnum = gazettiEnums.getNewspaperFromName(npName);
        Category categoryEnum = gazettiEnums.getCategoryFromName(cat);
        return new NewsCatModel(npEnum, categoryEnum);
    }

    @Override
    public void onFinishAddingListener(String npName, String cat) {

        try {
            NewsCatModel newsCatModel = createNewsCatModel(npName, cat);
            if (!isCellPresent(newsCatModel)) {
                CellModel newCell = new CellModel(newsCatModel);
                cellList.add(cellList.size()-1, newCell);
                adapter.notifyDataSetChanged();
                UserPrefUtil.getInstance(this).addUserPref(newCell);
            }
        } catch (Exception e) {
            Crashlytics.log(npName + ", " + cat);
            Crashlytics.logException(e);
            if(cellList!=null){
                Crashlytics.log(cellList.toString());
            } else {
                Crashlytics.log("Cell List is null!!");
            }
        }

    }

    private boolean isCellPresent(NewsCatModel csvObject) {
        boolean isCellPresent = false;
        for (CellModel cellObj : cellList) {
            boolean catMatch = (cellObj.getCategoryTitle().equalsIgnoreCase(csvObject.getCategoryTitle()));
            boolean npMatch = (cellObj.getNewspaperImage().equalsIgnoreCase(csvObject.getNewspaperImage()));
            if ((catMatch && npMatch)) {
                isCellPresent = true;
                break;
            }
        }
        return isCellPresent;
    }

    @Override
    public void onDestroy() {
        NewsCatFileUtil.getInstance(this).destroyUtil();
        UserPrefUtil.getInstance(this).destroyUtil();
        ConfigService.getInstance().destroyConfigService();
        super.onDestroy();
    }

    @Override
    public void newContentDoneButton() {
        if(homeScreenFragment!=null){
            homeScreenFragment.refreshCellGrid();
        }
    }
}
