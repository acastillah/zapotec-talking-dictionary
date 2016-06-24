package edu.haverford.cs.zapotectalkingdictionary;

import java.io.File;

import edu.haverford.cs.zapotectalkingdictionary.UploadActivity.DeleteRunnable;
import edu.haverford.cs.zapotectalkingdictionary.UploadActivity.UploadCompleteDialogFragment;
import edu.haverford.cs.zapotectalkingdictionary.UploadActivity.UploadHandlerMessageObject;
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

public class ContentManagementFragment extends Fragment {
	
	Button uploadButton = null;
	Button deleteButton = null;
	Activity activity = null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_content_management, container, false);
        
        activity = getActivity();
        
        ((MainActivity)getActivity()).setActionBarTitle(R.string.settings);

        uploadButton = (Button) view.findViewById(R.id.upload_button);
        deleteButton = (Button) view.findViewById(R.id.delete_button);
        
		uploadButton.setOnClickListener(new uploadButtonOnClickListener());
		deleteButton.setOnClickListener(new deleteButtonOnClickListener(getActivity()));
        
		return view;
    }
		
	public class uploadButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(getActivity(), UploadActivity.class);
		    startActivity(intent);
		}
		
	}
	
	public class deleteButtonOnClickListener implements OnClickListener {

		FragmentActivity activity = null;
		
		public deleteButtonOnClickListener(FragmentActivity activity) {
			this.activity = activity;
		}
		
		@Override
		public void onClick(View arg0) {
			new deleteFilesDialogFragment().show(activity.getSupportFragmentManager(), "Dialog");
		}
		
	}
	
	public static class deleteFilesDialogFragment extends DialogFragment {
				
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {

	    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle("Are you sure you want to delete all photos and audio files?");

	    	
	    		builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {

	                	   new Thread(new DeleteAllFilesInDirectoryRunnable(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Zapotec Talking Dictionary/photos")).start();
	                	   new Thread(new DeleteAllFilesInDirectoryRunnable(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Zapotec Talking Dictionary/audio")).start();
	                	   new Thread(new DeleteAllFilesInDirectoryRunnable(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Zapotec Talking Dictionary/temp")).start();
	                	   Toast.makeText(getActivity(), "Files deleted", Toast.LENGTH_SHORT).show();
	                   }
	               });
	        

		        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
		                   public void onClick(DialogInterface dialog, int id) {
		                	   
		                   }
		               });
	    	
	        return builder.create();
	    }
	}
	
	public static class DeleteAllFilesInDirectoryRunnable implements Runnable {

		private String directoryPath = null;
		public DeleteAllFilesInDirectoryRunnable(String directoryPath) {
			this.directoryPath = directoryPath;
		}
		
		@Override
		public void run() {
//			Log.i("DELETE FILES", "Background thread deleting all files in director \'" + directoryPath + "\'");
			
			File dir = new File(directoryPath);
			if (dir.exists() && dir.isDirectory()) {

				File[] files = dir.listFiles();
				for (File file : files)
					file.delete();	
				
			}
			else {
//				Log.e("DELETE FILES", "Error: \'" + directoryPath + "\' does not exist or is not a directory");
			}
		}
	}
}
