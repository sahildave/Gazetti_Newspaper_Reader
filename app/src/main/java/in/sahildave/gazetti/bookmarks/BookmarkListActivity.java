package in.sahildave.gazetti.bookmarks;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.crashlytics.android.Crashlytics;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.bookmarks.BookmarkDetailFragment.BookmarkLoadArticleCallback;
import in.sahildave.gazetti.bookmarks.BookmarkListFragment.BookmarkSelectedListeners;

public class BookmarkListActivity extends AppCompatActivity implements BookmarkSelectedListeners, BookmarkLoadArticleCallback{
    private static final String TAG = BookmarkListActivity.class.getName();

    public boolean mTwoPane;
    BookmarkListFragment bookmarkListFragment;
    private BookmarkLoadingCallback bookmarkLoadingCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d(TAG, "Activity onCreate");
        setContentView(R.layout.activity_website_list);

        setTitle("Bookmarks");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_default_color)));
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if (findViewById(R.id.website_detail_container) != null) {
            mTwoPane = true;
        }

        bookmarkListFragment = (BookmarkListFragment) getSupportFragmentManager().findFragmentByTag("bookmarkList");

        if (bookmarkListFragment == null) {
            bookmarkListFragment = new BookmarkListFragment();
            Bundle layoutBundle = new Bundle();
            layoutBundle.putBoolean("mTwoPane", mTwoPane);
            bookmarkListFragment.setArguments(layoutBundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.website_list_container, bookmarkListFragment, "bookmarkList").commit();
        }
        bookmarkLoadingCallback = new BookmarkLoadingCallback(this);
    }

    @Override
    public void onItemSelected(String headlineText) {
        try {
            if(headlineText!=null){
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(BookmarkDetailFragment.HEADLINE_CLICKED, headlineText);
                    BookmarkDetailFragment detailFragment = new BookmarkDetailFragment();
                    detailFragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.website_detail_container, detailFragment, "bookmarkDetail").commit();
                } else {
                    Intent detailIntent = new Intent(this, BookmarkDetailActivity.class);
                    detailIntent.putExtra(BookmarkDetailFragment.HEADLINE_CLICKED, headlineText);
                    startActivity(detailIntent);
                }
            } else {
                Crashlytics.log("Headline clicked is null ? "+(null==headlineText));
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
    }
    /****************************/
    /***** CALLBACK METHODS *****/
    /**
     * ************************
     */

    @Override
    public void onPreExecute(View rootView) {
        bookmarkLoadingCallback.onPreExecute(rootView);
    }

    @Override
    public void setHeaderStub(View headerStub) {
        bookmarkLoadingCallback.setHeaderStub(headerStub);
    }

    @Override
    public void onPostExecute(String[] result, String mArticlePubDate) {
        bookmarkLoadingCallback.onPostExecute(result, mArticlePubDate);
    }
}
