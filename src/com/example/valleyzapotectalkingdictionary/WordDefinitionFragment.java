package com.example.valleyzapotectalkingdictionary;

import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
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

public class WordDefinitionFragment extends Fragment{
	
	private TextView word;
	private TextView pos;
	private TextView definition_Eng;
	private TextView definition_Spa;
	private TextView variant;
	private TextView speaker;
	private String[] info;
	
	private PlayButton playButton = null;
	
	private static final String audioFileDirectory = "audio";		// under assets
	private String audioFileName = null;
	private AssetFileDescriptor audioFileFD = null;
	
	private static final String imageFileDirectory = "images";		// under assets
	private AssetFileDescriptor imageFileFD = null;

	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		Bundle bundle = this.getArguments();
        info = bundle.getStringArray("WORD");
        
		// Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_word_definition, container, false);
        
        word = (TextView) v.findViewById(R.id.Word_Zap);
        pos = (TextView) v.findViewById(R.id.PartOfSpeech);
        definition_Eng = (TextView) v.findViewById(R.id.Word_Eng);
        definition_Spa = (TextView) v.findViewById(R.id.Word_Spa);
        variant = (TextView) v.findViewById(R.id.Variant);
        speaker = (TextView) v.findViewById(R.id.Speaker);
        
        playButton = new PlayButton(getActivity());
 
        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.PlayButtonContainer);
        layout.addView(playButton);
        
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)playButton.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        playButton.setLayoutParams(layoutParams);
        
        setUpDisplay();
        
        return v;
    }
	
	public void setUpDisplay(){
		Word w = new Word(Integer.parseInt(info[0]), info[1],info[2],info[3],info[4],info[5],info[6],info[7],info[8],info[9],info[10],info[11],info[12]);
		
//		Log.i("WORD DEF", "id=" + w.getID()
//				+ "\nword=" + w.getName()
//				+ "\ngloss=" + w.getGloss()
//				+ "\nipa=" + w.getIPA()
//				+ "\npos=" + w.getPos()
//				+ "\nusage_example=" + w.getUsage()
//				+ "\ndialect=" + w.getDialect()
//				+ "\nmetadata=" + w.getMetadata()
//				+ "\nauthority=" + w.getAuthority()
//				+ "\naudio=" + w.getAudio()
//				+ "\nimage=" + w.getIMG()
//				+ "\nsemantic_ids=" + w.getSemantic()
//				+ "\nes_gloss=" + w.getEsGloss());
		
		String name = "<b>" + w.getName()+ "</b> "; 
		word.setText(Html.fromHtml(name));
		
		if (!w.getPos().equals(""))
			pos.setText(w.getPos());
		else
			pos.setVisibility(View.GONE);
		
		if (!w.getGloss().equals("")){
			String sourceString = "<b>" + getResources().getString(R.string.english) + ":</b> " + w.getGloss(); 
			definition_Eng.setText(Html.fromHtml(sourceString));
		}else
			definition_Eng.setVisibility(View.GONE);
		
		if (!w.getEsGloss().equals("")){
			String sourceString = "<b>" + getResources().getString(R.string.spanish) + ":</b> " + w.getEsGloss(); 
			definition_Spa.setText(Html.fromHtml(sourceString));
		}else
			definition_Spa.setVisibility(View.GONE);
		
		if (!w.getDialect().equals(""))
			variant.setText(w.getDialect());
		else
			variant.setVisibility(View.GONE);
		
		if (!w.getAuthority().equals("")){
			String authority = "<b>" + getResources().getString(R.string.speaker) + "</b> " + w.getAuthority(); 
			speaker.setText(Html.fromHtml(authority));
		}
		else
			speaker.setVisibility(View.GONE);
			
		AssetManager assetManager = getActivity().getAssets();
		
		if (!w.getAudio().equals("")) {
			audioFileName = "audio/" + w.getAudio();
			
			String HtmlUnescapedQuote = StringEscapeUtils.unescapeHtml3("&#8217;");
			String imageFileNameQuote = "'";
			
			audioFileName = audioFileName.replace(HtmlUnescapedQuote, imageFileNameQuote);
						
			try {
				audioFileFD = assetManager.openFd(audioFileName);
			} catch (IOException e) {
				Log.i("AUDIO", "Failed to open input stream for audio file");
			}
			
			if (audioFileFD != null) {
				Log.i("AUDIO", "Opened audio file, fd=" + audioFileFD.getFileDescriptor());
			}
			else {
				playButton.setVisibility(View.GONE);
				Log.i("Hidden button", "AUDIO");
			}
			
		}
		
		else{
			playButton.setVisibility(View.GONE);
		}
		
		
		
		try {
			String[] assets = assetManager.list("");
			int i=1;
			for (String a : assets)
				Log.i("ASSET", i + "..... " + a);
			
			assets = assetManager.list("audio");
			Log.i("ASSET", "there are " + assets.length);
			for (String a : assets)
				Log.i("ASSET", i++ + " " + a);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		if (!w.getIMG().equals("")) {
			String imageFileName = "images/IMG_6114-scaled.jpg";
			
			//...
			
			// WHY CAN'T I COPY THE IMAGE FILES INTO THE PROJECT?
		}
	}
	
    
    class PlayButton extends ImageButton {
    	private MediaPlayer player = null;
        boolean mStartPlaying = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                onPlay(mStartPlaying);
                mStartPlaying = !mStartPlaying;
            }
        };
        
        OnCompletionListener listener = new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				onPlay(mStartPlaying);
                mStartPlaying = !mStartPlaying;
			}
        };
        

        public PlayButton(Context ctx) {
            super(ctx);
            this.setImageResource(R.drawable.audio_play);
            GradientDrawable buttonShape = new GradientDrawable();
            buttonShape.setCornerRadius(10);
            //this.setBackgroundDrawable(buttonShape);
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
				player.setOnCompletionListener(listener);
				
//                player.setDataSource(audioFileFD.getFileDescriptor());
				
				// must call setDataSource giving offset and length in addition to FD!!!
				// calling setDataSource with just FD plays all of the audio files in the directory
                player.setDataSource(audioFileFD.getFileDescriptor(), audioFileFD.getStartOffset(), audioFileFD.getLength ());
                
                player.prepare();
                player.start();
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
