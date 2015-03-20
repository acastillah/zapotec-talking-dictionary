package com.example.valleyzapotectalkingdictionary;

import java.io.IOException;

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

public class AudioCaptureFragment extends Fragment {

    private static final String LOG_TAG = "AudioRecordTest";
    private static String mFileName = null;

    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    private PlayButton   mPlayButton = null;
    private MediaPlayer   mPlayer = null;
    
    private FileNameEditText mFileNameEditText = null;
    private SaveButton	mSaveButton = null;
	
    public AudioCaptureFragment() {
    	// WHERE DO WE WANT THESE TO BE SAVED? INTERNALLY OR EXTERNALLY?
    	
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/valleyzapotectalkingdictionary_audiofile.3gp";
    }
    
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
	
	private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
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
				
				// user must enter file name before saving
				if (s.length() == 0) {
					mSaveButton.setEnabled(false); 
            	}
            	else {
            		mSaveButton.setEnabled(true); 
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
            public void onClick(View v) {
            	String userDefinedFileName = mFileNameEditText.getText().toString();
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
