package com.example.valleyzapotectalkingdictionary;

import java.util.Locale;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks  {

	private NavigationDrawerFragment mNavigationDrawerFragment;

	@SuppressWarnings("unused")
	static private Spinner searchSpinner, domainSpinner;
	private int Language_search = 0;
	@SuppressWarnings("unused")
	private static Menu menu = null;
	private CharSequence mTitle;
	
	private int setLanguage(Spinner spinner) {
		SharedPreferences prefs = this.getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
		Editor prefEditor = prefs.edit();
		
		if (!prefs.contains(Preferences.LANGUAGE))
			prefEditor.putString(Preferences.LANGUAGE, Preferences.ENGLISH);
		
		prefEditor.commit();
		
		Log.i("LANG", "App displaying in " + prefs.getString(Preferences.LANGUAGE, "no language"));
		
		prefs = this.getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
		String lang = prefs.getString(Preferences.LANGUAGE, Preferences.ENGLISH);
		Locale locale = Preferences.ENGLISH_LOC;
		int selection = 0;
		
		if (lang.equals(Preferences.SPANISH)) {
			locale = Preferences.SPANISH_LOC;
			selection = 1;
		}
		else if (lang.equals(Preferences.ZAPOTEC)) {
			locale = Preferences.ZAPOTEC_LOC;
			selection = 2;
		}
		else {
			locale = Preferences.ENGLISH_LOC;
			selection = 0;
		}
		
		Locale.setDefault(locale);
		
//		if (spinner != null)
//			spinner.setSelection(selection);
		
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config, 
		    getBaseContext().getResources().getDisplayMetrics());
		
		return selection;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	 
		
		/** Set the language **/
		
		setLanguage(null);
		
		/*********************/
		
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
	    SearchView searchView = (SearchView) findViewById(R.id.searchBAR);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);		
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));	
		searchView.setBackgroundColor(Color.GRAY);
		mTitle = getString(R.string.app_name);
		addListenerOnSpinnerItemSelection(); 
		Log.i("LANG", "After spinner: " + Integer.toString(Language_search));
        handleIntent(getIntent());
	}
	

	public boolean onCreateOptionsMenu(Menu menu) {
				
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Inflate the menu; this adds items to the action bar if it is present.
			MainActivity.menu = menu;
			getMenuInflater().inflate(R.menu.main, menu);
			//LanguageInterface.setLanguageInterfaceButtons(menu);
			restoreActionBar();
			
			MenuItem spinnerItem = menu.findItem(R.id.spinner);
			Spinner spinner = (Spinner) MenuItemCompat.getActionView(spinnerItem);
			
			if (spinner != null) {
				// Create an ArrayAdapter using the string array and a default spinner layout
				ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				        R.array.languages_array, android.R.layout.simple_spinner_item);
				// Specify the layout to use when the list of choices appears
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// Apply the adapter to the spinner
				spinner.setAdapter(adapter);
				
				OnItemSelectedListener listener = new LanguageSpinnerItemSelectedListener();
				spinner.setOnItemSelectedListener(listener);
				
				int selection = setLanguage(spinner);
				spinner.setSelection(selection);
			}
			else {
			}
			
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
		
		switch (item.getItemId()) {}
		
		return super.onOptionsItemSelected(item);
	}
	
	 public void addListenerOnSpinnerItemSelection() {
			searchSpinner = (Spinner) findViewById(R.id.search_spinner);
			CustomItemSelectedListener listener = new CustomItemSelectedListener();
			searchSpinner.setOnItemSelectedListener(listener);
			Language_search = listener.getLanguage();
	  }
	
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
		/*
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
		*/
	}
	
	@Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
//        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
//            // handles a click on a search suggestion; launches activity to show word
//            Intent wordIntent = new Intent(this, WordDefinitionActivity.class);
//            wordIntent.setData(intent.getData());
//            startActivity(wordIntent);
//            finish();
//        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
    	if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
    		// handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
            showResults(query);
        }
    }
    
    private void showResults(String query) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		Fragment fragment = new SearchResultsFragment();
        transaction.addToBackStack(null); 
		Log.i("LANG", "Show Results: " + Integer.toString(Language_search));
        Bundle bundle = new Bundle();
        bundle.putString("QUERY", query);
        bundle.putInt("LANG", Language_search);
        ((Fragment) fragment).setArguments(bundle);
		transaction.replace(R.id.container, fragment).commit();				
    }


	@Override
	public void onNavigationDrawerItemSelected(int position) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = null;
		
		SharedPreferences preferences = getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
		
		// Options all users see
		switch(position+1){
			case 1:
				fragment = new UpdateFragment();	
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
		}
		
		// Options only linguists see
		if (preferences.getBoolean(Preferences.IS_LINGUIST, false) == true) {
			if (position+1 == 4) {
				Log.i("NAV", "ImageCaptureFragment selected");
				fragment = new ImageCaptureFragment();
				mTitle = getString(R.string.photo_section);
			}
			else if (position+1 == 5) {
				Log.i("NAV", "AudioCaptureFragment selected");
				fragment = new AudioCaptureFragment();
				mTitle = getString(R.string.audio_section);
			}
		}
		
		// Options only non-logged in users see
		else {
			Log.i("NAV", "User is not logged in");
			if (position+1 == 4) {
				Log.i("NAV", "PasswordFragment selected");
				fragment = new PasswordFragment(); // something is wrong with the fragment?
				mTitle = getString(R.string.password_section);
			}
			
		}			
		fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.container, fragment).commit();				
	}
}


