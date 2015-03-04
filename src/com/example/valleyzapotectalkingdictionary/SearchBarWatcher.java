package com.example.valleyzapotectalkingdictionary;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class SearchBarWatcher implements TextWatcher {

	private View view;
	private Fragment fragment;
	
	public void initialize(View view, Fragment fragment) {
		this.view = view;
		this.fragment = fragment;
	}
	
	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// if search bar is empty, display word of the day, hide search results
		// else, search bar contains text, hide word of the day, show search results
		
		if (count == 0) { // no text in search bar
			Log.i("SEARCH BAR WATCHER", "Search bar contains no text");
//			Log.i("VIEW", view.toString());
//			Log.i("VIEW", view.getParent().toString());
			
//			FragmentTransaction transaction = FragmentManager.beginTransaction();
			
			/* 1. Show word of the day, if vertical
			 * 2. Hide search results
			 */
			
		}
		
		
		FragmentManager fm = fragment.getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		
		Fragment wordOfTheDay = fm.findFragmentByTag("wordOfTheDay");
		Fragment searchResult = fm.findFragmentByTag("searchResult");
		
		//String searchText = searchBar.getText().toString();
		
		if (count == 0) { // nothing to search for
			
			/* 1. Display word of the day fragment if vertical
			 * 2. Hide search result fragment
			 */
			
			if (wordOfTheDay != null && fragment.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				Log.i("SEARCH BAR WATCHER", "Showing Word Of The Day fragment");
				transaction.show(wordOfTheDay);
			}
			
			if (searchResult != null) {
				Log.i("SEARCH BAR WATCHER", "Hiding search result fragment");
				transaction.hide(searchResult);
			}
		}
		else { // search for results
			
			/* 1. Hide word of the day fragment
			 * 2. Display search result fragment
			 * 3. Run search algorithm & update search result fragment
			 */
			
			
			if (wordOfTheDay != null) {
				Log.i("SEARCH BAR WATCHER", "Hiding Word Of The Day fragment");
				transaction.hide(wordOfTheDay);
			}
			
			if (searchResult != null) {
				Log.i("SEARCH BAR WATCHER", "Showing search result fragment");
				transaction.show(searchResult);
				// run search results
			}
		}
		
		transaction.commit();
	}
}
