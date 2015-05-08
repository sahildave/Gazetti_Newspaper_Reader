package in.sahildave.gazetti.preference;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.view.MenuItem;
import com.crashlytics.android.Crashlytics;
import in.sahildave.gazetti.R;

public class SettingsActivity extends PreferenceActivity {

    int actionBarColorId = -1;
    private String app_ver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            app_ver = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            Crashlytics.logException(e);
        }

    }

    @SuppressLint("NewApi")
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();

        actionBarColorId = getIntent().getIntExtra("ActionBarColor", -1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);

            if (actionBarColorId != -1) {
                getActionBar().setDisplayShowTitleEnabled(false);
                getActionBar().setBackgroundDrawable(new ColorDrawable(actionBarColorId));
                getActionBar().setDisplayShowTitleEnabled(true);
            } else {
                getActionBar().setDisplayShowTitleEnabled(false);
                getActionBar().setBackgroundDrawable(
                        new ColorDrawable(getResources().getColor(R.color.actionbar_default_color)));
                getActionBar().setDisplayShowTitleEnabled(true);
            }
        }

        // Feed Selector
        Preference feedSelectPref = (Preference) findPreference("feedSelectPref");
        feedSelectPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                startFeedSelector();
                return true;
            }
        });

        // License Section
        Preference licensePref = (Preference) findPreference("licensePref");
        licensePref.setSummary("Build Version : "+app_ver);
        licensePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                Intent licenseIntent = new Intent(SettingsActivity.this, LicensesActivity.class);
                if (actionBarColorId != -1) {
                    licenseIntent.putExtra("ActionBarColor", actionBarColorId);
                }
                startActivity(licenseIntent);
                return true;
            }
        });
    }

    private void startFeedSelector() {
        Intent feedIntent = new Intent(this, FeedSelectSettingsActivity.class);
        if (actionBarColorId != -1) {
            feedIntent.putExtra("ActionBarColor", actionBarColorId);
        }
        startActivity(feedIntent);
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

    private void setupSimplePreferencesScreen() {

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.pref_general);

    }

}
