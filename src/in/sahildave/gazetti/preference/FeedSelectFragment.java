package in.sahildave.gazetti.preference;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import in.sahildave.gazetti.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedSelectFragment extends Fragment {

    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    private int mPageNumber;
    private PreferenceExpListAdapter expListAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    private FeedSelectCallback callback;

    public interface FeedSelectCallback {
        public void fsFragBackButton();

        public void fsFragDoneButton(Map<String, Object> mChildCheckStates);
    }

    public static FeedSelectFragment create(int pageNumber) {
        FeedSelectFragment fragment = new FeedSelectFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public FeedSelectFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof FeedSelectCallback)) {
            throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
        }
        callback = (FeedSelectCallback) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.feed_select_fragment_explist, container, false);
        ExpandableListView expListView = (ExpandableListView) rootView.findViewById(R.id.feed_select_expandable_list);

        prepareListData();
        expListAdapter = new PreferenceExpListAdapter(getActivity(), listDataHeader, listDataChild);
        expListView.setAdapter(expListAdapter);
        expListAdapter.setExpList(expListView);

        // Watch for button clicks.
        Button back_button = (Button) rootView.findViewById(R.id.feed_select_explist_back_button);
        back_button.setOnClickListener(new OnClickListener() {
            public void onClick(View back) {
                callback.fsFragBackButton();
            }
        });

        Button done_button = (Button) rootView.findViewById(R.id.feed_select_explist_done_button);
        done_button.setOnClickListener(new OnClickListener() {
            public void onClick(View next) {
                callback.fsFragDoneButton(expListAdapter.getClickedStates());
            }
        });

        return rootView;
    }

    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add(0, "The Hindu");
        listDataHeader.add(1, "The Times of India");
        listDataHeader.add(2, "First Post");
        listDataHeader.add(3, "The Indian Express");

        // Adding child data
        List<String> th = new ArrayList<String>();
        th.add("National");
        th.add("International");
        th.add("Sports");
        th.add("Science");
        th.add("Entertainment");
        th.add("Business");
        th.add("Blogs and Editorials");

        List<String> toi = new ArrayList<String>();
        toi.add("National");
        toi.add("International");
        toi.add("Sports");
        toi.add("Science");
        toi.add("Entertainment");
        toi.add("Business");
        toi.add("Blogs and Editorials");

        List<String> fp = new ArrayList<String>();
        fp.add("National");
        fp.add("International");
        fp.add("Sports");
        fp.add("Science");
        fp.add("Entertainment");
        fp.add("Business");
        fp.add("Blogs and Editorials");

        List<String> tie = new ArrayList<String>();
        tie.add("National");
        tie.add("International");
        tie.add("Sports");
        tie.add("Science");
        tie.add("Entertainment");
        tie.add("Business");
        tie.add("Blogs and Editorials");

        // Header, Child data
        listDataChild.put(listDataHeader.get(0), th);
        listDataChild.put(listDataHeader.get(1), toi);
        listDataChild.put(listDataHeader.get(2), fp);
        listDataChild.put(listDataHeader.get(3), tie);
    }

}
