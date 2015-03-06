package com.example.valleyzapotectalkingdictionary;

import com.example.valleyzapotectalkingdictionary.MainActivity.LanguageInterface;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;


public class SettingsActivity extends ActionBarActivity {

	private static Menu menu = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		Log.i("onCreate","1");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.menu = menu;
		getMenuInflater().inflate(R.menu.main, menu);
		LanguageInterface.setLanguageInterfaceButtons(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
			case R.id.englishInterface:
				LanguageInterface.interfaceLanguage = LanguageInterface.LANGUAGE_ENGLISH;
				LanguageInterface.setLanguageInterfaceButtons(this.menu);
				return true;
			case R.id.spanishInterface:
				LanguageInterface.interfaceLanguage = LanguageInterface.LANGUAGE_SPANISH;
				LanguageInterface.setLanguageInterfaceButtons(this.menu);
				return true;
			case R.id.zapotecInterface:
				LanguageInterface.interfaceLanguage = LanguageInterface.LANGUAGE_ZAPOTEC;
				LanguageInterface.setLanguageInterfaceButtons(this.menu);
				return true;

		}
		
		return super.onOptionsItemSelected(item);
	}


}
