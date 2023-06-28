package Main;

import static Main.Main.ANSI_BLACK;
import static Main.Main.ANSI_GREEN;
import static Main.Main.SortString;
import static Main.Main.memory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.Comparator;


public class Condensed {
    private double min_prev;
    
    public Map<String, Set<Point>> F; // for caculate PI
    private CPTree CpTree;
    private Map<String, Integer> SC;
    private Map<String, Double> C;
    
    private Map<String, Integer> CountCandidate;
    private List<String> SetClique;
    
    public void Condensed(List<Point> S, double min_dist, double min_pre)
    {
        this.min_prev = min_pre;
        F = Neighborhood_Preprocess(S, min_dist);
        System.out.println(ANSI_GREEN+"Done Neighborhood Preprocess P: "); memory();
        
        Candidate_Generation(S, min_pre);
        System.out.println(ANSI_GREEN+"Done Candidate Generation P: "); memory();
            
        Clique_Candidate();
        System.out.println(ANSI_GREEN+"Done Clique Candidate P: "); memory();
        
        Colocation_Search();
        System.out.println(ANSI_GREEN+"Done Colocation Search P: "); memory();
    }
    
    //#<editor-fold desc="Neighborhood materialization">
    public Map<String, Set<Point>> Neighborhood_Preprocess(List<Point> S, double min_dist) 
    {
        Map<String, Set<Point>> E = new HashMap();
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
                                if(!s_1.SN.contains(s_2)) s_1.SN.add(s_2);
                            }
                            if(!s_1.FN.contains(s_2.Feature)) s_1.FN.add(s_2.Feature);
                        }
                    }
                }
            }
        }
        for(Point p : S)
        {
            p.SortNeighbor();
            if(!E.containsKey(p.Feature)) E.put(p.Feature, new HashSet());
            E.get(p.Feature).add(p);
        }
        return E;
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
    
    private void Candidate_Generation(List<Point> S, double min_pre){
        C = new HashMap();
        for(String Feature: F.keySet())
        {
            SC = new HashMap();
            
            CpTree = new CPTree();
            CpTree.Build_CP_Tree(F.get(Feature));
            
            Gen_Star_Candidate(Feature);
            
            for(String sc : SC.keySet())
            {
                double pi = starPR(sc, CpTree.Root.Children.get(Feature).Count);
                if(pi>=min_pre) C.put(sc, pi);
            }
        }
        SC = null; CpTree =null;
    }
        private void Gen_Star_Candidate(String Feature){
            Node currNode = CpTree.Root.Children.get(Feature);
            String F_candidate = "";
            Travel2(currNode, F_candidate);
        }
        private void Travel2(Node currNode, String candidate){
            String Candidate = candidate+currNode.Feature;
            if(currNode.Children.isEmpty()) // is leaf node
            {
                FindCandidates(Candidate, currNode.Count);
                CpTree.RemoveNode(currNode);
                return;
            }
            for(String child : currNode.Children.keySet())
            {
                Travel2(currNode.Children.get(child), Candidate);
            }
            if(currNode.Count>0)
            {
                FindCandidates(Candidate, currNode.Count);
                CpTree.RemoveNode(currNode);
            }
        }
        private void FindCandidates(String Candidate, int count){
            String Core_Feature = ""+Candidate.charAt(0);
            String sub_col = Candidate.substring(1);
            
            int total =(int)Math.pow(2, sub_col.length());
            for(int i=1; i<total; i++)
            {
                String ConvertBinary = Integer.toBinaryString(i);
                int them = sub_col.length()-ConvertBinary.length();
                for(int j=0; j<them; j++)
                {
                    ConvertBinary = "0"+ConvertBinary;
                }
                String Col = "";
                for(int j=0; j<sub_col.length(); j++)
                {
                    if(ConvertBinary.charAt(j)=='1')
                    {
                        Col += sub_col.charAt(j);
                    }
                }
                Col = Core_Feature+Col;
                
                if(!SC.containsKey(Col)) SC.put(Col, count);
                else {
                    int dem = SC.get(Col)+count;
                    SC.put(Col, dem);
                }
            }
        }
        private double starPR(String sc, int root_count){
            double PI = 1;
            int count = 0;
            
            int tuso = SC.get(sc);
            for(int i=0; i<sc.length(); i++)
            {
                double Fea_pi = (double) tuso/root_count;
                if(PI>= Fea_pi)
                {
                    count++;
                    PI = Fea_pi;
                }
            }
            if(count == 0 ) return 0;
            return PI;
        }
        
    private void Clique_Candidate(){
        CountCandidate = new HashMap();
        Set<String> currCandidate;
        for(String candidate : C.keySet())
        {
            currCandidate = new HashSet();
            for(int i=0; i<candidate.length(); i++)
            {
                String feature = ""+candidate.charAt(i);
                currCandidate.add(feature);
            }
            String Clique_name="";
            for(String feature : currCandidate)
            {
                Clique_name+= feature;
            }
            Clique_name = SortString(Clique_name);
            if(!CountCandidate.containsKey(Clique_name)) CountCandidate.put(Clique_name, 0);
            CountCandidate.put(Clique_name, CountCandidate.get(Clique_name)+1);
        }
        SetClique = new ArrayList();
        for(String clique: CountCandidate.keySet())
        {
            if(clique.length() == CountCandidate.get(clique)) 
            {
                SetClique.add(clique);
            }
        }
        CountCandidate = null;
        Collections.sort(SetClique, new Comparator<String>(){
            public int compare(String a, String b)
            {
                if(a.length() == b.length())
                {
                    return a.compareTo(b);
                }
                else return a.length()-b.length();
            }
        });
    }
    
    private void Colocation_Search(){
        EnumerationTree ETree = new EnumerationTree(F);
        for(String key: F.keySet())
        {
            ETree.AddNode(key, min_prev);
        }
        for(String clique : SetClique)
        {
            ETree.AddNode(clique, min_prev);
        }
        ETree.ShowColocation();
    }
}
