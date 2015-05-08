package com.example.valleyzapotectalkingdictionary;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.example.valleyzapotectalkingdictionary.UpdateFragment.UpdateDialogFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class UploadActivity extends FragmentActivity {

	final static public String DROPBOX_APP_KEY = "4kls68jin9bezoo";
	final static public String DROPBOX_APP_SECRET = "m3g7pnv0mjrgw3d";
 
	final static public AccessType ACCESS_TYPE = AccessType.DROPBOX;
	
	final static public String ACCESS_TOKEN = "ACCESS_TOKEN";
	final static public String ACCESS_TOKEN_KEY = "ACCESS_TOKEN_KEY";
	final static public String ACCESS_TOKEN_SECRET = "ACCESS_TOKEN_SECRET";

	private DropboxAPI<AndroidAuthSession> mDBApi;	
	
	private boolean authenticated = false;
	
	UploadHandler uploadHandler = null;
//	DeleteHandler deleteHandler = null;
	
	TextView uploading = null;
	TextView uploading1 = null;
	TextView uploading2 = null;
	TextView uploading3 = null;
	
	public UploadActivity() {}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload);
		
//		if (!authenticated) {
		
		uploading = (TextView) this.findViewById(R.id.uploading);
		uploading1 = (TextView) this.findViewById(R.id.uploading1);
		uploading2 = (TextView) this.findViewById(R.id.uploading2);
		uploading3 = (TextView) this.findViewById(R.id.uploading3);
		
		uploadHandler = new UploadHandler(this);
//		deleteHandler = new DeleteHandler(this);
		
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
		
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.upload, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		// TODO: save authentication
		
	    super.onResume();
	    Log.i("UPLOAD", "UploadActivity resumed");
	    
//	    if (!authenticated) {
	    
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
//				authenticated = true;
				
				new Thread(new UploadRunnable()).start();
				
	        } catch (IllegalStateException e) {
	            Log.i("DbAuthLog", "Error authenticating", e);
	            // do something?
	        }
	    }
//	    }
	    else {
	    	Log.i("UPLOAD", "Authentication unsuccessful");
	    	// DO SOMETHING
	    }
	}
	
	class UploadHandler extends Handler {
		private FragmentActivity activity = null;
		
		public UploadHandler(FragmentActivity activity) {
			this.activity = activity;
		}
		
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			Log.i("HANDLER", "Handler received message");
			
			if (msg.obj.getClass().equals(UploadHandlerMessageObject.class)) {
				Log.i("HANDLER", "Upload message received");
				UploadHandlerMessageObject mObj = (UploadHandlerMessageObject) msg.obj;
				
	
//				if (activity.getClass().equals(UploadActivity.class))
//					((UploadActivity)activity).deleteHandler.doNotDeleteList = mObj.badFileList;
//				
				
				new UploadCompleteDialogFragment(activity, mObj).show(activity.getSupportFragmentManager(), "Dialog");
			}
			else if (msg.obj.getClass().equals(DeleteHandlerMessageObject.class)) {
				Log.i("HANDLER", "Delete message received");
				Toast.makeText(activity, "Files deleted", Toast.LENGTH_SHORT).show();
		     	activity.finish();
			}
			else {
				Log.i("HANDLER", "Do not recognize message type");
			}
		}
	}
	
