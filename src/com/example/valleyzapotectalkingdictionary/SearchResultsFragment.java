package com.example.valleyzapotectalkingdictionary;

import org.apache.commons.lang3.StringEscapeUtils;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SearchResultsFragment extends Fragment {
	private String query;
	private ListView mListView;
	private TextView mTextView;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		Bundle bundle = this.getArguments();
        query = bundle.getString("QUERY");
		// Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.search, container, false);
        mTextView = (TextView) v.findViewById(R.id.text);
        mListView = (ListView) v.findViewById(R.id.list);        
        showWords();
        return v;
        
    }
	
	public void showWords(){
		DictionaryDatabase db = new DictionaryDatabase(getActivity());
		
    	Cursor cursor = db.getMatchWord(query);
    	if (cursor == null) {
            // There are no results
        	Log.i("Results", "None");
        	
    		mTextView.setText("No Results");
    		//mTextView.setText(getString(R.string.no_results, new Object[] {query}));
        } 
    	else {
   		 	cursor.moveToFirst();
            // Specify the columns we want to display in the result
            String[] from = new String[] { DictionaryDatabase.KEY_GLOSS,
                                           DictionaryDatabase.KEY_WORD };
//            // Specify the corresponding layout elements where we want the columns to go
            int[] to = new int[] { R.id.word_ENG,
                                        R.id.word_ZAP };
            // Create a simple cursor adapter for the definitions and apply them to the ListView
            SimpleCursorAdapter words = new SimpleCursorAdapter(getActivity(),R.layout.search_results, cursor, from, to,0);
            mListView.setAdapter(words);
        	Log.i("Results", "adapter set");

            // Define the on-click listener for the list items
            mListView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Build the Intent used to open WordActivity with a specific word Uri
                    //Intent wordIntent = new Intent(getActivity(), WordDefinitionActivity.class);
                    ///Uri data = Uri.withAppendedPath(DictionaryProvider.CONTENT_URI,
                    //                                String.valueOf(id));
                    //wordIntent.setData(data);
                    //startActivity(wordIntent);
            		//startActivity(new Intent(getActivity(), WordDefinitionActivity.class));
                	FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            		Fragment fragment = new WordDefinitionFragment();
            		Cursor c = ((SimpleCursorAdapter)parent.getAdapter()).getCursor();
            		c.moveToPosition(position);      
            		String[] w = {c.getString(0),
    		                c.getString(1), c.getString(2), c.getString(3), 
    		                c.getString(4), c.getString(5), c.getString(6), 
    		                c.getString(7), c.getString(8), c.getString(9),
    		                c.getString(10), c.getString(11), c.getString(12)};
            		Bundle bundle = new Bundle();
            	    bundle.putStringArray("WORD", w);
            	    ((Fragment) fragment).setArguments(bundle);
            		transaction.addToBackStack(null);            
            		transaction.replace(R.id.container, fragment).commit();		
                }
            });    	
          }
	}
}
