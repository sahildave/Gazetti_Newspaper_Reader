package in.sahildave.gazetti.bookmarks;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.crashlytics.android.Crashlytics;
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.bookmarks.sqlite.BookmarkDataSource;
import in.sahildave.gazetti.bookmarks.sqlite.BookmarkModel;

import java.util.List;

public class BookmarkListFragment extends ListFragment {

    // Tags
    private static final String TAG = BookmarkListFragment.class.getName();
    private BookmarkSelectedListeners mBookmarkSelectedListeners = sDummyBookmarkSelectedListeners;
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    // Retained objects
    private static int mActivatedPosition = 1;

    private ListView mListView;

    // From Bundle
    private boolean mTwoPane;

    // Booleans
    private boolean firstRun = false;
    private BookmarkDataSource dataSource;

    public interface BookmarkSelectedListeners {
        public void onItemSelected(String headlineText);
    }

    private static BookmarkSelectedListeners sDummyBookmarkSelectedListeners = new BookmarkSelectedListeners() {
        @Override
        public void onItemSelected(String headlineText) {
        }
    };

    public BookmarkListFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof BookmarkSelectedListeners)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mTwoPane = getArguments().getBoolean("mTwoPane");
        mBookmarkSelectedListeners = (BookmarkSelectedListeners) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstRun = true;
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookmark_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = getListView();
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "ListFragment in onActivityCreated ");

        if (mTwoPane && !firstRun) {
            setActivatedPosition(mActivatedPosition);
        }
        dataSource = new BookmarkDataSource(getActivity());
        dataSource.open();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "Fragment in onDetach");
        mBookmarkSelectedListeners = sDummyBookmarkSelectedListeners;
    }

    @Override
    public void onDestroy() {
        dataSource.close();
        super.onDestroy();
        Log.d(TAG, "Fragment in onDestroy");
        mActivatedPosition = 1;
    }

    @Override
    public void onResume() {
        dataSource.open();
        List<BookmarkModel> values = dataSource.getAllBookmarkModels();
        BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(getActivity(), values);
        SwingBottomInAnimationAdapter animAdapter = new SwingBottomInAnimationAdapter(bookmarkAdapter);
        ScaleInAnimationAdapter animAdapterMultiple = new ScaleInAnimationAdapter(animAdapter);
        animAdapterMultiple.setAbsListView(mListView);

        mListView.setAdapter(animAdapterMultiple);
        super.onResume();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
        TextView headlineTextView = null;
        String headlineText = null;
        try {
            headlineTextView = (TextView) view.findViewById(R.id.headline);
            if(headlineTextView!=null){
                headlineText = (String) headlineTextView.getText();
                setActivatedPosition(position);
                mBookmarkSelectedListeners.onItemSelected(headlineText);
            }

        } catch (Exception e) {
            Log.d(TAG, "Exception in onListItemClick ",e);
            Crashlytics.log(Log.ERROR, TAG, "Is headlineTextView null - "+(null==headlineTextView));
            Crashlytics.log(Log.ERROR, TAG, "Is headlineText null - "+(null==headlineText));
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            mListView.setItemChecked(mActivatedPosition, false);
        } else {
            mListView.setItemChecked(position, true);
        }
        mActivatedPosition = position;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mActivatedPosition != ListView.INVALID_POSITION) {
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
        super.onSaveInstanceState(outState);
    }
}
