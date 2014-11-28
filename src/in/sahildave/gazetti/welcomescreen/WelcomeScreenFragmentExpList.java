package in.sahildave.gazetti.welcomescreen;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.welcomescreen.WelcomeScreenExpListAdapter.CheckBoxInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WelcomeScreenFragmentExpList extends Fragment {

    public static final String ARG_PAGE = "page";
    private static final String LOG_TAG = WelcomeScreenFragmentExpList.class.getName();
    private static int originalBottomMargin;

    private int mPageNumber;
    private WelcomeScreenExpListAdapter expListAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    private WelcomeScreenFeedSelectCallback callback;
    private Button doneButton;
    private static LinearLayout topLayer;
    public static boolean topLayerHidden;
    private static LinearLayout bottomLayer;

    public interface WelcomeScreenFeedSelectCallback {
        public void fsFragDoneButton(Map<String, Object> mChildCheckStates);
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
        View rootView = inflater.inflate(R.layout.fragment_welcome_screen_feed, container, false);
        topLayer = (LinearLayout) rootView.findViewById(R.id.top_layer);
        bottomLayer = (LinearLayout) rootView.findViewById(R.id.bottom_layer);
        ExpandableListView expListView = (ExpandableListView) rootView
                .findViewById(R.id.welcome_feed_select_expandable_list);

        doneButton = (Button) rootView.findViewById(R.id.welcome_feed_select_explist_done_button);
        doneButton.setEnabled(false);
        doneButton.setOnClickListener(new OnClickListener() {
            public void onClick(View next) {
                Map<String, Object> mChildCheckStates = expListAdapter.getClickedStates();
                callback.fsFragDoneButton(mChildCheckStates);
            }
        });

        prepareListData();
        expListAdapter = new WelcomeScreenExpListAdapter(getActivity(), listDataHeader, listDataChild, checkBoxInterface);
        expListAdapter.setExpList(expListView);
        expListView.setAdapter(expListAdapter);

        expListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Log.d(LOG_TAG, "Top Layer Hidden ? " + topLayerHidden);
                if (topLayer.getVisibility()==View.VISIBLE) {
                    LayoutParams originalParams = (LayoutParams) bottomLayer.getLayoutParams();
                    originalBottomMargin = originalParams.bottomMargin;

                    LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 0, 0, 0);
                    bottomLayer.setLayoutParams(params);

                    topLayer.setVisibility(View.GONE);
                    topLayerHidden = true;
                }
                return false;
            }
        });

        return rootView;
    }

    CheckBoxInterface checkBoxInterface = new CheckBoxInterface() {
        @Override
        public void checkBoxClicked(boolean isChecked) {
            if(isChecked){
                doneButton.setText(R.string.welcome_exp_list_button_enabled);
            } else {
                doneButton.setText(R.string.welcome_exp_list_button_disabled);
            }
            doneButton.setEnabled(isChecked);
        }
    };

    public static boolean handleBackPressed(){
        if(topLayerHidden){
            topLayer.setVisibility(View.VISIBLE);
            topLayerHidden = false;

            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(0,0,0,originalBottomMargin);
            bottomLayer.setLayoutParams(params);

            return false;
        } else {
            return true;
        }
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
