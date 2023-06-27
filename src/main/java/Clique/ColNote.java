package Clique;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ColNote {
    public String F;
    
    public Map<String, ColNote> NextF;
    public Set<String> SuperSet;
    
    public ColNote (){SuperSet = new HashSet();NextF = new HashMap();}
    public ColNote(String F){
        this.F = F; 
        SuperSet = new HashSet();
        NextF = new HashMap();
    }
}
