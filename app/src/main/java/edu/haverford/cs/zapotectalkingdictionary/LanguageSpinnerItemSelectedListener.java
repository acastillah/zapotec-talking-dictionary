package edu.haverford.cs.zapotectalkingdictionary;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

public class LanguageSpinnerItemSelectedListener implements
		OnItemSelectedListener {

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

		((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
		
		SharedPreferences prefs = parent.getContext().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
		Editor prefEditor = prefs.edit();
		
		Configuration config = new Configuration();
		
		if (prefs.contains(Preferences.LANGUAGE)) {
			prefEditor.remove(Preferences.LANGUAGE);
		}
		
		switch (pos) {
			case 0:
				prefEditor.putString(Preferences.LANGUAGE, Preferences.SPANISH);
				config.locale = Preferences.SPANISH_LOC;
				break;
			case 1:
				prefEditor.putString(Preferences.LANGUAGE, Preferences.ZAPOTEC);
				config.locale = Preferences.ZAPOTEC_LOC;
				break;
			case 2:
				prefEditor.putString(Preferences.LANGUAGE, Preferences.ENGLISH);
				config.locale = Preferences.ENGLISH_LOC;
				break;
			default:
				prefEditor.putString(Preferences.LANGUAGE, Preferences.SPANISH);
				config.locale = Preferences.SPANISH_LOC;
		}
		
		prefEditor.commit();		
		
		parent.getResources().updateConfiguration(config, null);
		
		// need to recreate activity if this is not the first time the spinner is set
		// (first time spinner is set is when the activity is initially created,
		// but the language is already set correctly at that point)
		Activity activity = (Activity) view.getContext();
		if (((MainActivity)activity).recreate) {
			prefEditor.putBoolean(Preferences.LANGUAGE_CHANGE, true);
			prefEditor.commit();
			activity.recreate();
		}
		else {
			((MainActivity)activity).recreate = true;
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

}