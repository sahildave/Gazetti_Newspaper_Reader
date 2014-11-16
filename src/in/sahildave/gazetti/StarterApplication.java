package in.sahildave.gazetti;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import com.parse.ConfigCallback;
import com.parse.Parse;
import com.parse.ParseConfig;
import com.parse.ParseException;
import in.sahildave.gazetti.util.ConfigService;
import in.sahildave.gazetti.util.Constants;
import in.sahildave.gazetti.util.NewsCatFileUtil;

public class StarterApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, Constants.PARSE_APP_ID, Constants.PARSE_CLIENT_KEY);
        Parse.enableLocalDatastore(this);
        Crashlytics.getInstance().setDebugMode(true);
        Crashlytics.start(this);

        NewsCatFileUtil.init(this);
        ParseConfig.getInBackground(new ConfigCallback() {
            @Override
            public void done(ParseConfig config, ParseException e) {
                new ConfigService();
            }
        });
    }
}
