package com.example.valleyzapotectalkingdictionary;

import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

public class SearchBarWatcher implements TextWatcher {

	@SuppressWarnings("unused")
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
		
		FragmentManager fm = fragment.getChildFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		
		if (count == 0 && fragment.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { // no text in search bar
			Log.i("SEARCH BAR WATCHER", "Search bar contains no text");
			transaction.replace(R.id.child_fragment,new WordOfTheDayFragment());
		}
		
		else{
			transaction.replace(R.id.child_fragment,new SearchResultFragment());
		}

		
		transaction.commit();
	}
}
