package in.sahildave.gazetti.homescreen.newcontent;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.util.NewsCatFileUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sahil on 29/11/14.
 */
public class DialogNewContent extends DialogFragment {

    private View dialogView;
    private ExpandableListView expListView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private DialogNewContentExpListAdaper expListAdapter;
    private Button doneButton;
    private LinearLayout topLayer;
    private Button closeButton;
    private NewContentCallback activityCallback;

    public interface NewContentCallback {
        void newContentDoneButton();
        void newContentCloseButton();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            activityCallback = (NewContentCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NewContentCallback");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.NO_TITLE_GREEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        dialogView = inflater.inflate(R.layout.dialog_new_content, null);
        expListView = (ExpandableListView) dialogView.findViewById(R.id.new_content_expandable_list);
        topLayer = (LinearLayout) dialogView.findViewById(R.id.new_content_top_layer);
        doneButton = (Button) dialogView.findViewById(R.id.new_content_done_button);
        closeButton = (Button) dialogView.findViewById(R.id.new_content_close_button);
        prepareListData();

        expListAdapter = new DialogNewContentExpListAdaper(getActivity(), listDataHeader, listDataChild);
        expListAdapter.setExpList(expListView);
        expListView.setAdapter(expListAdapter);

        expListView.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (topLayer.getVisibility()==View.VISIBLE) {
                    topLayer.setVisibility(View.GONE);
                }
                return false;
            }
        });

        doneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> selectedStates = expListAdapter.getClickedStates();
                NewsCatFileUtil.getInstance(getActivity()).updateSelectionWithNewAssets(selectedStates);
                dismiss();
                activityCallback.newContentDoneButton();
            }
        });

        closeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                activityCallback.newContentCloseButton();
            }
        });

        return dialogView;
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
        th.add("Blogs and Editorials");

        List<String> toi = new ArrayList<String>();
        toi.add("Blogs and Editorials");

        List<String> fp = new ArrayList<String>();
        fp.add("Blogs and Editorials");

        List<String> tie = new ArrayList<String>();
        tie.add("Blogs and Editorials");

        // Header, Child data
        listDataChild.put(listDataHeader.get(0), th);
        listDataChild.put(listDataHeader.get(1), toi);
        listDataChild.put(listDataHeader.get(2), fp);
        listDataChild.put(listDataHeader.get(3), tie);
    }
}
