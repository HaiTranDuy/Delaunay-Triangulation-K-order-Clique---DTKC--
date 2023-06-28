package Main;
import java.util.HashMap;
import java.util.Map;

public class Node {
    public boolean isRoot = false;
    
    public String Feature="";
    public int Count;
    
    public Node Parent = null;
    public Map<String, Node> Children = new HashMap();
    
    public Node(){isRoot = true;}
    public Node(String Feature){
        this.Feature = Feature;
        this.Count = 1;
    }
}
