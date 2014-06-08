package com.example.try_masterdetail.homescreen.adapter;

public class NewsCatCsvObject {

	String npId;
	String npName;
	String catId;
	String catName;
	String npImage;
	String catImage;

	public String getNpId() {
		return npId;
	}

	public void setNpId(String npId) {
		this.npId = npId;
	}

	public String getNpName() {
		return npName;
	}

	public void setNpName(String npName) {
		this.npName = npName;
	}

	public String getCatId() {
		return catId;
	}

	public void setCatId(String catId) {
		this.catId = catId;
	}

	public String getCatName() {
		return catName;
	}

	public void setCatName(String catName) {
		this.catName = catName;
	}

	public String getNpImage() {
		return npImage;
	}

	public void setNpImage(String npImage) {
		this.npImage = npImage;
	}

	public String getCatImage() {
		return catImage;
	}

	public void setCatImage(String catImage) {
		this.catImage = catImage;
	}

	@Override
	public String toString() {
		return "CSVObject [npId=" + npId + ", npName=" + npName + ", catId=" + catId + ", catName=" + catName
				+ ", npImage=" + npImage + ", catImage=" + catImage + "]";
	}
	
	

}
