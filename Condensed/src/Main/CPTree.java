package Main;
import java.util.Set;

public class CPTree {
    Node Root;
    
    public CPTree(){}
    
    public void Build_CP_Tree(Set<Point> E){
        Root = new Node();
        
        Node PreNode;
        for(Point p : E)
        {
            PreNode = FindPreNode(p.Feature,Root);
            for(String next_node : p.FN)
            {
                PreNode = FindPreNode(next_node, PreNode);
            }
        }
    }
        private Node FindPreNode(String feature, Node PreNode){
            if(PreNode.Children.containsKey(feature))
            {
                PreNode.Children.get(feature).Count+=1;
                return PreNode.Children.get(feature);
            }
            else
            {
                Node child = AddNode(feature, PreNode);
                return child;
            }
        }
        private Node AddNode( String Feature, Node Parent){
            Node child = new Node(Feature);
            child.Parent = Parent;
            Parent.Children.put(Feature, child);
            return child;
        }
    public void RemoveNode(Node currNode)
    {
        int remove_count = currNode.Count;
        while(currNode!= Root)
        {
            Node parent = currNode.Parent;
            if(currNode.Parent!= Root) currNode.Count -=remove_count;
            if(currNode.Count<=0)
            {
                //parent.Children.remove(currNode.Feature);
                currNode.Parent = null;
            }
            currNode = parent;
        }
    }
}
