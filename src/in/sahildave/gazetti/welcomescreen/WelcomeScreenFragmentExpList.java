package in.sahildave.gazetti.welcomescreen;

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

public class WelcomeScreenFragmentExpList extends Fragment {

    public static final String ARG_PAGE = "page";

    private int mPageNumber;
    private WelcomeScreenExpListAdapter expListAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    private WelcomeScreenFeedSelectCallback callback;

    public interface WelcomeScreenFeedSelectCallback {
        public void fsFragBackButton();

        public void fsFragDoneButton(HashMap<Integer, boolean[]> mChildCheckStates);
    }

    public static WelcomeScreenFragmentExpList create(int pageNumber) {
        WelcomeScreenFragmentExpList fragment = new WelcomeScreenFragmentExpList();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public WelcomeScreenFragmentExpList() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof WelcomeScreenFeedSelectCallback)) {
            throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
        }
        callback = (WelcomeScreenFeedSelectCallback) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_welcome_screen_feed, container, false);

        ExpandableListView expListView = (ExpandableListView) rootView
                .findViewById(R.id.welcome_feed_select_expandable_list);

        prepareListData();
        expListAdapter = new WelcomeScreenExpListAdapter(getActivity(), listDataHeader, listDataChild);
        expListAdapter.setExpList(expListView);
        expListView.setAdapter(expListAdapter);

        Button done_button = (Button) rootView.findViewById(R.id.welcome_feed_select_explist_done_button);
        done_button.setOnClickListener(new OnClickListener() {
            public void onClick(View next) {
                HashMap<Integer, boolean[]> mChildCheckStates = expListAdapter.getClickedStates();
                callback.fsFragDoneButton(mChildCheckStates);
            }
        });
        return rootView;
    }

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

        List<String> toi = new ArrayList<String>();
        toi.add("National");
        toi.add("International");
        toi.add("Sports");
        toi.add("Science");
        toi.add("Entertainment");

        List<String> fp = new ArrayList<String>();
        fp.add("National");
        fp.add("International");
        fp.add("Sports");
        fp.add("Science");
        fp.add("Entertainment");

        List<String> tie = new ArrayList<String>();
        tie.add("National");
        tie.add("International");
        tie.add("Sports");
        tie.add("Science");
        tie.add("Entertainment");

        // Header, Child data
        listDataChild.put(listDataHeader.get(0), th);
        listDataChild.put(listDataHeader.get(1), toi);
        listDataChild.put(listDataHeader.get(2), fp);
        listDataChild.put(listDataHeader.get(3), tie);
    }
}
