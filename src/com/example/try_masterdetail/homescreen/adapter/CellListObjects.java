package com.example.try_masterdetail.homescreen.adapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.try_masterdetail.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CellListObjects {
	Context context;
	List<GridCellModel> cellList;

	public CellListObjects(Context context) {
		this.context = context;
	}

	public void updateCellListByFeedPrefs() {

		SharedPreferences sharedPref = context.getSharedPreferences("FeedPrefs", Context.MODE_PRIVATE);
		String defValue = context.getResources().getString(R.string.pref_feeds_selected_defvalue);
		String str = sharedPref.getString("feedPreference", defValue);

		Gson gson = new Gson();
		Type type = new TypeToken<HashMap<Integer, boolean[]>>() {
		}.getType();

		HashMap<Integer, boolean[]> feedsRequired = gson.fromJson(str, type);

		if (cellList == null) {
			cellList = new ArrayList<GridCellModel>();
		} else {
			cellList.clear();
		}

		for (Integer npIdint : feedsRequired.keySet()) {
			String npIdString = npIdint.toString();
			// Log.d("FEED", npIdString);

			for (int catIdint = 0; catIdint < feedsRequired.get(npIdint).length; catIdint++) {
				// Log.d("FEED", " - " + catIdint);

				Boolean isSelected = feedsRequired.get(npIdint)[catIdint];
				if (isSelected) {
					ReadNewsCatCSV readCsv = new ReadNewsCatCSV(context);
					System.out.println(npIdString + ", " + catIdint);

					String catIdString = String.valueOf(catIdint);
					NewsCatCsvObject csvObjectNpId = readCsv.getObjectByNPId(npIdString, catIdString);

					String npImageString = csvObjectNpId.getNpImage();
					String catNameString = csvObjectNpId.getCatName();

					cellList.add(new GridCellModel(npImageString, catNameString));
					readCsv.close();
				}

			}

		}

		saveCellList(cellList);

	}

	public void saveCellList(List<GridCellModel> cellList) {
		Gson gson = new Gson();
		String cellListString = gson.toJson(cellList);
		System.out.println("CELLLIST SAVED - " + cellListString);

		SharedPreferences cellListPrefs = context.getSharedPreferences("CellList", Context.MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = cellListPrefs.edit();
		prefEditor.putString("CellListPreference", cellListString);
		int oldFeedVersion = cellListPrefs.getInt("feedVersion", 0);
		prefEditor.putInt("feedVersion", ++oldFeedVersion);
		prefEditor.commit();

	}

	public List<GridCellModel> getCellListFromPrefs() {

		SharedPreferences sharedPref = context.getSharedPreferences("CellList", Context.MODE_PRIVATE);
		String defValueCellList = context.getResources().getString(R.string.cell_list_defvalue);
		String str = sharedPref.getString("CellListPreference", defValueCellList);

		Gson gson = new Gson();
		Type type = new TypeToken<List<GridCellModel>>() {
		}.getType();

		List<GridCellModel> cellListFromPrefs = gson.fromJson(str, type);

		return cellListFromPrefs;
	}
}
