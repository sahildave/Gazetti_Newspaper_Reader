package in.sahildave.gazetti.util;

import android.content.Context;
import in.sahildave.gazetti.homescreen.adapter.CellModel;
import in.sahildave.gazetti.util.GazettiEnums.Category;
import in.sahildave.gazetti.util.GazettiEnums.Newspapers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sahil on 9/11/14.
 */
public class UserPrefUtil {
    private static final String LOG_TAG = UserPrefUtil.class.getName();
    private static UserPrefUtil _instance = null;
    private Context context;

    public static synchronized UserPrefUtil getInstance(Context context){
        if (_instance == null) {
            _instance = new UserPrefUtil(context.getApplicationContext());
        }
        return _instance;
    }

    private UserPrefUtil(Context parentContext) {
        context = parentContext;
    }

    public List<CellModel> getUserPrefCellList(){

        List<CellModel> returnList = new ArrayList<CellModel>();
        Map<String, List<String>> userPrefMap = NewsCatFileUtil.getInstance(context).getUserSelectionMap();

        GazettiEnums gazettiEnums = new GazettiEnums();
        for (String newspaper : userPrefMap.keySet()) {
            List<String> categoriesSelected = userPrefMap.get(newspaper);
            Newspapers npEnum = gazettiEnums.getNewspaperFromName(newspaper);
            for (String category : categoriesSelected) {
                Category catEnum = gazettiEnums.getCategoryFromName(category);
                CellModel cellModel = new CellModel(npEnum, catEnum);
                returnList.add(cellModel);
            }
        }

        //Log.d(LOG_TAG, "Returning CellList - "+returnList);
        return returnList;
    }

    public void replaceUserPref(CellModel oldCell, CellModel newCell){
        Map<String, List<String>> userPrefMap = NewsCatFileUtil.getInstance(context).getUserSelectionMap();
        String oldNewspaper = oldCell.getNewspaperTitle();
        String oldCategory = oldCell.getCategoryTitle();
        String newNewspaper = newCell.getNewspaperTitle();
        String newCategory = newCell.getCategoryTitle();

        if(oldNewspaper.equalsIgnoreCase(newNewspaper)){
            if(userPrefMap.containsKey(oldNewspaper)) {
                List<String> categories = userPrefMap.get(oldNewspaper);
                if(categories.contains(oldCategory)){
                    categories.remove(oldCategory);
                    categories.add(newCategory);

                    userPrefMap.remove(oldNewspaper);
                    //Log.d(LOG_TAG, "Removed - "+oldCell.toString()+", Added - "+newCell.toString());
                    updateUserSelectionMap(userPrefMap, oldNewspaper, categories);
                }
            }
        } else {
            deleteUserPref(oldCell);
            addUserPref(newCell);
        }
    }

    public void addUserPref(CellModel newCell){
        Map<String, List<String>> userPrefMap = NewsCatFileUtil.getInstance(context).getUserSelectionMap();
        String newspaper = newCell.getNewspaperTitle();
        String category = newCell.getCategoryTitle();

        List<String> categories;
        if(userPrefMap.containsKey(newspaper)){
            categories = userPrefMap.get(newspaper);
            if(!categories.contains(category)){
                categories.add(category);
                userPrefMap.remove(newspaper);
            }
        } else {
            categories = new ArrayList<String>();
            categories.add(category);
        }
        //Log.d(LOG_TAG, "Added - "+newCell.toString());
        updateUserSelectionMap(userPrefMap, newspaper, categories);
    }

    public void deleteUserPref(CellModel deleteCell){
        Map<String, List<String>> userPrefMap = NewsCatFileUtil.getInstance(context).getUserSelectionMap();
        String newspaper = deleteCell.getNewspaperTitle();
        String category = deleteCell.getCategoryTitle();

        if(userPrefMap.containsKey(newspaper)){
            List<String> categories = userPrefMap.get(newspaper);
            if(categories.contains(category)){
                categories.remove(category);
                userPrefMap.remove(newspaper);

                //Log.d(LOG_TAG, "Deleted - "+deleteCell.toString());
                updateUserSelectionMap(userPrefMap, newspaper, categories);
            }
        }
    }

    private void updateUserSelectionMap(Map<String, List<String>> userPrefMap, String newspaper, List<String> categories) {
        userPrefMap.put(newspaper, categories);
        NewsCatFileUtil.getInstance(context).setUserSelectionMap(userPrefMap);

        updateJsonMapFile();
    }

    private void updateJsonMapFile(){
        NewsCatFileUtil.getInstance(context).convertUserFeedMapToJsonMap();
    }

    public void destroyUtil() {
        _instance = null;
        context = null;
    }
}
