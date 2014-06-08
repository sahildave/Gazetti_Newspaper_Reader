package com.example.try_masterdetail.preference;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.try_masterdetail.R;
import com.example.try_masterdetail.homescreen.adapter.GridCellModel;
import com.example.try_masterdetail.homescreen.adapter.NewsCatCsvObject;
import com.example.try_masterdetail.homescreen.adapter.ReadNewsCatCSV;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FeedPrefObject {

	Context context;
	List<GridCellModel> cellList;

	public FeedPrefObject(Context context) {
		this.context = context;
	}

	public void updateFeedPrefs() {

		SharedPreferences sharedPref = context.getSharedPreferences("CellList", Context.MODE_PRIVATE);
		String defValueCellList = context.getResources().getString(R.string.cell_list_defvalue);
		String str = sharedPref.getString("CellListPreference", defValueCellList);

		Gson gson = new Gson();
		Type type = new TypeToken<List<GridCellModel>>() {
		}.getType();

		List<GridCellModel> cellListFromPrefs = gson.fromJson(str, type);

		HashMap<Integer, boolean[]> mChildCheckStates = new HashMap<Integer, boolean[]>();

		for (GridCellModel objects : cellListFromPrefs) {
			String npImage = objects.getNewspaperImage();
			String catName = objects.getTitleCategory();
			System.out.println(npImage + ", " + catName);

			if (!npImage.equals("add_new")) {
				ReadNewsCatCSV readCsv = new ReadNewsCatCSV(context);
				NewsCatCsvObject csvObjectNpImage = readCsv.getObjectByNPImage(npImage, catName);

				System.out.println(csvObjectNpImage.toString());

				if (csvObjectNpImage != null && csvObjectNpImage.getNpId() != null) {
					String npIdString = csvObjectNpImage.getNpId();
					String catIdString = csvObjectNpImage.getCatId();

					int npIdint = Integer.parseInt(npIdString);
					int catIdint = Integer.parseInt(catIdString);

					if (!mChildCheckStates.containsKey(npIdint)) {
						boolean value[] = new boolean[5];
						mChildCheckStates.put(npIdint, value);
					}

					mChildCheckStates.get(npIdint)[catIdint] = true;
				}
				saveFeedPrefs(mChildCheckStates);
				readCsv.close();
			}
		}

	}

	public void saveFeedPrefs(HashMap<Integer, boolean[]> mChildCheckStates) {

		Gson gson = new Gson();
		String feedsChecked = gson.toJson(mChildCheckStates);
		SharedPreferences feedPrefs = context.getSharedPreferences("FeedPrefs", Context.MODE_PRIVATE);
		SharedPreferences.Editor prefEditor = feedPrefs.edit();
		prefEditor.putString("feedPreference", feedsChecked);
		prefEditor.commit();

		Log.d("HomeScreen", "New Feeds - " + feedsChecked);
	}
}
