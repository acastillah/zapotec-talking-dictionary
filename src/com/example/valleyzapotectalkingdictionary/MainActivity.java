package com.example.valleyzapotectalkingdictionary;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks  {

	private static final int LINEAR_LAYOUT_ID = 12345; // needed for adding fragments
//	private static SearchBarFragment searchBarFragment = new SearchBarFragment();
//	private static WordOfTheDayFragment wordOfTheDayFragment = new WordOfTheDayFragment();
//	private static SearchResultFragment searchResultFragment = new SearchResultFragment();
	private NavigationDrawerFragment mNavigationDrawerFragment;
//	private UpdateFragment updateFragment = new UpdateFragment();


	private static Menu menu = null;
	private CharSequence mTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
		mTitle = getTitle();

		
//		/*** Set up the layout of the activity ***/
//		
//		// find container in XML to update
//		LinearLayout fragContainer = (LinearLayout) findViewById(R.id.llFragmentContainer);
//		
//		// ll will go inside fragContainer (the layout defined in activity_main.xml)
//		LinearLayout ll = new LinearLayout(this);
//		ll.setOrientation(LinearLayout.VERTICAL);
//		
//		ll.setId(LINEAR_LAYOUT_ID); // must set an id to use in the add command
//		
		//FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//		
//		// only add fragments once
//		if (getFragmentManager().findFragmentByTag("searchBar") == null) {
//			transaction.add(ll.getId(), searchBarFragment, "searchBar");
//			Log.i("MAIN ACTIVITY", "Added new SearchBarFragment");
//		}
//		if (getFragmentManager().findFragmentByTag("wordOfTheDay") == null) {
//			transaction.add(ll.getId(), wordOfTheDayFragment, "wordOfTheDay");
//			Log.i("MAIN ACTIVITY", "Added new WordOfTheDayFragment");
//		}
//		if (getFragmentManager().findFragmentByTag("searchResult") == null) {
//			transaction.add(ll.getId(), searchResultFragment, "searchResult");
//			Log.i("MAIN ACTIVITY", "Added new SearchResultFragment");
//			transaction.hide(searchResultFragment);
//			Log.i("MAIN ACTIVITY", "Hid SearchBarFragment");
//		}
//		
//		// if phone is horizontal, hide wordOfTheDay
//		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//			Log.i("MAIN ACTIVITY", "Device is horizontal");
//			//transaction.hide(getFragmentManager().findFragmentByTag("wordOfTheDay")); // doesn't work if you use wordOfTheDayFragment directly for some reason
//			//transaction.hide(getSupportFragmentManager().findFragmentByTag(wordOfTheDay));
//			Log.i("MAIN ACTIVITY", "Hid WordOfTheDayFragment");
//		}
//		else { // Configuration.ORIENTATION_PORTRAIT, display word of the day
//			Log.i("MAIN ACTIVITY", "Device is vertical");
//			//transaction.show(wordDay);
//			Log.i("MAIN ACTIVITY", "Showed WordOfTheDayFragment");
//		}
//		
//		if (searchBarFragment.searchBarIsEmpty()) {
//			Log.i("MAIN ACTIVITY", "Search bar is empty");
//			
//			if (getFragmentManager().findFragmentByTag("searchResult") != null) {
//			transaction.hide(getFragmentManager().findFragmentByTag("searchResult"));
//			Log.i("MAIN ACTIVITY", "Hid SearchResultFragment");
//			}
//		}
//		else {
//			Log.i("MAIN ACTIVITY", "Search bar contains text");
//			transaction.show(searchResultFragment);
//			Log.i("MAIN ACTIVITY", "Showed SearchResultFragment");
//		}
//		
//		
//		transaction.commit();
//
//		fragContainer.addView(ll);
//		
		
		Log.i("MAIN ACTIVITY", "End of onCreate");
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Inflate the menu; this adds items to the action bar if it is present.
			MainActivity.menu = menu;
			getMenuInflater().inflate(R.menu.main, menu);
			LanguageInterface.setLanguageInterfaceButtons(menu);
			restoreActionBar();
			return true;

		}
		return super.onCreateOptionsMenu(menu);
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case R.id.englishInterface:
				LanguageInterface.interfaceLanguage = LanguageInterface.LANGUAGE_ENGLISH;
				LanguageInterface.setLanguageInterfaceButtons(MainActivity.menu);
				return true;
			case R.id.spanishInterface:
				LanguageInterface.interfaceLanguage = LanguageInterface.LANGUAGE_SPANISH;
				LanguageInterface.setLanguageInterfaceButtons(MainActivity.menu);
				return true;
			case R.id.zapotecInterface:
				LanguageInterface.interfaceLanguage = LanguageInterface.LANGUAGE_ZAPOTEC;
				LanguageInterface.setLanguageInterfaceButtons(MainActivity.menu);
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
	
	public void displayWordOfTheDay(View v) {
		Log.i("WORD OF THE DAY FRAGMENT", "Word of the day was clicked");
		startActivity(new Intent(this, WordDefinitionActivity.class));		
	}
	
	static class LanguageInterface {
		public static final int LANGUAGE_ENGLISH = 0;
		public static final int LANGUAGE_SPANISH = 1;
		public static final int LANGUAGE_ZAPOTEC = 2;
		
		public static int interfaceLanguage = 0;
		
		public static void setLanguageInterfaceButtons(Menu menu) {
			for (int i = 0; i < menu.size(); i++)
	            menu.getItem(i).setVisible(true);
			
			MenuItem menuItem = null;
			switch (LanguageInterface.interfaceLanguage) {
				case LanguageInterface.LANGUAGE_ENGLISH:
					menuItem = menu.findItem(R.id.englishInterface);
					break;
				case LanguageInterface.LANGUAGE_SPANISH:
					menuItem = menu.findItem(R.id.spanishInterface);
					break;
				case LanguageInterface.LANGUAGE_ZAPOTEC:
					menuItem = menu.findItem(R.id.zapotecInterface);
					break;
			}	
			
			if (menuItem != null) {
				menuItem.setVisible(false);
			}
		}
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment;
		
		switch(position+1){
			case 1:
				fragment = new SearchBarFragment();	
				mTitle = getString(R.string.title_main_section);
				break;
			case 2:
				fragment = new UpdateFragment();
				mTitle = getString(R.string.title_section2);
				break;
			case 3:
				fragment = new AboutFragment();
				mTitle = getString(R.string.title_section3);
				break;
			default:
				fragment = new PlaceholderFragment();
		}
		
		fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();				
		if (position+1 == 1){
			Log.i("Word","1");
			Fragment WordFragment = new WordOfTheDayFragment();
			fragmentManager.beginTransaction().add(WordFragment, "wordDay");
		}
		
	}

}
