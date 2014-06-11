package com.example.try_masterdetail.homescreen;

import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.example.try_masterdetail.R;
import com.example.try_masterdetail.homescreen.adapter.AddCellDialogFragment;
import com.example.try_masterdetail.homescreen.adapter.AddCellDialogFragment.AddCellDialogListener;
import com.example.try_masterdetail.homescreen.adapter.CellListObjects;
import com.example.try_masterdetail.homescreen.adapter.EditCellDialogFragment;
import com.example.try_masterdetail.homescreen.adapter.EditCellDialogFragment.EditCellDialogListener;
import com.example.try_masterdetail.homescreen.adapter.GridCellModel;
import com.example.try_masterdetail.homescreen.adapter.ImageAdapter;
import com.example.try_masterdetail.homescreen.adapter.NewsCatCsvObject;
import com.example.try_masterdetail.homescreen.adapter.ReadNewsCatCSV;
import com.example.try_masterdetail.news_activities.WebsiteListActivity;
import com.example.try_masterdetail.preference.FeedPrefObject;
import com.example.try_masterdetail.preference.SettingsActivity;
import com.example.try_masterdetail.welcomescreen.WelcomeScreenViewPagerActivity;

public class HomeScreenActivity extends ActionBarActivity implements HomeScreenFragment.Callbacks,
		AddCellDialogListener, EditCellDialogListener {
	private Fragment homeScreenFragment;
	private FragmentManager fm;
	private List<GridCellModel> cellList;
	private ImageAdapter adapter;
	public View actionBarCustomView;
	private ImageButton settingsFromActionbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		setContentView(R.layout.homescreen_activity);

		actionBarCustomView = LayoutInflater.from(this).inflate(R.layout.homescreen_actionbar, null);
		ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
				ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(actionBarCustomView, params);

		settingsFromActionbar = (ImageButton) actionBarCustomView.findViewById(R.id.settingsFromActionBar);
		settingsFromActionbar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(HomeScreenActivity.this, "Touched Settings", Toast.LENGTH_SHORT).show();
				Intent settingIntent = new Intent(HomeScreenActivity.this, SettingsActivity.class);
				startActivity(settingIntent);

			}
		});

		if (getIntent().getBooleanExtra("Exit me", false)) {
			finish();
			return; // add this to prevent from doing unnecessary stuffs
		}

		fm = getSupportFragmentManager();
		homeScreenFragment = fm.findFragmentByTag("homeScreen");

		if (homeScreenFragment == null) {
			homeScreenFragment = new HomeScreenFragment();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.homescreen_container, homeScreenFragment, "homeScreen").commit();
		}

		if (isFirstTime()) {

			// new
			// AlertDialog.Builder(HomeScreenActivity.this).setTitle("Updated").setMessage(R.string.first_message)
			// .setPositiveButton("OK", new DialogInterface.OnClickListener() {
			// public void onClick(DialogInterface dialog, int id) {
			// dialog.cancel();
			// }
			// }).show();

			Intent welcomIntent = new Intent(this, WelcomeScreenViewPagerActivity.class);
			startActivity(welcomIntent);

		}

	}

	/*
	 * Checks that application runs first time and write flag at
	 * SharedPreferences
	 * 
	 * @return true if 1st time
	 */
	private boolean isFirstTime() {
		SharedPreferences preferences = getSharedPreferences("RanBeforePref", MODE_PRIVATE);
		boolean ranBefore = preferences.getBoolean("RanBefore", false);
		System.out.print("Actvitiy - " + ranBefore);
		return !ranBefore;
	}

	@Override
	public void showAddNewCellDialog(List<GridCellModel> cellList, ImageAdapter adapter) {
		this.cellList = cellList;
		this.adapter = adapter;

		AddCellDialogFragment addCellDialog = new AddCellDialogFragment();
		addCellDialog.show(fm, "addCell");

	}

	@Override
	public void showEditCellDialog(int position, String newspaper, String category, List<GridCellModel> cellList,
			ImageAdapter adapter) {
		this.cellList = cellList;
		this.adapter = adapter;
		if (newspaper.length() > 7 && newspaper.substring(newspaper.length() - 7).equals("_custom")) {
			newspaper = newspaper.substring(0, newspaper.length() - 7);
		}
		int newspaperId = cellList.get(position).getDefaultNewspaperId(newspaper);

		EditCellDialogFragment editCellDialog = EditCellDialogFragment.newInstance(position, newspaperId, category);
		editCellDialog.show(fm, "editCell");

	}

	@Override
	public void onFinishEditingListener(int editPosition, String npName, String cat, boolean edited) {

		if (edited) {

			ReadNewsCatCSV readCsv = new ReadNewsCatCSV(this);
			NewsCatCsvObject csvObject = readCsv.getObjectByNPName(npName, cat);

			GridCellModel newCell = new GridCellModel(csvObject.getNpImage(), csvObject.getCatName());
			cellList.set(editPosition, newCell);
			adapter.notifyDataSetChanged();

			readCsv.close();

			CellListObjects cellListObject = new CellListObjects(this);
			cellListObject.saveCellList(cellList);

			// Update feedPrefs
			FeedPrefObject feedPrefObject = new FeedPrefObject(this);
			feedPrefObject.updateFeedPrefs();
		}

	}

	@Override
	public void onFinishAddingListener(String npName, String cat) {

		ReadNewsCatCSV readCsv = new ReadNewsCatCSV(this);
		NewsCatCsvObject csvObject = readCsv.getObjectByNPName(npName, cat);

		GridCellModel newCell = new GridCellModel(csvObject.getNpImage(), csvObject.getCatName());
		cellList.add(cellList.size() - 1, newCell);
		adapter.notifyDataSetChanged();

		readCsv.close();

		CellListObjects cellListObject = new CellListObjects(this);
		cellListObject.saveCellList(cellList);

		// Update feedPrefs
		FeedPrefObject feedPrefObject = new FeedPrefObject(this);
		feedPrefObject.updateFeedPrefs();

	}

}
