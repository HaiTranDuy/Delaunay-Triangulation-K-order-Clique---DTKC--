package Clique;
import java.util.List;
import org.poly2tri.geometry.primitives.DPoint;

public class Node {
    public boolean IsRoot;
    public int rank=0;
    
    public DPoint data; // dung de lay BNs, cung co nghia la point de lay Clique sau nay
    public Node parent;
    public int ChildCount=0;
    
    // Thuoc tinh cua IDS: Right Sibling
    public List<Node> R_sibling;
    
    public Node(){}
    public Node(DPoint data)
    {
        this.data = data;
        IsRoot = false;       
        ChildCount = 0;
    }
}
