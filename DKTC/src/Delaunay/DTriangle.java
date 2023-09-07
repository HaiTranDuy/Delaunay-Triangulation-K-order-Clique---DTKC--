package Delaunay;

import java.util.List;
import org.poly2tri.geometry.primitives.DPoint;
public class DTriangle {
    public List<DPoint> S; // Point in this Delaunay Triangle clique
    
    public double Mean_local_SD;
    
    public DTriangle(List<DPoint> S) { this.S = S; }
    public double MEanLocal()
    {
        double ans =0;
        for(DPoint p : S)
        {
            ans+=p.local_SD;
        }
        ans = ans/S.size();
        return Mean_local_SD = ans;
    }
}
