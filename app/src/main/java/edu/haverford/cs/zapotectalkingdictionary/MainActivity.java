package edu.haverford.cs.zapotectalkingdictionary;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Locale;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.InputFilter;
import android.text.Spanned;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
//import android.util.Log;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks  {

	public boolean recreate = false;
	public boolean clearStack = false;
	
	private NavigationDrawerFragment mNavigationDrawerFragment;

	static public Spinner /*searchSpinner,*/ domainSpinner;
	private static int Language_search = 0;
	private static String domain_search = null;
	@SuppressWarnings("unused")
	private static Menu menu = null;
	private CharSequence mTitle;
	
	public SearchView searchView = null;
	
	private boolean configChange = false;
	
	Bundle bundle = null;
	
	public static String SEARCH_RESULTS_FRAGMENT = "SEARCH_RESULTS_FRAGMENT";
	public static String WORD_DEFINITION_FRAGMENT = "WORD_DEFINITION_FRAGMENT";
	
	public MainActivity() {
		bundle = new Bundle();
	}
		
	public static final InputFilter[] fileNameInputFilters = new InputFilter[] {
            new InputFilter() {
            	@Override
                public CharSequence filter(CharSequence src, int start,
                        int end, Spanned dst, int dstart, int dend) {

                    if(src.equals("")){ // for backspace
                        return src;
                    }
                    if(src.toString().matches("[a-zA-Z0-9 _ñáãàéẽèíìóòúùï]*"))
                    {
                    	return src;
                    }
                    return "";
                }
            }
        };



	public static final InputFilter[] userNameInputFilters = new InputFilter[] {
        new InputFilter() {
        	@Override
            public CharSequence filter(CharSequence src, int start,
                    int end, Spanned dst, int dstart, int dend) {

                if(src.equals("")){ // for backspace
                    return src;
                }
                if(src.toString().matches("[a-zA-Z0-9 _ñáãàéẽèíìóòúùï]*"))
                {
                    return src;
                }
                return "";
            }
        }
    };
	
	private int setLanguage(Spinner spinner) {
		SharedPreferences prefs = this.getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
		Editor prefEditor = prefs.edit();
		
		if (!prefs.contains(Preferences.LANGUAGE))
			prefEditor.putString(Preferences.LANGUAGE, Preferences.SPANISH);
		
		prefEditor.commit();
			
		prefs = this.getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
		String lang = prefs.getString(Preferences.LANGUAGE, Preferences.SPANISH);
		Locale locale = Preferences.SPANISH_LOC;
		int selection = 0;
		
		if (lang.equals(Preferences.SPANISH)) {
			locale = Preferences.SPANISH_LOC;
			selection = 0;
			Language_search = LanguageInterface.LANGUAGE_SPANISH;
		}
		else if (lang.equals(Preferences.ZAPOTEC)) {
			locale = Preferences.ZAPOTEC_LOC;
			selection = 1;
			Language_search = LanguageInterface.LANGUAGE_ZAPOTEC;
		}
		else {
			locale = Preferences.ENGLISH_LOC;
			selection = 2;
			Language_search = LanguageInterface.LANGUAGE_ENGLISH;
		}
		
		Locale.setDefault(locale);
		
//		if (spinner != null)
//			spinner.setSelection(selection);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
		return selection;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	 
		mTitle = getResources().getString(R.string.app_name);
		restoreActionBar();
		/** Set the language **/
		setLanguage(null);
		/*********************/
		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
	    searchView = (SearchView) findViewById(R.id.searchBAR);
	    searchView.setIconifiedByDefault(false);
	    searchView.setInputType(0);
//	    searchView.clearFocus();
	    searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
	                hideKeyboard(v);
	            }
			}
		});
	    
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);		
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));	
		searchView.setBackgroundColor(getResources().getColor(R.color.searchbar_background));
		mTitle = getString(R.string.app_name);
		domainSpinner = (Spinner) findViewById(R.id.domain_spinner);
		addListenerOnSpinnerItemSelection(); 
        handleIntent(getIntent());
        
        /***** UNCOMMENT AND EDIT USERAGREEMENTDIALOGFRAGMENT TO ADD A USER AGREEMENT *****/
