package com.example.valleyzapotectalkingdictionary;

import com.example.valleyzapotectalkingdictionary.MainActivity.DeleteAllFilesInDirectoryRunnable;
import com.example.valleyzapotectalkingdictionary.UploadActivity.DeleteRunnable;
import com.example.valleyzapotectalkingdictionary.UploadActivity.UploadCompleteDialogFragment;
import com.example.valleyzapotectalkingdictionary.UploadActivity.UploadHandlerMessageObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends Fragment {
	
	EditText usernameEditText = null;
	Button saveButton = null;
	Activity activity = null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        
        activity = getActivity();
        
        ((MainActivity)getActivity()).setActionBarTitle(R.string.settings);

        usernameEditText = (EditText) view.findViewById(R.id.username_editText);
        saveButton = (Button) view.findViewById(R.id.settings_submitButton);
        
        // fill in username if one already exists
        SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
		if (preferences.contains(Preferences.USERNAME))
			usernameEditText.setText( preferences.getString(Preferences.USERNAME, "") );
        
		usernameEditText.setFilters(MainActivity.userNameInputFilters);
		usernameEditText.addTextChangedListener(new usernameEditTextWatcher());
		saveButton.setOnClickListener(new saveButtonOnClickListener());
        
		return view;
    }
	
	public class usernameEditTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1,
				int arg2, int arg3) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			
		}
	}
	
	public class saveButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// save the new username
			SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
	        Editor editor = preferences.edit();
			editor.putString(Preferences.USERNAME, usernameEditText.getText().toString());
			editor.commit();
			
			((MainActivity)activity).hideKeyboard(usernameEditText);
			
			Toast.makeText(getActivity(), R.string.settingsSaved, Toast.LENGTH_SHORT).show();
		}
		
	}
}
