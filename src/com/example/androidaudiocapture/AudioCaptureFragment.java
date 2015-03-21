package com.example.androidaudiocapture;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AudioCaptureFragment extends Fragment {

    private static boolean mExternalStorageAvailable = false;
    private static boolean mExternalStorageWriteable = false;
	
    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;

    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    private PlayButton   mPlayButton = null;
    private MediaPlayer   mPlayer = null;
    
    private FileNameEditText mFileNameEditText = null;
    private SaveButton	mSaveButton = null;
    
    private boolean mRecorded = false;
    
    private static final String mFileExtension = ".3gp"; // WHAT FILE TYPE?
    private static final String dictionaryDirectoryName = "Zapotec Talking Dictionary";
    private static final String audioDirectoryName = "audio";
    private static String audioDirectoryFullPath = null;
	
    public AudioCaptureFragment() {
    	// WHERE DO WE WANT THESE TO BE SAVED? INTERNALLY OR EXTERNALLY?
    	
        
    }
    
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle icicle) {
    	super.onCreate(icicle);

        FragmentActivity activity = getActivity();
        
        LinearLayout fragmentLayout = new LinearLayout(activity);
        fragmentLayout.setOrientation(LinearLayout.VERTICAL);
        
        LinearLayout recPlayButtonsLayout = new LinearLayout(activity);
        
        mRecordButton = new RecordButton(activity);
        recPlayButtonsLayout.addView(mRecordButton,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        
        mPlayButton = new PlayButton(activity);
        recPlayButtonsLayout.addView(mPlayButton,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        
        fragmentLayout.addView(recPlayButtonsLayout,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        
        mFileNameEditText = new FileNameEditText(activity);
        fragmentLayout.addView(mFileNameEditText,
                new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0));
        
        mSaveButton = new SaveButton(activity);
        fragmentLayout.addView(mSaveButton,
                new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0));
        
        activity.setContentView(fragmentLayout);
        
        
        
        updateExternalStorageState();

        Log.i("DIR", "Setting audio dir path");
        
       // if (Environment.getExternalStorageState() == true) {
        	audioDirectoryFullPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        	Log.i("DIR", "audio dir full path=" + audioDirectoryFullPath);
        	
        	File appDir = new File(audioDirectoryFullPath, dictionaryDirectoryName);
        	if (!appDir.exists()) {
        		boolean success = appDir.mkdir();
        		Log.i("DIR", "appDir created=" + success);
        	}
        	else {
        		Log.i("DIR", "appDir exists");
        	}
        	
        	if (appDir.exists() && appDir.isDirectory()) {
        		Log.i("DIR", "appDir exists & is a directory");
        		
        		if (!appDir.canWrite())
        			appDir.setWritable(true);
        		
        		audioDirectoryFullPath += "/" + dictionaryDirectoryName;
        		Log.i("DIR", "audio dir full path=" + audioDirectoryFullPath);
        		        		
        		File audioDir = new File(audioDirectoryFullPath, audioDirectoryName);
        		if (!audioDir.exists()) {
        			audioDir.mkdir();
        		}
        		
        		if (audioDir.isDirectory()) {
        			if (!audioDir.canWrite())
        				audioDir.setWritable(true);
        			
        			audioDirectoryFullPath += "/" + audioDirectoryName;
        			Log.i("DIR", "audio dir full path=" + audioDirectoryFullPath);
        		}
        		
        	}
        	else {
        		Log.i("DIR", "app directory is not a directory");
        	}
        	
//        }
//        else {
//        	mRecordButton.setEnabled(false);
//        }
        	
        	
        	
        	
        	mFileName = audioDirectoryFullPath;
            mFileName += "/temp";
            mFileName += mFileExtension;
        	
        	
        	
        	
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_audio_capture, container, false);
		return view;
	}
	
    void updateExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
