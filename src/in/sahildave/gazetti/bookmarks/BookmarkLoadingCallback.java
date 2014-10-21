package in.sahildave.gazetti.bookmarks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.util.TextViewEx;

/**
 * Created by sahil on 4/10/14.
 */
public class BookmarkLoadingCallback {

    // CallBacks for handling asynctask for twoPane mode

    View rootView; // ScrollView from DetailFragment
    View headerStub;

    // Header
    SmoothProgressBar detailViewProgress;
    TextView mTitleTextView;
    ImageView mMainImageView;
    String mImageURL;
    String titleText = "";

    // Subtitle
    RelativeLayout mSubtitleLayout;
    TextView mArticlePubDateView;

    // Body
    TextViewEx mArticleTextView;
    String bodyText = "";

    // Footer
    LinearLayout mArticleFooter;

    LinearLayout mScrollToReadLayout;
    private boolean displayScrollToRead = false;
    private Animation slide_up;
    private Activity activity;

    public BookmarkLoadingCallback(Activity activity){
        this.activity = activity;
        slide_up = AnimationUtils.loadAnimation(activity, R.anim.slide_up);
    }

    public void onPreExecute(View rootView) {
        this.rootView = rootView;

        // initialize article views
        mArticleTextView = (TextViewEx) rootView.findViewById(R.id.article_body);
        mArticlePubDateView = (TextView) rootView.findViewById(R.id.pubDateView);
        mSubtitleLayout = (RelativeLayout) rootView.findViewById(R.id.subtitleLayout);
        mScrollToReadLayout = (LinearLayout) rootView.findViewById(R.id.scrollToReadLayout);
        mArticleFooter = (LinearLayout) rootView.findViewById(R.id.article_footer);

        // Progress Bar
        detailViewProgress = (SmoothProgressBar) rootView.findViewById(R.id.detailViewProgressBar);
        detailViewProgress.setVisibility(View.VISIBLE);
        detailViewProgress.progressiveStart();
    }

    public void setHeaderStub(View headerStub) {
        this.headerStub = headerStub;
    }

    public void onPostExecute(String[] result, String mArticlePubDate) {

        titleText = result[0];
        mImageURL = result[1];
        bodyText = result[2];

        mSubtitleLayout.setVisibility(View.VISIBLE);
        mArticleFooter.setVisibility(View.VISIBLE);
        mArticlePubDateView.setText(mArticlePubDate);
        mArticleTextView.setVisibility(View.VISIBLE);
        mArticleTextView.setText(bodyText, true);

        if (mImageURL == null || mImageURL.equalsIgnoreCase("")) {
            mTitleTextView = (TextView) headerStub.findViewById(R.id.article_title);
            mTitleTextView.setText(titleText);
            detailViewProgress.progressiveStop();
            detailViewProgress.setVisibility(View.GONE);
        } else {
            mTitleTextView = (TextView) headerStub.findViewById(R.id.article_header_title);
            mTitleTextView.setText(titleText);

            mMainImageView = (ImageView) headerStub.findViewById(R.id.article_header_image);
            Picasso picassoInstance = Picasso.with(activity);
            picassoInstance.load(mImageURL).into(mMainImageView, new Callback() {

                @Override
                public void onSuccess() {

                    mMainImageView.getViewTreeObserver().addOnGlobalLayoutListener(
                            new ViewTreeObserver.OnGlobalLayoutListener() {

                                @SuppressWarnings("deprecation")
                                @SuppressLint("NewApi")
                                @Override
                                public void onGlobalLayout() {

                                    // Get Display metrics according to the SDK
                                    Display display = activity.getWindowManager().getDefaultDisplay();
                                    Point screen = new Point();
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                                        display.getSize(screen);
                                    } else {
                                        screen.x = display.getWidth();
                                        screen.y = display.getHeight();
                                    }

                                    // StatusBar Height
                                    int statusBarHeight = 0;
                                    int resId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
                                    if (resId > 0) {
                                        statusBarHeight = activity.getResources().getDimensionPixelSize(resId);
                                    }

                                    // ActionBar Height
                                    TypedValue tv = new TypedValue();
                                    int actionBarHeight = 0;
                                    if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                                        actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                                                activity.getResources().getDisplayMetrics());
                                    }


                                    // Boolean to check if image+subtitle is
                                    // large enough.
                                    // If yes, then display "Scroll To Read"
                                    displayScrollToRead = (screen.y - statusBarHeight - actionBarHeight) < (mArticleTextView
                                            .getTop()) * 1.08;

                                    if (displayScrollToRead) {
                                        mScrollToReadLayout.startAnimation(slide_up);
                                        mScrollToReadLayout.setVisibility(View.VISIBLE);
                                    }

                                    // remove GlobalLayoutListener according to
                                    // SDK
                                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                                        mMainImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                    } else {
                                        mMainImageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                    }

                                }

                            });

                    detailViewProgress.progressiveStop();
                    detailViewProgress.setVisibility(View.GONE);

                }

                @Override
                public void onError() {}
            });
        }

        bodyText = null;
        titleText = null;
        mImageURL = null;
    }
}
