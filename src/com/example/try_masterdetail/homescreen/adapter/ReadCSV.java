package com.example.try_masterdetail.homescreen.adapter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

public class ReadCSV {

	Activity activity;
	CSVObject object;
	AssetManager assetManager;
	InputStream inputStream;
	BufferedReader br = null;
	String line = "";
	String csvSplitBy = ",";

	public ReadCSV(Activity activity) {
		this.activity = activity;
		object = new CSVObject();
		this.assetManager = activity.getAssets();
		this.inputStream = null;

		try {
			inputStream = assetManager.open("newscat.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public CSVObject getObjectByNPName(String npName, String catName) {
		try {

			br = new BufferedReader(new InputStreamReader(inputStream));
			while ((line = br.readLine()) != null) {
				String[] np = line.split(csvSplitBy);

				if (np[1].equals(npName) && np[3].equals(catName)) {

					object.setNpId(np[0]);
					object.setNpName(np[1]);
					object.setCatId(np[2]);
					object.setCatName(np[3]);
					object.setNpImage(np[4]);
					object.setCatImage(np[5]);
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

	public CSVObject getObjectByNPImage(String npImage, String catName) {
		try {

			br = new BufferedReader(new InputStreamReader(inputStream));
			while ((line = br.readLine()) != null) {
				String[] np = line.split(csvSplitBy);

				if (np[4].equals(npImage) && np[3].equals(catName)) {

					object.setNpId(np[0]);
					object.setNpName(np[1]);
					object.setCatId(np[2]);
					object.setCatName(np[3]);
					object.setNpImage(np[4]);
					object.setCatImage(np[5]);
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

	public void close() {
		object = null;

	}
}
