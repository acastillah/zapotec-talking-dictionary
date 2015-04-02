package com.example.valleyzapotectalkingdictionary;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DictionaryDatabase {

    private static final String TAG = "DictionaryDatabase";
    private static final String DATABASE_NAME = "dictionary";
    private static final int DATABASE_VERSION = 8;
    private static final String TABLE_WORDS = "words";
    private final DictionaryOpenHelper mDatabaseOpenHelper;
    private int search = 0;
    
 // Words Table Columns names
    private static final String KEY_ID = "_id";
    static final String KEY_WORD = "lang"; //WORD IN ZAPOTEC
    static final String KEY_GLOSS = "gloss"; //WORD IN ENGLISH
    private static final String KEY_IPA = "ipa";
    private static final String KEY_POS = "pos";
    private static final String KEY_USAGE = "usage_example";
    private static final String KEY_DIALECT = "dialect";
    private static final String KEY_META = "metadata";
    private static final String KEY_AUTHORITY = "authority";
    private static final String KEY_AUDIO = "audio";
    private static final String KEY_IMG = "image";
    private static final String KEY_SEMANTIC = "semantic_ids";
    private static final String KEY_ESGLOSS = "es_gloss"; //WORD IN SPANISH
    
    public DictionaryDatabase(Context context) {
        mDatabaseOpenHelper = new DictionaryOpenHelper(context);

    }
    
    public Cursor getMatch(String q, int language){
    	search = language;
		Log.i("LANG", "get Match: " + Integer.toString(search));

    	String KEY = null;
    	if (search == 0){
    		KEY = null;
    	}
    	else if (search == 1){
    		KEY = KEY_WORD;
    	}
    	else if (search == 2){
    		KEY = KEY_GLOSS;
    	}
    	else if (search == 3){
    		KEY = KEY_ESGLOSS;
    	}
    	Log.i("Searching", Integer.toString(search));
    	SQLiteDatabase db = mDatabaseOpenHelper.getReadableDatabase();
    	//String[] params = {"%" + param1 + "%", "%" + param2 + "%", "%" + param3 + "%"};
        Cursor cursor = db.query(TABLE_WORDS, new String[] { KEY_ID,
                KEY_WORD, KEY_IPA, KEY_GLOSS, KEY_POS, KEY_USAGE, KEY_DIALECT, KEY_META, KEY_AUTHORITY,
                KEY_AUDIO, KEY_IMG, KEY_SEMANTIC, KEY_ESGLOSS}, KEY + "=?",
                new String[] { String.valueOf(q) }, null, null, null, null);
        if(cursor==null){
    		return null;
    	}
        if(!cursor.moveToFirst()){
    		return null;
    	}
		return cursor;

    }
    
    public Cursor getMatchWord(String query, int language){
    	search = language;
    	Cursor found = mDatabaseOpenHelper.getEntry(query);
    	if(found==null){
    		return null;
    	}
    	if(!found.moveToFirst()){
    		return null;
    	}
    	return found;
    }
    
    public class DictionaryOpenHelper extends SQLiteOpenHelper {
		
        private final Context mHelperContext;
	 
	    public DictionaryOpenHelper(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mHelperContext = context;
	    }
	 
	    // Creating Tables
	    @Override
	    public void onCreate(SQLiteDatabase db) {
	        String CREATE_WORDS_TABLE = "CREATE TABLE " + TABLE_WORDS + "("
	                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_WORD + " TEXT,"
	                + KEY_IPA + " TEXT," + KEY_GLOSS + " TEXT," + KEY_POS + " TEXT,"
	                + KEY_USAGE + " TEXT," + KEY_DIALECT + " TEXT," + KEY_META + " TEXT," 
	                + KEY_AUTHORITY + " TEXT," + KEY_AUDIO + " TEXT," + KEY_IMG + " TEXT," 
	                + KEY_SEMANTIC + " TEXT," + KEY_ESGLOSS + " TEXT" + ")";
            db.execSQL(CREATE_WORDS_TABLE);
	        loadDictionary();
	    }
	    
	    private void loadDictionary() {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        loadWords();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }
	 
	    private void loadWords() throws IOException {
            Log.d(TAG, "Loading words...");
            Iterator<?> i = JSONReadFromFile();
        	while (i.hasNext()) {
        		JSONObject innerObj = (JSONObject) i.next();		                
		            String id 			= StringEscapeUtils.unescapeHtml4((String) innerObj.get("oid"));
		            String word 		= StringEscapeUtils.unescapeHtml4((String) innerObj.get("lang"));
		            String ipa 			= StringEscapeUtils.unescapeHtml4((String) innerObj.get("ipa"));		            
		            String gloss 		= StringEscapeUtils.unescapeHtml4((String) innerObj.get("gloss"));
		            String pos 			= StringEscapeUtils.unescapeHtml4((String) innerObj.get("pos"));
		            String usage 		= StringEscapeUtils.unescapeHtml4((String) innerObj.get("usage_example"));
		            String dialect 		= StringEscapeUtils.unescapeHtml4((String) innerObj.get("dialect"));
		            String metadata 	= StringEscapeUtils.unescapeHtml4((String) innerObj.get("metadata"));
		            String authority 	= StringEscapeUtils.unescapeHtml4((String) innerObj.get("authority"));
		            String audio 		= StringEscapeUtils.unescapeHtml4((String) innerObj.get("audio"));
		            String image 		= StringEscapeUtils.unescapeHtml4((String) innerObj.get("image"));
		            String semantic_ids = StringEscapeUtils.unescapeHtml4((String) innerObj.get("semantic_ids"));
		            String es_gloss 	= StringEscapeUtils.unescapeHtml4((String) innerObj.get("es_gloss"));
		            Word w = new Word(Integer.parseInt(id),word,ipa,gloss,pos,usage,dialect,metadata,authority,audio,image,semantic_ids,es_gloss);
		            long word_id = addWord(w);
                    if (word_id < 0) {
                        Log.e(TAG, "unable to add word: " + word);
                    }
        	}      
            Log.d(TAG, "DONE loading words.");
        }
	    
	    public Iterator<?> JSONReadFromFile() {

			try {
				String str="";
				StringBuffer buf = new StringBuffer();
	            final Resources resources = mHelperContext.getResources();
				InputStream is = resources.openRawResource(R.raw.teotitlan_export);
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				if (is!=null) {							
					while ((str = reader.readLine()) != null) {	
						buf.append(str + "\n" );
					}				
				}		
				is.close();	
				
				JSONParser parser=new JSONParser();
	            JSONArray jsonArray = (JSONArray) parser.parse(buf.toString());
	 	        	
				Iterator<?> i = jsonArray.iterator();
	        			 
	        	return i;  

			} catch (Exception e) {
				Log.i("Parsing failed", "FAIL");
			}
			return null;
	    }
	    
	    // Upgrading database
	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        // Drop older table if existed
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
	 
	        // Create tables again
	        onCreate(db);
	    }
	    
	 // Adding new contact
	    public long addWord(Word word) {
	    	SQLiteDatabase db = this.getWritableDatabase();
	    	 
	        ContentValues values = new ContentValues();
	        values.put(KEY_ID, word.getID());
	        values.put(KEY_WORD, word.getName()); // Name
	        values.put(KEY_IPA, word.getIPA()); // IPA
	        values.put(KEY_GLOSS, word.getGloss()); // Gloss
	        values.put(KEY_POS, word.getPos()); // pos       
	        values.put(KEY_USAGE, word.getUsage()); // usage
	        values.put(KEY_DIALECT, word.getDialect()); // DIALECT
	        values.put(KEY_META, word.getMetadata()); // METADATA
	        values.put(KEY_AUTHORITY, word.getAuthority()); // AUTHORITY
	        values.put(KEY_AUDIO, word.getAudio()); // audio
	        values.put(KEY_IMG, word.getIMG()); // img
	        values.put(KEY_SEMANTIC, word.getSemantic()); // semantic
	        values.put(KEY_ESGLOSS, word.getEsGloss()); // es_gloss
	        
	        // Inserting Row
	        long result = db.insert(TABLE_WORDS, null, values);
	        db.close(); // Closing database connection
	        return result;
	    }
	     
	    // Getting single contact
	    public Cursor getEntry(String q) {
	    	SQLiteDatabase db = this.getReadableDatabase();
	    	Log.i("getting entry","of id");
	        Cursor cursor = db.query(TABLE_WORDS, new String[] { KEY_ID,
	                KEY_WORD, KEY_IPA, KEY_GLOSS, KEY_POS, KEY_USAGE, KEY_DIALECT, KEY_META, KEY_AUTHORITY,
	                KEY_AUDIO, KEY_IMG, KEY_SEMANTIC, KEY_ESGLOSS}, KEY_GLOSS + "=?",
	                new String[] { String.valueOf(q) }, null, null, null, null);
	        return cursor;
		}
	     
	    // Getting All Contacts
	    public List<Word> getAllWords() {
			return null;}
	     
	    // Getting contacts Count
	    public int getWordCount() {
			return 0;
		}
	    
	     
	}
	
}
