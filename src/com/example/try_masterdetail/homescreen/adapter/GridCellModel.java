package com.example.try_masterdetail.homescreen.adapter;

public class GridCellModel {

	private String newspaperImage;
	private String titleCategory;

	public GridCellModel(String newspaperTitle, String categoryTitle) {
		setNewspaperImage(newspaperTitle);
		setTitleCategory(categoryTitle);
	}

	public String getNewspaperImage() {
		return newspaperImage;
	}

	public void setNewspaperImage(String newspaperImage) {
		this.newspaperImage = newspaperImage;
	}

	public String getTitleCategory() {
		return titleCategory;
	}

	public void setTitleCategory(String titleCategory) {
		this.titleCategory = titleCategory;
	}

	public int getDefaultNewspaperId(String newspaperImage) {
		switch (newspaperImage) {
		case "th":
			return 0;
		case "toi":
			return 1;
		case "fp":
			return 2;
		case "ht":
			return 3;
		case "tie":
			return 4;
		case "th_custom":
			return 0;
		case "toi_custom":
			return 1;
		case "fp_custom":
			return 2;
		case "ht_custom":
			return 3;
		case "tie_custom":
			return 4;
		default:
			return -1;
		}
	}

	@Override
	public String toString() {
		return newspaperImage;
	}

}
