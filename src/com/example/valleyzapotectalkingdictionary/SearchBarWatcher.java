package com.example.valleyzapotectalkingdictionary;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class SearchBarWatcher implements TextWatcher {

	private View view;
	
	public void initialize(View view) {
		this.view = view;
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
		}
		else { // there is text is search bar
			Log.i("SEARCH BAR WATCHER", "Search bar contains text");
		}
	}

}
