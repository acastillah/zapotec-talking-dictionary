package edu.haverford.cs.zapotectalkingdictionary;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
//import android.util.Log;
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
	public String msg = null;
	// Progress Dialog
	public ProgressDialog pDialog;
    public static String hash[] = {null,null,null};
	
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
        	editor.putBoolean(Preferences.DOWNLOAD_PHOTOS, false);
        }
        
        if (!preferences.contains(Preferences.DOWNLOAD_AUDIO)) {
        	editor.putBoolean(Preferences.DOWNLOAD_AUDIO, true);
        }
        
        if (!preferences.contains(Preferences.LAST_DB_UPDATE)) {
//        	Log.i("UPDATE", "no previous update date available");
        	Calendar cal = Calendar.getInstance();
        	String date = DictionaryDatabase.dateFormat_US.format(cal.getTime());
        	editor.putString(Preferences.LAST_DB_UPDATE, date);
        }
        
        editor.commit();
                
        downloadPicturesCheckbox.setChecked(preferences.getBoolean(Preferences.DOWNLOAD_PHOTOS, false));
        downloadAudioCheckbox.setChecked(preferences.getBoolean(Preferences.DOWNLOAD_AUDIO, false));
        	        
		downloadPicturesCheckbox.setOnCheckedChangeListener(new CheckboxListener(Preferences.DOWNLOAD_PHOTOS));
		downloadAudioCheckbox.setOnCheckedChangeListener(new CheckboxListener(Preferences.DOWNLOAD_AUDIO));

		updateButton.setOnClickListener(new UpdateButtonListener());
		
		dbspecs.setText(preferences.getLong(Preferences.DB_SIZE, 0) + " " + dbspecs.getText().toString());
		
		if (preferences.getString(Preferences.LANGUAGE, "").equals(Preferences.ENGLISH)) {
//			Log.i("UPDATE", "English, setting last update date/time");
			lastUpdateView.append(" " + preferences.getString(Preferences.LAST_DB_UPDATE, ""));
       	}
       	else {
//       		Log.i("UPDATE", "English, setting last update date/time");
       		Calendar calMX = Calendar.getInstance();
       		boolean dateParseSuccess = false;
       		try {
				calMX.setTime(DictionaryDatabase.dateFormat_US.parse(preferences.getString(Preferences.LAST_DB_UPDATE, "")));
				dateParseSuccess = true;
			} catch (ParseException e) {
				e.printStackTrace();
			}
       		if (dateParseSuccess) {
       				String dateMX = DictionaryDatabase.dateFormat_MX.format(calMX.getTime());
       				lastUpdateView.append(" " + dateMX);
       		}
       	}
		
		
		
		
//		Calendar cal = Calendar.getInstance();
//       	String date = DictionaryDatabase.dateFormat_US.format(cal.getTime());
//       	editor.putString(Preferences.LAST_DB_UPDATE, date);
//       	editor.commit();
       	
