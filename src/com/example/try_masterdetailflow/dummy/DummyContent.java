package com.example.try_masterdetailflow.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

	/**
	 * An array of sample (dummy) items.
	 */
	public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

	/**
	 * A map of sample (dummy) items, by ID.
	 */
	public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

	static {
		// Add 3 sample items.
		addItem(new DummyItem("1", "eBookFrenzy", "http://www.ebookfrenzy.com"));
		addItem(new DummyItem("2", "Google", "http://www.google.com"));
		addItem(new DummyItem("3", "Android", "http://www.android.com"));
	}

	private static void addItem(DummyItem item) {
		ITEMS.add(item);
		ITEM_MAP.put(item.id, item);
	}

	/**
	 * A dummy item representing a piece of content.
	 */
	public static class DummyItem {
		public String id;
		public String website_name;
		public String website_url;

		public DummyItem(String id, String website_name, String website_url) {
			this.id = id;
			this.website_name = website_name;
			this.website_url = website_url;
		}

		@Override
		public String toString() {
			return website_name;
		}
	}
}
