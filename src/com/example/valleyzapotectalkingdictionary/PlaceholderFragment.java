package com.example.valleyzapotectalkingdictionary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
	
	/**
	 * Returns a new instance of this fragment for the given section number.
	 */
	public static Fragment newInstance(int sectionNumber) {
		Fragment fragment;
		
		switch(sectionNumber){
			case 1:
				fragment = new SearchBarFragment();	
				break;
			case 2:
				fragment = new UpdateFragment();
				break;
			case 3:
				fragment = new AboutFragment();
				break;
			default:
				fragment = new PlaceholderFragment();
		}

		return fragment;
		
	}

	public PlaceholderFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		return rootView;
	}

}

