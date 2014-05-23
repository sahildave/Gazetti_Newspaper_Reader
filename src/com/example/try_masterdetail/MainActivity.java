package com.example.try_masterdetail;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.example.try_masterdetail.R;
import com.example.try_masterdetail.AddCellDialogFragment.AddCellDialogListener;
import com.example.try_masterdetail.EditCellDialogFragment.EditCellDialogListener;
import com.example.try_masterdetail.news_activities.WebsiteDetailActivity;
import com.example.try_masterdetail.news_activities.WebsiteDetailFragment;
import com.example.try_masterdetail.news_activities.WebsiteListActivity;

public class MainActivity extends FragmentActivity implements AddCellDialogListener, EditCellDialogListener {

	List<GridCellModel> cellList;
	ImageAdapter adapter;
	FragmentManager fm = getSupportFragmentManager();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homescreen_activity);
		GridView gridview = (GridView) findViewById(R.id.gridview);

		cellList = new ArrayList<GridCellModel>();

		cellList.add(new GridCellModel("th", "Sports"));
		cellList.add(new GridCellModel("th", "National"));
		cellList.add(new GridCellModel("th", "International"));
		cellList.add(new GridCellModel("th", "Science"));
		cellList.add(new GridCellModel("th", "Entertainment"));
		cellList.add(new GridCellModel("add_new", "Add New"));

		adapter = new ImageAdapter(this, cellList);
		gridview.setAdapter(adapter);

		registerForContextMenu(gridview);

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				if (position == (cellList.size() - 1)) {
					showAddNewCellDialog();
				} else {
					GridCellModel clickedObject = cellList.get(position);
					String npImage = clickedObject.getNewspaperImage();
					String catName = clickedObject.getTitleCategory();

					ReadCSV readCsv = new ReadCSV(MainActivity.this);
					CSVObject csvObject = readCsv.getObjectByNPImage(npImage, catName);

					String npId = csvObject.getNpId();
					String catId = csvObject.getCatId();
					String npName = csvObject.getNpName();

					npId = String.valueOf(Integer.parseInt(npId) + 1);
					catId = String.valueOf(Integer.parseInt(catId) + 1);

					Intent detailIntent = new Intent(MainActivity.this, WebsiteListActivity.class);
					detailIntent.putExtra("npId", npId);
					detailIntent.putExtra("catId", catId);
					detailIntent.putExtra("npName", npName);
					detailIntent.putExtra("catName", catName);
					startActivity(detailIntent);

				}

			}
		});

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
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
				Toast.makeText(this, "Cannot Edit", Toast.LENGTH_SHORT).show();
				return true;
			}
			showEditCellDialog(position, newspaper, category);
			return true;
		case R.id.delete:
			if (position == (cellList.size() - 1)) {
				Toast.makeText(this, "Cannot Delete", Toast.LENGTH_SHORT).show();
				return true;
			}
			cellList.remove(position);
			adapter.notifyDataSetChanged();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	protected void showAddNewCellDialog() {
		AddCellDialogFragment addCellDialog = new AddCellDialogFragment();
		addCellDialog.show(fm, "addCell");
	}

	protected void showEditCellDialog(int position, String newspaper, String category) {
		if (newspaper.length() > 7 && newspaper.substring(newspaper.length() - 7).equals("_custom")) {
			newspaper = newspaper.substring(0, newspaper.length() - 7);
		}
		int newspaperId = cellList.get(position).getDefaultNewspaperId(newspaper);

		EditCellDialogFragment editCellDialog = EditCellDialogFragment.newInstance(position, newspaperId, category);
		editCellDialog.show(fm, "editCell");
	}

	@Override
	public void onFinishEditingListener(int editPosition, String npName, String cat, boolean edited) {

		if (edited) {

			ReadCSV readCsv = new ReadCSV(this);
			CSVObject csvObject = readCsv.getObjectByNPName(npName, cat);

			GridCellModel newCell = new GridCellModel(csvObject.getNpImage(), csvObject.getCatName());
			cellList.set(editPosition, newCell);
			adapter.notifyDataSetChanged();
		}

	}

	@Override
	public void onFinishAddingListener(String npName, String cat) {

		ReadCSV readCsv = new ReadCSV(this);
		CSVObject csvObject = readCsv.getObjectByNPName(npName, cat);

		GridCellModel newCell = new GridCellModel(csvObject.getNpImage(), csvObject.getCatName());
		cellList.add(cellList.size() - 1, newCell);
		adapter.notifyDataSetChanged();

		readCsv.close();

	}

}
