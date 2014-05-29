package com.example.try_masterdetail.homescreen;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.try_masterdetail.R;
import com.example.try_masterdetail.homescreen.adapter.CellListObjects;
import com.example.try_masterdetail.homescreen.adapter.GridCellModel;
import com.example.try_masterdetail.homescreen.adapter.ImageAdapter;
import com.example.try_masterdetail.homescreen.adapter.NewsCatCsvObject;
import com.example.try_masterdetail.homescreen.adapter.ReadNewsCatCSV;
import com.example.try_masterdetail.news_activities.WebsiteListActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

public class HomeScreenFragment extends Fragment {
	Callbacks activityCallback;

	private GridView gridview;
	List<GridCellModel> cellList;
	ImageAdapter adapter;
	private int feedVersion;
	SwingBottomInAnimationAdapter animAdapter;
	AlphaInAnimationAdapter animAdapterMultiple;

	private String TAG = "HomeScreen";

	private boolean firstRun;

	public HomeScreenFragment() {

	}

	public interface Callbacks {
		public void showAddNewCellDialog(List<GridCellModel> cellList, ImageAdapter adapter);

		public void showEditCellDialog(int position, String newspaper, String category, List<GridCellModel> cellList,
				ImageAdapter adapter);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			activityCallback = (Callbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement ToolbarListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "ListFragment in onCreate ");
		setRetainInstance(true);

		SharedPreferences sharedPref = getActivity().getSharedPreferences("CellList", Context.MODE_PRIVATE);
		feedVersion = sharedPref.getInt("feedVersion", 0);

		firstRun = true;
		Log.d(TAG, "FEEDVERSION onCreate - " + feedVersion);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.homescreen_fragment, container, false);
		firstRun = false;
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		// cellList = new ArrayList<GridCellModel>();
		gridview = (GridView) getActivity().findViewById(R.id.gridview);

		if (getActivity().findViewById(R.id.homescreen_background_kenburns) == null) {
			loadImageForBackground();
		}
		Log.d(TAG, "ListFragment in onViewCreated ");
		Log.d(TAG, "FEEDVERSION onViewCreated - " + feedVersion);

		CellListObjects cellListObject = new CellListObjects(getActivity());
		cellList = cellListObject.getCellListFromPrefs();

		GridCellModel modelObject = cellList.get(cellList.size() - 1);
		if (!modelObject.getNewspaperImage().equals("add_new")) {
			cellList.add(new GridCellModel("add_new", "Add New"));
		}

		adapter = new ImageAdapter(getActivity(), cellList);

		animAdapter = new SwingBottomInAnimationAdapter(adapter);
		animAdapterMultiple = new AlphaInAnimationAdapter(animAdapter);
		animAdapterMultiple.setAbsListView(gridview);

		gridview.setAdapter(animAdapterMultiple);

