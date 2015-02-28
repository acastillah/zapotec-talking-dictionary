package com.example.valleyzapotectalkingdictionary;

import android.support.v7.app.ActionBarActivity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class MainActivity extends ActionBarActivity {

	private static final int LINEAR_LAYOUT_ID = 12345;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// find container in XML to update
		LinearLayout fragContainer = (LinearLayout) findViewById(R.id.llFragmentContainer);
		
		// ll will go inside fragContainer (the layout defined in activity_main.xml)
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		
		ll.setId(LINEAR_LAYOUT_ID);
		
		getFragmentManager().beginTransaction().add(ll.getId(), new SearchBarFragment(), "tag1").commit();
		getFragmentManager().beginTransaction().add(ll.getId(), new WordOfTheDayFragment(), "tag2").commit();
		
		fragContainer.addView(ll);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case R.id.englishInterface:
				// switch to English UI
				return true;
			case R.id.spanishInterface:
				// switch to Spanish UI
				return true;
			case R.id.zapotecInterface:
				// switch to Zapotec UI
				return true;
			case R.id.about:
				startActivity(new Intent(this, AboutActivity.class));
				return true;
			case R.id.update:
				startActivity(new Intent(this, UpdateActivity.class));
				return true;
			case R.id.settings:
				startActivity(new Intent(this, SettingsActivity.class));
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public void searchWord(View view) {
		Intent intent = new Intent(this, SearchResultActivity.class);
		startActivity(intent);
	}
}
