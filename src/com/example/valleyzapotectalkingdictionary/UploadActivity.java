package com.example.valleyzapotectalkingdictionary;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class UploadActivity extends Activity {

	final static public String DROPBOX_APP_KEY = "4kls68jin9bezoo";
	final static public String DROPBOX_APP_SECRET = "m3g7pnv0mjrgw3d";
 
	final static public AccessType ACCESS_TYPE = AccessType.DROPBOX;
	
	final static public String ACCESS_TOKEN = "ACCESS_TOKEN";
	final static public String ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY";
	final static public String ACCESS_TOKEN_SECRET = "ACCESS_TOKEN_SECRET";

	private DropboxAPI<AndroidAuthSession> mDBApi;	
	
	public UploadActivity() {

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		
		// create a new authentication session
		AppKeyPair appKeys = new AppKeyPair(DROPBOX_APP_KEY, DROPBOX_APP_SECRET);
		
		// auto-authentication not working
		SharedPreferences preferences = getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
		String accessToken = preferences.getString(ACCESS_TOKEN, "");
		String accessToken_key = preferences.getString(ACCESS_TOKEN_KEY, "");
		String accessToken_secret = preferences.getString(ACCESS_TOKEN_SECRET, "");
		
		AndroidAuthSession session;
		if (accessToken.equals("") || accessToken_key.equals("") || accessToken_secret.equals("")) {
			Log.i("UPLOAD", "Fresh auth session");
			session = new AndroidAuthSession(appKeys);
		}
		else {
			Log.i("UPLOAD", "Auth session using access token");
			session = new AndroidAuthSession(appKeys, new AccessTokenPair(accessToken_key, accessToken_secret));
		}

		mDBApi = new DropboxAPI<AndroidAuthSession>(session);
		
		// start authentication session
		mDBApi.getSession().startOAuth2Authentication(this);
		Log.i("UPLOAD", "authentication session started");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.upload, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    Log.i("UPLOAD", "UploadActivity resumed");
	    
	    if (mDBApi.getSession().authenticationSuccessful()) {
	        try {
	            // finish the authentication session
	            mDBApi.getSession().finishAuthentication();
	            Toast.makeText(this, "Session authenticated", Toast.LENGTH_SHORT).show();
	            Log.i("UPLOAD", "Authentication session finished");
	            
	            // save the access token for the session
	            String accessToken = mDBApi.getSession().getOAuth2AccessToken();
//	            String accessToken_key = mDBApi.getSession().getAccessTokenPair().key;
//	            String accessToken_secret = mDBApi.getSession().getAccessTokenPair().secret;
	            
	            SharedPreferences preferences = getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
	            Editor editor = preferences.edit();
				editor.putString(ACCESS_TOKEN, accessToken);
//				editor.putString(ACCESS_TOKEN_KEY, accessToken_key);
//				editor.putString(ACCESS_TOKEN_SECRET, accessToken_secret);
				editor.commit();
				
				Log.i("UPDATE", "accessToken=" + accessToken);
				
				new Thread(new FileUploadRunnable()).start();
				
	        } catch (IllegalStateException e) {
	            Log.i("DbAuthLog", "Error authenticating", e);
	            // do something?
	        }
	    }
	    else {
	    	Log.i("UPLOAD", "Authentication unsuccessful");
	    	// DO SOMETHING
	    }
	}
	
	class FileUploadRunnable implements Runnable {

		@Override
		public void run() {
			Log.i("UPLOAD", "Background thread uploading files...");
			
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "test.txt");
			Log.i("UPLOAD", "file path="+file.getAbsolutePath());
			try {
				file.createNewFile();
				Log.i("UPLOAD", "file created");
				FileInputStream inputStream = new FileInputStream(file);
				Log.i("UPLOAD", "Input stream opened");
				Entry response = mDBApi.putFile("/testUploaded.txt", inputStream, file.length(), null, null);
				Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
				
			} catch (DropboxUnlinkedException e) {
			    Log.e("DbExampleLog", "User has unlinked.");
			} catch (DropboxException e) {
			    Log.e("DbExampleLog", "Something went wrong while uploading.");
			} catch (IOException e) {
				Log.e("DbExampleLog", "Error creating file or with input stream.");
			}
			finally {
				Log.i("UPLOAD", "Background thread exiting...");
			}
			
		}
		
	}
}
