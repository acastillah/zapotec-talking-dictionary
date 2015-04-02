package com.example.valleyzapotectalkingdictionary;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

public class LanguageSpinnerItemSelectedListener implements
		OnItemSelectedListener {

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

		String selection = parent.getItemAtPosition(pos).toString();
		Log.i("SPINNER", selection + "selected");
		
		((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
		
		SharedPreferences prefs = parent.getContext().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
		Editor prefEditor = prefs.edit();
		
		if (selection.equals("ESP"))
			prefEditor.putString(Preferences.LANGUAGE, Preferences.SPANISH);
		else if (selection.equals("ZAP"))
			prefEditor.putString(Preferences.LANGUAGE, Preferences.ZAPOTEC);
		else // ENG default
			prefEditor.putString(Preferences.LANGUAGE, Preferences.ENGLISH);
		
		prefEditor.commit();
		
		// need to recreate activity
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}