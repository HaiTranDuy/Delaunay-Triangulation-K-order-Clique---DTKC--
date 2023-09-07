package Clique;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ColTree {
    public class TreeNode{
        String Col;
        List<TreeNode> subsets;
        public TreeNode(){}
        public TreeNode(String Col){this.Col = Col; subsets = new ArrayList();}
        @Override
        public String toString() {
            return Col;
        }
    }
    
    TreeNode tail = new TreeNode();
    Set<TreeNode> NodeThrough;
    
    public void Ini_Tree(Set<String> keys) {
        List<String> KEYS = new ArrayList();
        Map<String, TreeNode> map = new HashMap();
        for(String key : keys)
        {
            if(key.length()>=2){
                KEYS.add(key);
                TreeNode node = new TreeNode(key);
                map.put(key, node);
            }
        }
        Collections.sort(KEYS, new Comparator<String>() {
            public int compare(String a, String b)
            {
                if(b.length() == a.length())
                {
                    return b.compareTo(a);
                }
                else return b.length()-a.length();
            }
        }); 
        tail.subsets = new ArrayList();
        for(String col : KEYS) {
            NodeThrough = new HashSet();
            AddNode(col, map);
        }
        map.clear(); map = null;
        KEYS.clear(); KEYS = null;
        NodeThrough.clear(); NodeThrough = null;
    }
        private void AddNode(String Col, Map<String, TreeNode> map) {
            boolean check = false;
            if(tail.subsets != null && tail.subsets.size() > 0){
            for(TreeNode node : tail.subsets)
            {
                if(CheckSubSet(Col, node.Col))
                {
                    NodeThrough.add(node);
                    AddOtherNode(node, Col, map);
                    check = true;
                }
            }
            }
            if(check == false)
            {
                tail.subsets.add(map.get(Col));
            }
        }
        private void AddOtherNode(TreeNode NODE, String Col, Map<String, TreeNode> map) {
            boolean check = false;
            if(NODE.subsets != null && NODE.subsets.size() > 0){
                for(TreeNode node : NODE.subsets)
                {
                    if(CheckSubSet(Col, node.Col))
                    {
                        if( !NodeThrough.contains(node))
                        {
                            NodeThrough.add(node);
                            AddOtherNode(node, Col, map);
                        }
                        check = true;
                    }
                }
            }
            if(check == false && !NODE.Col.equals(Col))
            {
                NODE.subsets.add(map.get(Col));
            }
        }
        private boolean CheckSubSet(String A, String B) { // check A is subset of B or not
            if(A.length()>B.length()) return false;
            int j = 0, dem=0;
            
            for(int i=0; i<A.length(); i++)
            {
                if(A.charAt(i)<B.charAt(j)) break;
                while(j<B.length())
                {
                    if(A.charAt(i) == B.charAt(j))
                    {
                        dem++;
                        j++;
                        break;
                    }
                    else if(A.charAt(i)>B.charAt(j))
                    {
                        j++;
                    }
                    else break;
                    if(dem==A.length()) break;
                }
                if(dem==A.length() || j>= B.length()) break;
            }
            
            if(dem==A.length()) return true;
            else return false;
        }
        
    public List<String> GetSuperSet(String col) {
        List<String> SuperSet = new ArrayList();
        
        List<TreeNode> candidates = new ArrayList();
        Set<String> NodeThrough = new HashSet();
        for(TreeNode node : tail.subsets) {candidates.add(node); NodeThrough.add(node.Col);} 
        int chiso = 0;
        while(chiso < candidates.size())
        {
            if(CheckSubSet(col, candidates.get(chiso).Col))
            {
                SuperSet.add(candidates.get(chiso).Col);
                for(TreeNode node: candidates.get(chiso).subsets)
                {
                    if(!NodeThrough.contains(node.Col)) {candidates.add(node); NodeThrough.add(node.Col);}
                }
            }
            chiso++;
        }
        return SuperSet;
    }
}

