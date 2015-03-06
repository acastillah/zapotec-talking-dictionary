package com.example.valleyzapotectalkingdictionary;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class SearchBarFragment extends Fragment implements OnClickListener {
	EditText searchBar = null;
	ImageButton searchButton = null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_search_bar, container, false);
        
		searchBar = (EditText) view.findViewById(R.id.searchBox);
		SearchBarWatcher searchBarWatcher = new SearchBarWatcher();
		searchBarWatcher.initialize(view, this);
		searchBar.addTextChangedListener(searchBarWatcher);
		
		searchButton = (ImageButton) view.findViewById(R.id.searchButton);

		if (searchButton != null) {
			searchButton.setOnClickListener(this);
			Log.i("SEARCH BAR FRAGMENT", "searchButton listener was set");
		}
		
		Fragment WordFragment = new WordOfTheDayFragment();
		Fragment ResultFragment = new SearchResultFragment();
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			transaction.replace(R.id.child_fragment,WordFragment).commit();
		}
		else { // Configuration.ORIENTATION_LANDSCAPE, display word of the day
			transaction.hide(WordFragment).commit();
		}
		
		if (!searchBarIsEmpty()) {
			Log.i("SEarch","not empty");
			transaction.replace(R.id.child_fragment,ResultFragment).commit();
		}
		else {
		}
		
		return view;
    }

	@Override
	public void onClick(View v) {
		Log.i("SEARCH BAR FRAGMENT", "searchButton listener was called");
		
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		
		Fragment wordOfTheDay = fm.findFragmentByTag("wordOfTheDay");
		Fragment searchResult = fm.findFragmentByTag("searchResult");
		
		String searchText = searchBar.getText().toString();
		
		if (searchText.equals("")) { // nothing to search for
			
			/* 1. Display word of the day fragment if vertical
			 * 2. Hide search result fragment
			 */
			
			if (wordOfTheDay != null && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				Log.i("SEARCH BAR FRAGMENT", "Showing Word Of The Day fragment");
				transaction.show(wordOfTheDay);
			}
			
			if (searchResult != null) {
				Log.i("SEARCH BAR FRAGMENT", "Hiding search result fragment");
				transaction.hide(searchResult);
			}
		}
		else { // search for results
			
			/* 1. Hide word of the day fragment
			 * 2. Display search result fragment
			 * 3. Run search algorithm & update search result fragment
			 */
			
			
			if (wordOfTheDay != null) {
				Log.i("SEARCH BAR FRAGMENT", "Hiding Word Of The Day fragment");
				transaction.hide(wordOfTheDay);
			}
			
			if (searchResult != null) {
				Log.i("SEARCH BAR FRAGMENT", "Showing search result fragment");
				transaction.show(searchResult);
				// run search results
			}
		}
		
		transaction.commit();
	}
	
	public boolean searchBarIsEmpty() {
		if (searchBar == null)
			return true;
		
		String searchText = searchBar.getText().toString();
		return (searchText.equals(""));
	}
}
