package com.example.valleyzapotectalkingdictionary;

import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

import org.apache.commons.lang3.StringEscapeUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainPageFragment extends Fragment{

	private TextView word;
	private TextView pos;
	private TextView definition_Eng;
	private TextView definition_Spa;
	private TextView variant;
	private TextView speaker;
	private TextView wordday;
	private TextView dbspecs;
	
	Word w;
	long db_size = 0;

	private String audioFileName = null;
	private AssetFileDescriptor audioFileFD = null;
	private PlayButton playButton = null;
	DictionaryDatabase db;


	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		
        View v = inflater.inflate(R.layout.fragment_main_page, container, false);
        
        word = (TextView) v.findViewById(R.id.Word_Zap);
        pos = (TextView) v.findViewById(R.id.PartOfSpeech);
        definition_Eng = (TextView) v.findViewById(R.id.Word_Eng);
        definition_Spa = (TextView) v.findViewById(R.id.Word_Spa);
        variant = (TextView) v.findViewById(R.id.Variant);
        speaker = (TextView) v.findViewById(R.id.Speaker);
        wordday = (TextView) v.findViewById(R.id.wordDAY);
        dbspecs = (TextView) v.findViewById(R.id.db_specs);
		
		// Inflate the layout for this fragment
		
		playButton = new PlayButton(getActivity());
//      playButton.setPadding(0, 0, 10, 0);

	      RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.PlayButtonContainer);
	      layout.addView(playButton);
	      
	      RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)playButton.getLayoutParams();
	      layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//	      layoutParams.setMargins(10, 10, 10, 10);
	      playButton.setLayoutParams(layoutParams);
	      playButton.setVisibility(View.GONE);
		
	      
	      updateWord();
	      
	      
	      SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
	      if (preferences.getLong(Preferences.DB_SIZE, 0) != db_size && db_size > 0) {
	    	  Editor editor = preferences.edit();
				editor.putLong(Preferences.DB_SIZE, db_size);
				editor.commit();
	      }
	      else {
	    	  db_size = preferences.getLong(Preferences.DB_SIZE, 0);
	      }
	      
	      dbspecs.setText(db_size + " " + dbspecs.getText().toString());
	      
	      
