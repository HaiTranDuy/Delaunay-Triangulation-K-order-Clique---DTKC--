package Clique;
import java.util.List;

public class Node {
    public boolean IsRoot;
    public int rank=0;
    
    public Point data; // dung de lay BNs, cung co nghia la point de lay Clique sau nay
    public Node parent;
    public int ChildCount=0;
    
    // Thuoc tinh cua IDS: Right Sibling
    public List<Node> R_sibling;
    
    public Node(){}
    public Node(Point data)
    {
        this.data = data;
        IsRoot = false;       
        ChildCount = 0;
    }
}
