package com.example.valleyzapotectalkingdictionary;
 
public class Word {
     
    //private variables
    private int id;
    private int db_id;
    private String word;
    private String gloss;
    private String ipa;
    private String pos;
    private String usage_example;
    private String dialect;
    private String metadata;
    private String authority;
    private String audio;
    private String image;
    private String semantic_ids;
    private String es_gloss;
        
    // Empty constructor
    public Word(){
         
    }
    // constructor
    public Word(int id, int db_id, String w, String ipa, String g, String p, String usage, String dialect, String metadata, String authority, String audio, String img, String sem, String esgloss){
        this.id = id;
        this.db_id = db_id;
        this.word = w;
        this.gloss = g;
        this.ipa = ipa;
        this.pos = p;
        this.usage_example = usage;
        this.dialect = dialect;
        this.metadata = metadata;
        this.authority = authority;
        this.audio = audio;
        this.image = img;
        this.semantic_ids = sem;
        this.es_gloss = esgloss;
    }

    // getting ID
    public int getID(){
        return this.id;
    }
     
    // setting id
    public void setID(int id){
        this.id = id;
    }
    
 // getting ID
    public int getDB_ID(){
        return this.db_id;
    }
     
    // setting id
    public void setDB_ID(int db_id){
        this.db_id = db_id;
    }
     
    // getting name
    public String getName(){
        return this.word;
    }
     
    public String getGloss(){
        return this.gloss;
    }
    
    public String getIPA(){
        return this.ipa;
    }

    public String getPos(){
        return this.pos;
    }    
    
    public String getUsage(){
        return this.usage_example;
    }
    public String getDialect(){
        return this.dialect;
    }
    
    public String getMetadata(){
        return this.metadata;
    }
    
    public String getAuthority(){
        return this.authority;
    }

    public String getAudio(){
        return this.audio;
    }
    
    public String getIMG(){
        return this.image;
    }
    
    public String getSemantic(){
        return this.semantic_ids;
    }
    public String getEsGloss(){
        return this.es_gloss;
    }

    
    // setting name
    public void setWord(String w){
        this.word = w;
    }

}

