package com.example.try_masterdetail.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.example.try_masterdetail.R;
import com.example.try_masterdetail.homescreen.adapter.CellModel;
import com.example.try_masterdetail.homescreen.adapter.NewsCatModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CellListUtil {
    private String TAG = "HomeScreen";
    private static Context context;
    List<CellModel> cellList;

    public CellListUtil(Context context) {
        this.context = context;
    }

    public void updateCellListByUserSelection() {

        HashMap<Integer, boolean[]> cellListFromUserSelection = UserSelectionUtil.getUserFeedSelection();

        if (cellList == null) {
            cellList = new ArrayList<CellModel>();
        } else {
            cellList.clear();
        }

        for (Integer npIdint : cellListFromUserSelection.keySet()) {

            for (int catIdint = 0; catIdint < cellListFromUserSelection.get(npIdint).length; catIdint++) {

                Boolean isSelected = cellListFromUserSelection.get(npIdint)[catIdint];

                if (isSelected) {
                    CsvFileUtil csvFile = new CsvFileUtil(context);

                    String npIdString = String.valueOf(npIdint);
                    String catIdString = String.valueOf(catIdint);

                    Log.d(TAG, "Adding ID - "+npIdString+", "+catIdString);

                    NewsCatModel newsCatObject = csvFile.getObjectByNPId(npIdString, catIdString);
                    String npImageString = newsCatObject.getNpImage();
                    String catNameString = newsCatObject.getCatName();

                    Log.d(TAG, "Adding - "+npImageString+", "+catNameString);

                    cellList.add(new CellModel(npImageString, catNameString));
                    csvFile.closeUtilObject();
                }

            }

        }

        saveCellListToSharedPrefs(cellList);

    }

    public void saveCellListToSharedPrefs(List<CellModel> cellList) {
        SharedPreferences cellListPrefs = context.getSharedPreferences("CellList", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = cellListPrefs.edit();

        Gson gson = new Gson();
        String cellListString = gson.toJson(cellList);

        prefEditor.putString("CellListPreference", cellListString);
        int oldFeedVersion = cellListPrefs.getInt("feedVersion", 0);
        prefEditor.putInt("feedVersion", ++oldFeedVersion);
        prefEditor.commit();
    }

    public static List<CellModel> getCellListFromSharedPrefs() {

        SharedPreferences sharedPref = context.getSharedPreferences("CellList", Context.MODE_PRIVATE);
        String defValueCellList = context.getResources().getString(R.string.cell_list_defvalue);
        String str = sharedPref.getString("CellListPreference", defValueCellList);

        Gson gson = new Gson();
        Type type = new TypeToken<List<CellModel>>() {}.getType();

        return gson.fromJson(str, type);
    }
}
