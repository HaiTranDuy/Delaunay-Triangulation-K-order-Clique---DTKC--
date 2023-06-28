package Main;
import static Main.Main.ANSI_BLACK;
import static Main.Main.ANSI_GREEN;
import static Main.Main.Get_MaxMemory;
import static Main.Main.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Joinless {
    private double min_prev=0;
    
    private Map<String, Set<Point>> F;
    private Map<String, Set<Point>> SN;
    private int k;
    private List<String> Ck;
    private Map<String,Set<List<Point>>> SI;
    private Map<String, Set<Point>> Table_SI;
    
    private Set<String> Clique_check;
    
    private List<String> P;// a set of size k prevalent colocations
    //private List<List<String>> P = new ArrayList(); // a set of size k prevalent colocations
    public Map<String, Double> R = new HashMap();
    
    public void join_less(List<Point> S, double r, double min_pre){
        this.min_prev = min_pre;
        
        SN = gen_star_neighborhoods(S, r);
        k = 1; 
        P = GanFeature();
        
        while(!P.isEmpty())
        {
            List<String> Prev_col = new ArrayList();
            
            SI = new HashMap();
            Ck = Gen_Candidate_Colocations(P); Set<String> clone_CK = new HashSet(); clone_CK.addAll(Ck); 
            for(String candidate : clone_CK)
            {
                Table_SI = new HashMap();
                for(int i=0; i< candidate.length(); i++)
                {
                    String Feature = ""+candidate.charAt(i);
                    Table_SI.put(Feature, new HashSet());
                }
                
                Filter_Star_Instances(candidate, SN.get(""+candidate.charAt(0)));
                if(k>1){
                    double pi = Cal_PI(Table_SI);
                    if(pi>= min_prev)
                    {
                        Prev_col.add(candidate);
                        if(!R.containsKey(candidate)) R.put(candidate, pi);
                    }
                }
            }
            if(k==1) {
                Clique_check = GetClique();
                Prev_col = Select_Prevalent_Colocations(SI);
            }
            P= Prev_col;
            k+=1;
            System.out.println(ANSI_GREEN+"Done Colocation P : "+ANSI_BLACK+k+" size P: "+P.size()); memory();
        }
    }
    
    public void Feature(List<Point> S){
        this.F = new HashMap();
        for(Point p : S)
        {
            if(!F.containsKey(p.Feature)) 
            {
                F.put(p.Feature, new HashSet());
            }
            F.get(p.Feature).add(p);
        }
    }
    
    //#<editor-fold desc="Neighborhood materialization">
    public Map<String, Set<Point>> gen_star_neighborhoods(List<Point> S, double min_dist) 
    {
        Map<String, Set<Point>> sn = new HashMap();
        
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
                        }
                    }
                }
                
            }
        }
        for(Point p : S)
        {
            p.SortNeighbor();
            if(!sn.containsKey(p.Feature)) sn.put(p.Feature, new HashSet());
            sn.get(p.Feature).add(p);
        }
        return sn;
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
    
    private List<String> GanFeature(){
        List<String> Features = new ArrayList();
        for(String f: F.keySet()) Features.add(f);
        Collections.sort(Features, new Comparator<String>(){
                public int compare(String a, String b)
                {
                    return a.compareTo(b);
                }
            });
       return Features;
    }
    
    private List<String> Gen_Candidate_Colocations(List<String> P){
        List<String> C = new ArrayList();
        for(int i=0; i<P.size()-1; i++)
        {
            for(int j=i+1; j<P.size(); j++)
            {
                String new_col = GenColocation(P.get(i), P.get(j));
                if(new_col.length()>0 && !C.contains(new_col)) C.add(new_col);
            }
        }
        return C;
    }
        private String GenColocation(String A, String B){
            String new_col = "";
            if(A.length()!= B.length()) return new_col;
            else if(A.length() ==1) return A+B;
            String same_A = A.substring(1);
            String same_B = B.substring(0,B.length()-1);
            
            String R_A = A.substring(0,A.length()-1);
            String R_B = B.substring(1);
            if(same_A.equals(same_B)) 
            {
                new_col = A+B.charAt(B.length()-1);
            }
            if(R_B.equals(R_A))
            {
                new_col = B+A.charAt(A.length()-1);
            }
            return new_col;
        }
    private void Filter_Star_Instances(String colocation, Set<Point> Point_f){
        Map<String, Set<Point>> F_table = new HashMap();
        Map<String, Set<Point>> small_table = new HashMap();
        
        if(colocation.length() == 2) {Case2(colocation, Point_f); return;}
        for(int i=0; i<colocation.length(); i++)
        {
            String F = ""+colocation.charAt(i);
            F_table.put(F, new HashSet());
            small_table.put(F, new HashSet());
        }
        for(Point p : Point_f)
        {
            F_table.get(p.Feature).add(p);
            for(Point n : p.SN)
            {
                if(F_table.containsKey(n.Feature)) F_table.get(n.Feature).add(n);
            }
        }
        if(Cal_PI(F_table) < min_prev) { Ck.remove(colocation); return;}
        
        for(Point p: Point_f)
        {
            for(String key :small_table.keySet())
            {
                small_table.put(key, new HashSet());
            }
            small_table.get(p.Feature).add(p);
            for(Point n : p.SN)
            {
                if(small_table.containsKey(n.Feature)) small_table.get(n.Feature).add(n);
            }
            List<Point> star_instance = new ArrayList();
            Get_Instance(small_table, star_instance, colocation);
        }
    }
        private void Case2(String colocation, Set<Point> Point_f){
            String F2 = ""+colocation.charAt(1);
            for(Point p : Point_f)
            {
                for(Point n : p.SN)
                {
                    if(n.Feature.equals(F2))
                    {
                        List<Point> new_star = new ArrayList();
                        new_star.add(p);
                        new_star.add(n);
                        if(!SI.containsKey(colocation)) SI.put(colocation, new HashSet());
                        SI.get(colocation).add(new_star);
                    }
                }
            }
            if(!SI.containsKey(colocation)) Ck.remove(colocation);
        }
        private void Get_Instance (Map<String, Set<Point>> F_table, List<Point> star_instance, String colocation){
            List<Point> final_star = new ArrayList(); final_star.addAll(star_instance);
            if(final_star.size() >=colocation.length()) {
                if(Check_Clique(final_star)) 
                {
                    AddTable(final_star);
                }
                final_star = null;
                Get_MaxMemory();
                return;
            }
            int vt = final_star.size();
            String Feature = ""+colocation.charAt(vt);
            for(Point p: F_table.get(Feature))
            {
                final_star.add(p);
                Get_Instance(F_table, final_star, colocation);
                final_star.remove(p);
            }
        }
        private double Cal_PI(Set<List<Point>> Star_instance, String currCandidate){
            double minPRs = 1;
            int count =0;
            Map<String, Set<Point>> Table = new HashMap();
            for(List<Point> star_instance : Star_instance)
            {
                for(Point p : star_instance)
                {
                    if(!Table.containsKey(p.Feature)) Table.put(p.Feature, new HashSet());
                    Table.get(p.Feature).add(p);
                }
            }
            
            for(int i=0; i<currCandidate.length(); i++)
            {
                String fea = ""+currCandidate.charAt(i);
                int count_f = 0;
                if(Table.containsKey(fea)) count_f=Table.get(fea).size();
                
                if(minPRs>= (double) count_f/(double)F.get(fea).size())
                {
                    count++;
                    minPRs = (double) count_f/(double)F.get(fea).size();
                }
            }
            if(count==0) return 0.0;
            return minPRs;
        }
        private double Cal_PI(Map<String, Set<Point>> F_table){
            double minPRs = 1;
            int count =0;
            
            for(String feature : F_table.keySet())
            {
                if(minPRs >= (double)F_table.get(feature).size()/(double)F.get(feature).size())
                {
                    count++;
                    minPRs = (double)F_table.get(feature).size()/(double)F.get(feature).size();
                }
            }
            
            if(count==0) return 0;
            return minPRs;
        }
        private void AddTable(List<Point> final_star)
        {
            for(Point p: final_star)
            {
                Table_SI.get(p.Feature).add(p);
            }
        }
    private Set<String> GetClique(){
        Set<String> clique = new HashSet();
        for(String key : SI.keySet())
        {
            for(List<Point> instance : SI.get(key))
            {
                String cl = "";
                for(Point p: instance)
                {
                    cl+=p.Feature+p.Instance;
                }
                clique.add(cl);
            }
        }
        return clique;
    }
        private boolean Check_Clique(List<Point> S_instance){
            if(S_instance.size()>2)
            {
                return Check_Clique(S_instance.subList(1, S_instance.size()));
            }
            else{
                String curren_instance="";
                for(Point p : S_instance)
                {
                    curren_instance+=p.Feature+p.Instance;
                }

                if(Clique_check.contains(curren_instance)) return true;
                else return false;
            }
        }
    private List<String> Select_Prevalent_Colocations(Map<String,Set<List<Point>>> ci){
        List<String> col = new ArrayList();
        for(String key : ci.keySet())
        {
            double pi = Cal_PI(ci.get(key), key);
            if(pi>=min_prev)
            {
                col.add(key);
                if(!R.containsKey(key)) R.put(key, pi);
            }
        }
        return col;
    }
}
