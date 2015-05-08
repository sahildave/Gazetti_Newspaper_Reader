package in.sahildave.gazetti.util;

import android.content.Context;
import android.support.annotation.StringRes;

public class Constants {

    public static String getConstant(Context context, @StringRes int stringRes) {
        return context.getResources().getString(stringRes);
    }

    public static final String IS_FIRST_RUN = "first_run";
    public static final String GAZETTI= "gazetti";
    public static final String ASSET_VERSION= "AssetVersion";
}
