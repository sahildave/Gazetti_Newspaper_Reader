package in.sahildave.gazetti.welcomescreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;
import android.widget.Toast;
import com.jfeinstein.jazzyviewpager.JazzyViewPager;
import com.jfeinstein.jazzyviewpager.JazzyViewPager.TransitionEffect;
import in.sahildave.gazetti.R;
import in.sahildave.gazetti.homescreen.HomeScreenActivity;
import in.sahildave.gazetti.util.Constants;
import in.sahildave.gazetti.util.NewsCatFileUtil;
import in.sahildave.gazetti.util.UserPrefUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WelcomeScreenViewPagerActivity extends FragmentActivity implements
        WelcomeScreenFragmentExpList.WelcomeScreenFeedSelectCallback {

    private static final int NUM_ITEMS = 2;
    private JazzyViewPager mPager;
    private boolean selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen_view_pager);

        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(WelcomeScreenFragmentFirst.create(0));
        fragmentList.add(WelcomeScreenFragmentExpList.create(1));

        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragmentList);

        mPager = (JazzyViewPager) findViewById(R.id.welcome_screen_pager);
        mPager.setTransitionEffect(TransitionEffect.Tablet);
        mPager.setFadeEnabled(true);
        mPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();

            // go back home
            Intent intent = new Intent(this, HomeScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Exit me", true);
            startActivity(intent);
            finish();

        } else if (mPager.getCurrentItem() == 1 && !selected) {
            mPager.setCurrentItem(0);
        } else if (mPager.getCurrentItem() == 1 && selected) {
            super.onBackPressed();
        }

    }

    private void welcomeFinished() {
        SharedPreferences preferences = getSharedPreferences(Constants.IS_FIRST_RUN, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.IS_FIRST_RUN, false);
        editor.commit();
    }

    @Override
    public void fsFragDoneButton(Map<String, Object> mChildCheckStates) {

        Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
        selected = true;
        welcomeFinished();
        onBackPressed();

        UserPrefUtil.setUserPrefChanged(true);
        NewsCatFileUtil.getInstance().saveUserSelectionToJsonFile(mChildCheckStates);
    }

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList;

        public ScreenSlidePagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {

            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Object obj = super.instantiateItem(container, position);
            mPager.setObjectForPosition(obj, position);
            return obj;
        }
    }

}