//	class DeleteHandler extends Handler {
//		private FragmentActivity activity = null;
//		private List<String> doNotDeleteList = null;
//		
//		public DeleteHandler(FragmentActivity activity) {
//			this.activity = activity;
//		}
//		
//		public DeleteHandler(FragmentActivity activity, List<String> doNotDeleteList) {
//			this.activity = activity;
//			this.doNotDeleteList = doNotDeleteList;
//		}
//		
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//
//			Toast.makeText(activity, "Files deleted", Toast.LENGTH_SHORT).show();
//     	   activity.finish();
//		}
//	}
	
	class UploadRunnable implements Runnable {

		@Override
		public void run() {
			Log.i("UPLOAD", "Background thread uploading files...");
			
			UploadHandlerMessageObject mObj = new UploadHandlerMessageObject();
			mObj.uploadSuccessful = true;
			
			String externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			String dictionaryDirectoryName = "Zapotec Talking Dictionary";
			File dictionaryDir = new File(externalStoragePath, dictionaryDirectoryName);
			if (dictionaryDir.exists() && dictionaryDir.isDirectory()) {
				
				/** Upload photos **/
				File photoDir = new File(externalStoragePath+"/"+dictionaryDirectoryName, "photos");
				if (photoDir.exists() && photoDir.isDirectory()) {
					
					File[] files = photoDir.listFiles();
					for (File file : files) {
						String originalFileName = file.getName();
						String uploadedFileName = "/photos/";
						
						Log.i("UPLOAD", "file path="+file.getAbsolutePath());
						try {
							FileInputStream inputStream = new FileInputStream(file);
							
							if (originalFileName.contains("-")) {
								uploadedFileName += originalFileName.substring(0, originalFileName.indexOf('-'));
								uploadedFileName += originalFileName.substring(originalFileName.indexOf('.'));
							}
							else {
								uploadedFileName += originalFileName;
							}
							
							Entry response = mDBApi.putFile(uploadedFileName, inputStream, file.length(), null, null);
							Log.i("DbExampleLog", "Uploaded file \'" + originalFileName + "\'"
									+ "as \'" + uploadedFileName + "\'\n"
									+ "The uploaded file's rev is: " + response.rev);
							mObj.nImageFiles++;
							

						} catch (Exception e) {
							Log.i("DbExampleLog", "Error uploading file \'" + originalFileName + "\'");
							e.printStackTrace();
							mObj.uploadSuccessful = false;
							mObj.badFileList.add(file.getAbsolutePath());
						}
						
					}
				}
				else {
					Log.i("DbExampleLog", "Error: directory \'" + photoDir.getAbsolutePath() + "\' does not exist.");
					mObj.uploadSuccessful = false;
					mObj.badFileList.add(photoDir.getAbsolutePath());
				}
				

				/** Upload audio **/
				File audioDir = new File(externalStoragePath+"/"+dictionaryDirectoryName, "audio");
				if (audioDir.exists() && audioDir.isDirectory()) {
					
					File[] files = audioDir.listFiles();
					for (File file : files) {
						String originalFileName = file.getName();
						String uploadedFileName = "/audio/";
						
						Log.i("UPLOAD", "file path="+file.getAbsolutePath());
						try {
							FileInputStream inputStream = new FileInputStream(file);
							
//							uploadedFileName += originalFileName.substring(0, originalFileName.indexOf('-'));
//							uploadedFileName += originalFileName.substring(originalFileName.indexOf('.'));
							uploadedFileName += originalFileName;
							
							Entry response = mDBApi.putFile(uploadedFileName, inputStream, file.length(), null, null);
							Log.i("DbExampleLog", "Uploaded file \'" + originalFileName + "\'"
									+ "as \'" + uploadedFileName + "\'\n"
									+ "The uploaded file's rev is: " + response.rev);
							mObj.nAudioFiles++;
							

						} catch (Exception e) {
							Log.i("DbExampleLog", "Error uploading file \'" + originalFileName + "\'");
							mObj.uploadSuccessful = false;
							mObj.badFileList.add(file.getAbsolutePath());
						}
						
					}
				}
				else {
					Log.i("DbExampleLog", "Error: directory \'" + audioDir.getAbsolutePath() + "\' does not exist.");
					mObj.uploadSuccessful = false;
					mObj.badFileList.add(audioDir.getAbsolutePath());
				}
					
				
			}
			else {
				Log.i("DbExampleLog", "Error: directory \'" + dictionaryDir.getAbsolutePath() + "\' does not exist.");
				mObj.uploadSuccessful = false;
				mObj.badFileList.add(dictionaryDir.getAbsolutePath());
			}
			
			
			
			

			
			/**** COMMENTED OUT CODE SUCCESSFULLY UPLOADS TEST FILE ****/
//			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "test.txt");
//			Log.i("UPLOAD", "file path="+file.getAbsolutePath());
//			try {
//				file.createNewFile();
//				FileInputStream inputStream = new FileInputStream(file);
//				Entry response = mDBApi.putFile("/testUploaded.txt", inputStream, file.length(), null, null);
//				
//				Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
//				
//			} catch (DropboxUnlinkedException e) {
//			    Log.e("DbExampleLog", "User has unlinked.");
//			    mObj.uploadSuccessful = false;
//			} catch (DropboxException e) {
//			    Log.e("DbExampleLog", "Something went wrong while uploading.");
//			    mObj.uploadSuccessful = false;
//			} catch (IOException e) {
//				Log.e("DbExampleLog", "Error creating file or with input stream.");
//				mObj.uploadSuccessful = false;
//			}
//			finally {
//				Log.i("UPLOAD", "Background thread exiting...");
//			}
			
			
			
			
			
			// tell UI thread that upload is complete
			Message m = new Message();
			m.obj = mObj;
			uploadHandler.sendMessage(m);
		}
		
	}
	
	public class UploadHandlerMessageObject {
		Boolean uploadSuccessful = false;	// was uploading all files successful?
		int nAudioFiles = 0;				// number of audio files uploaded
		int nImageFiles = 0;				// number of image files uploaded
		List<String> badFileList = null;	// list of files unsuccessfully uploaded
		
		public UploadHandlerMessageObject() {
			badFileList = new ArrayList<String>();
		}
	}
	
	public class UploadCompleteDialogFragment extends DialogFragment {
		
		UploadHandlerMessageObject mObj = null;
		FragmentActivity activity = null;
		
		public UploadCompleteDialogFragment(FragmentActivity activity, UploadHandlerMessageObject mObj) {
			this.activity = activity;
			this.mObj = mObj;
		}
		
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {

	    	View dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_upload_success_dialog, null);
	    	
	    	// update the view
	    	TextView imagesUploaded_textView = (TextView) dialogView.findViewById(R.id.imagesUploaded_textView);
	    	TextView audioFilesUploaded_textView = (TextView) dialogView.findViewById(R.id.audioFilesUploaded_textView);
	    	TextView allFilesUploaded_textView = (TextView) dialogView.findViewById(R.id.allFilesUploaded_textView);
	    	TextView unsuccessfulUploads_textView = (TextView) dialogView.findViewById(R.id.unsuccessfulUploads_textView);
	    	TextView deleteLocalFiles_textView = (TextView) dialogView.findViewById(R.id.deleteLocalFiles_textView);
	    	
	    	imagesUploaded_textView.setText(mObj.nImageFiles + " " + imagesUploaded_textView.getText());
	    	audioFilesUploaded_textView.setText(mObj.nAudioFiles + " " + audioFilesUploaded_textView.getText());
	    	
	    	if (mObj.uploadSuccessful) {
	    		unsuccessfulUploads_textView.setVisibility(View.GONE);
	    	}
	    	else {
	    		allFilesUploaded_textView.setVisibility(View.GONE);
	    		for (String fileName : mObj.badFileList)
	    			unsuccessfulUploads_textView.append("\n\t" + fileName);
	    	}
	    	
	    	
	    	
	    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle("Upload complete");
	        builder.setView(dialogView);
	    	
	    	if (mObj.nImageFiles + mObj.nAudioFiles == 0) { // no files were uploaded
	    		deleteLocalFiles_textView.setVisibility(View.GONE);
	    		allFilesUploaded_textView.setVisibility(View.GONE);
	    		
	    		builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   activity.finish();
	                   }
	               });
	    	}
	    	else {
	    		builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   TextView statusView = (TextView) activity.findViewById(R.id.uploading);
	                	   statusView.setText("Deleting");
	                	   
	                	   new Thread(new DeleteRunnable(mObj.badFileList)).start();
//	                	   Toast.makeText(getActivity(), "Files deleted", Toast.LENGTH_SHORT).show();
//	                	   activity.finish();
	                   }
	               });
	        

		        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   activity.finish();
		                   }
		               });
	    	}
	    	
	        return builder.create();
	    }
	}
	
	class DeleteRunnable implements Runnable {

		private List<String> doNotDeleteList = null;
		public DeleteRunnable(List<String> doNotDeleteList) {
			this.doNotDeleteList = doNotDeleteList;
		}
		
		@Override
		public void run() {
			Log.i("DELETE", "Background thread deleting files...");
			Log.i("DELETE", "doNotDeleteList==null is " + (doNotDeleteList == null));
			
			DeleteHandlerMessageObject mObj = new DeleteHandlerMessageObject();
			
			String externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
			String dictionaryDirectoryName = "Zapotec Talking Dictionary";
			File dictionaryDir = new File(externalStoragePath, dictionaryDirectoryName);
			if (dictionaryDir.exists() && dictionaryDir.isDirectory()) {
				
				/** Delete photos **/
				File photoDir = new File(externalStoragePath+"/"+dictionaryDirectoryName, "photos");
				if (photoDir.exists() && photoDir.isDirectory()) {
					
					File[] files = photoDir.listFiles();
					if (doNotDeleteList != null) {
						for (File file : files) {
							if (!doNotDeleteList.contains(file.getAbsolutePath()))
								file.delete();
						}
					}
					else {
						for (File file : files)
							file.delete();
					}

				}
				else {}
				

				/** Delete audio **/
				File audioDir = new File(externalStoragePath+"/"+dictionaryDirectoryName, "audio");
				if (audioDir.exists() && audioDir.isDirectory()) {
					
					File[] files = audioDir.listFiles();
					if (doNotDeleteList != null) {
						for (File file : files) {
							if (!doNotDeleteList.contains(file.getAbsolutePath()))
								file.delete();
						}
					}
					else {
						for (File file : files)
							file.delete();
					}
				}
				else {}
					
				
			}
			else {}
			

			// tell UI thread that delte is complete
			Message m = new Message();
			m.obj = mObj;
			uploadHandler.sendMessage(m);

		}
	}
	
	public class DeleteHandlerMessageObject {
		
		public DeleteHandlerMessageObject() {}
	}
	

}