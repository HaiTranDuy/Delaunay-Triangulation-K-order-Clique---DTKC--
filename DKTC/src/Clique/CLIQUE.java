package Clique;

import static Demo.demo.total_cliques;
import static Demo.demo.total_Chash_key;

import static Main.Main.ANSI_BLACK;
import static Main.Main.ANSI_GREEN;
import static Main.Main.Get_MaxMemory;
import static Main.Main.memory;
import org.poly2tri.geometry.primitives.DPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class CLIQUE {

    private int k;
    public Node root;
    
    public Map<String, Integer> Features;
    public Map<String, Integer> CountFeature(List<DPoint> S) {
        Map<String, Integer> F = new HashMap();
        Map<String, List<DPoint>> CountF = new HashMap();
        
        for(DPoint p : S)
        {
            if(!CountF.containsKey(p.Feature))
            {
                CountF.put(p.Feature, new ArrayList());
            }
            if(!CountF.get(p.Feature).contains(p)) CountF.get(p.Feature).add(p);
        }
        for(String key: CountF.keySet())
        {
            if(!F.containsKey(key))
            {
                F.put(key, CountF.get(key).size());
            }
        }
        return F;
    }
    
    public void Clique_travel(List<DPoint> S, double min_prev ,int step, int type, int k) // step = so kiem nghiem den so buoc
    {
        this.k = k;
        long Cls = 0;
        Map<String, Map<String, List<DPoint>>> CHash;
        Map<String, Double> Colocation_pattern;
        
        switch (step) {
            case 0:
                Features = CountFeature(S);
                for(String key : Features.keySet())
                {
                    System.out.println(key+"    count: "+Features.get(key));
                }
                break;
            case 1:
                Features = CountFeature(S);
                for(DPoint p: S)
                {
                    System.out.print(p.BNs.size());
                    System.out.print("   -->");
                    p.Show();
                    System.out.print("<--   ");
                    for(DPoint sp : p.BNs)
                    {
                        sp.Show();System.out.print(" ");
                    }
                    System.out.println("");
                }   
                break;
            case 2:
                Features = CountFeature(S);
                if(type==0) {Cls = IDS(S); System.out.println("IDS");} // IDS
                else {Cls = DFS(S); System.out.println("DFS");} // IDS dung DFS
                break;
            case 3:
                Features = CountFeature(S);
                
                if(type==0) Cls = IDS(S); // IDS
                else Cls = DFS(S); // IDS dung DFS
                CHash = Chash;
//                List<String> Key = new ArrayList(); Key.addAll(CHash.keySet());
//                Collections.sort(Key, (String a, String b) -> {
//                    if(a.length() != b.length()) return b.length() - a.length();
//                    else
//                        return a.compareTo(b);
//                });
//                
//                for(String key: Key)
//                {
//                    System.out.println(key);
//                }   
                break;
            case 4:
                Features = CountFeature(S);
                
                if(type==0) Cls = IDS(S); // IDS
                else Cls = DFS(S); // IDS dung DFS
                System.out.println(ANSI_GREEN+"Done IDS: "+ANSI_BLACK+Cls+" clique"); memory();
                
                CHash = Chash;
                //CHash = Candidate_generation(Cls);
                System.out.println(ANSI_GREEN+"Done CHash: "+ANSI_BLACK+CHash.size()+" key"); memory();
                Colocation_pattern = Prevalent_Colocation_Filtering(CHash, min_prev);
                System.out.println(ANSI_GREEN+"Total Colocation Size: "+ANSI_BLACK+Colocation_pattern.size());
                
                System.out.println("");
                System.out.println("-----------------Test : PIs ------------------");
                for(String key : Colocation_pattern.keySet())
                {
                    System.out.print(key+"    ");
                    System.out.printf("%.8f", Colocation_pattern.get(key));
                    System.out.println("");
                }   
                break;
            default:
                break;
        }
    }
    
    //#<editor-fold desc="3.2 Clique generation">
    public long IDS(List<DPoint> S)
    {
        //List<List<Point>> Cls = new ArrayList();
        long count_cl = 0;
        
        ITree Itree = new ITree(); Itree.KhoiTao(); root = Itree.Root;
        
        for(DPoint s : S)
        {
            List<Node> queue = new ArrayList();
            Node HeadNode = Itree.AddHeadNode(s);
            queue.add(HeadNode);
            
            while(!queue.isEmpty())
            {
                Node currNode = queue.get(0);  queue.remove(0); 
                List<Node> childrenNodes = GetChildren(currNode);
                
                if(childrenNodes.isEmpty() || childrenNodes.size()<=0)
                {
                    List<DPoint> Clique = Itree.GetClique(currNode); if(Clique !=null) {
                        count_cl++;
//                        Cls.add(Clique);
                        AddCls(Clique);
                    }
                    Itree.RemoveAncestors(currNode);
                }
                else
                {
                    Itree.AddNodes(currNode, childrenNodes);
                    for(Node child : childrenNodes)
                    {
                        queue.add(child);
                    }
                }
            }
        }
        return count_cl;
    }
    public List<Node> GetChildren(Node currNode) // add children accroding lemma3
    {
        List<Node> childrenNodes = new ArrayList();
        
        if(currNode.parent == root || currNode.parent.IsRoot == true) // la headnode
        {
            for(DPoint p: currNode.data.BNs)
            {
                Node child = new Node(p);
                child.parent = currNode;
                childrenNodes.add(child);
            }
            int chiso=1;
            for(Node node: childrenNodes)
            {
                node.R_sibling = childrenNodes.subList(chiso, childrenNodes.size());
                chiso++;
            }
        }
        else
        {
            for(DPoint p: currNode.data.BNs)
            {
                if(CheckSiblingContaint(currNode.R_sibling,p))
                {
                    Node child = new Node(p);
                    child.parent = currNode;
                    childrenNodes.add(child);
                }
            }
            int chiso=1;
            for(Node node: childrenNodes)
            {
                node.R_sibling = childrenNodes.subList(chiso, childrenNodes.size());
                chiso++;
            }
        }
        return childrenNodes;
    }
    
    private boolean CheckSiblingContaint(List<Node> R_Sibling, DPoint p)
    {
        for(Node node: R_Sibling)
        {
            if(node.data.Feature.compareTo(p.Feature) > 0) return false;
            else if(node.data.Feature.compareTo(p.Feature) == 0 && node.data.Instance == p.Instance) return true;
        }
        return false;
    }
    
    private boolean CanClique;
    private Set<DPoint> CheckNewClique;
    public long DFS(List<DPoint> S) // DFS CAI TIEN IDS
    {
        long dem_cl = 0;
        ITree Itree = new ITree(); Itree.KhoiTao(); root = Itree.Root;
        for(DPoint p : S) p.Clear1();
        System.gc();
        Collections.sort(S, new Comparator<DPoint>(){
            public int compare(DPoint a, DPoint b)
            {
                if(a.Feature.equals(b.Feature))
                {
                    return a.Instance-b.Instance;
                }
                else
                {
                    return a.Feature.compareTo(b.Feature);
                }
            }
        });
        for(DPoint p : S)
        {
            Node HeadNode = Itree.AddHeadNode(p); 
            List<Node> stack = new ArrayList(); stack.add(HeadNode);
            CheckNewClique = new HashSet(); 
            
            while(!stack.isEmpty())
            {
                Node currNode = stack.get(stack.size()-1); stack.remove(stack.size()-1); 
                if(!CheckNewClique.contains(currNode.data) || currNode.rank <=k || k ==1) { CanClique = true; 
                    CheckNewClique.add(currNode.data);
                } // check neu gap node khong trong ChecknewClique thi mark la co the Clique va add node do vao list check
                List<Node> childrenNodes = GetChildren_DFS(currNode);
                
                if(childrenNodes.size()<=0)
                {
                    if(CanClique){
                        CanClique = false;
                        List<DPoint> Clique = Itree.GetClique(currNode); 
                        if(Clique !=null) {
                            dem_cl++;
                            AddCls(Clique);
                        } 
                    }
                    Itree.RemoveAncestors(currNode);
                }
                else
                {
                    Itree.AddNodes(currNode, childrenNodes);
                    for(int i = childrenNodes.size()-1; i>=0; i--)
                    {
                        stack.add(childrenNodes.get(i));
                    }
                    Get_MaxMemory();
                }
            }
            p.Clear2();
        }
        System.gc();
        CheckNewClique = null;
        return dem_cl;
    }
    public List<Node> GetChildren_DFS(Node currNode)
    {
        List<Node> childrenNodes = new ArrayList();
        
        if(currNode.parent == root || currNode.parent.IsRoot == true) // la headnode
        {
            if(currNode.data.ClearEdge) return childrenNodes;
            for(DPoint p: currNode.data.BNs)
            {
                Node child = new Node(p);
                child.parent = currNode;
                childrenNodes.add(child);
            }
            int chiso=1;
            for(Node node: childrenNodes)
            {
                node.R_sibling = childrenNodes.subList(chiso, childrenNodes.size());
                chiso++;
            }
        }
        else
        {
            //if(currNode.data.ClearEdge && !CanClique) return childrenNodes;
            for(DPoint p: currNode.data.BNs)
            {
                if(CheckSiblingContaint(currNode.R_sibling,p)) // hop
                {
                    Node child = new Node(p);
                    child.parent = currNode;
                    childrenNodes.add(child);
                    if(CanClique) CheckNewClique.remove(p);
                }
            }
            int chiso=1;
            for(Node node: childrenNodes)
            {
                node.R_sibling = childrenNodes.subList(chiso, childrenNodes.size());
                chiso++;
            }
            if(childrenNodes.size() == currNode.data.BNs.size()) {currNode.data.ClearEdge = true;}
        }
        return childrenNodes;
    }
    //#</editor-fold>
    //#<editor-fold desc="3.3 C-Hash">

    Map<String, Map<String, List<DPoint>>> Chash = new HashMap();
    public void AddCls(List<DPoint> Cl) {
        String newKey = GetFeature(Cl);
        if(!Chash.containsKey(newKey))
        {
            Chash.put(newKey, new HashMap());
        }
        for(int i=0; i<newKey.length(); i++)
        {
            String F = ""+newKey.charAt(i);
            if(!Chash.get(newKey).containsKey(F))
            {
                Chash.get(newKey).put(F, new ArrayList());
            }
            if(!Chash.get(newKey).get(F).contains(Cl.get(i))) Chash.get(newKey).get(F).add(Cl.get(i));
        }
        Get_MaxMemory();
    }
    public String GetFeature(List<DPoint> Cl) {
        String F="";
        for(DPoint p: Cl)
        {
            F+=p.Feature;
        }
        char[] charArray = F.toCharArray();
        Arrays.sort(charArray);
        F = new String(charArray);
        return F;
    }
    //#</editor-fold>
    //#<editor-fold desc="3.4 Prevalent co-locations filtering">
    public Map<String, Double> Prevalent_Colocation_Filtering(Map<String, Map<String, List<DPoint>>> CHash, double min_prev)
    {
        Map<String, Double> cs = new HashMap();
        List<String> candidates = new ArrayList();
        
        for(String key: CHash.keySet()){ 
            if(key.length()>=2){
                candidates.add(key);
            }
        }
        ColTree coltree = new ColTree(); coltree.Ini_Tree(Chash.keySet());
        System.out.println(ANSI_GREEN+"Done COl Tree. ");
        Collections.sort(candidates, new Comparator<String>() {
            public int compare(String a, String b)
                    {
                        if(b.length() == a.length())
                        {
                            return a.compareTo(b);
                        }
                        else return b.length()-a.length();
                    }
        });  // sort candidate with key giam dan
        while(!candidates.isEmpty())
        {
            String currCandidate = candidates.get(0);
            
            if(currCandidate.length()<2) {candidates.remove(0); continue;}
            
            double pi = CalculatePI(currCandidate, CHash, coltree);
            if(pi>= min_prev)
            {
                cs.put(currCandidate, pi);
            }
            candidates.remove(0);
            
            if(currCandidate.length()>2) GetDirectSub(currCandidate,candidates);
        }
        coltree = null;
        
        System.out.println(ANSI_GREEN+"Done Filter colocation: "); //memory();
        return cs;
    }
    
    public double CalculatePI(String currCandidate, Map<String, Map<String, List<DPoint>>> CHash, ColTree coltree)
    {
        double minPRs = 1;
        
        Map<String, Set<DPoint>> Inst = new HashMap();
        for(int i=0; i<currCandidate.length(); i++)
        {
            String F = ""+currCandidate.charAt(i);
            Inst.put(F, new HashSet());
        }
        List<String> supersets = coltree.GetSuperSet(currCandidate);
        for(String CDs: supersets)
        {
            Map<String, List<DPoint>> CD = CHash.get(CDs);
            for(int i=0; i<currCandidate.length(); i++)
            {
                String F = ""+currCandidate.charAt(i);
                for(DPoint p: CD.get(F))
                {
                    Inst.get(F).add(p);
                }
            }
        }
        
        int count =0;
        for(int i=0; i<currCandidate.length(); i++)
        {
            String F = ""+currCandidate.charAt(i);
            int count_f = Inst.get(F).size();
            if(minPRs>= (double) count_f/(double)Features.get(F))
            {
                count++;
                minPRs = (double) count_f/(double)Features.get(F);
            }
        }
        if(count==0) return 0;
        
        return minPRs;
    }
    
    public void GetAllSubsets(String currCandidate, Set<String> Set, Map<String, Double> cs) // ABCDE 
    {
        if(currCandidate.length() <= 2) return;
        
        for(int i=0; i<currCandidate.length(); i++)
        {
            String subset = currCandidate.substring(0, i) + currCandidate.substring(i+1,currCandidate.length());
            
            if(!Set.contains(subset) && !cs.containsKey(subset)) 
            {
                Set.add(subset);
                GetAllSubsets(subset, Set, cs);
            }
        }
    }
    
    public Map<String, Double> CalculatePIs(Set<String> subsets, Map<String, Map<String, List<DPoint>>> CHash, ColTree coltree)
    {
        Map<String, Double> PIs = new HashMap();
        for(String colocation: subsets)
        {
            double PI = CalculatePI(colocation, CHash, coltree);
            PIs.put(colocation, PI);
        }
        return PIs;
    }
    
    public void GetDirectSub(String currCandidate, List<String> candidates) {
        if(currCandidate.length() <= 2) return ;
        for(int i=0; i<currCandidate.length(); i++)
        {
            String subset = currCandidate.substring(0, i) + currCandidate.substring(i+1,currCandidate.length());
            if(!candidates.contains(subset) && subset.length() >=2) {
                candidates.add(FindPos(candidates, currCandidate),subset); 
            }
        }
    }
    
 
    private int FindPos(List<String> candidates, String currCandidate) {
        int st = 0, end = candidates.size()-1;
        while(st< end)
        {
            int mid = (end+st)/2;
            String candidate = candidates.get(mid);
            if(candidate.length() == currCandidate.length()) return mid;
            else if(candidate.length() > currCandidate.length())
            {
                st = mid+1;
            }
            else 
            {
                end = mid -1;
            }
        }
        return st;
    }
    //#</editor-fold>
    
    
    //#<editor-fold desc="Demo">
    public Map<String, Double> Clique_demo(List<DPoint> S, double min_prev, int k) // step = so kiem nghiem den so buoc
    {
        Map<String, Double> ans = new HashMap();
        this.k = k;
        Features = CountFeature(S);
        total_cliques = DFS(S);
        total_Chash_key = Chash.size();
        
        ans = Prevalent_Colocation_Filtering(Chash, min_prev);
        return ans;
    }
    //#</editor-fold>
}
