package com.example.valleyzapotectalkingdictionary;

import android.app.Fragment;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class SearchBarFragment extends Fragment {	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_search_bar, container, false);
		View view = inflater.inflate(R.layout.fragment_search_bar, container, false);
        
		EditText searchBar = (EditText) view.findViewById(R.id.searchBox);
		SearchBarWatcher searchBarWatcher = new SearchBarWatcher();
		searchBarWatcher.initialize(view);
		searchBar.addTextChangedListener(searchBarWatcher);
		
		return view;
    }
}
