package com.example.valleyzapotectalkingdictionary;

import com.example.valleyzapotectalkingdictionary.MainActivity.LanguageInterface;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class UpdateActivity extends ActionBarActivity {

	private static Menu menu = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("Update");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.menu = menu;
		getMenuInflater().inflate(R.menu.about, menu);
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
}
