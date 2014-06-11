package com.example.try_masterdetail.preference;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.widget.TextView;

import com.example.try_masterdetail.R;

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

		String upcomingList = getResources().getString(R.string.aboutMeUpcoming);
		upcomingFeatures.setText(Html.fromHtml(upcomingList));

	}

}
