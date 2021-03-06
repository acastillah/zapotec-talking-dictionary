package edu.haverford.cs.zapotectalkingdictionary;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordFragment extends Fragment {
	
	private static final String PASSWORD = "oaxaca";
	
	private TextView incorrectPasswordView = null;
	private EditText passwordField = null;
	private Button submitButton = null;
	
	private Button logoutButton = null;
	
	 
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		 
//		Log.i("PASSWORD", "PasswordFragment onCreateView");
		View view = inflater.inflate(R.layout.fragment_password, container, false);
		
		SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
		if (preferences.getBoolean(Preferences.IS_LINGUIST, false) == false) {

			incorrectPasswordView = (TextView) view.findViewById(R.id.passwordIncorrect_textView);
			passwordField = (EditText) view.findViewById(R.id.password_editText);
			submitButton = (Button) view.findViewById(R.id.password_submitButton);
			
			incorrectPasswordView.setTextColor(Color.RED);
			incorrectPasswordView.setVisibility(View.INVISIBLE);
			
			submitButton.setEnabled(false);
			submitButton.setOnClickListener(new SubmitButtonOnClickListener());
			
			passwordField.addTextChangedListener(new PasswordEditTextWatcher());
		
		}
		else {
			TextView explanation = (TextView) view.findViewById(R.id.passwordExplanation_textView);
			explanation.setText(R.string.logoutExplanation);
			
			TextView passwordTextView = (TextView) view.findViewById(R.id.password_textView);
			passwordTextView.setVisibility(View.INVISIBLE);
			
			incorrectPasswordView = (TextView) view.findViewById(R.id.passwordIncorrect_textView);
			incorrectPasswordView.setVisibility(View.INVISIBLE);
			
			passwordField = (EditText) view.findViewById(R.id.password_editText);
			passwordField.setVisibility(View.INVISIBLE);
			
			logoutButton = (Button) view.findViewById(R.id.password_submitButton);
			logoutButton.setText(R.string.logout);
			logoutButton.setOnClickListener(new LogoutButtonOnClickListener());
		}
		
		return view; 
	}
	
	public class SubmitButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
//			Log.i("LOGIN", "button clicked");
			
			// password accepted
			if (passwordField.getText().toString().equals(PASSWORD)) {
//				Log.i("LOGIN", "valid password");
				submitButton.setEnabled(false);
				
				SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putBoolean(Preferences.IS_LINGUIST, true);
				editor.putBoolean(Preferences.LOGIN_STATUS_CHANGE, true); 
//				editor.putString(Preferences.USERNAME, username);
				editor.commit();
				
				// Inform user that password was accepted
				Toast.makeText(getActivity(), R.string.passwordAccepted, Toast.LENGTH_SHORT).show();
				
				// Change nav bar options
				FragmentManager fragmentManager = getFragmentManager();
				Fragment fragment = null;
				fragment = new MainPageFragment();	
				fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();	
				getActivity().recreate();
				

				
				// REMOVE OLD ACTIVITY FROM THE STACK?
			}
			
			// password not accepted
			else {
				incorrectPasswordView.setVisibility(View.VISIBLE);
			}
		}
		
	}
	
	public class LogoutButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
//			Log.i("LOGOUT", "button clicked");
			
			SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.putBoolean(Preferences.IS_LINGUIST, false);
			editor.putBoolean(Preferences.LOGIN_STATUS_CHANGE, true);
			editor.commit();
			
			// Inform user that password was accepted
			Toast.makeText(getActivity(), R.string.logoutAccepted, Toast.LENGTH_SHORT).show();
			
			FragmentManager fragmentManager = getFragmentManager();
			Fragment fragment = null;
			fragment = new MainPageFragment();	
			fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();	
			getActivity().recreate();
		}
	}
	
	public class PasswordEditTextWatcher implements TextWatcher {

		@Override
		public void afterTextChanged(Editable arg0) {}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {}

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2,
				int arg3) {

			if (s.length() > 0) {
				incorrectPasswordView.setVisibility(View.INVISIBLE);
				submitButton.setEnabled(true);
			}
			else {
				submitButton.setEnabled(false);
			}
		}
		
	}
}
