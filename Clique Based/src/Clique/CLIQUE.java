package Clique;

import static Clique.Main.ANSI_BLACK;
import static Clique.Main.ANSI_GREEN;
import static Clique.Main.Get_MaxMemory;
import static Clique.Main.Step;
import static Clique.Main.loaiIDS;
import static Clique.Main.memory;

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
    public Node root;
    
    public Map<String, Integer> Features;
    public Map<String, Integer> CountFeature(List<Point> S)
    {
        Map<String, Integer> F = new HashMap();
        Map<String, List<Point>> CountF = new HashMap();
        
        for(Point p : S)
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
    public void Clique_travel(double min_dist, List<Point> S, double min_prev ,int step, int type) // step = so kiem nghiem den so buoc
    {
        long Cls = 0;
        Map<String, Map<String, List<Point>>> CHash;
        Map<String, Double> Colocation_pattern;
        
        switch (step) {
            case 0:
                Features = CountFeature(S);
                for(String key : Features.keySet())
                {
                    System.out.println(key+"    count: "+Features.get(key));
                }   break;
            case 1:
                Features = CountFeature(S);
                Neighborhood_Materialization(min_dist, S);
                long average_neighrbor=0; int count = 1;
                for(Point p: S)
                {
                    average_neighrbor += p.BNs.size(); if(p.BNs.size()>0) count++;
                    
                    //for(Point sp : p.SNs) {sp.Show();System.out.print(" ");}
                    System.out.print(p.BNs.size());
                    System.out.print("   -->");
                    p.Show();
                    System.out.print("<--   ");
                    int dem = 0;
                    for(Point sp : p.BNs)
                    {
                        if(dem>50) break;
                        dem++;
                        sp.Show();System.out.print(" ");
                    }
                    System.out.println("");
                } 
                System.out.println("Average neighbor: " + average_neighrbor/count);
                break;
            case 2:
                Features = CountFeature(S);
                Neighborhood_Materialization(min_dist, S);
                Cls = IDS(S); // IDS
                break;
            case 3:
                Features = CountFeature(S);
                Neighborhood_Materialization(min_dist, S);
                
                Cls = IDS(S); // IDS
                CHash = Chash;
                
                List<String> Key = new ArrayList(); Key.addAll(CHash.keySet());
                Collections.sort(Key, (String a, String b) -> {
                    if(a.length() != b.length()) return b.length() - a.length();
                    else
                        return a.compareTo(b);
                });
                
                for(String key: Key)
                {
                    System.out.println(key);
                }   break;
            case 4:
                Features = CountFeature(S);
                Neighborhood_Materialization(min_dist, S);
                int coun=0;
                for(Point p: S)
                {
                    coun+=p.BNs.size();
                }
                coun = coun/S.size();
                System.out.println(ANSI_GREEN+"Done Neighbor: "+ANSI_BLACK+coun+" Average neighbor"); memory();
                
                Cls = IDS(S); // IDS
                System.out.println(ANSI_GREEN+"Done IDS: "+ANSI_BLACK+Cls+" clique"); memory();
                
                CHash = Chash;
                System.out.println(ANSI_GREEN+"Done CHash: "+ANSI_BLACK+CHash.size()+" key"); memory();
                
                Colocation_pattern = Prevalent_Colocation_Filtering(CHash, min_prev);
                System.out.println(ANSI_GREEN+"Done Filter colocation: "); memory();
                
                System.out.println("");
                System.out.println("min_prev: "+min_prev+" Step: "+ Step+" IDS: "+loaiIDS);
                System.out.println("-----------------Test : PIs ------------------");
                
                System.out.println(ANSI_GREEN+"Total Colocation Size: "+ANSI_BLACK+Colocation_pattern.size());
                for(String key : Colocation_pattern.keySet())
                {
                    System.out.println(key+"    "+Colocation_pattern.get(key));
                }   
                break;
           
            default:
                break;
        }
    }
    
    //#<editor-fold desc="3.1 Neighborhood materialization">
    public void Neighborhood_Materialization(double min_dist, List<Point> S) 
    {
        List<List<Grid>> Grids = DivideSpace(min_dist, S);
        
        for(int i=0; i<Grids.size(); i++) 
        {
            for(int j=0; j<Grids.get(i).size(); j++)
            {
                Grid g = Grids.get(i).get(j); // Travers each grid
                List<Point> ngrids = GetNeighborGrids(g, Grids);
                
                for(Point s_1: g.Instances)
                {
                    for(Point s_2 : ngrids)
                    {
                        if(Is_Neighbor(s_1, s_2, min_dist))
                        {
                            String  name1 = s_1.Feature+s_1.Instance,
                                    name2 = s_2.Feature+s_2.Instance;
                            
                            if(name1.compareTo(name2)<0)
                            {
                                if(!s_1.BNs.contains(s_2)) s_1.BNs.add(s_2);
                                //if(!s_2.SNs.contains(s_1)) s_2.SNs.add(s_1);
                            }
                            else if(name1.compareTo(name2)>0)
                            {
                                if(!s_2.BNs.contains(s_1)) s_2.BNs.add(s_1);
                                //if(!s_1.SNs.contains(s_2)) s_1.SNs.add(s_2);
                            }
                        }
                    }
                }
                
            }
        }
        for(Point p : S)
        {
            p.SortNeighbor();
        }
    }
    public boolean Is_Neighbor(Point s_1, Point s_2, double min_dist)
    {
        String name1 = s_1.Feature+s_1.Instance,
                name2 = s_2.Feature+s_2.Instance;
        
        if(!name1.equals(name2) && !s_1.Feature.equals(s_2.Feature))
        {
            if(s_1.Length(s_2)<= min_dist)
            {
                return true;
            }
            else return false;
        }
        else {
            return false;
        }
    }
    public List<Point> GetNeighborGrids(Grid g, List<List<Grid>> Grids)
    {
        List<Point> ans = new ArrayList(); 
        
        for(int i=g.CorX-1; i<=g.CorX+1; i++)
        {
            for(int j=g.CorY-1; j<=g.CorY+1; j++)
            {
                if(i>=0 && i<Grids.size() && j >=0 && j<Grids.get(0).size())
                {
                    Grid gird = Grids.get(i).get(j);
                    ans.addAll(gird.Instances);
                }
            }
        }
        return ans;
    }
    public List<List<Grid>> DivideSpace(double min_dist, List<Point> S)
    {
        List<List<Grid>> Grids = new ArrayList();
        
        double Min_X = 500000,
                Min_Y = 500000,
                Max_X = -1,
                Max_Y = -1; // khong gian A
        
        for(Point p : S)  
        {
            if(p.CorX > Max_X) Max_X = p.CorX;
            if(p.CorX < Min_X) Min_X = p.CorX;
            if(p.CorY > Max_Y) Max_Y = p.CorY;
            if(p.CorY < Min_Y) Min_Y = p.CorY;
        }
        
        if(Min_X > 0) {Min_X = 0;} 
        if(Min_Y > 0) {Min_Y = 0;} 
        for(Point p: S) // Chinh cac diem ve truc toa do duong
        {
            p.CorX += Min_X;
            p.CorY += Min_Y;
        }
        
        int XGrid = (int)((Max_X-Min_X)/min_dist)+1, // Xgrid = so grid ngang; Ygrid = so grid doc
            YGrid = (int)((Max_Y-Min_Y)/min_dist)+1;
        
        for(int i=0; i<XGrid; i++)
        {
            Grids.add(new ArrayList());
            List<Grid> gridY = Grids.get(i);
            
            for(int j=0; j<YGrid; j++)
            {
                gridY.add(new Grid(i, j));
            }
        }
        
        for(Point p: S)
        {
            int     chisoXGrid = (int) (p.CorX/min_dist),
                    chisoYGrid = (int) (p.CorY/min_dist);
            Grids.get(chisoXGrid).get(chisoYGrid).Add(p);
        }
        return Grids;
    }
    //#</editor-fold>
    //#<editor-fold desc="3.2 Clique generation">
    public long IDS(List<Point> S)
    {
        //List<List<Point>> Cls = new ArrayList();
        long count_cl = 0;
        
        ITree Itree = new ITree(); Itree.KhoiTao(); root = Itree.Root;
        
        for(Point s : S)
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
                    List<Point> Clique = Itree.GetClique(currNode); if(Clique !=null) {
                        count_cl++;
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
            Get_MaxMemory();
        }
        return count_cl;
    }
    public List<Node> GetChildren(Node currNode) // add children accroding lemma3
    {
        List<Node> childrenNodes = new ArrayList();
        
        if(currNode.parent == root || currNode.parent.IsRoot == true) // la headnode
        {
            for(Point p: currNode.data.BNs)
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
            for(Point p: currNode.data.BNs)
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
        Get_MaxMemory();
        return childrenNodes;
    }
    
    private boolean CheckSiblingContaint(List<Node> R_Sibling, Point p)
    {
        for(Node node: R_Sibling)
        {
            if(node.data.Feature.compareTo(p.Feature) > 0) return false;
            else if(node.data.Feature.compareTo(p.Feature) == 0 && node.data.Instance == p.Instance) return true;
        }
        return false;
    }
    //#</editor-fold>
    //#<editor-fold desc="3.3">
    Map<String, Map<String, List<Point>>> Chash = new HashMap();
    public void AddCls(List<Point> Cl)
    {
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
    
    public String GetFeature(List<Point> Cl)
    {
        String F="";
        for(Point p: Cl)
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
    public Map<String, Double> Prevalent_Colocation_Filtering(Map<String, Map<String, List<Point>>> CHash, double min_prev)
    {
        Map<String, Double> cs = new HashMap();
        List<String> Chash_key = new ArrayList();
        List<String> candidates = new ArrayList(); 
        for(String key: CHash.keySet()){ candidates.add(key); Chash_key.add(key);}
        
        Collections.sort(candidates, new Comparator<String>()
        {
            public int compare(String a, String b)
                    {
                        if(b.length() == a.length())
                        {
                            return a.compareTo(b);
                        }
                        else return b.length()-a.length();
                    }
        });  // sort candidate with key giam dan
        Collections.sort(Chash_key, new Comparator<String>()
        {
            public int compare(String a, String b)
                    {
                        if(b.length() == a.length())
                        {
                            return a.compareTo(b);
                        }
                        else return b.length()-a.length();
                    }
        });
//        for(String ck: Chash_key)
//        {
//            System.out.println(ck);
//        }
        
        while(!candidates.isEmpty())
        {
            String currCandidate = candidates.get(0);
            //System.out.println(currCandidate+"   "+candidates.size()+"   "+currCandidate.length());
            
            double pi = CalculatePI(currCandidate, CHash, Chash_key);
            if(pi>= min_prev)
            {
                candidates.remove(currCandidate);
                cs.put(currCandidate, pi);
                
                Set<String> subsets = new HashSet(); GetAllSubsets(currCandidate, subsets, cs);
                Map<String, Double> PIs = CalculatePIs(subsets, CHash, Chash_key);
                cs.putAll(PIs);
                for(String subset: subsets)
                {
                    if(candidates.contains(subset))
                    {
                        candidates.remove(subset);
                    }
                }
            }
            else 
            {
                candidates.remove(currCandidate);
                GetDirectSub(currCandidate, cs,candidates);
                
                Collections.sort(candidates, new Comparator<String>() //* cai tien add with sort o day
                {
                    public int compare(String a, String b)
                    {
                        if(b.length() == a.length())
                        {
                            return a.compareTo(b);
                        }
                        else return b.length()-a.length();
                    }
                });
            }
        }
        
        return cs;
    }
    
    public double CalculatePI(String currCandidate, Map<String, Map<String, List<Point>>> CHash, List<String> Chash_key)
    {
        double minPRs = 1;
        
        Map<String, Set<Point>> Inst = new HashMap();
        for(int i=0; i<currCandidate.length(); i++)
        {
            String F = ""+currCandidate.charAt(i);
            Inst.put(F, new HashSet());
        }
        
        List<String> supersets = GetSuperSets(CHash, currCandidate, Chash_key);
        for(String CDs: supersets)
        {
            Map<String, List<Point>> CD = CHash.get(CDs);
            for(int i=0; i<currCandidate.length(); i++)
            {
                String F = ""+currCandidate.charAt(i);
                for(Point p: CD.get(F))
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
    public List<String> GetSuperSets(Map<String, Map<String, List<Point>>> CHash,String currCandidate, List<String> Chash_key)
    {
        int start = 0, end = Chash_key.size()-1;
        while(true)
        {
            int mid = (end+start)/2;
            
            String key_first = Chash_key.get(mid);
            String key_after = Chash_key.get(mid+1);
            
            if(key_first.length() == key_after.length())
            {
                if(key_first.length()>currCandidate.length()) start = mid+1;
                else end = mid;
            }
            else
            {
                if(key_after.length() <= currCandidate.length() && key_first.length() > currCandidate.length()){ start = mid; break;}
                if(key_first.length() <= currCandidate.length()) { end = mid;}
                else
                {
                    start = mid+1;
                }
            }
            if(start == end || mid == start) break;
        }// dau ra la start
        //System.out.println("Curr: "+ currCandidate.length()+"key: "+key_first.length()+" key_after: "+key_after.length());
        
        List<String> supersets = new ArrayList();
        for(int i=0; i<=start; i++)
        {
            String key = Chash_key.get(i);
            if(IsSuperSet(currCandidate, key))
            {
                supersets.add(key);
            } 
        }
        if(Chash_key.contains(currCandidate)) supersets.add(currCandidate);

        return supersets;
    }
    public boolean IsSuperSet(String a, String b) // check if b is superset of a
    {
        int length_a = a.length();
        int count=0;
        if(b.length()< length_a)
        {
            return false;
        }
        for(int i=0; i<a.length(); i++)
        {
            String check=""+a.charAt(i);
            if(b.contains(check)) count++;
        }
        if(count == length_a) return true;
        else return false;
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
    
    public Map<String, Double> CalculatePIs(Set<String> subsets, Map<String, Map<String, List<Point>>> CHash, List<String> Chash_key)
    {
        Map<String, Double> PIs = new HashMap();
        for(String colocation: subsets)
        {
            double PI = CalculatePI(colocation, CHash, Chash_key);
            PIs.put(colocation, PI);
        }
        return PIs;
    }
    
    public void GetDirectSub(String currCandidate, Map<String, Double> cs, List<String> candidates) 
    {
        if(currCandidate.length() <= 2) return ;
        for(int i=0; i<currCandidate.length(); i++)
        {
            String subset = currCandidate.substring(0, i) + currCandidate.substring(i+1,currCandidate.length());
            if(!cs.containsKey(subset) && !candidates.contains(subset)) candidates.add(subset);
        }
    }
    //#</editor-fold>
}