//        handleExternalStorageState(mExternalStorageAvailable, mExternalStorageWriteable);
    }
	
	private void onRecord(boolean start) {
        if (start) {
            startRecording();
            mRecorded = false;
            mSaveButton.setEnabled(false);
            mPlayButton.setEnabled(false);
        } 
        else {
            stopRecording();
            mRecorded = true;
            mPlayButton.setEnabled(true);
            
            if (!mFileNameEditText.getText().toString().equals(""))
            	mSaveButton.setEnabled(true); 
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
            mRecordButton.setEnabled(false);
            mSaveButton.setEnabled(false);
            mFileNameEditText.setEnabled(false);
        } else {
            stopPlaying();
            mRecordButton.setEnabled(true);
            mFileNameEditText.setEnabled(true);
            
            if (!mFileNameEditText.getText().toString().equals(""))
            	mSaveButton.setEnabled(true);
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
    

    class RecordButton extends Button {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }
    }

    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText("Stop playing");
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };
        

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }
    }
    
    class FileNameEditText extends EditText {

    	TextWatcher watcher = new TextWatcher() {
    		
			@Override
			public void afterTextChanged(Editable arg0) {}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
				// user must enter file name before saving & there must be audio recorded to save
				if (s.length() != 0 && mRecorded == true) {
					mSaveButton.setEnabled(true); 
            	}
            	else {
            		mSaveButton.setEnabled(false); 
            	}
			}
			
		};
		
		public FileNameEditText(Context context) {
			super(context);
			addTextChangedListener(watcher);
		}
    	
    }
    
    class SaveButton extends Button {

        OnClickListener clicker = new OnClickListener() {
        	
            @SuppressLint("NewApi")
			public void onClick(View v) {
            	// disable file name field and save button
            	// so that user cannot change anything while we are saving the audio file
            	mRecordButton.setEnabled(false);
            	mPlayButton.setEnabled(false);
            	mFileNameEditText.setEnabled(false);
                mSaveButton.setEnabled(false);
            	
            	String userDefinedFileName = mFileNameEditText.getText().toString();
            	File defaultAudioFile = new File(mFileName);
            	
            	if (defaultAudioFile.exists()) {
            	
	            	Log.i("SAVE AUDIO", 
	            			"default file path=" + mFileName +
	            			" canRead()=" + defaultAudioFile.canRead() +
	            			", canWrite()=" + defaultAudioFile.canWrite());
	            	
	            	String newFileName = audioDirectoryFullPath;
	                newFileName += "/" + userDefinedFileName;
	                newFileName += mFileExtension;
	                
	                File newAudioFile = new File(newFileName);
	                try {
						newAudioFile.createNewFile();

		                Log.i("SAVE AUDIO", 
		            			"new file path=" + newFileName +
		            			" canRead()=" + newAudioFile.canRead() +
		            			", canWrite()=" + newAudioFile.canWrite());
		                
		                boolean renameSuccessful = defaultAudioFile.renameTo(newAudioFile);
		            	Log.i("SAVE AUDIO", "renameSuccessful=" + renameSuccessful);
		            	
		            	if (newAudioFile.exists()) {
		            		Log.i("SAVE AUDIO", "New file exists");
		            		Log.i("SAVE AUDIO", "New file name=" + newAudioFile.getAbsolutePath());
		            	}
		            	else {
		            		Log.i("SAVE AUDIO", "New does not file exist");
		            	}
		            	
		            	if (defaultAudioFile.exists()) {
		            		Log.i("SAVE AUDIO", "Default file exists");
		            		defaultAudioFile.delete();
		            	}
		            	else {
		            		Log.i("SAVE AUDIO", "Default does not file exist");
		            	}
		            	
					} catch (IOException e) {
						Log.e("SAVE AUDIO", "Could not create new audio file");
					}
	                newAudioFile.setWritable(true);
            	}
            	// close view
            }
        };
        

        public SaveButton(Context ctx) {
            super(ctx);
            setText("Save");
            setOnClickListener(clicker);
            setEnabled(false);
        }
    }
}
