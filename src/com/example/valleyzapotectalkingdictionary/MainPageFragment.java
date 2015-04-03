package com.example.valleyzapotectalkingdictionary;

import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

import org.apache.commons.lang3.StringEscapeUtils;

import com.example.valleyzapotectalkingdictionary.WordDefinitionFragment.PlayButton;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainPageFragment extends Fragment{

	private TextView word;
	private TextView pos;
	private TextView definition_Eng;
	private TextView definition_Spa;
	private TextView variant;
	private TextView speaker;
	private String audioFileName = null;
	private AssetFileDescriptor audioFileFD = null;
	private PlayButton playButton = null;

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
		updateWord();
		// Inflate the layout for this fragment
        return v;

	}
	
	public void updateWord(){
		Calendar cl = Calendar.getInstance();
		int day = cl.get(Calendar.DATE);
		int month = cl.get(Calendar.MONTH);	
		String date = Integer.toString(day) + Integer.toString(month);
		Random rn = new Random(Integer.parseInt(date));
	    int number = rn.nextInt((640 - 5) + 1) + 5;
	    Log.i("Number", Integer.toString(number));
	    
		DictionaryDatabase db = new DictionaryDatabase(getActivity());
		Cursor c = db.getIDmatch(number);
		if (c != null) {
   		 	c.moveToFirst();
   		 	Word w = new Word(Integer.parseInt(c.getString(0)),
	                c.getString(1), c.getString(2), c.getString(3), 
	                c.getString(4), c.getString(5), c.getString(6), 
	                c.getString(7), c.getString(8), c.getString(9),
	                c.getString(10), c.getString(11), c.getString(12));
   		 	
		word.setText(w.getName());
		if (!w.getPos().equals(""))
			pos.setText(w.getPos());
		else
			pos.setVisibility(View.GONE);
		if (!w.getGloss().equals(""))
			definition_Eng.setText(R.string.EnglishDefinition + w.getGloss());
		else
			definition_Eng.setVisibility(View.GONE);
		if (!w.getEsGloss().equals(""))
			definition_Spa.setText(R.string.SpanishDefinition + w.getEsGloss());
		else
			definition_Spa.setVisibility(View.GONE);
		if (!w.getDialect().equals(""))
			variant.setText(w.getDialect());
		else
			variant.setVisibility(View.GONE);
		if (!w.getAuthority().equals(""))
			speaker.setText("Speaker: " + w.getAuthority());
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
			}
			else {
				playButton.setVisibility(View.GONE);
			}
			
		}
		}
		
		
	    
	}
	
}
