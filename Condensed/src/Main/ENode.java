package Main;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ENode {
    public boolean IsRoot = false;
    public String Feature;
    public double PI = 0;
    public Set<List<Point>> Instance;
    
    public ENode Parent;
    public Map<String, ENode> Children = new HashMap();
    
    public ENode(){}
    public ENode(String Feature)
    {
        this.Feature = Feature;
    }
    
}
