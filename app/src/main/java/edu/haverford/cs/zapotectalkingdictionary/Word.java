package edu.haverford.cs.zapotectalkingdictionary;
 
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
	    public Word(int id, int db_id, String word, String ipa, String gloss, String pos, String usage_example, String dialect, String metadata, String authority, String audio, String img, String sem, String es_gloss){
	        this.id = id;
	        this.db_id = db_id;
	        this.word = (word != null) ? word : "";
	        this.gloss = (gloss != null) ? gloss : "";
	        this.ipa = (ipa != null) ? ipa : "";
	        this.pos = (pos != null) ? pos : "";
	        this.usage_example = (usage_example != null) ? usage_example : "";
	        this.dialect = (dialect != null) ? dialect : "";
	        this.metadata = (metadata != null) ? metadata : "";
	        this.authority = (authority != null) ? authority : "";
	        this.audio = (audio != null) ? audio : "";
	        this.image = (img != null) ? img : "";
	        this.semantic_ids = (sem != null) ? sem : "";
	        this.es_gloss = (es_gloss != null) ? es_gloss : "";
	    }
	
	    public int getID(){
	        return this.id;
	    }

	    public int getDB_ID(){
	        return this.db_id;
	    }

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

	    public void setWord(String w){
	        this.word = w;
	    }
}

