package com.example.try_masterdetail;

import android.app.Application;
import android.util.Log;

import com.example.try_masterdetail.util.Constants;
import com.parse.Parse;

public class StarterApplication extends Application {
	private static final String TAG = "MasterDetail";

	@Override
	public void onCreate() {
		super.onCreate();

		Parse.initialize(this, Constants.PARSE_APP_ID, Constants.PARSE_CLIENT_KEY);
		// Parse.enableLocalDatastore(this);
		Log.d(TAG, "in Application");
	}
}
