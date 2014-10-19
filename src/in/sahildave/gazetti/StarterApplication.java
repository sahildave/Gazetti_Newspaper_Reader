package in.sahildave.gazetti;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import com.parse.Parse;
import in.sahildave.gazetti.util.Constants;

public class StarterApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, Constants.PARSE_APP_ID, Constants.PARSE_CLIENT_KEY);
        Parse.enableLocalDatastore(this);
        Crashlytics.getInstance().setDebugMode(true);
    }
}
