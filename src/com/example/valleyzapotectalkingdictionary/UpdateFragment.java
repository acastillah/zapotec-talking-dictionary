package com.example.valleyzapotectalkingdictionary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
	
	private CheckBox downloadPicturesCheckbox = null;
	private CheckBox downloadAudioCheckbox = null;
	private Button updateButton = null;
	private TextView dbspecs = null;
	private TextView lastUpdateView = null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_update, container, false);
        
        downloadPicturesCheckbox = (CheckBox) view.findViewById(R.id.downloadPicturesCheckbox);
        downloadAudioCheckbox = (CheckBox) view.findViewById(R.id.downloadAudioCheckbox);
        updateButton = (Button) view.findViewById(R.id.updateButton);
        dbspecs = (TextView) view.findViewById(R.id.db_specs);
        lastUpdateView = (TextView) view.findViewById(R.id.last_updated);
        
        
        
        SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
        Editor editor = preferences.edit(); 
        
        if(!preferences.contains(Preferences.DOWNLOAD_PHOTOS)) {
        	editor.putBoolean(Preferences.DOWNLOAD_PHOTOS, true);
        }
        
        if (!preferences.contains(Preferences.DOWNLOAD_AUDIO)) {
        	editor.putBoolean(Preferences.DOWNLOAD_AUDIO, true);
        }
        
        if (!preferences.contains(Preferences.LAST_DB_UPDATE)) {
        	Calendar cal = Calendar.getInstance();
        	cal.set(DictionaryDatabase.DB_YEAR, 
        			DictionaryDatabase.DB_MONTH, 
        			DictionaryDatabase.DB_DAY, 
        			DictionaryDatabase.DB_HOUR, 
        			DictionaryDatabase.DB_MINUTE);
        	String date = DictionaryDatabase.dateFormat.format(cal.getTime());
        	editor.putString(Preferences.LAST_DB_UPDATE, date);
        }
        
        editor.commit();
        
//        preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
        
        downloadPicturesCheckbox.setChecked(preferences.getBoolean(Preferences.DOWNLOAD_PHOTOS, false));
        downloadAudioCheckbox.setChecked(preferences.getBoolean(Preferences.DOWNLOAD_AUDIO, false));
        	        
		downloadPicturesCheckbox.setOnCheckedChangeListener(new CheckboxListener(Preferences.DOWNLOAD_PHOTOS));
		downloadAudioCheckbox.setOnCheckedChangeListener(new CheckboxListener(Preferences.DOWNLOAD_AUDIO));

		updateButton.setOnClickListener(new UpdateButtonListener());
		
		dbspecs.setText(preferences.getLong(Preferences.DB_SIZE, 0) + " " + dbspecs.getText().toString());
		lastUpdateView.append(" " + preferences.getString(Preferences.LAST_DB_UPDATE, ""));

		
		// users may only update once per day
		if (!preferences.getString(Preferences.LAST_DB_UPDATE, "").equals("")) {
		Calendar lastUpdate = Calendar.getInstance();
			try {
				lastUpdate.setTime(DictionaryDatabase.dateFormat.parse(preferences.getString(Preferences.LAST_DB_UPDATE, "")));
				Calendar today = Calendar.getInstance();
				if (today.get(Calendar.YEAR) > lastUpdate.get(Calendar.YEAR)
						|| today.get(Calendar.MONTH) > lastUpdate.get(Calendar.MONTH)
						|| today.get(Calendar.DATE) > lastUpdate.get(Calendar.DATE)) {
					updateButton.setEnabled(true);
				}
				else {
					updateButton.setEnabled(false);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
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
						
			editor.putBoolean(pref, isChecked);
			
			editor.commit();
			
		}
		
	}
	
	public class UpdateButtonListener implements OnClickListener {
		
		@Override
		public void onClick(View arg0) {			
			//downloading photos only is not an option
			//DictionaryDatabase db = new DictionaryDatabase(getActivity());
			//db.update(old, newv);
		
			new UpdateDialogFragment().show(getFragmentManager(), "Dialog");
		}
		
	}
	
	
	public class UpdateDialogFragment extends DialogFragment {
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {

	    	// you can put whatever you want in the layout, including checkboxes, etc.
	    	View dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_update_dialog, null);
	    	
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle("Title");
	        builder.setView(dialogView);
	        
	        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
							// proceed with updates
	                	   updateButton.setEnabled(false);
	                	   
		                	SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
		           			Editor editor = preferences.edit();
		           			Calendar cal = Calendar.getInstance();
		                   	String date = DictionaryDatabase.dateFormat.format(cal.getTime());
		                   	editor.putString(Preferences.LAST_DB_UPDATE, date);
		                   	editor.commit();
		                   	
		                   	lastUpdateView.setText(R.string.last_updated);
		                   	lastUpdateView.append(" " + preferences.getString(Preferences.LAST_DB_UPDATE, ""));
		                   	
		                   	// UPDATE DB
		           			
		           			Toast.makeText(getActivity(), R.string.upToDate, Toast.LENGTH_SHORT).show();
	                   }
	               });
	        
	        // you can also set the "neutral" button if you want
//	        builder.setNeutralButton("Other option", new DialogInterface.OnClickListener() {
//	                public void onClick(DialogInterface dialog, int id) {
//									// do something
//                }
//            });
	        
	        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   // cancel update
	                   }
	               });
	        

	        return builder.create();
	    }
	}
	
}
