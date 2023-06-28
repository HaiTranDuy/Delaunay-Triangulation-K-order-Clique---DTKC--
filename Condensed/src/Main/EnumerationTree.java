package Main;

import static Main.Main.Get_MaxMemory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnumerationTree {
    public ENode Root;
    private Map<String, Set<Point>> F; // for caculate PI
    private Map<String, Set<Point>> Table_SI;
    public Map<String, Double> R = new HashMap();
    
    public EnumerationTree(){}
    public EnumerationTree(Map<String, Set<Point>> F){this.F = F; Root = new ENode(); Root.IsRoot = true;}
    
    private Set<String> Clique_check = new HashSet();
    
    public void AddNode(String Col, double min_prev) // col = ABCD , 
    {
        if(Col.length() == 1)
        {
            ENode new_child = new ENode(Col);
            Root.Children.put(Col, new_child);
        }
        else if(Col.length() == 2)
        {
            ENode currNode = Root;
            for(int i=0; i<Col.length()-1; i++)
            {
                String Feature = ""+Col.charAt(i);
                currNode = currNode.Children.get(Feature);
            }
            
            Set<List<Point>> Instances = GetInstance2(F.get(""+Col.charAt(0)) ,Col);
            double pi = CalPI(Instances, Col);
            if(pi >= min_prev)
            {
                if(!R.containsKey(Col)) R.put(Col, pi);
                GetClique(Instances);
                
                ENode new_child = new ENode(""+Col.charAt(1));
                new_child.Instance = Instances;
                new_child.PI = pi;
                new_child.Parent = currNode;
                
                currNode.Children.put(""+Col.charAt(1), new_child);
            }
        }
        else
        {
            Table_SI = new HashMap();
            for(int i=0; i<Col.length(); i++)
            {
                String Feature = ""+Col.charAt(i);
                Table_SI.put(Feature, new HashSet());
            }
            
            ENode currNode = Root;
            for(int i=0; i<Col.length()-1; i++)
            {
                String Feature = ""+Col.charAt(i);
                if(currNode.Children.containsKey(Feature)) currNode = currNode.Children.get(Feature);
                else return; // don't have pre colocation
            }
            
            GetInstanceS(F.get(""+Col.charAt(0)) ,Col);
            double pi = Cal_PI(Table_SI);
            if(pi >= min_prev)
            {
                if(!R.containsKey(Col)) {R.put(Col, pi);
                }
                
                ENode new_child = new ENode(""+Col.charAt(Col.length()-1));
                new_child.PI = pi;
                new_child.Parent = currNode;
                
                currNode.Children.put(""+Col.charAt(Col.length()-1), new_child);
            }
        }
    }
        private Set<List<Point>> GetInstance2(Set<Point> SetF, String Col){
            Set<List<Point>> Instances = new HashSet();
            
            for(Point star : SetF)
            {
                for(Point nei: star.SN)
                {
                    if(nei.Feature.equals(""+Col.charAt(1)))
                    {
                        List<Point> instance = new ArrayList();
                        instance.add(star);
                        instance.add(nei);
                        Instances.add(instance);
                    }
                }
            }
            return Instances;
        }
        private double CalPI(Set<List<Point>> Instances, String Col){
            double minPRs=1;
            Map<String, Set<Point>> Table = new HashMap();
            for(List<Point> instance: Instances)
            {
                for(Point p : instance)
                {
                    if(!Table.containsKey(p.Feature)) Table.put(p.Feature, new HashSet());
                    Table.get(p.Feature).add(p);
                }
            }
            
            int count =0;
            for(int i=0; i<Col.length(); i++)
            {
                String fea = ""+Col.charAt(i);
                int count_f = 0;
                if(Table.containsKey(fea)) count_f=Table.get(fea).size();
                
                if(minPRs>= (double) count_f/(double)F.get(fea).size())
                {
                    count++;
                    minPRs = (double) count_f/(double)F.get(fea).size();
                }
            }
            
            if(count==0) return 0;
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
        private void GetInstanceS(Set<Point> SetF, String Col){
            Map<String, Set<Point>> F_table = new HashMap();
            for(int i=0; i<Col.length(); i++)
            {
                String Feature = ""+Col.charAt(i);
                F_table.put(Feature, new HashSet());
            }
            for(Point p: SetF)
            {
                for(String key :F_table.keySet())
                {
                    F_table.put(key, new HashSet());
                }
                F_table.get(p.Feature).add(p);
                for(Point n : p.SN)
                {
                    if(F_table.containsKey(n.Feature)) F_table.get(n.Feature).add(n);
                }
                List<Point> star_instance = new ArrayList();
                Get_Instance(F_table, star_instance, Col);
            }
        }
        private void Get_Instance (Map<String, Set<Point>> F_table, List<Point> star_instance, String colocation){
            List<Point> final_star = new ArrayList(); final_star.addAll(star_instance);
            if(final_star.size() >= F_table.size()) {
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
        private void GetClique(Set<List<Point>> Instance ){
            for(List<Point> instance : Instance)
            {
                String cl = "";
                for(Point p: instance)
                {
                    cl+=p.Feature+p.Instance;
                }
                Clique_check.add(cl);
            }
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
        private void AddTable(List<Point> final_star){
            for(Point p: final_star)
            {
                Table_SI.get(p.Feature).add(p);
            }
        }
    public void ShowColocation()
    {
        System.out.println("Total colocation: "+R.size());
        for(String col: R.keySet())
        {
            System.out.println(col+"  :"+R.get(col));
        }
    }
}
