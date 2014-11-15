package in.sahildave.gazetti.util;

import android.content.Context;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

/**
 * Created by sahil on 8/11/14.
 */
public class NewsCatFileUtil {

    public static final String NEWS_CAT_FILE = "newsCat.json";
    private static String LOG_TAG = NewsCatFileUtil.class.getName();
    private static Context context;
    public static Map<String, Object> fullJsonMap;
    public static Map<String, List<String>> userSelectionMap;
    private static NewsCatFileUtil _instance = null;

    public static void init(Context parentContext) {
        context = parentContext;
        _instance = null; //new session will be created
        getInstance();
    }

    private NewsCatFileUtil() {
        initUserSelectionMap();
    }

    public static NewsCatFileUtil getInstance(){
        synchronized (NewsCatFileUtil.class) {
            if (_instance == null) {
                _instance = new NewsCatFileUtil();
            }
            return _instance;
        }
    }

    private void initUserSelectionMap() {
        try {
            String jsonString = readFromFile(NEWS_CAT_FILE);
            Log.d(LOG_TAG, "jsonString - "+jsonString);

            fullJsonMap = JsonHelper.toMap(new JSONObject(jsonString));
            Log.d(LOG_TAG, "fullJsonMap - " + fullJsonMap.toString());

            userSelectionMap = getUserFeedMapFromJsonMap();
            Log.d(LOG_TAG, "UserSelectionMap - " + userSelectionMap.toString());
        } catch (JSONException e) {
            Crashlytics.logException(e);
        }
    }

    public boolean isNewsCatSelected(String newspaper, String category){
        boolean selected;
        if(fullJsonMap.containsKey(newspaper)){
            Map <String, Boolean> categories = getAllCategoriesFromJson(newspaper);
            selected = getValueOfKey(categories, category);
        } else {
            Crashlytics.log(Log.ERROR, LOG_TAG, "Key: " + newspaper + " not found..returning false");
            selected = false;
        }
        return selected;
    }

    public void selectNewsCat(String newspaper, String category){
        if(fullJsonMap.containsKey(newspaper)){
            Map <String, Boolean> categories = getAllCategoriesFromJson(newspaper);
            setValueOfKey(categories, category, true);
        } else {
            Crashlytics.log(Log.ERROR, LOG_TAG, "Key: " + newspaper + " not found..returning false");
            throw new RuntimeException("Key: " + newspaper + " not found");
        }
    }

    public void deselectNewsCat(String newspaper, String category){
        if(fullJsonMap.containsKey(newspaper)){
            Map <String, Boolean> categories = getAllCategoriesFromJson(newspaper);
            setValueOfKey(categories, category, false);
        } else {
            Crashlytics.log(Log.ERROR, LOG_TAG, "Key: " + newspaper + " not found..returning false");
            throw new RuntimeException("Key: " + newspaper + " not found");
        }
    }

    //TODO: make it private
    public void saveUserSelectionToJsonFile(Map<String, Object> userSelectionJsonMap){
        try {
            Object jsonString = JsonHelper.toJSON(userSelectionJsonMap);
            Log.d(LOG_TAG, "Saving - "+jsonString.toString());
            userSelectionMap = getUserFeedMapFromJsonMap();
            Log.d(LOG_TAG, "Updated UserSelectionMap - " + userSelectionMap.toString());
            writeToInternalStorage(jsonString.toString(), NEWS_CAT_FILE);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            initUserSelectionMap();
        }
    }

    private Map<String, Boolean> getAllCategoriesFromJson(String newspaper) {
        return (Map<String, Boolean>) fullJsonMap.get(newspaper);
    }

    public Map<String, Object> getFullJsonMap(){
        return fullJsonMap;
    }

    private Map<String, List<String>> getUserFeedMapFromJsonMap(){
        Map<String, List<String>> returnMap = new HashMap<String, List<String>>();

        for (String newspaper : fullJsonMap.keySet()) {
            Map<String, Boolean> categories = getAllCategoriesFromJson(newspaper);

            Iterator<String> catIterator = categories.keySet().iterator();
            List<String> selectedCategories = new ArrayList<String>();
            while (catIterator.hasNext()) {
                String category = catIterator.next();
                if (isNewsCatSelected(newspaper, category)) {
                    Log.d(LOG_TAG, "ADDING - " + newspaper + ", " + category);
                    selectedCategories.add(category);
                }
            }

            if (selectedCategories.size() > 0) {
                returnMap.put(newspaper, selectedCategories);
            }
        }
        Log.d(LOG_TAG, "USER FEED - "+returnMap);
        return returnMap;
    }

    public void convertUserFeedMapToJsonMap(){
        for (String newspaper : fullJsonMap.keySet()) {
            Map<String, Boolean> allCategories = getAllCategoriesFromJson(newspaper);
            List<String> selectedCatForNewspaper = userSelectionMap.get(newspaper);

            for (String category : allCategories.keySet()) {
                if (selectedCatForNewspaper != null && selectedCatForNewspaper.contains(category)) {
                    selectNewsCat(newspaper, category);
                } else {
                    deselectNewsCat(newspaper, category);
                }
            }
        }

        saveUserSelectionToJsonFile(fullJsonMap);
    }

    private boolean getValueOfKey(Map<String, Boolean> map, String key) {
        if (isKeyPresent(map, key)) {
            return map.get(key);
        } else {
            Crashlytics.log(Log.ERROR, LOG_TAG, "Key: " + key + " not found..returning false");
        }
        return false;
    }

    private void setValueOfKey(Map<String, Boolean> map, String key, Boolean value){
        if (isKeyPresent(map, key)) {
            map.put(key, value);
        } else {
            Crashlytics.log(Log.ERROR, LOG_TAG, "Key: " + key + " not found..returning false");
        }
    }

    private boolean isKeyPresent(Map<String, Boolean> map, String key) {
        return map.containsKey(key);
    }


    private String readFromFile(String fileName){

        StringBuilder returnString = new StringBuilder();
        InputStreamReader isr = null;
        BufferedReader input = null;
        FileInputStream fis = null;
        InputStream is = null;
        try {
            File file = new File(context.getCacheDir(), fileName);
            if(file.exists()) {
                //Reading from Internal
                Log.d(LOG_TAG, "Reading from Internal");
                fis = new FileInputStream(file);
                isr = new InputStreamReader(fis);
            } else {
                //Reading from Assets
                Log.d(LOG_TAG, "Reading from Assets");
                is = context.getAssets().open(fileName);
                isr = new InputStreamReader(is);
            }

            input = new BufferedReader(isr);
            String line;
            while ((line = input.readLine()) != null) {
                returnString.append(line);
                returnString.append("\n");
            }
            writeToInternalStorage(returnString.toString(), fileName);
        } catch (FileNotFoundException e){
            Crashlytics.logException(e);
        }catch (Exception e) {
            Crashlytics.logException(e);
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fis != null)
                    fis.close();
                if (is != null)
                    is.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                Crashlytics.logException(e2);
            }
        }
        return returnString.toString();
    }

    private void writeToInternalStorage(String content, String fileName){
        FileOutputStream fos;
        try {
            File file = new File(context.getCacheDir(), fileName);
            fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
        } catch (IOException e) {
            Crashlytics.logException(e);
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }
}
