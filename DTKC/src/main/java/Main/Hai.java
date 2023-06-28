package Main;
import static Main.Main.ANSI_GREEN;
import static Main.Main.memory;

import org.poly2tri.geometry.primitives.DPoint;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Hai {
    //#<editor-fold desc="K order neighbor">
    private Set<DPoint> DV; //the set of points,
    // DE the set of edges in TIN
    private Set<DPoint> DU; //set of k-order neighbors
    private Set<String>SE; //set of searched edges
    private List<DPoint>QV; //queue of points that have already been searched as neighbours and are extendable for searching next order neighbours
    
    private List<Set<DPoint>> K_order; // set of k order neighbor
    private Set<DPoint> SetPointK; // set point at order k
    
    public void K_Order_Neighbor(List<DPoint> S, Set<String> DE, int K)
    {
        int Neighbor=0;
        for(DPoint p: S)
        {// Step 1
            int k = 0; // order k
            DU = new HashSet(); SE = new HashSet();  QV = new ArrayList(); K_order = new ArrayList();SetPointK = new HashSet();
            DPoint v = p; 
            QV.add(v);
            DU.add(v);
            
            SetPointK.add(p);
            K_order.add(SetPointK);
            int count = 0;
            
            SetPointK = new HashSet();
            while ((!QV.isEmpty() && k<K))
            {
                count++;
                v = QV.get(0);
                QV.remove(0);
                
                for(DPoint order1_point: v.F1Node) // Step 2
                {
                    String  name1 = v.Feature+v.Instance+order1_point.Feature+order1_point.Instance,
                            name2 = order1_point.Feature+order1_point.Instance+v.Feature+v.Instance;
                    if((DE.contains(name1) || DE.contains(name2)) && (!SE.contains(name1)&&!SE.contains(name2))) //I
                        {
                            SE.add(name1); // I-a
                            // && order1_point.Feature.compareTo(p.Feature)>0
                            if(!DU.contains(order1_point) && order1_point.Feature.compareTo(p.Feature)>0) 
                            {
                                DU.add(order1_point);
                                QV.add(order1_point);
                                SetPointK.add(order1_point);
                            } // I-b
                        }
                }
                if(count>= K_order.get(k).size())
                {
                    count=0;
                    k=k+1;
                    K_order.add(SetPointK);
                    SetPointK = new HashSet();
                }
            }
            DU.remove(p);
            Neighbor+=DU.size();
            
            p.BNs.addAll(DU);
            p.SortNeighbor();
        }
        System.out.println(ANSI_GREEN+"Done k neighbor, Average neighbor: "+Neighbor/S.size());  memory();
    }
    //#</editor-fold>
}
