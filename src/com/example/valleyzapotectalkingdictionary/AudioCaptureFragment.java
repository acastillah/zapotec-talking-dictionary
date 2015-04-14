package com.example.valleyzapotectalkingdictionary;

import java.io.File;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
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
import android.widget.Toast;

public class AudioCaptureFragment extends Fragment implements Parcelable {

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
    
    private boolean recordButtonEnabled = true;
    private boolean playButtonEnabled = false;
    private boolean fileNameEditTextEnabled = true;
    private boolean saveButtonEnabled = false;
    private boolean mRecorded = false;
    
    private static final String RECORD_BUTTON_ENABLED = "RECORD_BUTTON_ENABLED";
    private static final String PLAY_BUTTON_ENABLED = "PLAY_BUTTON_ENABLED";
    private static final String FILE_NAME_EDIT_TEXT_ENABLED = "FILE_NAME_EDIT_TEXT_ENABLED";
    private static final String SAVE_BUTTON_ENABLED = "SAVE_BUTTON_ENABLED";
    
    private static final String mFileExtension = ".3gp";
    private static final String mFileExtension_desired = ".wav";
    private static final String dictionaryDirectoryName = "Zapotec Talking Dictionary";
    private static final String audioDirectoryName = "audio";
    private static String audioDirectoryFullPath = null;
	
