package org.poly2tri.geometry.primitives;

import Delaunay.DTriangle;
import Delaunay.Delaunay;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DPoint {
    public String Feature;
    public int Instance;
    public double CorX, CorY, CorZ;
    
    public DPoint(){}
    public DPoint(String Feature, int Instance, double CorX, double CorY)
    {
        this(CorX, CorY, 0);
        int heso=1;
        this.Feature = Feature;
        this.Instance = Instance;
    }
    public DPoint (double X, double Y, double Z)
    {
        int heso=1;
        CorX = X*heso;
        CorY = Y*heso;
        CorZ = Z*heso;
    }
    // Get
    public Double X()
    {
        return CorX;
    }
    public Double Y()
    {
        return CorY;
    }
    public void Show(int type)
    {
        if(type==1){
            System.out.print(CorX+"      "+CorY+"     ");
        }
        else 
        {
            System.out.print(Feature+Instance+"    ");
        }
    }
    
    //#<editor-fold desc="For constraint Delaunay">
    public boolean InDT = false;
   
    public List<List<DPoint>> Star_DT;
    public double local_mean,  
                 local_SD, 
                 constant_global,
                 constant_local;
    
    public double Local_Length=0; // length edge have this point
    public int CountEdge =0; // number edge have this point
    
    public double SumLocalSD = 0;
    
    public Set<DPoint> F1Node = new HashSet(); // point near this point. F1 edge
    public Set<DPoint> F2Node = new HashSet(); // point near this point but in F2 edge
    
    public boolean Equal(DPoint p)
    {
        if(this.Feature.equals(p.Feature) && this.Instance == p.Instance) return true;
        else return false;
    }
    public void AddPoint(DPoint p) { F1Node.add(p); }
    public void RemovePoint(DPoint p) { F1Node.remove(p); }
    
    public Double Length(DPoint p) { return Math.sqrt((this.CorX-p.CorX)*(this.CorX-p.CorX) +(this.CorY-p.CorY)*(this.CorY-p.CorY)); } // length from this point to point p
    
    public int Degree() { return this.F1Node.size(); } // number of vertices near this point
    
    public void LocalMean () { 
        local_mean = Local_Length/(double)CountEdge;
    } // Def 6: Local mean
    
    public void LocalSD() // Def 7: Local SD
    {
        LocalMean();
        double hieu = 0;
        for(DPoint p: F1Node)
        {
            double e = Length(p);
            hieu += (e-local_mean)*(e-local_mean);
        }
        hieu = hieu *(1/(double)(Degree()-1));
        hieu = Math.sqrt(hieu);
        local_SD = hieu;
    }
    
    public void Global_Positive_Edge(Delaunay DT) // Def 10: Global positive edge 
    {
        LocalMean();
        double GM = DT.Global_mean, Global_SD = DT.Global_SD;
        constant_global = GM+Global_SD*(GM/local_mean);
    }
    
    public void GetF2Node() // Generate F2 Point from F1 point
    {
        for(DPoint p : F1Node)
        {
            for(DPoint F2: p.F1Node)
            {
                if(F2 != this && !F1Node.contains(F2) && !F2Node.contains(F2))
                {
                    F2Node.add(F2);
                }
            }
        }
    }
    
    public double Local_meanF2()
    {
        double ans = 0;
        for(DPoint p: F1Node)
        {
            ans += Length(p);
        }
        for(DPoint p: F2Node)
        {
            ans += Length(p);
        }
        float sum =0; sum+= (double) F1Node.size(); sum+= (double) F2Node.size();
        ans = (1/sum)* ans; 
        return ans;
    }
    
    public void local_pps(DTriangle DT)
    {
        constant_local = Local_meanF2() + DT.Mean_local_SD;
    }
    //#</editor-fold>
    
    public List<List<DPoint>> StarDelaunayTriangle() // 5
    {
        Star_DT = new ArrayList();
        for(DPoint p: F1Node) p.InDT =false;
        
        for(DPoint p: F1Node)
        {
            p.InDT = true;
            for(DPoint near_p: p.F1Node)
            {
                if(near_p.InDT == false && near_p.F1Node.contains(this))
                {
                    Star_DT.add(new ArrayList<DPoint>());
                    Star_DT.get(Star_DT.size()-1).add(this);
                    Star_DT.get(Star_DT.size()-1).add(near_p);
                    Star_DT.get(Star_DT.size()-1).add(p);
                }
            }
        }
        return Star_DT;
    }
}
