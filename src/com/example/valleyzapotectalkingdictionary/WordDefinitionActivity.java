package com.example.valleyzapotectalkingdictionary;

import com.example.valleyzapotectalkingdictionary.MainActivity.LanguageInterface;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class WordDefinitionActivity extends ActionBarActivity {

	private static Menu menu = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_word_definition);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("bdua");
		actionBar.setDisplayHomeAsUpEnabled(true);
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
			case R.id.settings:
				Toast.makeText(this, "Settings.", Toast.LENGTH_SHORT).show();
				return true;
			case R.id.help:
				Toast.makeText(this, "Help.", Toast.LENGTH_SHORT).show();
				return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
