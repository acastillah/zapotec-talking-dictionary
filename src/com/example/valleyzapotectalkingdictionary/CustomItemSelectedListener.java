package com.example.valleyzapotectalkingdictionary;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class CustomItemSelectedListener implements OnItemSelectedListener {
		 	
	private int language = 0;
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
//			Toast.makeText(parent.getContext(), 
//				"OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
//				Toast.LENGTH_SHORT).show();
	        language = pos;
			Log.i("LANG", "Inside Listener: " + Integer.toString(pos));
		  }
		 
		  @Override
		  public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		  }
		  
		  public int getLanguage(){
			Log.i("LANG", "GET LANG: " + Integer.toString(language));
			return language;
		  }
	
}
