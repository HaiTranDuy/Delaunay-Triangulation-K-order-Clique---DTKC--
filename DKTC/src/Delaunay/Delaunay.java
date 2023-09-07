package Delaunay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.poly2tri.Poly2Tri;
import org.poly2tri.geometry.primitives.DPoint;
import org.poly2tri.triangulation.TriangulationPoint;
import org.poly2tri.triangulation.sets.PointSet;
import org.poly2tri.triangulation.delaunay.DelaunayTriangle;
import org.poly2tri.triangulation.point.TPoint;

public class Delaunay {
    private List<DPoint> points;
    public Map<String,Edge> DelaunayEdge;
    public double   Global_mean,
                    Global_SD;

    //#<editor-fold desc="Delaunay triangle clone with no constraint">
    public Delaunay(List<DPoint> points)
    {
        this.points = points;
    }
    
    public void CreateDelaunay()
    {
        List<TriangulationPoint> listpoint = new ArrayList();
        for(DPoint p : points)
        {
            TPoint point = mkPt(p.CorX,p.CorY, p);
            listpoint.add(point);
        }
        PointSet pointsets = new PointSet(listpoint);

        Poly2Tri.triangulate(pointsets);
        List<DelaunayTriangle> triangles = pointsets.getTriangles();
        DelaunayEdge = new HashMap();
        for(DelaunayTriangle tri : triangles)
        {
            CreateEdge(tri.points[0].getPoint(), tri.points[1].getPoint());
            CreateEdge(tri.points[1].getPoint(), tri.points[2].getPoint());
            CreateEdge(tri.points[0].getPoint(), tri.points[2].getPoint());
        }
        GlobalMean(); GlobalSD();
        //System.out.println(triangles.size());
    }
        private TPoint mkPt(double x, double y, DPoint point) {
            return new TPoint(x, y, point);
        }
        private void CreateEdge(DPoint a, DPoint b){
            String  name1 = a.Feature+a.Instance+b.Feature+b.Instance,
                    name2 = b.Feature+b.Instance+a.Feature+a.Instance;
            if(!a.Feature.equals(b.Feature) && !DelaunayEdge.containsKey(name1) && !DelaunayEdge.containsKey(name2))
            {
                a.AddPoint(b);
                b.AddPoint(a);
                DelaunayEdge.put(name1, new Edge(a, b));
            }
        }
        
    //#</editor-fold>
        
    //#<editor-fold desc="Chia cac diem ra thanh cac Delaunay triangle nho">
    public List<DTriangle> DivideDT(List<DPoint> S) // Function for push divide DT after delete local constraint edge
    {
        List<DTriangle> SetDT = new ArrayList();
        
        for(DPoint p : S)
        {
            if(!p.InDT)
            {
                List<DPoint> SetPoint = new ArrayList();
                SetPoint = SetPointDT(p);
                DTriangle new_DT = new DTriangle(SetPoint);
                SetDT.add(new_DT);
            }
        }
        return SetDT;
    }
    public List<DPoint> SetPointDT(DPoint p)
    {
        List<DPoint> DT_Points = new ArrayList();
        List<DPoint> Queue = new ArrayList();
        
        Queue.add(p);
        while(!Queue.isEmpty())
        {
            DPoint curr = Queue.get(0); Queue.remove(0);
            DT_Points.add(curr);
            curr.InDT = true;
            
            for(DPoint near_point: curr.F1Node)
            {
                if(near_point.InDT == false && !Queue.contains(near_point) && !DT_Points.contains(near_point))
                {
                    Queue.add(near_point);
                }
            }
        }
        return DT_Points;
    }
    
    //#</editor-fold>
    
    public void GlobalMean() // Def 8: Global mean
    {
        double ans=0;
        for(String key : DelaunayEdge.keySet())
        {
            ans+= DelaunayEdge.get(key).length;
        }
        Global_mean = ans/(double)DelaunayEdge.size();
    }
    public void GlobalSD() // Def 9: Global SD
    {
        double ans=0;
        for(String key : DelaunayEdge.keySet())
        {
            ans+=(DelaunayEdge.get(key).length-Global_mean)*(DelaunayEdge.get(key).length-Global_mean);
        }
 
        double tyso = 1/((double)DelaunayEdge.size()-1);
        ans *=tyso;
        ans = Math.sqrt(ans);
        Global_SD = ans;
    }
    public void Remove_Global()
    {
        Set<String> delKey = new HashSet();
        for(String key : DelaunayEdge.keySet())
        {
            Edge edge = DelaunayEdge.get(key);
            DPoint start = edge.Start; 
            DPoint end = edge.End;
            
            if(edge.length > start.constant_global || edge.length > end.constant_global)
            {
                start.F1Node.remove(end); start.Local_Length -= edge.length; start.CountEdge--;
                end.F1Node.remove(start); end.Local_Length -= edge.length; end.CountEdge--;
                delKey.add(key);
            }
        }
        for(String key: delKey)
        {
            DelaunayEdge.remove(key);
        }
    }
    public void Conduct_LocalSD()
    {
        for(String key : DelaunayEdge.keySet())
        {
            Edge e = DelaunayEdge.get(key);
            DPoint start = e.Start, end = e.End;
            start.SumLocalSD += (e.length-start.local_mean)*(e.length-start.local_mean);
            end.SumLocalSD += (e.length-end.local_mean)*(e.length-end.local_mean);
        }
        for(DPoint p: points)
        {
            p.local_SD = Math.sqrt(p.SumLocalSD/(double)(p.CountEdge-1));
        }
    }
    public void Remove_Lobal()
    {
        Set<String> delKey = new HashSet();
        for(String key : DelaunayEdge.keySet())
        {
            Edge edge = DelaunayEdge.get(key);
            DPoint start = edge.Start; 
            DPoint end = edge.End;
            
            if(edge.length > start.constant_local || edge.length > end.constant_local)
            {
                start.F1Node.remove(end); start.Local_Length -= edge.length; start.CountEdge--;
                end.F1Node.remove(start); end.Local_Length -= edge.length; end.CountEdge--;
                delKey.add(key);
            }
        }
        for(String key: delKey)
        {
            DelaunayEdge.remove(key);
        }
    }
}
