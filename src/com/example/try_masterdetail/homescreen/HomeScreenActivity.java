package com.example.try_masterdetail.homescreen;

import java.util.List;

import com.example.try_masterdetail.R;
import com.example.try_masterdetail.homescreen.adapter.AddCellDialogFragment;
import com.example.try_masterdetail.homescreen.adapter.CSVObject;
import com.example.try_masterdetail.homescreen.adapter.EditCellDialogFragment;
import com.example.try_masterdetail.homescreen.adapter.GridCellModel;
import com.example.try_masterdetail.homescreen.adapter.ImageAdapter;
import com.example.try_masterdetail.homescreen.adapter.ReadCSV;
import com.example.try_masterdetail.homescreen.adapter.AddCellDialogFragment.AddCellDialogListener;
import com.example.try_masterdetail.homescreen.adapter.EditCellDialogFragment.EditCellDialogListener;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class HomeScreenActivity extends FragmentActivity implements HomeScreenFragment.Callbacks,
		AddCellDialogListener, EditCellDialogListener {
	private Fragment homeScreenFragment;
	private FragmentManager fm;
	List<GridCellModel> cellList;
	ImageAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homescreen_activity);
		fm = getSupportFragmentManager();
		homeScreenFragment = fm.findFragmentByTag("homeScreen");

		if (homeScreenFragment == null) {
			homeScreenFragment = new HomeScreenFragment();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.homescreen_container, homeScreenFragment, "homeScreen").commit();
		}

		if (isFirstTime()) {

			new AlertDialog.Builder(HomeScreenActivity.this).setTitle("Updated").setMessage(R.string.first_message)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					}).show();

		}

	}

	/*
	 * Checks that application runs first time and write flag at
	 * SharedPreferences
	 * 
	 * @return true if 1st time
	 */
	private boolean isFirstTime() {
		SharedPreferences preferences = getPreferences(MODE_PRIVATE);
		boolean ranBefore = preferences.getBoolean("RanBefore", false);
		if (!ranBefore) {
			// first time
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean("RanBefore", true);
			editor.commit();
		}
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

			ReadCSV readCsv = new ReadCSV(this);
			CSVObject csvObject = readCsv.getObjectByNPName(npName, cat);

			GridCellModel newCell = new GridCellModel(csvObject.getNpImage(), csvObject.getCatName());
			cellList.set(editPosition, newCell);
			adapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onFinishAddingListener(String npName, String cat) {

		ReadCSV readCsv = new ReadCSV(this);
		CSVObject csvObject = readCsv.getObjectByNPName(npName, cat);

		GridCellModel newCell = new GridCellModel(csvObject.getNpImage(), csvObject.getCatName());
		cellList.add(cellList.size() - 1, newCell);
		adapter.notifyDataSetChanged();

		readCsv.close();

	}

}
