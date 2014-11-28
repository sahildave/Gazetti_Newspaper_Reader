package in.sahildave.gazetti.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import in.sahildave.gazetti.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

/**
 * Created by sahil on 8/11/14.
 */
public class NewsCatFileUtil {

    private static final String NEWS_CAT_FILE = "newsCat.json";
    private static String LOG_TAG = NewsCatFileUtil.class.getName();
    private static NewsCatFileUtil _instance = null;
    private Context context;
    public  Map<String, Object> fullJsonMap;
    public Map<String, List<String>> userSelectionMap;
    private int compiledAssetVersion;
    private int sharedPrefsAssetVersion;
    private SharedPreferences sharedPreferences;
    private boolean UserPrefChanged = false;

    private NewsCatFileUtil(Context parentContext) {
        context = parentContext;

        sharedPreferences = context.getSharedPreferences(Constants.GAZETTI, Context.MODE_PRIVATE);
        sharedPrefsAssetVersion = sharedPreferences.getInt(Constants.ASSET_VERSION, 0);
        compiledAssetVersion = context.getResources().getInteger(R.integer.assetVersion);

        initUserSelectionMap();
        Log.d(LOG_TAG, "Asset Versions: compiled - " + compiledAssetVersion + ", shared - " + sharedPrefsAssetVersion);
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

    public Map<String, List<String>> getUserSelectionMap() {
        return userSelectionMap;
    }

    public void setUserSelectionMap(Map<String, List<String>> userSelectionMap) {
        this.userSelectionMap = userSelectionMap;
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
    public void saveUserSelectionToJsonFile(Map<String, Object> newFullJsonMap){
        try {
            fullJsonMap = newFullJsonMap;
            Object jsonString = JsonHelper.toJSON(newFullJsonMap);
            Log.d(LOG_TAG, "Updating fullJson - "+jsonString.toString());

            userSelectionMap = getUserFeedMapFromJsonMap();
            Log.d(LOG_TAG, "Updated UserSelectionMap - " + userSelectionMap.toString());

            writeToInternalStorage(jsonString.toString(), NEWS_CAT_FILE);
        } catch (JSONException e) {
            e.printStackTrace();
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

        setUserPrefChanged(true);
        saveUserSelectionToJsonFile(fullJsonMap);
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


    private String readFromFile(String fileName){

        StringBuilder returnString = new StringBuilder();
        InputStreamReader isr = null;
        BufferedReader input = null;
        FileInputStream fis = null;
        InputStream is = null;
        try {
            File file = new File(context.getCacheDir(), fileName);
            boolean readFromAsset = !file.exists() || isAssetFileNew();
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

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                sharedPreferences.edit().putInt(Constants.ASSET_VERSION, compiledAssetVersion).commit();
            }
        }.execute();

    }

    public boolean isAssetFileNew(){
        boolean returnData = compiledAssetVersion > sharedPrefsAssetVersion;
        Log.d(LOG_TAG, "Is AssetFile New - "+returnData);
        return returnData;
    }
}
