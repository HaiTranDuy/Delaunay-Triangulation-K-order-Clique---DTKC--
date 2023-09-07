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
    
    public void K_Order_Neighbor(List<DPoint> S, Set<String> DE, int K) {
        for(DPoint p : S)
        {
            Set<DPoint> Pre_K_points = new HashSet();
            for(DPoint f1 : p.F1Node) {if(f1.Feature.compareTo(p.Feature)>0) Pre_K_points.add(f1);}
            for(DPoint f1: Pre_K_points) {if(f1.Feature.compareTo(p.Feature)>0) p.BNs.add(f1);}
                
            int k = 2;
            while(k<=K && Pre_K_points.size() > 0)
            {
                Set<DPoint> K_points = new HashSet();
                for(DPoint Pre_point : Pre_K_points)
                {
                    for(DPoint f1: Pre_point.F1Node)
                    {
                        if(!p.BNs.contains(f1) && f1.Feature.compareTo(p.Feature)>0)
                        {
                            p.BNs.add(f1);
                            K_points.add(f1);
                        }
                    }
                }
                Pre_K_points.clear(); Pre_K_points.addAll(K_points);
                k++;
            }
        }
        long sum = 0;
        for(DPoint p : S)
        {
            p.SortNeighbor(); 
            sum+=p.BNs.size();
        }
        System.out.println(ANSI_GREEN+"Done k neighbor, Average neighbor: "+sum/S.size());
    }
    //#</editor-fold>
}
