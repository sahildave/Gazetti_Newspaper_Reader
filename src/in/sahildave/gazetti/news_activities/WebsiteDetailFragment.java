package in.sahildave.gazetti.news_activities;

import android.app.Activity;
import android.content.Intent;
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
import com.parse.*;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.news_activities.adapter.NewsAdapter;
import in.sahildave.gazetti.news_activities.fetch.firstPost;
import in.sahildave.gazetti.news_activities.fetch.hindu;
import in.sahildave.gazetti.news_activities.fetch.indianExpress;
import in.sahildave.gazetti.news_activities.fetch.toi;
import in.sahildave.gazetti.util.Constants;

import java.util.List;

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

        setRetainInstance(true);

        if(getArguments()!=null){
            npNameString = getArguments().getString("npName");
            catNameString = getArguments().getString("catName");

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
        setupReadItLaterButton(rootView);
        Button mShareButton = (Button) rootView.findViewById(R.id.shareContent);
        Button mViewInBrowser = (Button) rootView.findViewById(R.id.viewInBrowser);

        TextView categoryName = (TextView) rootView.findViewById(R.id.category);
        categoryName.setText(catNameString);
        categoryName.setVisibility(View.VISIBLE);

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
        setHasOptionsMenu(true);
        return rootView;
    }

    private void setupReadItLaterButton(View rootView) {
        mReadItLater = (Button) rootView.findViewById(R.id.read_it_later);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Constants.READ_IT_LATER);
        query.whereEqualTo("link", mArticleURL);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                Log.d(TAG, "===setupReadItLaterButton "+parseObjects.size());
                if(e==null && parseObjects.size()>0){
                    bookmarked=true;
                    mReadItLater.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bookmark_done, 0, 0, 0);
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detail_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Toast.makeText(getActivity(), "Clicked "+item.getTitle(), Toast.LENGTH_SHORT).show();
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
            final ParseObject readItLaterObject = createReadItLaterObject();
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.READ_IT_LATER);
            query.whereEqualTo("objectId", mArticleURL);
            query.fromLocalDatastore();
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    Log.d(TAG, "parseObjects "+parseObjects.size());

                    if (bookmarked) {
                        ParseObject.unpinAllInBackground(parseObjects, new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d(TAG, "Unpinned");
                                    mReadItLater.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bookmark, 0, 0, 0);
                                    bookmarked = false;
                                } else {
                                    Log.d(TAG, "Exception in unpinning", e);
                                }
                            }
                        });
                    } else if (!bookmarked) {
                        readItLaterObject.unpinInBackground();
                        readItLaterObject.pinInBackground(Constants.READ_IT_LATER, new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d(TAG, "Pinned");
                                    mReadItLater.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bookmark_done, 0, 0, 0);
                                    bookmarked = true;
                                }
                            }
                        });
                    }
                }
            });
        }
    };

    private ParseObject createReadItLaterObject() {
        final ParseObject readItLaterObject = new ParseObject(Constants.READ_IT_LATER);
        try {
            readItLaterObject.put("title", mArticleHeadline);
            readItLaterObject.put("body", mArticleBody);
            readItLaterObject.put("link", mArticleURL);
            readItLaterObject.put("pubDate", mArticlePubDate);
            readItLaterObject.put("image", mArticleImageURL);
            readItLaterObject.put("npName", npNameString);
            readItLaterObject.put("catName", catNameString);
            readItLaterObject.setObjectId(mArticleURL);
        } catch (Exception e) {
            Log.e(TAG, "Exception while reading bookmarks - " + e.getMessage(), e);
            Crashlytics.log(Log.ERROR, TAG, "Exception while reading bookmarks - " + e.getMessage());
        }
        return readItLaterObject;
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
