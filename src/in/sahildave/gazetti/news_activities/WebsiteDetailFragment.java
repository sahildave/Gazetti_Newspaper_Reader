package in.sahildave.gazetti.news_activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.crashlytics.android.Crashlytics;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.bookmarks.sqlite.BookmarkDataSource;
import in.sahildave.gazetti.bookmarks.sqlite.BookmarkModel;
import in.sahildave.gazetti.news_activities.adapter.NewsAdapter;
import in.sahildave.gazetti.news_activities.fetch.firstPost;
import in.sahildave.gazetti.news_activities.fetch.hindu;
import in.sahildave.gazetti.news_activities.fetch.indianExpress;
import in.sahildave.gazetti.news_activities.fetch.toi;
import in.sahildave.gazetti.util.ShareButtonListener;

public class WebsiteDetailFragment extends Fragment {
    private static final String TAG = "DFRAGMENT";
    private static final String TAG_ASYNC = "ASYNC";

    public static final String HEADLINE_CLICKED = "mArticleHeadline";

    private String mArticleImageURL;
    private String mArticleURL;
    private String mArticlePubDate;
    private String mArticleHeadline;
    private String mArticleBody;
    private String[] articleContent;

    private LoadArticleCallback mCallbacks;

    private LinearLayout mScrollToReadLayout;

    private GestureDetectorCompat mDetector;
    private Animation slide_down;

    private String npNameString;
    private String catNameString;
    private Button mReadItLater;
    private boolean bookmarked = false;
    private BookmarkDataSource dataSource;
    private Button mViewInBrowser;
    private int actionBarColor = Color.DKGRAY;

    static interface LoadArticleCallback {
        void onPreExecute(View rootView);

        void setHeaderStub(View headerStub);

        void onPostExecute(String[] result, String mArticlePubDate);

        void articleNotFound(String mArticleURL);
    }

    public WebsiteDetailFragment() {
//        Log.d(TAG, "DetailFragment constructor");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof LoadArticleCallback)) {
            throw new IllegalStateException("Activity must implement the LoadArticleCallback interface.");
        }
//        Log.d(TAG, "DetailFragment onAttach");

        mCallbacks = (LoadArticleCallback) activity;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataSource = new BookmarkDataSource(getActivity());
        dataSource.open();
        setRetainInstance(true);

        if(getArguments()!=null){
            npNameString = getArguments().getString("npName");
            catNameString = getArguments().getString("catName");
            actionBarColor = getArguments().getInt("actionBarColor");

            if (getArguments().containsKey(HEADLINE_CLICKED)) {
                mArticleHeadline = getArguments().getString(HEADLINE_CLICKED);
                mArticleURL = NewsAdapter.linkMap.get(mArticleHeadline);
                mArticlePubDate = NewsAdapter.pubDateMap.get(mArticleHeadline);
            }
        }

        mDetector = new GestureDetectorCompat(getActivity(), new MyGestureListener());
        slide_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "DetailFragment onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_website_detail, container, false);

        ImageButton mNewspaperTile = (ImageButton) rootView.findViewById(R.id.newspaperTile);
        mReadItLater = (Button) rootView.findViewById(R.id.read_it_later);
        Button mShareButton = (Button) rootView.findViewById(R.id.shareContent);
        mViewInBrowser = (Button) rootView.findViewById(R.id.viewInBrowser);

        mShareButton.setTextColor(actionBarColor);
        mViewInBrowser.setTextColor(actionBarColor);

        TextView categoryName = (TextView) rootView.findViewById(R.id.category);
        categoryName.setText(catNameString);
        categoryName.setVisibility(View.VISIBLE);
        categoryName.setTextColor(actionBarColor);
        TextView mArticlePubDateView = (TextView) rootView.findViewById(R.id.pubDateView);
        mArticlePubDateView.setTextColor(actionBarColor);

        if (npNameString.equals("The Hindu")) {
            mNewspaperTile.setImageResource(R.drawable.ic_hindu);
        } else if (npNameString.equals("The Times of India")) {
            mNewspaperTile.setImageResource(R.drawable.ic_toi);
        } else if (npNameString.equals("The Indian Express")) {
            mNewspaperTile.setImageResource(R.drawable.ic_tie);
        } else if(npNameString.equals("First Post")) {
            mNewspaperTile.setImageResource(R.drawable.ic_fp);
        }

