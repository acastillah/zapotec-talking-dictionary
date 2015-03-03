package com.example.valleyzapotectalkingdictionary;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SearchResultFragment extends Fragment {
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_result, container, false);
        
        
    }
	
//	public void displayWord(View view) {
//		Log.i("SEARCH RESULT FRAGMENT", "Displaying word details...");
//	}
}
