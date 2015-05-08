package in.sahildave.gazetti.news_activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.news_activities.WebsiteDetailFragment.LoadArticleCallback;

public class WebsiteDetailActivity extends AppCompatActivity implements LoadArticleCallback {

    WebsiteDetailFragment mDetailFragment;
    private ArticleLoadingCallback articleLoadingCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website_detail);

        int ActionBarColorId = getIntent().getIntExtra("ActionBarColor", -1);
        String ActionBarTitleString = getIntent().getStringExtra("ActionBarTitle");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(ActionBarTitleString);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ActionBarColorId));
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mDetailFragment = (WebsiteDetailFragment) getSupportFragmentManager().findFragmentByTag("detail");

        if (mDetailFragment == null) {
            mDetailFragment = new WebsiteDetailFragment();

            Bundle arguments = new Bundle();
            arguments.putString("npName", getIntent().getStringExtra("npName"));
            arguments.putString("catName", getIntent().getStringExtra("catName"));
            arguments.putInt("actionBarColor", ActionBarColorId);
            arguments.putString(WebsiteDetailFragment.HEADLINE_CLICKED,
                    getIntent().getStringExtra(WebsiteDetailFragment.HEADLINE_CLICKED));
            mDetailFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.website_detail_container, mDetailFragment, "detail").commit();
        }

        Animation slide_up = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        articleLoadingCallback = new ArticleLoadingCallback(this);

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
        articleLoadingCallback.onPreExecute(rootView);
    }

    @Override
    public void setHeaderStub(View headerStub) {
        articleLoadingCallback.setHeaderStub(headerStub);
    }

    @Override
    public void onPostExecute(String[] result, String mArticlePubDate) {
        articleLoadingCallback.onPostExecute(result, mArticlePubDate);
    }

    @Override
    public void articleNotFound(String mArticleUrl) {
        articleLoadingCallback.articleNotFound(mArticleUrl);
    }

}
