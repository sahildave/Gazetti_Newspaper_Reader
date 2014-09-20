package com.example.try_masterdetail.homescreen.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.try_masterdetail.R;

import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ImageAdapter extends BaseAdapter {
	// private String TAG = "HomeScreen";
	private Context mContext;
	private int cell_dimen;
	private LayoutInflater mInflater;
	List<CellModel> cellList;

	public ImageAdapter(Context c, List<CellModel> cellList) {
		mContext = c;
		this.cell_dimen = (int) c.getResources().getDimension(R.dimen.cell_dimen);
		mInflater = LayoutInflater.from(c);
		this.cellList = cellList;
	}

	public int getCount() {
		return cellList.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	static class ViewHolder {
		ImageView cellItemImage;
		TextView cellItemTitle;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.cell_layout, parent, false);

			holder = new ViewHolder();
			holder.cellItemImage = (ImageView) convertView.findViewById(R.id.cellItemImageView);
			holder.cellItemTitle = (TextView) convertView.findViewById(R.id.cellItemTitle);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		CellModel modelObject = cellList.get(position);

		String uri = modelObject.getNewspaperImage();
		int resID = mContext.getResources().getIdentifier(uri, "drawable", mContext.getPackageName());

		holder.cellItemImage.setLayoutParams(new RelativeLayout.LayoutParams(cell_dimen, cell_dimen));
		holder.cellItemImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
		holder.cellItemImage.setPadding(8, 8, 8, 8);
		holder.cellItemImage.setImageResource(resID);

		holder.cellItemTitle.setText(modelObject.getTitleCategory());

		return convertView;
	}
}