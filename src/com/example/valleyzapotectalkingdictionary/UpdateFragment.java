package com.example.valleyzapotectalkingdictionary;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import android.widget.CompoundButton.OnCheckedChangeListener;

public class UpdateFragment extends Fragment {

	//public static final String ARG_SECTION_NUMBER = "section_number";
	
	private CheckBox downloadPicturesCheckbox = null;
	private CheckBox downloadAudioCheckbox = null;
	private Button updateButton = null;
	private TextView dbspecs = null;
	private TextView lastUpdateView = null;
	public String msg = null;
	
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
//				if (today.get(Calendar.YEAR) > lastUpdate.get(Calendar.YEAR)
//						|| today.get(Calendar.MONTH) > lastUpdate.get(Calendar.MONTH)
//						|| today.get(Calendar.DATE) > lastUpdate.get(Calendar.DATE)) {
					updateButton.setEnabled(true);
//				}
//				else {
//					updateButton.setEnabled(false);
//				}
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
	
			new Thread(new Runnable() {
                public void run() {
                	String size = String.format("%.2f", getSize());
                	msg = "Would you like to download a file of this size?\n" + size + "MB.";
        			new UpdateDialogFragment().show(getFragmentManager(), "Dialog");
                	
                }
                
            }).start();
			
		}
		
	}
	
	
	public class UpdateDialogFragment extends DialogFragment {
		
		public TextView dialogMSG = null;
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {

	    	// you can put whatever you want in the layout, including checkboxes, etc.
	    	View dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_update_dialog, null);

	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle("Title");
	        builder.setView(dialogView);
	        
	        dialogMSG = (TextView) dialogView.findViewById(R.id.size_update);
        	dialogMSG.setText(msg);


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
		                   	//DictionaryDatabase db = new DictionaryDatabase(getActivity());
		        			//db.update();
		                   	new Thread(new Runnable() {
		                        public void run() {
		                        	
		                        }
		                    }).start();
		           			
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
	
	public int getHash(){
		try{	
			URL url = new URL("http://talkingdictionary.swarthmore.edu/dl/retrieve.php");
	        String urlParam = "dict=teotitlan&current=true&current_hash=true&dl_type=" + Integer.toString(0);			         
	        HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			DataOutputStream hr = new DataOutputStream(con.getOutputStream());
				hr.writeBytes(urlParam);
				Log.i("download", "write bytes");
				hr.flush();
				Log.i("download", "flush");
				hr.close();
				Log.i("download", Integer.toString(con.getContentLength()));
				Log.i("download", Integer.toString(con.getResponseCode()));		    								
				int hash = con.hashCode();
				Log.i("download",Integer.toString(hash));
				
				String str="";
				StringBuffer buf = new StringBuffer();
				InputStream is = con.getInputStream();

				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				if (is!=null) {							
					while ((str = reader.readLine()) != null) {	
						buf.append(str + "\n" );
					}				
				}		
				is.close();	
				
				Log.i("download", buf.toString());
				
           } catch (IOException e){
        	  
           }
		return 0;
	}
	
	public String getSize(){
		
		try{	
			URL url = new URL("http://talkingdictionary.swarthmore.edu/dl/retrieve.php");
	        String urlParam = "dict=teotitlan&current=true&size=true&dl_type=" + Integer.toString(0);			         
	        HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			DataOutputStream hr = new DataOutputStream(con.getOutputStream());
			hr.writeBytes(urlParam);
			hr.flush();
			hr.close();
			Log.i("download", Integer.toString(con.getContentLength()));
			Log.i("download", Integer.toString(con.getResponseCode()));		    								
				
			String str="";
			StringBuffer buf = new StringBuffer();
			InputStream is = con.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			if (is!=null) {							
				while ((str = reader.readLine()) != null) {	
					buf.append(str + "\n" );
				}				
			}		
			is.close();	
			
			Log.i("download", buf.toString());
			
			float sz = Float.parseFloat(buf.toString());
			sz = sz/(1024*1024);
			
			return Float.toString(sz);
           } catch (IOException e){
        	  
           }
		return null;
		
		
	}
	
}
