package in.sahildave.gazetti.bookmarks;

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
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.util.Constants;

public class BookmarkDetailFragment extends Fragment {
    private static final String TAG = BookmarkDetailFragment.class.getName();

    public static final String HEADLINE_CLICKED = "mArticleHeadline";

    private String mArticleImageURL;
    private String mArticleURL;
    private String mArticlePubDate;
    private String mArticleHeadline;
    private String mArticleBody;
    private String[] articleContent;

    private BookmarkLoadArticleCallback mCallbacks;

    private LinearLayout mScrollToReadLayout;

    private GestureDetectorCompat mDetector;
    private Animation slide_down;

    private String npNameString;
    private String catNameString;
    private Button mReadItLater;
    private boolean bookmarked = false;

    static interface BookmarkLoadArticleCallback {
        void onPreExecute(View rootView);

        void setHeaderStub(View headerStub);

        void onPostExecute(String[] result, String mArticlePubDate);
    }

    public BookmarkDetailFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof BookmarkLoadArticleCallback)) {
            throw new IllegalStateException("Activity must implement the BookmarkLoadArticleCallback interface.");
        }
        mCallbacks = (BookmarkLoadArticleCallback) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if(getArguments()!=null){

            if (getArguments().containsKey(HEADLINE_CLICKED)) {
                mArticleHeadline = getArguments().getString(HEADLINE_CLICKED);
                mArticleURL = BookmarkAdapter.linkMap.get(mArticleHeadline);
                mArticlePubDate = BookmarkAdapter.pubDateMap.get(mArticleHeadline);
                mArticleBody = BookmarkAdapter.articleBody.get(mArticleHeadline);
                mArticleImageURL = BookmarkAdapter.articleImage.get(mArticleHeadline);
                npNameString = BookmarkAdapter.npNameMap.get(mArticleHeadline);
                catNameString = BookmarkAdapter.catNameMap.get(mArticleHeadline);

                Log.d(TAG, "BookmarkDetailFragment - \n"
                        +mArticleHeadline+"\n"
                        +mArticleURL+"\n"
                        +mArticlePubDate+"\n"
                        +mArticleImageURL+"\n"
                        +mArticleBody+"\n"
                        +catNameString+"\n"
                        +npNameString+"\n");
            }
        }

        mDetector = new GestureDetectorCompat(getActivity(), new MyGestureListener());
        slide_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "BookmarkDetailFragment onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_website_detail, container, false);

        ImageButton mNewspaperTile = (ImageButton) rootView.findViewById(R.id.newspaperTile);
        mReadItLater = (Button) rootView.findViewById(R.id.read_it_later);
        mReadItLater.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bookmark_done, 0, 0, 0);
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

        new ArticleLoadAsyncTask(rootView).execute();

        return rootView;
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

            if(bookmarked){
                ParseObject.unpinAllInBackground(mArticleHeadline, new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        mReadItLater.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bookmark, 0, 0, 0);
                        bookmarked = false;
                    }
                });
            }else {
                ParseObject readItLaterObject = new ParseObject(Constants.READ_IT_LATER);
                try {
                    readItLaterObject.put("title", mArticleHeadline);
                    readItLaterObject.put("body", mArticleBody);
                    readItLaterObject.put("pubDate", mArticlePubDate);
                    readItLaterObject.put("image", mArticleImageURL);
                    readItLaterObject.put("npName", npNameString);
                    readItLaterObject.put("catName", catNameString);
                } catch (Exception e) {
                    Log.e(TAG, "Exception while reading bookmarks - " + e.getMessage(), e);
                    Crashlytics.log(Log.ERROR, TAG, "Exception while reading bookmarks - " + e.getMessage());
                }


                readItLaterObject.pinInBackground("readitlater", new SaveCallback() {

                    @Override
                    public void done(ParseException e) {
                        mReadItLater.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bookmark_done, 0, 0, 0);
                        bookmarked = true;
                    }
                });
            }
        }
    };

    public class ArticleLoadAsyncTask extends AsyncTask<Void, String, String[]> {

        View rootView;

        public ArticleLoadAsyncTask(View rootView) {
            articleContent = new String[3];
            this.rootView = rootView;
        }

        @Override
        protected void onPreExecute() {
            if (mCallbacks != null) {
                mCallbacks.onPreExecute(rootView);
            }

        }

        @Override
        protected String[] doInBackground(Void... params) {

            try {
                articleContent[0] = mArticleHeadline;
                articleContent[1] = mArticleImageURL;
                articleContent[2] = mArticleBody;
            } catch (Exception e) {
                Log.e(TAG, "Exception while reading bookmarks - " + e.getMessage(), e);
                Crashlytics.log(Log.ERROR, TAG, "Exception while reading bookmarks - " + e.getMessage());
            }

            return articleContent;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (mCallbacks != null) {
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

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
            return super.onFling(event1, event2, velocityX, velocityY);
        }
    }
}
