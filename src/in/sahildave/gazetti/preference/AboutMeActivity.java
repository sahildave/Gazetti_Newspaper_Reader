package in.sahildave.gazetti.preference;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.view.MenuItem;
import android.widget.TextView;
import in.sahildave.gazetti.R;

public class AboutMeActivity extends ActionBarActivity {

    private TextView upcomingFeatures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        int ActionBarColorId = getIntent().getIntExtra("ActionBarColor", -1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (ActionBarColorId != -1) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ActionBarColorId));
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        } else {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setBackgroundDrawable(
                    new ColorDrawable(getResources().getColor(R.color.actionbar_default_color)));
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        getSupportActionBar().setTitle("About Me");

        upcomingFeatures = (TextView) findViewById(R.id.aboutMeUpcomingFeatures);

        CharSequence t1 = getText(R.string.aboutMeUpcoming_one);
        SpannableString s1 = new SpannableString(t1);
        s1.setSpan(new BulletSpan(15), 0, t1.length(), 0);

        CharSequence t2 = getText(R.string.aboutMeUpcoming_two);
        SpannableString s2 = new SpannableString(t2);
        s2.setSpan(new BulletSpan(15), 0, t2.length(), 0);

        CharSequence t3 = getText(R.string.aboutMeUpcoming_three);
        SpannableString s3 = new SpannableString(t3);
        s3.setSpan(new BulletSpan(15), 0, t3.length(), 0);

        upcomingFeatures.setText(TextUtils.concat(s1, s2, s3));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();

            return true;
        }
        return super.onOptionsItemSelected(item);

    }

}
