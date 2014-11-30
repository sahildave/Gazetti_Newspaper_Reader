package in.sahildave.gazetti.util;

import android.content.Context;
import android.os.AsyncTask;
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

    private static final String NEWS_CAT_FILE = "newsCat.json";
    private static final String NEW_DATA_FILE = "newData.json";
    private static String LOG_TAG = NewsCatFileUtil.class.getName();
    private static NewsCatFileUtil _instance = null;
    private Context context;
    public  Map<String, Object> fullJsonMap;
    public Map<String, List<String>> userSelectionMap;
    private boolean UserPrefChanged = false;

    private NewsCatFileUtil(Context parentContext) {
        context = parentContext;
        initUserSelectionMap();
    }

    public static synchronized NewsCatFileUtil getInstance(Context context){
        if (_instance == null) {
            _instance = new NewsCatFileUtil(context.getApplicationContext());
        }
        return _instance;
    }

    public void destroyUtil(){
        _instance = null;
        fullJsonMap = null;
        userSelectionMap = null;
        context = null;
    }

    private void initUserSelectionMap() {
        try {
            String jsonString = readFromFile(NEWS_CAT_FILE);
            Log.d(LOG_TAG, "jsonString - "+jsonString);

            fullJsonMap = JsonHelper.toMap(new JSONObject(jsonString));
            Log.d(LOG_TAG, "fullJsonMap - " + fullJsonMap.toString());

            userSelectionMap = getUserFeedMapFromJsonMap(fullJsonMap);
            Log.d(LOG_TAG, "UserSelectionMap - " + userSelectionMap.toString());
        } catch (JSONException e) {
            Crashlytics.logException(e);
        }
    }

    public boolean isNewsCatSelected(String newspaper, String category){
        return isNewsCatSelected(newspaper, category, fullJsonMap);
    }

    public boolean isNewsCatSelected(String newspaper, String category, Map<String, Object> map){
        boolean selected;
        if(map.containsKey(newspaper)){
            Map <String, Boolean> categories = getAllCategoriesFromJsonMap(newspaper, map);
            selected = getValueOfKey(categories, category);
        } else {
            Crashlytics.log(Log.ERROR, LOG_TAG, "Key: " + newspaper + " not found..returning false");
            selected = false;
        }
        return selected;
    }

    public Map<String, List<String>> getUserSelectionMap() {
        return userSelectionMap;
    }

    public void setUserSelectionMap(Map<String, List<String>> userSelectionMap) {
        this.userSelectionMap = userSelectionMap;
    }

    public void selectNewsCat(String newspaper, String category, Map<String, Object> map){
        if(map.containsKey(newspaper)){
            Map <String, Boolean> categories = getAllCategoriesFromJsonMap(newspaper, map);
            setValueOfKey(categories, category, true);
        } else {
            Crashlytics.log(Log.ERROR, LOG_TAG, "Key: " + newspaper + " not found..returning false");
            throw new RuntimeException("Key: " + newspaper + " not found");
        }
    }

    public void deselectNewsCat(String newspaper, String category, Map<String, Object> map){
        if(map.containsKey(newspaper)){
            Map <String, Boolean> categories = getAllCategoriesFromJsonMap(newspaper, map);
            setValueOfKey(categories, category, false);
        } else {
            Crashlytics.log(Log.ERROR, LOG_TAG, "Key: " + newspaper + " not found..returning false");
            throw new RuntimeException("Key: " + newspaper + " not found");
        }
    }

    //TODO: make it private
    public void saveUserSelectionToJsonFile(Map<String, Object> newFullJsonMap){
        try {
            fullJsonMap = newFullJsonMap;
            Object jsonString = JsonHelper.toJSON(newFullJsonMap);
            Log.d(LOG_TAG, "Updating fullJson - "+jsonString.toString());

            userSelectionMap = getUserFeedMapFromJsonMap(fullJsonMap);
            Log.d(LOG_TAG, "Updated UserSelectionMap - " + userSelectionMap.toString());

            writeToInternalStorage(jsonString.toString(), NEWS_CAT_FILE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Boolean> getAllCategoriesFromJsonMap(String newspaper, Map<String, Object> map) {
        return (Map<String, Boolean>) map.get(newspaper);
    }

    public Map<String, Object> getFullJsonMap(){
        return fullJsonMap;
    }

    private Map<String, List<String>> getUserFeedMapFromJsonMap(Map<String, Object> map){
        Map<String, List<String>> returnMap = new HashMap<String, List<String>>();

        for (String newspaper : map.keySet()) {
            Map<String, Boolean> categories = getAllCategoriesFromJsonMap(newspaper, map);

            Iterator<String> catIterator = categories.keySet().iterator();
            List<String> selectedCategories = new ArrayList<String>();
            while (catIterator.hasNext()) {
                String category = catIterator.next();
                if (isNewsCatSelected(newspaper, category, map)) {
                    //Log.d(LOG_TAG, "ADDING - " + newspaper + ", " + category);
                    selectedCategories.add(category);
                }
            }

            if (selectedCategories.size() > 0) {
                returnMap.put(newspaper, selectedCategories);
            }
        }
        //Log.d(LOG_TAG, "USER FEED - "+returnMap);
        return returnMap;
    }

    public void convertUserFeedMapToJsonMap(){
        convertUserFeedMapToJsonMap(fullJsonMap);
    }

    public void convertUserFeedMapToJsonMap(Map<String, Object> map){
    for (String newspaper : map.keySet()) {
        Map<String, Boolean> allCategories = getAllCategoriesFromJsonMap(newspaper, map);
        List<String> selectedCatForNewspaper = userSelectionMap.get(newspaper);

        for (String category : allCategories.keySet()) {
            if (selectedCatForNewspaper != null && selectedCatForNewspaper.contains(category)) {
                selectNewsCat(newspaper, category, map);
            } else {
                deselectNewsCat(newspaper, category, map);
            }
        }
    }

    setUserPrefChanged(true);
    saveUserSelectionToJsonFile(map);
    }

    public boolean isUserPrefChanged() {
        return UserPrefChanged;
    }

    public void setUserPrefChanged(boolean userPrefChanged) {
        Log.d(LOG_TAG, "Setting UserPrefChanged to "+userPrefChanged);
        UserPrefChanged = userPrefChanged;
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

    public void updateNewsCatFileWithNewAssets() {
        try {
            String jsonString = readFromFile(NEWS_CAT_FILE, true);
            Log.d(LOG_TAG, "newJsonString - "+jsonString);

            Map<String, Object> newFullJsonMap = JsonHelper.toMap(new JSONObject(jsonString));
            Log.d(LOG_TAG, "newFullJsonMap - " + newFullJsonMap.toString());

            convertUserFeedMapToJsonMap(newFullJsonMap);
            Map<String, List<String>>  newUserSelectionMap = getUserFeedMapFromJsonMap(newFullJsonMap);
            Log.d(LOG_TAG, "newUserSelectionMap - "+newUserSelectionMap);

            setUserSelectionMap(newUserSelectionMap);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateSelectionWithNewAssets(Map<String, Object> map){
        Log.d(LOG_TAG, "Received map - "+map);
        Map<String, List<String>> selected = getUserFeedMapFromJsonMap(map);
        Log.d(LOG_TAG, "New selected - "+selected);

        for (String newspaper : selected.keySet()) {
            List<String> categories;
            if (userSelectionMap.containsKey(newspaper)) {
                categories = userSelectionMap.get(newspaper);
                categories.addAll(selected.get(newspaper));
            } else {
                categories = selected.get(newspaper);
            }
            userSelectionMap.put(newspaper, categories);

            Log.d(LOG_TAG, "New selection for "+newspaper+ " is "+categories);
        }
        Log.d(LOG_TAG, "newUserSelectionMap - "+userSelectionMap);

        setUserPrefChanged(true);
        convertUserFeedMapToJsonMap();
    }

    public String readFromFile(String fileName){
        File file = new File(context.getCacheDir(), fileName);
        boolean readFromAsset = !file.exists();

        return readFromFile(fileName, readFromAsset);
    }

    public String readFromFile(String fileName, boolean readFromAsset) {
        StringBuilder returnString = new StringBuilder();
        InputStreamReader isr = null;
        BufferedReader input = null;
        FileInputStream fis = null;
        InputStream is = null;
        try {
            File file = new File(context.getCacheDir(), fileName);
            if(readFromAsset) {
                //Reading from Assets
                Log.d(LOG_TAG, "Reading from Assets");
                is = context.getAssets().open(fileName);
                isr = new InputStreamReader(is);
            } else {
                //Reading from Internal
                Log.d(LOG_TAG, "Reading from Internal");
                fis = new FileInputStream(file);
                isr = new InputStreamReader(fis);
            }

            input = new BufferedReader(isr);
            String line;
            while ((line = input.readLine()) != null) {
                returnString.append(line);
                returnString.append("\n");
            }

            if(readFromAsset){
                writeToInternalStorage(returnString.toString(), fileName);
            }
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

    private void writeToInternalStorage(final String content, final String fileName){

        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

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
                Log.d(LOG_TAG, "File written to internal storage!");
                return null;
            }
        }.execute();

    }
}
