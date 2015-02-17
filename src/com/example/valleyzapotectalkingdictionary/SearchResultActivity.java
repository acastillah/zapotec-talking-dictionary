package com.example.valleyzapotectalkingdictionary;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class SearchResultActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_result);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		//int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
		switch (item.getItemId()) {
		case R.id.side_menu:
			Intent intent = new Intent(this, MenuActivity.class);
			startActivity(intent);
			return true;
		case R.id.englishInterface:
			// switch to English UI
			return true;
		case R.id.spanishInterface:
			// switch to Spanish UI
			return true;
		case R.id.zapotecInterface:
			// switch to Zapotec UI
			return true;
	}
		
		return super.onOptionsItemSelected(item);
	}
	
	public void displayWord(View view) {
		Intent intent = new Intent(this, WordDefinitionActivity.class);
		startActivity(intent);
	}
	
	public void searchWord(View view) {
		Intent intent = new Intent(this, SearchResultActivity.class);
		startActivity(intent);
	}
}
