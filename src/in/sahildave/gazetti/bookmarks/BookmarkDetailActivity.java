package in.sahildave.gazetti.bookmarks;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.bookmarks.BookmarkDetailFragment.BookmarkLoadArticleCallback;

public class BookmarkDetailActivity extends ActionBarActivity implements BookmarkLoadArticleCallback {
    BookmarkDetailFragment mDetailFragment;
    private BookmarkLoadingCallback bookmarkLoadingCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website_detail);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_default_color)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mDetailFragment = (BookmarkDetailFragment) getSupportFragmentManager().findFragmentByTag("bookmarkDetail");

        if (mDetailFragment == null) {
            mDetailFragment = new BookmarkDetailFragment();
            Bundle arguments = new Bundle();

            String headlineClicked = BookmarkDetailFragment.HEADLINE_CLICKED;
            arguments.putString(headlineClicked, getIntent().getStringExtra(headlineClicked));
            mDetailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.website_detail_container, mDetailFragment, "bookmarkDetail").commit();
        }
        bookmarkLoadingCallback = new BookmarkLoadingCallback(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