    public AudioCaptureFragment() {}
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(getActivity(), "landscape", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            Toast.makeText(getActivity(), "portrait", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	super.onSaveInstanceState(savedInstanceState);
    	
    	Log.i("AUDIO", "onSaveInstanceState");
    	
    	savedInstanceState.putBoolean(RECORD_BUTTON_ENABLED, mRecordButton.isEnabled());
    	savedInstanceState.putBoolean(PLAY_BUTTON_ENABLED, mPlayButton.isEnabled());
    	savedInstanceState.putBoolean(FILE_NAME_EDIT_TEXT_ENABLED, mFileNameEditText.isEnabled());
    	savedInstanceState.putBoolean(SAVE_BUTTON_ENABLED, mSaveButton.isEnabled());
    	
    	
//    	outState.putParcelable("AudioCaptureFrag", this);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	
    	Log.i("AUDIO", "onActivityCreated");
    	
    	if (savedInstanceState != null) {
    		Log.i("AUDIO", "savedInstanceState is not null");
	    	recordButtonEnabled = savedInstanceState.getBoolean(RECORD_BUTTON_ENABLED);
	    	playButtonEnabled = savedInstanceState.getBoolean(PLAY_BUTTON_ENABLED);
	    	fileNameEditTextEnabled = savedInstanceState.getBoolean(FILE_NAME_EDIT_TEXT_ENABLED);
	    	saveButtonEnabled = savedInstanceState.getBoolean(SAVE_BUTTON_ENABLED);
	    	
	    	Log.i("AUDIO", "Record button enabled=" + recordButtonEnabled);
	    	Log.i("AUDIO", "Play button enabled=" + playButtonEnabled);
	    	Log.i("AUDIO", "File name enabled=" + fileNameEditTextEnabled);
	    	Log.i("AUDIO", "Save button enabled=" + saveButtonEnabled);
	    	
	    	mRecordButton.setEnabled(recordButtonEnabled);
	    	mPlayButton.setEnabled(playButtonEnabled);
	    	mFileNameEditText.setEnabled(fileNameEditTextEnabled);
	    	mSaveButton.setEnabled(saveButtonEnabled);
    	}
    	
    	Log.i("AUDIO", "onActivityCreated end");
    	
    }
    
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle icicle) {
    	super.onCreate(icicle);
        
        updateExternalStorageState();

        Log.i("DIR", "Setting audio dir path");
        
//        if (Environment.getExternalStorageState() == true) {
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
        	
        	
        	
        // set the name (full path) to the temporary file
    	mFileName = audioDirectoryFullPath;
        mFileName += "/temp";
        mFileName += mFileExtension_desired;
        	
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
		Log.i("AUDIO", "onCreateView");
		
        // Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_audio_capture, container, false);
		
		FragmentActivity activity = getActivity();
        
        LinearLayout fragmentLayout = new LinearLayout(activity);
        fragmentLayout.setOrientation(LinearLayout.VERTICAL);
        
        TextView audioFragmentDescription = new TextView(activity);
        audioFragmentDescription.setText(R.string.audioFragmentDescription);
        audioFragmentDescription.setPadding(20, 20, 20, 20);
        fragmentLayout.addView(audioFragmentDescription);
        
        LinearLayout recPlayButtonsLayout = new LinearLayout(activity);
        
        mRecordButton = new RecordButton(activity);
        mRecordButton.setEnabled(recordButtonEnabled);
        recPlayButtonsLayout.addView(mRecordButton,
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        
        mPlayButton = new PlayButton(activity);
        mPlayButton.setEnabled(playButtonEnabled);
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
        
        LinearLayout fileNameLayout = new LinearLayout(activity);
        fileNameLayout.setOrientation(LinearLayout.HORIZONTAL);
        
        TextView nameAudioTextView = new TextView(activity);
        nameAudioTextView.setText(R.string.audioName);
        nameAudioTextView.setPadding(20, 0, 0, 0);
        fileNameLayout.addView(nameAudioTextView,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        
        mFileNameEditText = new FileNameEditText(activity);
        mFileNameEditText.setEnabled(fileNameEditTextEnabled);
        mFileNameEditText.setEms(10);
        fileNameLayout.addView(mFileNameEditText,
                new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0));
        
        TextView audioExtension = new TextView(activity);
        audioExtension.setText(".3gp");
        fileNameLayout.addView(audioExtension);
        
        fragmentLayout.addView(fileNameLayout,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        0));
        
        mSaveButton = new SaveButton(activity);
        mSaveButton.setEnabled(saveButtonEnabled);
        fragmentLayout.addView(mSaveButton,
                new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0));
        
        
    	mRecordButton.setEnabled(recordButtonEnabled);
    	mPlayButton.setEnabled(playButtonEnabled);
    	mFileNameEditText.setEnabled(fileNameEditTextEnabled);
    	mSaveButton.setEnabled(saveButtonEnabled);
        
        
        return fragmentLayout;
        
        
        
        
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
                    setText(R.string.stopRecording);
                } else {
                    setText(R.string.startRecording);
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx) {
            super(ctx);
            setText(R.string.startRecording);
            setOnClickListener(clicker);
        }
    }

    class PlayButton extends Button {
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    setText(R.string.stopPlaying);
                } else {
                    setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        };
        
        OnCompletionListener listener = new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				onPlay(mStartPlaying);
                setText(R.string.startPlaying);
                mStartPlaying = !mStartPlaying;
			}
        };
        

        public PlayButton(Context ctx) {
            super(ctx);
            setText(R.string.startPlaying);
            setOnClickListener(clicker);
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
            	mPlayer.setOnCompletionListener(listener);
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
	                newFileName += mFileExtension_desired;
	                
	                File newAudioFile = new File(newFileName);
	                try {
						newAudioFile.createNewFile();

		                Log.i("SAVE AUDIO", 
		            			"new file path=" + newFileName +
		            			" canRead()=" + newAudioFile.canRead() +
		            			", canWrite()=" + newAudioFile.canWrite());
		                
		                boolean renameSuccessful = defaultAudioFile.renameTo(newAudioFile);
		            	Log.i("SAVE AUDIO", "renameSuccessful=" + renameSuccessful);
		            	
		            	// Inform the user that saving the audio was successful
		            	Toast.makeText(getActivity(), "Photo saved", Toast.LENGTH_SHORT).show();
		            	
		            	// Reset the form so user can record more audio
		            	mRecordButton.setEnabled(true);
		            	mSaveButton.setEnabled(false);
		            	mFileNameEditText.setText("");
		            	mFileNameEditText.setEnabled(true);
		            	
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
            setText(R.string.save);
            setOnClickListener(clicker);
//            setEnabled(false);
        }
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}