        mNewspaperTile.setOnClickListener(webViewCalled);
        mViewInBrowser.setOnClickListener(webViewCalled);
        mReadItLater.setOnClickListener(readItLater);
        mShareButton.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                new ShareButtonListener(getActivity(), mArticleURL, mArticleHeadline);
            }
        });

        mScrollToReadLayout = (LinearLayout) rootView.findViewById(R.id.scrollToReadLayout);
        mScrollToReadLayout.setVisibility(View.INVISIBLE);

        Log.d(TAG, "Async called");
        ArticleLoadAsyncTask mTask = new ArticleLoadAsyncTask(rootView);
        mTask.execute();

        ScrollView mScrollView = (ScrollView) rootView.findViewById(R.id.scroller);

        mScrollView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.d(TAG, "ScrollView ACTION_UP");
                    if (mScrollToReadLayout.getVisibility() == View.VISIBLE) {
                        mScrollToReadLayout.startAnimation(slide_down);
                        mScrollToReadLayout.setVisibility(View.INVISIBLE);
                    }
                    return mDetector.onTouchEvent(event);
                }

                return false;
            }
        });
        setupReadItLaterButton();
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setReadItLaterButtonColor();
    }

    private void setReadItLaterButtonColor() {
        PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(actionBarColor, PorterDuff.Mode.SRC_ATOP);

        mReadItLater.getCompoundDrawables()[0].setColorFilter(colorFilter);
        mReadItLater.setTextColor(actionBarColor);
    }

    private void setupReadItLaterButton() {
        dataSource.open();
        if(dataSource.isBookmarked(mArticleURL)){
            mReadItLater.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bookmark_done, 0, 0, 0);
            bookmarked = true;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detail_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_share:
                new ShareButtonListener(getActivity(), mArticleURL, mArticleHeadline);
                return true;
            case R.id.action_view_in_browser:
                mViewInBrowser.performClick();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onDetach() {
        Log.d(TAG, "DetailFragment onDetach");
        super.onDetach();
        mCallbacks = null;
    }

    private OnClickListener webViewCalled = new OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mArticleURL)));

        }
    };

    private OnClickListener readItLater = new OnClickListener() {

        @Override
        public void onClick(View v) {
            dataSource.open();
            if (bookmarked) {
                dataSource.deleteBookmarkModelEntry(mArticleURL);
                mReadItLater.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bookmark, 0, 0, 0);
                setReadItLaterButtonColor();
                bookmarked=false;
            } else if (!bookmarked) {
                dataSource.createBookmarkModelEntry(getBookmarkModelObject());
                mReadItLater.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bookmark_done, 0, 0, 0);
                setReadItLaterButtonColor();
                bookmarked=true;
            }
        }
    };

    private BookmarkModel getBookmarkModelObject() {
        final BookmarkModel bookmarkModel = new BookmarkModel();
        try {
            bookmarkModel.setmArticleHeadline(mArticleHeadline);
            bookmarkModel.setCategoryName(catNameString);
            bookmarkModel.setNewspaperName(npNameString);
            bookmarkModel.setmArticleURL(mArticleURL);
            bookmarkModel.setmArticleBody(mArticleBody);
            bookmarkModel.setmArticleImageURL(mArticleImageURL);
            bookmarkModel.setmArticlePubDate(mArticlePubDate);
        } catch (Exception e) {
            Log.e(TAG, "Exception while creating bookmark object - " + e.getMessage(), e);
            Crashlytics.log(Log.ERROR, TAG, "Exception while creating bookmark object - " + e.getMessage());
        }
        return bookmarkModel;
    }

    public class ArticleLoadAsyncTask extends AsyncTask<Void, String, String[]> {

        View rootView;

        public ArticleLoadAsyncTask(View rootView) {
            Log.d(TAG_ASYNC, "Async Constructor");
            this.rootView = rootView;
        }

        @Override
        protected void onPreExecute() {
            Log.d(TAG_ASYNC, "Async onPreExecute");

            if (mCallbacks != null) {
                mCallbacks.onPreExecute(rootView);
            }

        }

        @Override
        protected String[] doInBackground(Void... params) {

            if (npNameString.equalsIgnoreCase("The Hindu")) {
                hindu hinduObject = new hindu(mArticleURL);
                articleContent = hinduObject.getHinduArticleContent();
            } else if (npNameString.equalsIgnoreCase("The Times of India")) {
                toi toiObject = new toi(mArticleURL);
                articleContent = toiObject.getToiArticleContent();
            } else if (npNameString.equalsIgnoreCase("First Post")) {
                firstPost fpObject = new firstPost(mArticleURL);
                articleContent = fpObject.getFirstPostArticleContent();
            } else if (npNameString.equalsIgnoreCase("The Indian Express")) {
                indianExpress tieObject = new indianExpress(mArticleURL);
                articleContent = tieObject.getTIEArticleContent();
            }

            if (articleContent[0] == null || articleContent[0].length() == 0) {
                articleContent[0] = mArticleHeadline;
            }

            if (articleContent[2] == null || articleContent[2].length() == 0) {
                //TODO: Add alarm here
                return null;
            }

            mArticleImageURL = articleContent[1];
            mArticleBody = articleContent[2];

            return articleContent;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (mCallbacks != null) {
                Log.d(TAG_ASYNC, "Async onPostExecute");
                if(result==null){
                    mCallbacks.articleNotFound(mArticleURL);
                } else {
                    View headerStub;
                    if (mArticleImageURL == null) {
                        headerStub = ((ViewStub) rootView.findViewById(R.id.article_title_stub_import)).inflate();
                    } else {
                        headerStub = ((ViewStub) rootView.findViewById(R.id.article_header_stub_import)).inflate();
                    }
                    mCallbacks.setHeaderStub(headerStub);
                    mCallbacks.onPostExecute(result, mArticlePubDate);
                }
            }
        }
    }

    @Override
    public void onResume() {
        dataSource.open();
        super.onResume();
    }

    @Override
    public void onPause() {
        dataSource.close();
        super.onPause();
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            // Log.d(TAG, "ONDOWN");
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            // Log.d(TAG, "ONFLING");
            return super.onFling(event1, event2, velocityX, velocityY);
        }
    }
}
