package in.sahildave.gazetti.util;

import android.content.Context;
import android.content.res.AssetManager;
import in.sahildave.gazetti.homescreen.adapter.NewsCatModel;

import java.io.*;

public class CsvFileUtil {

    Context context;
    NewsCatModel object = null;
    AssetManager assetManager;
    InputStream inputStream;
    BufferedReader br = null;
    String line = "";
    String csvSplitBy = ",";

    public CsvFileUtil(Context context) {
        this.context = context;
        object = new NewsCatModel();
        this.assetManager = context.getAssets();
        this.inputStream = null;

        try {
            inputStream = assetManager.open("newscat.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public NewsCatModel getObjectByNPName(String npName, String catName) {
        try {

            br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                String[] np = line.split(csvSplitBy);

                if (np[1].equals(npName) && np[3].equals(catName)) {
                    setupReturnObject(np);
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return object;
    }

    public NewsCatModel getObjectByNPImage(String npImage, String catName) {
        try {

            br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                String[] np = line.split(csvSplitBy);

                if (np[4].equals(npImage) && np[3].equals(catName)) {
                    setupReturnObject(np);
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return object;
    }

    public NewsCatModel getObjectByNPId(String npId, String catId) {

        try {

            br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                String[] np = line.split(csvSplitBy);

                System.out.println("getObjectByNPId == " + np[0] + " - " + npId + ", " + np[2] + " - " + catId);
                if (np[0].equals(npId) && np[2].equals(catId)) {
                    setupReturnObject(np);
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return object;
    }

    public void closeUtilObject() {
        object = null;
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void setupReturnObject(String[] np) {

        object.setNpId(np[0]);
        object.setNpName(np[1]);
        object.setCatId(np[2]);
        object.setCatName(np[3]);
        object.setNpImage(np[4]);
        object.setCatImage(np[5]);
        System.out.println("Object - " + object.toString());
    }
}
