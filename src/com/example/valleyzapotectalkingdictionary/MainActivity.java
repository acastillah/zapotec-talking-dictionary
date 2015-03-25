package com.example.valleyzapotectalkingdictionary;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks  {

	private NavigationDrawerFragment mNavigationDrawerFragment;

	@SuppressWarnings("unused")
	private static Menu menu = null;
	private CharSequence mTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
		//mTitle = getTitle();
		JSONReadFromFile();
		mTitle = "Search";
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		
		JSONReadFromFile();
		
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Inflate the menu; this adds items to the action bar if it is present.
			MainActivity.menu = menu;
			getMenuInflater().inflate(R.menu.main, menu);
			//LanguageInterface.setLanguageInterfaceButtons(menu);
			restoreActionBar();
			
		    MenuItem searchItem = menu.findItem(R.id.action_search);
		    SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		    OnClickListener searchClickListener = new SearchClickListener();
			searchView.setOnSearchClickListener(searchClickListener );
			//searchView.setIconified(false);
			
			
			MenuItem spinnerItem = menu.findItem(R.id.spinner);
			Spinner spinner = (Spinner) MenuItemCompat.getActionView(spinnerItem);
			
			if (spinner != null) {
				Log.i("SPINNER", "Spinner is not null");
				// Create an ArrayAdapter using the string array and a default spinner layout
				ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
				        R.array.languages_array, android.R.layout.simple_spinner_item);
				// Specify the layout to use when the list of choices appears
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				// Apply the adapter to the spinner
				spinner.setAdapter(adapter);
				
				OnItemSelectedListener listener = new LanguageSpinnerItemSelectedListener();
				spinner.setOnItemSelectedListener(listener);
			}
			else {
				Log.i("SPINNER", "Spinner is null");
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
		
		switch (item.getItemId()) {
//			case R.id.englishInterface:
//				LanguageInterface.interfaceLanguage = LanguageInterface.LANGUAGE_ENGLISH;
//				//LanguageInterface.setLanguageInterfaceButtons(MainActivity.menu);
//				return true;
//			case R.id.spanishInterface:
//				LanguageInterface.interfaceLanguage = LanguageInterface.LANGUAGE_SPANISH;
//				//LanguageInterface.setLanguageInterfaceButtons(MainActivity.menu);
//				return true;
//			case R.id.zapotecInterface:
//				LanguageInterface.interfaceLanguage = LanguageInterface.LANGUAGE_ZAPOTEC;
//				//LanguageInterface.setLanguageInterfaceButtons(MainActivity.menu);
//				return true;
			case R.id.settings:
				Toast.makeText(this, "Settings.", Toast.LENGTH_SHORT).show();
				return true;
			case R.id.help:
				Toast.makeText(this, "Help.", Toast.LENGTH_SHORT).show();
				return true;
		}
		
		return super.onOptionsItemSelected(item);
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
	public void onNavigationDrawerItemSelected(int position) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = null;
		
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
			case 4:
				Log.i("NAV", "ImageCaptureFragment selected");
				fragment = new ImageCaptureFragment();
				mTitle = getString(R.string.photo_section);
				break;
			case 5:
				Log.i("NAV", "AudioCaptureFragment selected");
				fragment = new AudioCaptureFragment();
				mTitle = getString(R.string.audio_section);
				break;
		}
		fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();				
	}
	
	@SuppressWarnings("unused")
	public void JSONReadFromFile() {

		try {
			String str="";
			StringBuffer buf = new StringBuffer();			
			InputStream is = this.getResources().openRawResource(R.raw.teotitlan_export);
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			if (is!=null) {							
				while ((str = reader.readLine()) != null) {	
					buf.append(str + "\n" );
				}				
			}		
			is.close();	
			
			JSONParser parser=new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(buf.toString());
 	        	
			Iterator<?> i = jsonArray.iterator();
        			 
        	while (i.hasNext()) {
        		JSONObject innerObj = (JSONObject) i.next();		                
		            String id = (String) innerObj.get("oid");
		            String word = (String) innerObj.get("lang");
		            String pronunciation = (String) innerObj.get("ipa");
        	}        

		} catch (Exception e) {
			Log.i("Parsing failed", "FAIL");
		}

}

}
