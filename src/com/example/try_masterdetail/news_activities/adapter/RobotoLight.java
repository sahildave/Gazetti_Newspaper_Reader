package com.example.try_masterdetail.news_activities.adapter;

import android.content.Context;
import android.graphics.Typeface;

public class RobotoLight {

	private Context context;
	private static RobotoLight instance;

	public RobotoLight(Context context) {
		this.context = context;
	}

	public static RobotoLight getInstance(Context context) {
		synchronized (RobotoLight.class) {
			if (instance == null)
				instance = new RobotoLight(context);
			return instance;
		}
	}

	public Typeface getTypeFace() {
		return Typeface.createFromAsset(context.getResources().getAssets(), "Roboto-Light.ttf");
	}

}
