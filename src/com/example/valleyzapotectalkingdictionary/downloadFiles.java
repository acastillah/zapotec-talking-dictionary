package com.example.valleyzapotectalkingdictionary;

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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

class download{
	private Context downloadContext;
    public static String hash[] = {null,null,null};
	 public download(Context context) {
    downloadContext = context;   
	 }
	
/**
 * Background Async Task to download file
 * */
class downloadFiles extends AsyncTask<String, String, String> {
	private ProgressDialog pDialog;
	
	DictionaryDatabase db = new DictionaryDatabase(downloadContext);
    String response;
	/**
     * Before starting background thread
     * Show Progress Bar Dialog
     * */
	@Override
    protected void onPreExecute() {
       super.onPreExecute();
        pDialog = new ProgressDialog(downloadContext);
        pDialog.setMessage("Downloading file...");
        pDialog.setIndeterminate(false);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(true);
        pDialog.show();        	
    }

    /**
     * Downloading file in background thread
     * */
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
				urlParam = "dict=teotitlan&export=true&dl_type=" + Integer.toString(type);
			} 
			else{
				urlParam = "dict=teotitlan&export=true&dl_type=" + Integer.toString(type) + "&hash=" + hash[type];
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
				String path = downloadContext.getFilesDir().getAbsolutePath();
				File file = new File(downloadContext.getFilesDir(), "content.zip");
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
		         while ((ze = zis.getNextEntry()) != null) {
		             filename = ze.getName();
		             if (ze.isDirectory()) {
		                File fmd = new File(path, filename);
		                fmd.mkdirs();
		                continue;
		             }
		             FileOutputStream fout = new FileOutputStream(path + "/" + filename);
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
		         db.update();
			}
        } catch (Exception e) {
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
		Toast.makeText(downloadContext, response, Toast.LENGTH_SHORT).show();

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
    	  Log.i("download", "hash failed");
       }
}

public int getType(){
	SharedPreferences preferences = downloadContext.getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
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

