package in.sahildave.gazetti.preference;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.webkit.WebView;
import in.sahildave.gazetti.R;

public class LicensesActivity extends ActionBarActivity {

    // private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenses);

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
        getSupportActionBar().setTitle("Open Source Licenses");
        ((WebView) findViewById(R.id.licenses_web_view)).loadUrl("file:///android_asset/licenses.html");
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
