package com.example.valleyzapotectalkingdictionary;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class CustomItemSelectedListener implements OnItemSelectedListener {
		 	
	private int language = 0;
	
	public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
			Toast.makeText(parent.getContext(), 
				"OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
				Toast.LENGTH_SHORT).show();
	        language = pos;
			//pos 0 is zapotec
			//pos 1 is english
			//pos 2 is spanish
			//pos 3 is pronunciation guide
			
		  }
		 
		  @Override
		  public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		  }
		  
		  public int getLanguage(){
			  return language;
		  }
	
}