		registerForContextMenu(gridview);
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				if (position == (cellList.size() - 1)) {
					activityCallback.showAddNewCellDialog(cellList, adapter);
				} else {
					GridCellModel clickedObject = cellList.get(position);
					String npImage = clickedObject.getNewspaperImage();
					String catName = clickedObject.getTitleCategory();

					ReadNewsCatCSV readCsvNpImage = new ReadNewsCatCSV(getActivity());
					NewsCatCsvObject csvObject = readCsvNpImage.getObjectByNPImage(npImage, catName);

					String npId = csvObject.getNpId();
					String catId = csvObject.getCatId();
					String npName = csvObject.getNpName();

					npId = String.valueOf(Integer.parseInt(npId) + 1);
					catId = String.valueOf(Integer.parseInt(catId) + 1);

					Intent headlinesIntent = new Intent(getActivity(), WebsiteListActivity.class);
					headlinesIntent.putExtra("npId", npId);
					headlinesIntent.putExtra("catId", catId);
					headlinesIntent.putExtra("npName", npName);
					headlinesIntent.putExtra("catName", catName);
					startActivity(headlinesIntent);

				}

			}
		});

	}

	private void loadImageForBackground() {
		// get a random image, if null then get image_0
		Random rand = new Random();
		int n = rand.nextInt(4) + 1;
		String backgroundImageUri = "image_" + n;
		int resID = getResources().getIdentifier(backgroundImageUri, "drawable", getActivity().getPackageName());
		System.out.println(n + ", " + resID);
		if (resID == 0) {
			resID = getResources().getIdentifier("image_0", "drawable", getActivity().getPackageName());
		}

		// Bitmap Options
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getResources(), resID, options);

		// Raw height and width of image
		int imageHeight = options.outHeight;
		int imageWidth = options.outWidth;
		int inSampleSize = 1;

		// height and width of screen
		int reqHeight = getResources().getDisplayMetrics().heightPixels;
		int reqWidth = getResources().getDisplayMetrics().widthPixels;

		// SampleSize Calculations
		if (imageHeight > reqHeight || imageWidth > reqWidth) {

			final int halfHeight = imageHeight / 2;
			final int halfWidth = imageWidth / 2;

			// Calculate the largest inSampleSize value that is a power of 2
			// and keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}

			// This offers some additional logic in case the image has a strange
			// aspect ratio. Anything more than 2x the requested pixels we'll
			// sample down
			// further
			long totalPixels = imageWidth * imageHeight / inSampleSize;
			final long totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels > totalReqPixelsCap) {
				inSampleSize *= 2;
				totalPixels /= 2;
			}
		}

		// TODO: Check what is inBitmap

		options.inJustDecodeBounds = false;
		Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), resID, options);
		ImageView homeScreenBackground = (ImageView) getActivity().findViewById(R.id.homescreen_background);
		homeScreenBackground.setImageBitmap(mBitmap);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.gridview_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = info.position;
		String newspaper = cellList.get(position).getNewspaperImage();
		String category = cellList.get(position).getTitleCategory();

		switch (item.getItemId()) {
		case R.id.edit:
			if (position == (cellList.size() - 1)) {
				Toast.makeText(getActivity(), "Cannot Edit", Toast.LENGTH_SHORT).show();
				return true;
			}
			activityCallback.showEditCellDialog(position, newspaper, category, cellList, adapter);
			return true;
		case R.id.delete:
			if (position == (cellList.size() - 1)) {
				Toast.makeText(getActivity(), "Cannot Delete", Toast.LENGTH_SHORT).show();
				return true;
			}
			cellList.remove(position);
			adapter.notifyDataSetChanged();

			CellListObjects cellListObject = new CellListObjects(getActivity());
			cellListObject.saveCellList(cellList);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "ListFragment in onResume ");
		Log.d(TAG, "FEEDVERSION onResume - " + feedVersion);

		SharedPreferences sharedPref = getActivity().getSharedPreferences("CellList", Context.MODE_PRIVATE);
		int newfeedVersion = sharedPref.getInt("feedVersion", 0);
		Log.d(TAG, "NEWFEEDVERSION onResume - " + newfeedVersion);

		if (!firstRun && (newfeedVersion > feedVersion)) {
			// TODO
			Log.d(TAG, "RELOADING - " + cellList.size());
			feedVersion = newfeedVersion;
			CellListObjects cellListObject = new CellListObjects(getActivity());
			// cellListObject.saveCellList(cellList);
			cellList.clear();
			cellList = cellListObject.getCellListFromPrefs();

			GridCellModel modelObject = cellList.get(cellList.size() - 1);
			if (!modelObject.getNewspaperImage().equals("add_new")) {
				cellList.add(new GridCellModel("add_new", "Add New"));
			}

			adapter = new ImageAdapter(getActivity(), cellList);
			// adapter.notifyDataSetChanged();

			animAdapter = new SwingBottomInAnimationAdapter(adapter);
			animAdapterMultiple = new AlphaInAnimationAdapter(animAdapter);
			animAdapterMultiple.setAbsListView(gridview);

			gridview.setAdapter(animAdapterMultiple);

			Log.d(TAG, "LOADED - " + cellList.size());

		}

	}

}
