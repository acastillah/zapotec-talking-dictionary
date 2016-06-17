package edu.haverford.cs.zapotectalkingdictionary;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
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
	private int lang;
	private String domain;
	private TextView mTextView;
	private View v = null;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		
		((MainActivity)getActivity()).setActionBarTitle(R.string.searchResults);
		
		Bundle bundle = this.getArguments();
        query = bundle.getString("QUERY");
        lang = bundle.getInt("LANG");
        domain = bundle.getString("DOM");
        
        
		// Inflate the layout for this fragment
        v = inflater.inflate(R.layout.search, container, false);
        mTextView = (TextView) v.findViewById(R.id.text);
        mListView = (ListView) v.findViewById(R.id.list);        
        showWords();
        
        
        return v;
        
    }

	public void showWords(){
		DictionaryDatabase db = new DictionaryDatabase(getActivity());
		Cursor cursor = db.getMatch(query, MainActivity.LanguageInterface.LANGUAGE_ENGLISH, domain);
    	if (cursor == null) {
            // There are no results        	
    		mTextView.setText(getResources().getString(R.string.noResults));
        } 
    	else {
   		 	cursor.moveToFirst();
   		 	
   		 	// Specify the columns we want to display in the result
            String[] from = null;	
            if (lang == MainActivity.LanguageInterface.LANGUAGE_SPANISH)
            	from = new String[] { DictionaryDatabase.KEY_WORD, DictionaryDatabase.KEY_ESGLOSS };
            else if (lang == MainActivity.LanguageInterface.LANGUAGE_ZAPOTEC)
            	from = new String[] { DictionaryDatabase.KEY_WORD, DictionaryDatabase.KEY_ESGLOSS };
            else 
            	from = new String[] { DictionaryDatabase.KEY_WORD, DictionaryDatabase.KEY_GLOSS };
            
   		 	// Specify the corresponding layout elements where we want the columns to go
            int[] to = new int[] { R.id.word_Searched, R.id.word_Definition };
            // Create a simple cursor adapter for the definitions and apply them to the ListView
            SimpleCursorAdapter words = new SimpleCursorAdapter(getActivity(),R.layout.search_results, cursor, from, to,0);
            mListView.setAdapter(words);

            // Define the on-click listener for the list items
            mListView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                	FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            		Fragment fragment = new WordDefinitionFragment();
            		Cursor c = ((SimpleCursorAdapter)parent.getAdapter()).getCursor();
            		c.moveToPosition(position);  
            		
            		String[] w = {c.getString(0),
    		                c.getString(1), c.getString(2), c.getString(3), 
    		                c.getString(4), c.getString(5), c.getString(6), 
    		                c.getString(7), c.getString(8), c.getString(9),
    		                c.getString(10), c.getString(11), c.getString(12), c.getString(13)};
            		Bundle bundle = new Bundle();
            	    bundle.putStringArray("WORD", w);
            	    ((Fragment) fragment).setArguments(bundle);
            		transaction.addToBackStack(null);            
            		transaction.replace(R.id.container, fragment, MainActivity.WORD_DEFINITION_FRAGMENT).commit();	
            		MainActivity.domainSpinner.setSelection(0);

            		if (v != null) {
	            		SearchView searchView = (SearchView) v.findViewById(R.id.searchView1);
	            		if (searchView != null) {
	//            			searchView.setQuery("", false);
	            			searchView.clearFocus();
	            		}
            		}
                }
            });    	
          }
	}
}
