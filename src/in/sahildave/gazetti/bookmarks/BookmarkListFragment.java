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
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class BookmarkListFragment extends ListFragment {

    // Tags
    private static final String TAG = BookmarkListFragment.class.getName();
    private BookmarkSelectedListeners mBookmarkSelectedListeners = sDummyBookmarkSelectedListeners;
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    // Retained objects
    private static int mActivatedPosition = 1;

    // For HeadlinesList
    private BookmarkAdapter bookmarkAdapter;
    private ListView mListView;
    private List<ParseObject> retainedList = new ArrayList<ParseObject>();

    // From Bundle
    private boolean mTwoPane;

    // Booleans
    private boolean firstRun = false;

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

        bookmarkAdapter = new BookmarkAdapter(getActivity(), retainedList);
        SwingBottomInAnimationAdapter animAdapter = new SwingBottomInAnimationAdapter(bookmarkAdapter);
        ScaleInAnimationAdapter animAdapterMultiple = new ScaleInAnimationAdapter(animAdapter);
        animAdapterMultiple.setAbsListView(mListView);

        mListView.setAdapter(animAdapterMultiple);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "ListFragment in onActivityCreated ");

        if (mTwoPane && !firstRun) {
            setActivatedPosition(mActivatedPosition);
        }

        getBookmarks();

    }

    private void getBookmarks() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(Constants.READ_IT_LATER);
        query.fromLocalDatastore();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> bookmarks, ParseException e) {
                if(e == null) {
                    Log.d(TAG, "Bookmarks - "+bookmarks.size());
                    retainedList.addAll(bookmarks);
                    bookmarkAdapter.notifyDataSetChanged();
                    if (mTwoPane) {
                        mListView.performItemClick(bookmarkAdapter.getView(mActivatedPosition - 1, null, null),
                                mActivatedPosition, mActivatedPosition);
                    }
                    firstRun = false;
                } else {
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    Crashlytics.log(Log.ERROR, TAG, "Exception while reading bookmarks - "+e.getMessage());
                }
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "Fragment in onDetach");
        mBookmarkSelectedListeners = sDummyBookmarkSelectedListeners;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Fragment in onDestroy");
        mActivatedPosition = 1;
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
