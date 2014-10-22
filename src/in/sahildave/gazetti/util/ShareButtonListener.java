package in.sahildave.gazetti.util;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
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
    private static final int MESSAGE_LENGTH = 137;

    Context context;
    String mArticleURL;
    String mArticleHeadLine;
    public ShareButtonListener(Context context, String mArticleURL, String mArticleHeadline) {
        Log.d(ShareButtonListener.class.getName(), "Sharing...");
        this.context = context;
        this.mArticleURL = mArticleURL;
        this.mArticleHeadLine = mArticleHeadline;

        mArticleHeadLine = mArticleHeadLine.length() > HEADLINE_LENGTH ?
                mArticleHeadLine.substring(0, HEADLINE_LENGTH) :
                mArticleHeadLine;

        shorten(mArticleURL);

    }

    private void shorten(String longUrl){

        new AsyncTask<String, Void, String>(){

            @Override
            protected String doInBackground(String... params) {
                String longUrl = params[0];

                Urlshortener.Builder builder = new Urlshortener.Builder (AndroidHttp.newCompatibleTransport(), AndroidJsonFactory.getDefaultInstance(), null);
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

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    mArticleURL = s;

                    String intentString = mArticleHeadLine + " - "+mArticleURL;
                    intentString = intentString.length() > MESSAGE_LENGTH ?
                            intentString.substring(0, MESSAGE_LENGTH)+"..." :
                            intentString;

                    Log.d(ShareButtonListener.class.getName(), "Sharing content - " + intentString);

                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, intentString);
                    sendIntent.setType("text/");
                    context.startActivity(Intent.createChooser(sendIntent, "Share with"));
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    e.printStackTrace();
                }
            }
        }.execute(longUrl);
    }
}
