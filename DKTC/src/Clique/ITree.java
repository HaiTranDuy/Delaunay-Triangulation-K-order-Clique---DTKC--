package Clique;
import org.poly2tri.geometry.primitives.DPoint;

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
        Root = new Node(new DPoint("Root",1,1.0,1.0));
        Root.IsRoot = true; 
        Root.ChildCount = 0;
    }
    
    public Node AddHeadNode(DPoint s)
    {
        headNode = new Node(s);
        
        headNode.parent = Root;
        Root.ChildCount++;
        headNode.rank =1;
        
        return headNode;
    }
    
    public List<DPoint> GetClique(Node currNode)
    {
        List<DPoint> clique = new ArrayList();
        if(currNode.parent.IsRoot) return clique;
        
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
            child.rank = child.parent.rank+1;
        }
    }
}
