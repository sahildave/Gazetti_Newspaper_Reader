package in.sahildave.gazetti.util;

import android.util.Log;
import in.sahildave.gazetti.homescreen.adapter.CellModelNew;
import in.sahildave.gazetti.util.Enums.Newspapers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by sahil on 9/11/14.
 */
public class CellListJsonUtil {
    private static final String LOG_TAG = CellListJsonUtil.class.getName();

    public static List<CellModelNew> getUserPrefCellList(){

        List<CellModelNew> returnList = new ArrayList<CellModelNew>();
        Map<String, List<String>> userPrefMap = UserSelectionJsonUtil.getInstance().getUserFeedMap();

        Iterator<String> iterator = userPrefMap.keySet().iterator();
        while (iterator.hasNext()){
            String newspaper = (String) iterator.next();
            List<String> categoriesSelected = userPrefMap.get(newspaper);
            Newspapers npEnum = new Enums().getNewspaperFromName(newspaper);
            for(String category : categoriesSelected){
                CellModelNew cellModel = new CellModelNew(npEnum, category);
                returnList.add(cellModel);
            }
        }

        Log.d(LOG_TAG, "LIST - "+returnList);
        return returnList;
    }

}
