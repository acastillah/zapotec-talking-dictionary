package com.example.valleyzapotectalkingdictionary;

import android.support.v7.app.ActionBarActivity;
import android.text.TextWatcher;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends ActionBarActivity {

	private static final int LINEAR_LAYOUT_ID = 12345; // needed for adding fragments
	
	private static SearchBarFragment searchBarFragment = new SearchBarFragment();
	private static WordOfTheDayFragment wordOfTheDayFragment = new WordOfTheDayFragment();
	private static SearchResultFragment searchResultFragment = new SearchResultFragment();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/*** Set up the layout of the activity ***/
		
		// find container in XML to update
		LinearLayout fragContainer = (LinearLayout) findViewById(R.id.llFragmentContainer);
		
		// ll will go inside fragContainer (the layout defined in activity_main.xml)
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		
		ll.setId(LINEAR_LAYOUT_ID); // must set an id to use in the add command
		
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		
		// only add fragments once
		if (getFragmentManager().findFragmentByTag("searchBar") == null) {
			transaction.add(ll.getId(), searchBarFragment, "searchBar");
			Log.i("MAIN ACTIVITY", "Added new SearchBarFragment");
		}
		if (getFragmentManager().findFragmentByTag("wordOfTheDay") == null) {
			transaction.add(ll.getId(), wordOfTheDayFragment, "wordOfTheDay");
			Log.i("MAIN ACTIVITY", "Added new WordOfTheDayFragment");
		}
		if (getFragmentManager().findFragmentByTag("searchResult") == null) {
			transaction.add(ll.getId(), searchResultFragment, "searchResult");
			Log.i("MAIN ACTIVITY", "Added new SearchResultFragment");
			transaction.hide(searchResultFragment);
			Log.i("MAIN ACTIVITY", "Hid SearchBarFragment");
		}
		
		// if phone is horizontal, hide wordOfTheDay
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.i("MAIN ACTIVITY", "Device is horizontal");
			transaction.hide(getFragmentManager().findFragmentByTag("wordOfTheDay")); // doesn't work if you use wordOfTheDayFragment directly for some reason
			Log.i("MAIN ACTIVITY", "Hid WordOfTheDayFragment");
		}
		else { // Configuration.ORIENTATION_PORTRAIT, display word of the day
			Log.i("MAIN ACTIVITY", "Device is vertical");
			transaction.show(wordOfTheDayFragment);
			Log.i("MAIN ACTIVITY", "Showed WordOfTheDayFragment");
		}
		
		if (searchBarFragment.searchBarIsEmpty()) {
			Log.i("MAIN ACTIVITY", "Search bar is empty");
			
			if (getFragmentManager().findFragmentByTag("searchResult") != null) {
			transaction.hide(getFragmentManager().findFragmentByTag("searchResult"));
			Log.i("MAIN ACTIVITY", "Hid SearchResultFragment");
			}
		}
		else {
			Log.i("MAIN ACTIVITY", "Search bar contains text");
			transaction.show(searchResultFragment);
			Log.i("MAIN ACTIVITY", "Showed SearchResultFragment");
		}
		
		
		transaction.commit();

		fragContainer.addView(ll);
		
		
		Log.i("MAIN ACTIVITY", "End of onCreate");
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
	
//	public void searchWord(View view) {
//		Intent intent = new Intent(this, SearchResultActivity.class);
//		startActivity(intent);
//	}
	
	public void displayWord(View view) {
		Log.i("SEARCH RESULT FRAGMENT", "Displaying word details in a new WordDefinitionActivity...");
		startActivity(new Intent(this, WordDefinitionActivity.class));
	}
}