//       	lastUpdateView.setText(R.string.last_updated);
//       	
//       	if (preferences.getString(Preferences.LANGUAGE, "").equals(Preferences.ENGLISH)) {
//       		lastUpdateView.append(" " + date);
//       	}
//       	else {
//       		String dateMX = DictionaryDatabase.dateFormat_MX.format(cal.getTime());
//       		lastUpdateView.append(" " + dateMX);
//       	}
		

		// users may only update once per day 
		if (!preferences.getString(Preferences.LAST_DB_UPDATE, "").equals("")) {
		Calendar lastUpdate = Calendar.getInstance();
			try {
				lastUpdate.setTime(DictionaryDatabase.dateFormat_US.parse(preferences.getString(Preferences.LAST_DB_UPDATE, "")));
				Calendar today = Calendar.getInstance();
				if (!preferences.getBoolean("USER_HAS_UPDATED", false)) {
					updateButton.setEnabled(true);
				}
				else {
					if (today.get(Calendar.YEAR) > lastUpdate.get(Calendar.YEAR)
							|| today.get(Calendar.MONTH) > lastUpdate.get(Calendar.MONTH)
							|| today.get(Calendar.DATE) > lastUpdate.get(Calendar.DATE)) {
						updateButton.setEnabled(true);
					}
					else {
						updateButton.setEnabled(false);
					}
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
		public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
			SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.putBoolean(pref, isChecked);
			editor.commit();
		}
	}
	
	public class UpdateButtonListener implements OnClickListener {
		
		@Override
		public void onClick(View arg0) {	
			SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
			Editor editor = preferences.edit();
			editor.putBoolean("USER_HAS_UPDATED", true);
			editor.commit();
			new Thread(new Runnable() {
                public void run() {
                	String size = getSize();
                	msg = size + "MB";
        			new UpdateDialogFragment().show(getFragmentManager(), "Dialog");
                }
            }).start();
			
		}
	}
	
	public class UpdateDialogFragment extends DialogFragment {
		
		public TextView dialogMSG = null;
	    @SuppressLint("InflateParams")
		@Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {

	    	// you can put whatever you want in the layout, including checkboxes, etc.
	    	View dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_update_dialog, null);
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle(msg);
	        builder.setView(dialogView);
	        
	        dialogMSG = (TextView) dialogView.findViewById(R.id.size_update);
        	dialogMSG.setText(getResources().getString(R.string.downloadFileSizeText));

	        builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
							// proceed with updates
	                	   updateButton.setEnabled(false);
	                	   
		                	SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
		           			Editor editor = preferences.edit();
		           			Calendar cal = Calendar.getInstance();
		                   	String date = DictionaryDatabase.dateFormat_US.format(cal.getTime());
		                   	editor.putString(Preferences.LAST_DB_UPDATE, date);
		                   	editor.commit();
		                   	
		                   	lastUpdateView.setText(R.string.last_updated);
		                   	
		                   	if (preferences.getString(Preferences.LANGUAGE, "").equals(Preferences.ENGLISH)) {
		                   		lastUpdateView.append(" " + date);
		                   	}
		                   	else {
		                   		String dateMX = DictionaryDatabase.dateFormat_MX.format(cal.getTime());
		                   		lastUpdateView.append(" " + dateMX);
		                   	}
		                    
		                   	new downloadFiles().execute();
	                   }
	               });
	        
	        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   // cancel update
	                   }
	               });
	        return builder.create();
	    }
	}
	
	public String getSize(){		
		try{	
			int type = getType();
			URL url = new URL("http://talkingdictionary.swarthmore.edu/dl/retrieve.php");
	        String urlParam = "dict=teotitlan&current=true&size=true&dl_type=" + Integer.toString(type);
	        HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			DataOutputStream hr = new DataOutputStream(con.getOutputStream());
			hr.writeBytes(urlParam);
			hr.flush();
			hr.close();				
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
			float sz = Float.parseFloat(buf.toString());
			sz = sz/(1024*1024);
			
			return Float.toString(sz);
           } catch (IOException e){
        	  
           }
		return null;
		
	}		
	
	/**
	 * Background Async Task to download file
	 * */
	class downloadFiles extends AsyncTask<String, String, String> {
		private ProgressDialog pDialog;
		
		//DictionaryDatabase db = new DictionaryDatabase(getActivity());
	    String response;
		/**
	     * Before starting background thread
	     * Show Progress Bar Dialog
	     * */
		@Override
	    protected void onPreExecute() {
	       super.onPreExecute();
	        pDialog = new ProgressDialog(getActivity());
	        pDialog.setMessage("Downloading file...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCanceledOnTouchOutside(false);
	        pDialog.setMax(100);
	        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	        pDialog.setCancelable(true);
	        pDialog.show();        	
	    }

	    /*
	     * Downloading file in background thread
	     */
	    @Override
	    protected String doInBackground(String... f_url) {
	        try {
	        	HttpURLConnection con;
				int type = getType();
				URL url = new URL("http://talkingdictionary.swarthmore.edu/dl/retrieve.php");
				con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("POST");
				con.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(con.getOutputStream());
				String urlParam;
				if(hash[type] == null){
					urlParam = "dict=teotitlan&export=TRUE&dl_type=" + Integer.toString(type);
				} 
				else{
					urlParam = "dict=teotitlan&export=TRUE&dl_type=" + Integer.toString(type) + "&hash=" + hash[type];
				}
				wr.writeBytes(urlParam);
				wr.flush();
				wr.close();
				if(con.getContentLength()==0){
					if(con.getResponseCode()==403 || con.getResponseCode()==404){
						response = "Error. Try again later.";
					}
					else if(con.getResponseCode()==204){
						response = "No updates available.";
					}
				}
				else{
					String path = getActivity().getFilesDir().getAbsolutePath();
					File file = new File(getActivity().getFilesDir(), "content.zip");
					OutputStream output = new FileOutputStream(file);
					InputStream input = con.getInputStream();
					byte[] buffer = new byte[1024]; 
					int lengthOfFile = con.getContentLength();
					int bytesRead = input.read(buffer);
					long total = 0;
					while (bytesRead >= 0) {
					    total +=bytesRead;
					    output.write(buffer, 0, bytesRead);
					    bytesRead = input.read(buffer);
					    publishProgress(""+(int)((total*100)/lengthOfFile));
					}
				    output.flush();
				    output.close();
				    input.close();
		
				    InputStream is;
				    ZipInputStream zis;
				    String filename;
			        is = new FileInputStream(file);
			        zis = new ZipInputStream(new BufferedInputStream(is));          
			        ZipEntry ze;
			        int count;
			        String root = Environment.getExternalStorageDirectory().toString();
			        Boolean isPic = false;
			         while ((ze = zis.getNextEntry()) != null) {
			             filename = ze.getName();
			             if (ze.isDirectory()) {
				            	if(filename.contains("pix")){
				            		isPic = true;
				            		  File myDir = new File(root,filename);
				            		  myDir.mkdirs();
				            	}
				            	else{
				            		isPic = false;
				            		File fmd = new File(path, filename);
					                fmd.mkdirs();
				            	}
				                continue;
				          }
			             FileOutputStream fout;
			             if (filename.contains(".jpg") || (filename.contains(".JPG"))){//(isPic){
				             fout = new FileOutputStream(root + "/" + filename);
			             }
			             else{
				             fout = new FileOutputStream(path + "/" + filename);
			             }
			             while ((count = zis.read(buffer)) != -1) {
			                 fout.write(buffer, 0, count);             
			             }
			             fout.close();               
			             zis.closeEntry();
			         }
			         is.close();
			         zis.close();
			         getHash();
			         response = "Download finished";
				}
	        } catch (Exception e) {
				response = "Download finished"; //forces all words to install without images
	        }
			return null;
	    }

	    /**
	     * Updating progress bar
	     * */
	    protected void onProgressUpdate(String... progress) {
	        // setting progress percentage
			super.onProgressUpdate(progress);
	        pDialog.setProgress(Integer.parseInt(progress[0]));
	   }

	    /**
	     * After completing background task
	     * Dismiss the progress dialog
	     * **/
		@Override
	    protected void onPostExecute(String file_url) {
	        // dismiss the dialog after the file was downloaded
			pDialog.dismiss();
			if (response == "Download finished"){
				new setupDatabase().execute();
			}
			else{
				Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * Background Async Task to download file
	 * */
	class setupDatabase extends AsyncTask<String, String, String> {
		private ProgressDialog pDialog;
		DictionaryDatabase db = new DictionaryDatabase(getActivity());

		@Override
	    protected void onPreExecute() {
	       super.onPreExecute();
	        pDialog = new ProgressDialog(getActivity());
	        pDialog.setMessage(getResources().getString(R.string.settingUpDatabase));
	        pDialog.setIndeterminate(false);
	        pDialog.setCanceledOnTouchOutside(false);
	        pDialog.setMax(100);
	        pDialog.setCancelable(true);
	        pDialog.show();        	
	    }

	    @Override
	    protected String doInBackground(String... f_url) {
			         db.update();
			return null;
	    }

		@Override
	    protected void onPostExecute(String file_url) {
	        // dismiss the dialog after the file was downloaded
			pDialog.dismiss();
			long db_size = db.getSize();
			SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
			dbspecs.setText(db_size + " " + getString(R.string.db_specs));
		}
	}
	
	
	public void getHash(){
		try{	
			URL url = new URL("http://talkingdictionary.swarthmore.edu/dl/retrieve.php");
			int type = getType();
	        String urlParam = "dict=teotitlan&current=true&current_hash=true&dl_type=" + Integer.toString(type);
	        HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			DataOutputStream hr = new DataOutputStream(con.getOutputStream());
				hr.writeBytes(urlParam);
				hr.flush();
				hr.close();					
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
				hash[type] = buf.toString();
	       } catch (IOException e){
//	    	  Log.i("download", "hash failed");
	       }
	}

	public int getType(){
		SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
	   	Boolean pictures = preferences.getBoolean(Preferences.DOWNLOAD_PHOTOS, false);
	   	Boolean audio = preferences.getBoolean(Preferences.DOWNLOAD_AUDIO, false);
	   	int type;  	
	   	if (pictures && audio){
	   		type = 0;
	   	}
	   	else if(audio){
	   		type = 2;
	   	}
	   	else{
	   		type = 1;
	   	}
	   	return type;
	}
    
}
	
