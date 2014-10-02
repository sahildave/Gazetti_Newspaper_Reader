package in.sahildave.gazetti;

import android.app.Application;
import android.util.Log;
import com.parse.Parse;
import in.sahildave.gazetti.util.Constants;

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
