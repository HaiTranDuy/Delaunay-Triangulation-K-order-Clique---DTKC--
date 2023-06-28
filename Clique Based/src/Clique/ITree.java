package Clique;

import java.util.ArrayList;
import java.util.List;

public class ITree {
    public Node Root;
    public Node headNode;
    
    public void ITree()
    {
        Root.IsRoot = true; 
    }
    public void KhoiTao()
    {
        Root = new Node(new Point("Root",1,1,1));
        Root.IsRoot = true; 
        Root.ChildCount = 0;
    }
    
    public Node AddHeadNode(Point s)
    {
        headNode = new Node(s);
        
        headNode.parent = Root;
        Root.ChildCount++;
        
        return headNode;
    }
    
    public List<Point> GetClique(Node currNode)
    {
        List<Point> clique = new ArrayList();
        if(currNode.parent.IsRoot) return null;
        
        while(currNode.IsRoot == false)
        {
            clique.add(0, currNode.data);
            currNode = currNode.parent;
        }
        return clique;
    }
    
    public void RemoveAncestors(Node currNode)
    {
        if(currNode.IsRoot) return;
        Node Parrent = currNode.parent;
        Parrent.ChildCount--;
        
        currNode.parent = null;
        currNode = null;
        
        if(Parrent.ChildCount >0) return; 
        else RemoveAncestors(Parrent);
        
    }
    public void AddNodes(Node currNode, List<Node> ChildrenNodes)
    {
        currNode.ChildCount = ChildrenNodes.size();
        for(Node child: ChildrenNodes)
        {
            child.parent = currNode;
        }
    }
}