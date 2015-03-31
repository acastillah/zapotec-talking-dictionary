package com.example.valleyzapotectalkingdictionary;
 
public class Word {
     
    //private variables
    int id;
    String word;
    String gloss;
    String ipa;
    String pos;
    String usage_example;
    String dialect;
    String metadata;
    String authority;
    String audio;
    String image;
    String semantic_ids;
    String es_gloss;
        
    // Empty constructor
    public Word(){
         
    }
    // constructor
    public Word(int id, String w, String ipa, String g, String p, String usage, String dialect, String metadata, String authority, String audio, String img, String sem, String esgloss){
        this.id = id;
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

