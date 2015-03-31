package com.example.valleyzapotectalkingdictionary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class WordDefinitionFragment extends Fragment{
	
	private TextView word;
	private TextView definition;
	private TextView variant;
	private String[] info;

	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		Bundle bundle = this.getArguments();
        info = bundle.getStringArray("WORD");
		// Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_word_definition, container, false);
        word = (TextView) v.findViewById(R.id.Word_Zap);
        definition = (TextView) v.findViewById(R.id.Word_Eng);        
        variant = (TextView) v.findViewById(R.id.Variant);    
        setUpDisplay();
        return v;
    }
	
	public void setUpDisplay(){
		Word w = new Word(Integer.parseInt(info[0]), info[1],info[2],info[3],info[4],info[5],info[6],info[7],info[8],info[9],info[10],info[11],info[12]);
		word.setText(w.getName());
		definition.setText(w.getGloss());
		variant.setText(w.getDialect());
	}
}
