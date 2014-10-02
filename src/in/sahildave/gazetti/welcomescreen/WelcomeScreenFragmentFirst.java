package in.sahildave.gazetti.welcomescreen;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import in.sahildave.gazetti.R;

public class WelcomeScreenFragmentFirst extends Fragment {

    public static final String ARG_PAGE = "page";

    private int mPageNumber;

    public static WelcomeScreenFragmentFirst create(int pageNumber) {
        WelcomeScreenFragmentFirst fragment = new WelcomeScreenFragmentFirst();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public WelcomeScreenFragmentFirst() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_welcome_screen_first_fragment, container,
                false);
        return rootView;
    }

}
