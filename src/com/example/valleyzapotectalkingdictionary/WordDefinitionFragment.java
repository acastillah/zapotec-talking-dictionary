package com.example.valleyzapotectalkingdictionary;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringEscapeUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WordDefinitionFragment extends Fragment{
	
	private Word w;
	
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
	
	private ImageView image = null;
	private static final String imageFileDirectory = "images";		// under assets
	private InputStream imageStream = null;

	
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
//        playButton.setPadding(0, 0, 10, 0);
 
        RelativeLayout layout = (RelativeLayout) v.findViewById(R.id.PlayButtonContainer);
        layout.addView(playButton);
        
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)playButton.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//        layoutParams.setMargins(10, 10, 10, 10);
        playButton.setLayoutParams(layoutParams);
        
        
        image = (ImageView) v.findViewById(R.id.image);
        
        setUpDisplay();
        
        
       /*** If user is logged in && there is no photo for the word definition, display the ImageCaptureFragment ***/ 
        
        SharedPreferences preferences = getActivity().getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);
	        if(preferences.getBoolean(Preferences.IS_LINGUIST, false)
	        		&& w.getIMG().equals("") 
	        		&& true /* no image on local device */) {
	        
	        
	        LinearLayout fragContainer = (LinearLayout) v.findViewById(R.id.ImageFragmentContainer);
	
	        LinearLayout ll = new LinearLayout(v.getContext());
	        ll.setOrientation(LinearLayout.HORIZONTAL);
	
	        ll.setId(12345);
	        
	        String fileName = "Teotitlan_";
	        
	        String username = preferences.getString(Preferences.USERNAME, "");
	        if (!username.equals("")) {
	        	username = username.replace(" ", "");
	        	fileName += username + "_";
	        }
	        
	        fileName += w.getGloss();
	        
	        Bundle b = new Bundle();
	        b.putBoolean(ImageCaptureFragment.INSIDE_WORD_DEFINITION, true);
	        b.putBoolean(ImageCaptureFragment.LAUNCH_CAMERA, false);
	        b.putString(ImageCaptureFragment.FILE_NAME, fileName);
	        getFragmentManager().beginTransaction().add(ll.getId(), ImageCaptureFragment.newInstance(b), "someTag1").commit();
	
	        fragContainer.addView(ll);
        }
        
        
        /***********************************************************************************************************/
        

	        SearchView searchView = (SearchView) v.getRootView().findViewById(R.id.searchView1);
	        if (searchView != null) {
	        	Log.i("SEARCHVIEW", "not null");
//				searchView.setQuery("", false);
				searchView.clearFocus();
			}
        
        return v;
    }
	
	public void setUpDisplay(){
		w = new Word(Integer.parseInt(info[0]), info[1],info[2],info[3],info[4],info[5],info[6],info[7],info[8],info[9],info[10],info[11],info[12]);
		
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
		
		if (!w.getPos().equals("")) {
			pos.setText(w.getPos());
			pos.setTypeface(null, Typeface.ITALIC);
		}
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
		
		
		
//		try {
//			String[] assets = assetManager.list("");
//			int i=1;
//			for (String a : assets)
//				Log.i("ASSET", i + "..... " + a);
//			
//			assets = assetManager.list("audio");
//			Log.i("ASSET", "there are " + assets.length);
//			for (String a : assets)
//				Log.i("ASSET", i++ + " " + a);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		
		if (!w.getIMG().equals("")) {
			String imageFileName = imageFileDirectory + "/";
			imageFileName += w.getIMG().substring(0, w.getIMG().length()-4);
			imageFileName += "-scaled";
			imageFileName += w.getIMG().substring(w.getIMG().length()-4);
			
			Log.i("IMAGE", "image file name="+imageFileName);
			
			try {
				imageStream = assetManager.open(imageFileName);
			} catch (IOException e) {
				Log.i("IMAGE", "Failed to open input stream for image file");
			}
			
			if (imageStream != null) {
				Log.i("IMAGE", "Opened image stream");
				Bitmap bm = BitmapFactory.decodeStream(imageStream);
				
				int width = bm.getWidth();
				int height = bm.getHeight();
				Log.i("IMAGE", "width="+width+" height="+height);
				
				image.setImageBitmap(bm);
			}
			else {
				image.setVisibility(View.GONE);
			}
		}
		else {
			image.setVisibility(View.GONE);
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
