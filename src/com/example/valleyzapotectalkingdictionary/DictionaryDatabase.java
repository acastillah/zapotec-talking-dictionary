package com.example.valleyzapotectalkingdictionary;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class DictionaryDatabase {

    private static final String DATABASE_NAME = "dictionary";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_WORDS = "words";
    private final DictionaryOpenHelper mDatabaseOpenHelper;    
 // Words Table Columns names
    private static final String KEY_ID = "_id";			// the ID we assign
    private static final String KEY_DB_ID = "db_id";	// the ID from the original DB
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
    static final String KEY_ESGLOSS = "es_gloss"; //WORD IN SPANISH
    
    private static long db_size;
    
    // date of DB that comes with the app, given same date/time as email from Jeremy
    public static final int DB_YEAR = 2015;
    public static final int DB_MONTH = 1; // NOTE: months start at 0, not 1, so 1=February
    public static final int DB_DAY = 5;
    public static final int DB_HOUR = 22; // military time
    public static final int DB_MINUTE = 34;
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy kk:mm");
    
    public DictionaryDatabase(Context context) {
        mDatabaseOpenHelper = new DictionaryOpenHelper(context);   
        
    }
    
    public Cursor getMatch(String q, int language, String dom){
    	
    	SQLiteDatabase db = mDatabaseOpenHelper.getReadableDatabase();
    	String KEY = null;
    	q = q.replace("'", "''");
				KEY = "(" + KEY_WORD + " LIKE '%" + q + "%'" + " OR " + KEY_GLOSS +  " LIKE '%" + String.valueOf(q) 
							+ "%'" + " OR " + KEY_ESGLOSS + " LIKE '%" + String.valueOf(q) + "%'" + ")"; 
    	
		if (dom!=""){
			KEY = KEY + " AND " + KEY_SEMANTIC + " LIKE '%" + String.valueOf(dom) + "%'";
		}
				
		Cursor cursor = db.query(TABLE_WORDS, new String[] { KEY_ID, KEY_DB_ID,
        KEY_WORD, KEY_IPA, KEY_GLOSS, KEY_POS, KEY_USAGE, KEY_DIALECT, KEY_META, KEY_AUTHORITY,
        KEY_AUDIO, KEY_IMG, KEY_SEMANTIC, KEY_ESGLOSS}, KEY,
        null, null, null, null, null);

        if(cursor==null){
    		return null;
    	}
        if(!cursor.moveToFirst()){
    		return null;
    	}
		return cursor;
    }
    
    public Cursor getIDmatch(int id){
    	SQLiteDatabase db = mDatabaseOpenHelper.getReadableDatabase();
    	String selection = KEY_ID + "=?";
    	String[] selectionArgs = {Integer.toString(id)};
    	Cursor cursor = db.query(TABLE_WORDS,  new String[] { KEY_ID, KEY_DB_ID,
                KEY_WORD, KEY_IPA, KEY_GLOSS, KEY_POS, KEY_USAGE, KEY_DIALECT, KEY_META, KEY_AUTHORITY,
                KEY_AUDIO, KEY_IMG, KEY_SEMANTIC, KEY_ESGLOSS}, selection, selectionArgs, null, null, null);
    	if(cursor==null){
    		return null;
    	}
        if(!cursor.moveToFirst()){
    		return null;
    	}
		return cursor;    	
    }
    
    public void update(){
    	SQLiteDatabase db = mDatabaseOpenHelper.getReadableDatabase();
    		int old = DATABASE_VERSION;
        	int newv = old + 1;
        	mDatabaseOpenHelper.onUpgrade(db, old, newv);

    }
    public long getSize() {
    	return db_size;
    }
    
//    public String[] getDomainList(){
//		return (String[]) mDatabaseOpenHelper.domainList.toArray();
//    }
    
    public class DictionaryOpenHelper extends SQLiteOpenHelper {
		 private final Context mHelperContext;
    	//private final ArrayList<String> domainList = new ArrayList<String>();
		 SharedPreferences preferences;
		 
    	public DictionaryOpenHelper(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mHelperContext = context;
            preferences = mHelperContext.getSharedPreferences(Preferences.APP_SETTINGS, Activity.MODE_PRIVATE);

	    }
	   
	    // Creating Tables
	    @Override
	    public void onCreate(SQLiteDatabase db) {
	    	db_size = 0;
	        String CREATE_WORDS_TABLE = "CREATE TABLE " + TABLE_WORDS + "("
	                + KEY_ID + " INTEGER PRIMARY KEY," 
	                + KEY_DB_ID + " INTEGER,"
	        		+ KEY_WORD + " TEXT,"
	                + KEY_IPA + " TEXT," + KEY_GLOSS + " TEXT," + KEY_POS + " TEXT,"
	                + KEY_USAGE + " TEXT," + KEY_DIALECT + " TEXT," + KEY_META + " TEXT," 
	                + KEY_AUTHORITY + " TEXT," + KEY_AUDIO + " TEXT," + KEY_IMG + " TEXT," 
	                + KEY_SEMANTIC + " TEXT," + KEY_ESGLOSS + " TEXT" + ")";
            db.execSQL(CREATE_WORDS_TABLE);
	        loadDictionary();
	    }
	    
	    private void loadDictionary() {
	    	Log.i("database", Boolean.toString(preferences.getBoolean(Preferences.DB_LOADED, false)));
			if (preferences.getBoolean(Preferences.DB_LOADED, false) == false) {
	                   	new downloadFiles().execute();
				         Editor editor = preferences.edit(); 
				         editor.putBoolean(Preferences.DB_LOADED, true);
				         editor.commit();
			}
			else{
				try{
				loadWords();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
        }
	    
		class downloadFiles extends AsyncTask<String, String, String> {
			private ProgressDialog pDialog;
		    String response;

			@Override
		    protected void onPreExecute() {
		       super.onPreExecute();
		        pDialog = new ProgressDialog(mHelperContext);
		        pDialog.setMessage("Downloading file...");
		        pDialog.setIndeterminate(false);
		        pDialog.setCanceledOnTouchOutside(false);
		        pDialog.setMax(100);
		        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		        pDialog.setCancelable(true);
		        pDialog.show();        	
		    }

		    @Override
		    protected String doInBackground(String... f_url) {
				HttpURLConnection con;
		    	try{
					int type = getType();
					URL url = new URL("http://talkingdictionary.swarthmore.edu/dl/retrieve.php");
					con = (HttpURLConnection) url.openConnection();
					con.setRequestMethod("POST");
					con.setDoOutput(true);
					DataOutputStream wr = new DataOutputStream(con.getOutputStream());
					String urlParam;
					urlParam = "dict=teotitlan&export=true&dl_type=" + Integer.toString(type);
					wr.writeBytes(urlParam);
					wr.flush();
					wr.close();
					
					if(con.getContentLength()==0){
						if(con.getResponseCode()==403 || con.getResponseCode()==404){
							response = "Error. Try again later.";
						}
						else if(con.getResponseCode()==204){
							response = "No updates available.";
						}
					}
					else{
					String path = mHelperContext.getFilesDir().getAbsolutePath();
					File file = new File(mHelperContext.getFilesDir(), "content.zip");
					OutputStream output = new FileOutputStream(file);
					InputStream input = con.getInputStream();
					byte[] buffer = new byte[1024]; 
					int lengthOfFile = con.getContentLength();
					int bytesRead = input.read(buffer);
					long total = 0;
					while (bytesRead >= 0) {
					    total +=bytesRead;
					    output.write(buffer, 0, bytesRead);
					    bytesRead = input.read(buffer);
					    publishProgress(""+(int)((total*100)/lengthOfFile));
					}
				    output.flush();
				    output.close();
				    input.close();
							
				    InputStream is;
				    ZipInputStream zis;
				    String filename;
			        is = new FileInputStream(file);
			        zis = new ZipInputStream(new BufferedInputStream(is));          
			        ZipEntry ze;
			        int count;
			         while ((ze = zis.getNextEntry()) != null) {
			             filename = ze.getName();
			             if (ze.isDirectory()) {
				
				            		File fmd = new File(path, filename);
					                fmd.mkdirs();
				                continue;
				          }
			             FileOutputStream fout;
			             
				             fout = new FileOutputStream(path + "/" + filename);
			           
			             while ((count = zis.read(buffer)) != -1) {
			                 fout.write(buffer, 0, count);             
			             }
			             fout.close();               
			             zis.closeEntry();
			         }
			         is.close();
			         zis.close();
			         response = "Download finished";

					}
					} catch (IOException e) {
						e.printStackTrace();
					}
				return null;
		    	
		    }

		    protected void onProgressUpdate(String... progress) {
		        // setting progress percentage
				super.onProgressUpdate(progress);
		        pDialog.setProgress(Integer.parseInt(progress[0]));
		   }

			@Override
		    protected void onPostExecute(String file_url) {
		        // dismiss the dialog after the file was downloaded
				pDialog.dismiss();
				if (response == "Download finished"){
					new setupDatabase().execute();
				}
				else{
					Toast.makeText(mHelperContext, response, Toast.LENGTH_SHORT).show();
				}
			}
		}
	 
		/**
		 * Background Async Task to download file
		 * */
		class setupDatabase extends AsyncTask<String, String, String> {
			private ProgressDialog pDialog;
			DictionaryDatabase db = new DictionaryDatabase(mHelperContext);

			@Override
		    protected void onPreExecute() {
		       super.onPreExecute();
		        pDialog = new ProgressDialog(mHelperContext);
		        pDialog.setMessage("Setting up database...");
		        pDialog.setIndeterminate(false);
		        pDialog.setCanceledOnTouchOutside(false);
		        pDialog.setMax(100);
		        pDialog.setCancelable(true);
		        pDialog.show();        	
		    }

		    @Override
		    protected String doInBackground(String... f_url) {
		    	try {
					loadWords();
				} catch (IOException e) {
					e.printStackTrace();
				}
		    	return null;
		    }

			@Override
		    protected void onPostExecute(String file_url) {
		        // dismiss the dialog after the file was downloaded
				pDialog.dismiss();
				Toast.makeText(mHelperContext, "Finished setting up the database", Toast.LENGTH_SHORT).show();
			}
		}
		
	    private void loadWords() throws IOException {
	    	int id = 1;
            Iterator<?> i = JSONReadFromFile();
        	while (i.hasNext()) {
        		JSONObject innerObj = (JSONObject) i.next();		                
		            String db_id 		= StringEscapeUtils.unescapeHtml4((String) innerObj.get("oid"));
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
		            Word w = new Word(id++, Integer.parseInt(db_id),word,ipa,gloss,pos,usage,dialect,metadata,authority,audio,image,semantic_ids,es_gloss);
		            addWord(w);
        	}      
        }
		
		public int getType(){
			return 2;
		}
	    
	    public Iterator<?> JSONReadFromFile() {

			try {
				String str="";
				StringBuffer buf = new StringBuffer();
				InputStream is = new FileInputStream(mHelperContext.getFilesDir() + "/teotitlan_content/teotitlan_export.json");
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
			}
			return null;
	    }
	    	    
	    // Upgrading database
	    @Override
	    public void onUpgrade(final SQLiteDatabase db, int oldVersion, int newVersion) {
	    	//Drop older table if existed
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
	        // Create tables again
	        onCreate(db);
	    	
	    }
	    
	 // Adding new contact
	    public long addWord(Word word) {
	    	SQLiteDatabase db = this.getWritableDatabase();
	    	 	    	
	        ContentValues values = new ContentValues();
	        values.put(KEY_ID, word.getID());
	        values.put(KEY_DB_ID, word.getDB_ID());
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
	        values.put(KEY_ESGLOSS, word.getEsGloss()); // es_glosss
	        
	        // Inserting Row
	        long result = db.insert(TABLE_WORDS, null, values);
	        db.close(); // Closing database connection
	        
	        db_size++;
	        
	        return result;
	    }
	     
	    // Getting single contact
	    public Cursor getEntry(String q) {
	    	SQLiteDatabase db = this.getReadableDatabase();
	        Cursor cursor = db.query(TABLE_WORDS, new String[] { KEY_ID, KEY_DB_ID,
	                KEY_WORD, KEY_IPA, KEY_GLOSS, KEY_POS, KEY_USAGE, KEY_DIALECT, KEY_META, KEY_AUTHORITY,
	                KEY_AUDIO, KEY_IMG, KEY_SEMANTIC, KEY_ESGLOSS}, KEY_GLOSS + "=?",
	                new  String[] { String.valueOf(q) }, null, null, null, null);
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