//        SharedPreferences preferences = getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
//		if (preferences.getBoolean(Preferences.TERMS_ACCEPTED, false) == false) {
//			UserAgreementDialogFragment dialog = new UserAgreementDialogFragment();
//			dialog.show(getSupportFragmentManager(), "Dialog");
//		}
		
		if (this.getCurrentFocus() != null)
			this.getCurrentFocus().clearFocus();
		
		searchView.setFocusable(false);
//		hideKeyboard(getWindow().getDecorView().getRootView());
//		Log.i("ROOT", "null?=" + Boolean.toString(getWindow().getDecorView().getRootView() == null));
		
		
		if (savedInstanceState != null && savedInstanceState.containsKey("QUERY")) {
//			Log.i("SEARCH", "QUERY_SAVED");
//			showResults(savedInstanceState.getString("QUERY", ""));
		}
				
//		getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
		
	}
	
//	@Override
//	protected void onStart() {
//		super.onStart();
//		Log.i("ONSTART", "START");
//		SharedPreferences preferences = getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
//		if (preferences.getBoolean(Preferences.DB_LOADED, false) == false) {
//			Editor editor = preferences.edit();
//			editor.putBoolean(Preferences.DB_LOADED, true);
//			editor.commit();
//			this.recreate();
//		}
//	}

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
//				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				adapter.setDropDownViewResource(R.layout.actionbar_spinner_dropdown);
				// Apply the adapter to the spinner
				spinner.setAdapter(adapter);
				
				OnItemSelectedListener listener = new LanguageSpinnerItemSelectedListener();
				spinner.setOnItemSelectedListener(listener);
				
				recreate = false;
				int selection = setLanguage(spinner);
				spinner.setSelection(selection);
			}
			else {
			}
			
			return true;

		}
		return super.onCreateOptionsMenu(menu);
	}
	
	public void onSaveInstanceState(Bundle outState) {
		outState = this.bundle;
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);	
		
		actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionbar_background)));
		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
//			actionBar.setTitle(Html.fromHtml("<small>" + mTitle + "</small>"));
			actionBar.setTitle(mTitle);
		else
			actionBar.setTitle(mTitle);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {}
		
		return super.onOptionsItemSelected(item);
	}
	
	 public void addListenerOnSpinnerItemSelection() {
			
		   domainSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        	((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.domain_searchbar_text));

					domain_search = getResources().getStringArray(R.array.domain_options_search)[domainSpinner.getSelectedItemPosition()];
					if (pos==1){
						showAll();
					}

					else if (pos!=0){
		            	showDomain();
		            }

		        }
		        public void onNothingSelected(AdapterView<?> arg0) {
		        }
		    });    
	  }
	 
