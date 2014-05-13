package com.example.try_masterdetail;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;

public class StarterApplication extends Application {
	private static final String TAG = "MasterDetail";
	private static StarterApplication s_instance;

	public StarterApplication() {
		s_instance = this;
	}

	public static StarterApplication getApplication() {
		return s_instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// Add your initialization code here
		Parse.initialize(this, "EIBQFrIyVZBHDTwmEZqxaWn6yx10UNPo4gy7kkmR", "Fj96ZYVQziKR132klHkXDSpireivZZRaKZOmB0SK");
		Parse.enableLocalDatastore(this);
		Log.d(TAG, "in Application");
	}
}
