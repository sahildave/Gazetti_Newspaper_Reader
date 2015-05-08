package in.sahildave.gazetti.util;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import com.crashlytics.android.Crashlytics;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.services.urlshortener.Urlshortener;
import com.google.api.services.urlshortener.model.Url;

import java.io.IOException;

/**
 * Created by sahil on 22/10/14.
 */
public class ShareButtonListener {
    private static final int HEADLINE_LENGTH = 120;

    Activity activity;
    String mArticleURL;
    String mArticleHeadLine;
    public ShareButtonListener(Activity activity, final String mArticleURL, String mArticleHeadline) {
        //Log.d(ShareButtonListener.class.getName(), "Sharing...");
        this.activity = activity;
        this.mArticleURL = mArticleURL;
        this.mArticleHeadLine = mArticleHeadline;

        mArticleHeadLine = mArticleHeadLine.length() > HEADLINE_LENGTH ?
                mArticleHeadLine.substring(0, HEADLINE_LENGTH) :
                mArticleHeadLine;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                shorten(mArticleURL);
            }
        });

    }

    private void shorten(String longUrl){

        new AsyncTask<String, Void, String>(){

            @Override
            protected String doInBackground(String... params) {
                String longUrl = params[0];

                if(VERSION.SDK_INT >= 11 ){
                    Urlshortener.Builder builder = new Urlshortener.Builder(AndroidHttp.newCompatibleTransport(), AndroidJsonFactory.getDefaultInstance(), null);
                    Urlshortener urlshortener = builder.build();

                    Url url = new Url();
                    url.setLongUrl(longUrl);
                    try {
                        url = urlshortener.url().insert(url).execute();
                        return url.getId();
                    } catch (IOException e) {
                        return null;
                    }
                }
                return longUrl;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    mArticleURL = s;

                    String intentString = mArticleHeadLine + " - "+mArticleURL;

                    //Log.d(ShareButtonListener.class.getName(), "Sharing content - " + intentString);

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, intentString);
                    activity.startActivity(Intent.createChooser(sendIntent, "Share with"));
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            }
        }.execute(longUrl);
    }
}
