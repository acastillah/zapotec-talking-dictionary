package com.example.valleyzapotectalkingdictionary;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class UpdateFragment extends Fragment {

	//public static final String ARG_SECTION_NUMBER = "section_number";
	
//	private CheckBox downloadPicturesCheckbox = null;
//	private CheckBox downloadAudioCheckbox = null;
	private Button updateButton = null;
	private TextView dbspecs = null;
	private TextView lastUpdateView = null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_update, container, false);
        
//        downloadPicturesCheckbox = (CheckBox) view.findViewById(R.id.downloadPicturesCheckbox);
//        downloadAudioCheckbox = (CheckBox) view.findViewById(R.id.downloadAudioCheckbox);
        updateButton = (Button) view.findViewById(R.id.updateButton);
        dbspecs = (TextView) view.findViewById(R.id.db_specs);
        lastUpdateView = (TextView) view.findViewById(R.id.last_updated);
        
        
        
        SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
        Editor editor = preferences.edit(); 
        
        if(!preferences.contains(Preferences.DOWNLOAD_PHOTOS)) {
        	Log.i("UPDATE", "Added photo pref true");
        	editor.putBoolean(Preferences.DOWNLOAD_PHOTOS, true);
        }
        
        if (!preferences.contains(Preferences.DOWNLOAD_AUDIO)) {
        	editor.putBoolean(Preferences.DOWNLOAD_AUDIO, true);
        	Log.i("UPDATE", "Added audio pref true");
        }
        
        if (!preferences.contains(Preferences.LAST_DB_UPDATE)) {
        	Calendar cal = Calendar.getInstance();
        	cal.set(2015, 1, 5, 22, 34); // NOTE: months start at 0
        	SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy kk:mm");
        	String date = sdf.format(cal.getTime());
        	editor.putString(Preferences.LAST_DB_UPDATE, date);
        }
        
        editor.commit();
        
        preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
        
        Log.i("UPDATE", "Photo pref=" + preferences.getBoolean(Preferences.DOWNLOAD_PHOTOS, false));
        Log.i("UPDATE", "Audio pref=" + preferences.getBoolean(Preferences.DOWNLOAD_AUDIO, false));
        
//        downloadPicturesCheckbox.setChecked(preferences.getBoolean(Preferences.DOWNLOAD_PHOTOS, false));
//        downloadAudioCheckbox.setChecked(preferences.getBoolean(Preferences.DOWNLOAD_AUDIO, false));
//        	        
//		downloadPicturesCheckbox.setOnCheckedChangeListener(new CheckboxListener(Preferences.DOWNLOAD_PHOTOS));
//		downloadAudioCheckbox.setOnCheckedChangeListener(new CheckboxListener(Preferences.DOWNLOAD_AUDIO));

		updateButton.setOnClickListener(new UpdateButtonListener());
		
		dbspecs.setText(preferences.getLong(Preferences.DB_SIZE, 0) + " " + dbspecs.getText().toString());
		lastUpdateView.append(" " + preferences.getString(Preferences.LAST_DB_UPDATE, ""));
		
		return view;
    }
	
	public class CheckboxListener implements OnCheckedChangeListener {

		private String pref = null;
		
		public CheckboxListener(String pref) {
			this.pref = pref;
		}
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			
			SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
			Editor editor = preferences.edit();
			
			Log.i("UPDATE", pref + " pref=" + isChecked);
			
			editor.putBoolean(pref, isChecked);
			
			editor.commit();
			
		}
		
	}
	
	public class UpdateButtonListener implements OnClickListener {
		
		@Override
		public void onClick(View arg0) {

			Toast.makeText(getActivity(), R.string.upToDate, Toast.LENGTH_SHORT).show();	
		}
		
	}
	
}
