package com.example.valleyzapotectalkingdictionary;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class SettingsFragment extends Fragment {
	
	EditText usernameEditText = null;
	Button saveButton = null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        usernameEditText = (EditText) view.findViewById(R.id.username_editText);
        saveButton = (Button) view.findViewById(R.id.settings_submitButton);
        
        // fill in username if one already exists
        SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
		if (preferences.contains(Preferences.USERNAME))
			usernameEditText.setText( preferences.getString(Preferences.USERNAME, "") );
        
		saveButton.setOnClickListener(new saveButtonOnClickListener());
        
		return view;
    }
	
	public class saveButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// save the new username
			SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
	        Editor editor = preferences.edit();
			editor.putString(Preferences.USERNAME, usernameEditText.getText().toString());
			editor.commit();
		}
		
	}
}