//	      getActivity().getCurrentFocus().clearFocus(); // clear focus?
//	      v.requestFocus();
	      
	      if (getActivity().getCurrentFocus() != null)
				getActivity().getCurrentFocus().clearFocus();
	      
	      
        return v;

	}
	
	public void updateWord(){
		db = new DictionaryDatabase(getActivity());
		Calendar cl = Calendar.getInstance();
		int day = cl.get(Calendar.DATE);
		int month = cl.get(Calendar.MONTH);
		int year = cl.get(Calendar.YEAR);
		String date = Integer.toString(day) + Integer.toString(month) + Integer.toString(year);
		Random rn = new Random(Integer.parseInt(date));
	    //int number = rn.nextInt((640 - 5) + 1) + 5;	    
		//DictionaryDatabase db = new DictionaryDatabase(getActivity());
		db_size = db.getSize();
		SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
		if (db_size == 0 && preferences.getLong(Preferences.DB_SIZE, 0) > 0)
			db_size = preferences.getLong(Preferences.DB_SIZE, 0);

		if (db_size == 0)
			db_size = 519;
		
		int number = rn.nextInt((int)db_size); // this is the problem, ID no.s not serialized
		number = 10;
		Log.i("WORD", "num=" + number);
		Cursor c = db.getIDmatch(number);
		if (c != null) {
			c.moveToFirst();
			w = new Word(Integer.parseInt(c.getString(0)), c.getString(1),
					c.getString(2), c.getString(3), c.getString(4),
					c.getString(5), c.getString(6), c.getString(7),
					c.getString(8), c.getString(9), c.getString(10),
					c.getString(11), c.getString(12));

			// Typeface type =
			// Typeface.createFromAsset(getActivity().getAssets(),"fonts/Parisish.ttf");
			String wordofday = "<b>"
					+ getResources().getString(R.string.wordOfTheDay) + "</b";
			wordday.setText(Html.fromHtml(wordofday));
			// wordday.setTypeface(type);

			String name = "<b>" + w.getName() + "</b> ";
			word.setText(Html.fromHtml(name));

			Log.i("WORD", "word name=\'" + w.getName() + "\'");

			if (!w.getPos().equals("")) {
				pos.setTypeface(null, Typeface.ITALIC);
				pos.setText(w.getPos());
			} else
				pos.setVisibility(View.GONE);

			if (!w.getGloss().equals("")) {
				String sourceString = "<b>"
						+ getResources().getString(R.string.english) + ":</b> "
						+ w.getGloss();
				definition_Eng.setText(Html.fromHtml(sourceString));
			} else
				definition_Eng.setVisibility(View.GONE);

			if (!w.getEsGloss().equals("")) {
				String sourceString = "<b>"
						+ getResources().getString(R.string.spanish) + ":</b> "
						+ w.getEsGloss();
				definition_Spa.setText(Html.fromHtml(sourceString));
			} else
				definition_Spa.setVisibility(View.GONE);

			if (!w.getDialect().equals(""))
				variant.setText(w.getDialect());
			else
				variant.setVisibility(View.GONE);

			if (!w.getAuthority().equals("")) {
				String authority = "<b>"
						+ getResources().getString(R.string.speaker) + "</b> "
						+ w.getAuthority();
				speaker.setText(Html.fromHtml(authority));
			} else
				speaker.setVisibility(View.GONE);

			AssetManager assetManager = getActivity().getAssets();

			if (!w.getAudio().equals("")) {
				audioFileName = "audio/" + w.getAudio();

				String HtmlUnescapedQuote = StringEscapeUtils
						.unescapeHtml3("&#8217;");
				String imageFileNameQuote = "'";

				audioFileName = audioFileName.replace(HtmlUnescapedQuote,
						imageFileNameQuote);

				try {
					audioFileFD = assetManager.openFd(audioFileName);
				} catch (IOException e) {
					Log.i("AUDIO", "Failed to open input stream for audio file");
				}

				if (audioFileFD != null && playButton != null) {
					playButton.setVisibility(View.VISIBLE);
				} else {
					Log.i("WORD", "no fd or playButton == null");
//					playButton.setVisibility(View.GONE);
				}

			}
		}
		else {
			Log.i("WORD", "c is null");
//			playButton.setVisibility(View.GONE);
		}

	}

	class PlayButton extends ImageButton {
    	private MediaPlayer player = null;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
            	playButton.setEnabled(false);
                onPlay(true);
            }
        };
        
        OnCompletionListener listener = new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				onPlay(false);
                playButton.setEnabled(true);
			}
        };
        
        OnPreparedListener prepListener = new OnPreparedListener() {
        	public void onPrepared(MediaPlayer mp) {
        		playButton.setEnabled(false);
        		mp.start();
        	}
        };
        

        public PlayButton(Context ctx) {
            super(ctx);
            this.setImageResource(R.drawable.audio_play);
            setOnClickListener(clicker);
        }
        
    	public void onPlay(boolean start) {
            if (start) {
                startPlaying();
            } else {
                stopPlaying();
            }
        }

        public void startPlaying() {
            player = new MediaPlayer();
            try {
            	player.setOnPreparedListener(prepListener);
				player.setOnCompletionListener(listener);

				// must call setDataSource giving offset and length in addition to FD!!!
				// calling setDataSource with just FD plays all of the audio files in the directory
                player.setDataSource(audioFileFD.getFileDescriptor(), audioFileFD.getStartOffset(), audioFileFD.getLength ());
                
                player.prepareAsync();
            } catch (IOException e) {
                Log.e("AUDIO", "prepare() failed");
            }
        }

        public void stopPlaying() {
            player.release();
            player = null;
        }
    }
	
}