/*	public void displayWord(View view) {
		if (searchView != null) {
//			searchView.setQuery("", false);
			searchView.clearFocus();
		}
		startActivity(new Intent(this, WordDefinitionFragment.class));
	}
*/
	public void displayWordOfTheDay(View v) {
		startActivity(new Intent(this, WordDefinitionFragment.class));		
	}	 
	
	static class LanguageInterface {
		public static final int LANGUAGE_ENGLISH = 0;
		public static final int LANGUAGE_SPANISH = 1;
		public static final int LANGUAGE_ZAPOTEC = 2;
		
		public static int interfaceLanguage = 0;
	}
	
	@Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
    	if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
    		// handles a search query
            String q = intent.getStringExtra(SearchManager.QUERY);
            String query = q.trim();
            this.bundle.putString("QUERY", query);
            showResults(query);
			MainActivity.domainSpinner.setSelection(0);
        }
    }
    
    private void showDomain(){
    	FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		Fragment fragment = new SearchResultsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("QUERY", "");
		bundle.putInt("LANG", Language_search);
        bundle.putString("DOM", domain_search);
        ((Fragment) fragment).setArguments(bundle);
		transaction.addToBackStack(null).replace(R.id.container, fragment, SEARCH_RESULTS_FRAGMENT).commit();	
		
        if (searchView != null) {
//			searchView.setQuery("", false);
			searchView.clearFocus();
		}
    }

	private void showAll(){
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		Fragment fragment = new SearchResultsFragment();
		Bundle bundle = new Bundle();
		bundle.putString("QUERY", "");
		bundle.putInt("LANG", Language_search);
		bundle.putString("DOM", "");
		((Fragment) fragment).setArguments(bundle);
		transaction.addToBackStack(null).replace(R.id.container, fragment, SEARCH_RESULTS_FRAGMENT).commit();

		if (searchView != null) {
//			searchView.setQuery("", false);
			searchView.clearFocus();
		}
	}
    
    private void showResults(String query) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		Fragment fragment = new SearchResultsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("QUERY", query);
        bundle.putInt("LANG", Language_search);
        bundle.putString("DOM", "");

        
        ((Fragment) fragment).setArguments(bundle);
		transaction.addToBackStack(null).replace(R.id.container, fragment, SEARCH_RESULTS_FRAGMENT).commit();	
		
        if (searchView != null) {
//			searchView.setQuery("", false);
			searchView.clearFocus();
		}
    }

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment = null;
		SharedPreferences preferences = getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);

		if (preferences.getBoolean(Preferences.LOGIN_STATUS_CHANGE, false)) {
			Editor editor = preferences.edit();
			editor.putBoolean(Preferences.LOGIN_STATUS_CHANGE, false);
			editor.commit();
			
			// logged in, go to settings
			if (preferences.getBoolean(Preferences.IS_LINGUIST, false)) {
				fragment = new SettingsFragment();	
				mTitle = getString(R.string.title_settings);
			}
			// logged out, go to main page
			else {
				fragment = new MainPageFragment();	
				mTitle = getString(R.string.title_main_section);
			}
			
		}
		else {
			// Options all users see
			switch(position+1){
				case 1:
					fragment = new MainPageFragment();	
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
				if (position+1 == 4) { //  JUST ADD SOMETHING HERE WITH SHARED PREFS TO PICK CORRECT THING AFTER LOGGING IN
					fragment = new AudioCaptureFragment();
					mTitle = getString(R.string.audio_section);
				}
				else if (position+1 == 5) {
					Bundle b = new Bundle();
					if (configChange == false)
						b.putBoolean(ImageCaptureFragment.LAUNCH_CAMERA, true);
					fragment = ImageCaptureFragment.newInstance(b);
//					fragment = new ImageCaptureFragment();
					mTitle = getString(R.string.photo_section);
				}
				else if (position+1 == 6) {
					fragment = new SettingsFragment();	
					mTitle = getString(R.string.title_settings);
				}
				else if (position+1 == 7) {
					fragment = new ContentManagementFragment();	
					mTitle = getString(R.string.contentManagement);
				}
				else if (position+1 == 8) {
					fragment = new PasswordFragment();
					mTitle = getString(R.string.password_section);
				}
				else if (position+1 == 9) {
					fragment = new ReportFragment();
					mTitle = getString(R.string.report_a_problem);
				}
			}
			
			// Options only non-logged in users see
			else {
				if (position+1 == 4) {
					fragment = new PasswordFragment();
					mTitle = getString(R.string.password_section);
				}
				else if (position+1 == 5) {
					fragment = new ReportFragment();
					mTitle = getString(R.string.report_a_problem);
				}
			}	
		}
		
		if (searchView != null) {
			searchView.setQuery("", false);
			searchView.clearFocus();
		}
		
		if (preferences.getBoolean(Preferences.LANGUAGE_CHANGE, false)) {
//			fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
			Editor editor = preferences.edit();
			editor.putBoolean(Preferences.LANGUAGE_CHANGE, false);
			editor.commit();
		}
		
		configChange = false;
		fragmentManager.beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();		
	}
		
	public void hideKeyboard(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
//	    Log.i("CONFIG CHANGE", "orientation changed");
	}
	
	public void setActionBarTitle(String title) {
		this.mTitle = title;
		this.restoreActionBar();
	}
	
	public void setActionBarTitle(int resId) {
		this.mTitle = this.getResources().getString(resId);
		this.restoreActionBar();
	}

	
	@Override
	public void onBackPressed(){
		
		Fragment searchResult = (Fragment)getSupportFragmentManager().findFragmentByTag(SEARCH_RESULTS_FRAGMENT);
		Fragment wordDef = (Fragment)getSupportFragmentManager().findFragmentByTag(WORD_DEFINITION_FRAGMENT);
		if ( (searchResult != null && searchResult.isVisible())
				|| (wordDef != null && wordDef.isAdded()) ) {
		   super.onBackPressed();
		}
		else {
			this.finish();
		}
	}
}


