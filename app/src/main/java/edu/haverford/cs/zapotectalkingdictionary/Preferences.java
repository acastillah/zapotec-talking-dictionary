package edu.haverford.cs.zapotectalkingdictionary;

import java.util.Locale;

public class Preferences {
	public static final String APP_SETTINGS = "APP_SETTINGS";
	public static final String TERMS_ACCEPTED = "TERMS_ACCEPTED";
	public static final String DB_LOADED = "DB_LOADED";
	public static final String DB_SIZE = "DB_SIZE";
	public static final String LAST_DB_UPDATE = "LAST_UPDATE";
	
	public static final String DOWNLOAD_AUDIO = "DOWNLOAD_AUDIO";
	public static final String DOWNLOAD_PHOTOS = "DOWNLOAD_PHOTOS";
	
	public static final String IS_LINGUIST = "IS_LINGUIST";
	public static final String USERNAME = "USERNAME";
	public static final String LOGIN_STATUS_CHANGE = "LOGIN_STATUS_CHANGE";
	
	public static final String LANGUAGE = "LANGUAGE";
	public static final String ENGLISH = "en";
	public static final String SPANISH = "es";
	public static final String ZAPOTEC = "za";
	public static final String LANGUAGE_CHANGE = "LANGUAGE_CHANGE";
	
	public static final Locale ENGLISH_LOC = Locale.ENGLISH;
	public static final Locale SPANISH_LOC = new Locale(SPANISH);
	public static final Locale ZAPOTEC_LOC = new Locale(ZAPOTEC);
}
