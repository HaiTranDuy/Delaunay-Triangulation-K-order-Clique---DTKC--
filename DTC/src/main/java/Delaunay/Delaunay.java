package Delaunay;

import static Main.Main.Get_MaxMemory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    public Map<String, Edge> DelaunayEdge;
    public double Global_mean,
            Global_SD;

    //#<editor-fold desc="Delaunay triangle clone with no constraint">
    public Delaunay(List<DPoint> points) {
        this.points = points;
    }

    public void CreateDelaunay() {
        List<TriangulationPoint> listpoint = new ArrayList();
        for (DPoint p : points) {
            TPoint point = mkPt(p.CorX, p.CorY, p);
            listpoint.add(point);
        }
        PointSet pointsets = new PointSet(listpoint);

        Poly2Tri.triangulate(pointsets);
        List<DelaunayTriangle> triangles = pointsets.getTriangles();
        DelaunayEdge = new HashMap();
        for (DelaunayTriangle tri : triangles) {
            CreateEdge(tri.points[0].getPoint(), tri.points[1].getPoint());
            CreateEdge(tri.points[1].getPoint(), tri.points[2].getPoint());
            CreateEdge(tri.points[0].getPoint(), tri.points[2].getPoint());
        }
        GlobalMean();
        GlobalSD();
        //System.out.println(triangles.size());
    }

    private TPoint mkPt(double x, double y, DPoint point) {
        return new TPoint(x, y, point);
    }

    private void CreateEdge(DPoint a, DPoint b) {
        String name1 = a.Feature + a.Instance + b.Feature + b.Instance,
                name2 = b.Feature + b.Instance + a.Feature + a.Instance;
        if (!a.Feature.equals(b.Feature) && !DelaunayEdge.containsKey(name1) && !DelaunayEdge.containsKey(name2)) {
            a.AddPoint(b);
            b.AddPoint(a);
            DelaunayEdge.put(name1, new Edge(a, b));
        }
    }

    //#<editor-fold desc="Chia cac diem ra thanh cac Delaunay triangle nho">
    public List<DTriangle> DivideDT(List<DPoint> S) // Function for push divide DT after delete local constraint edge
    {
        List<DTriangle> SetDT = new ArrayList();

        for (DPoint p : S) {
            if (!p.InDT) {
                List<DPoint> SetPoint = new ArrayList();
                SetPoint = SetPointDT(p);
                DTriangle new_DT = new DTriangle(SetPoint);
                SetDT.add(new_DT);
            }
        }
        return SetDT;
    }

    public List<DPoint> SetPointDT(DPoint p) {
        List<DPoint> DT_Points = new ArrayList();
        List<DPoint> Queue = new ArrayList();

        Queue.add(p);
        while (!Queue.isEmpty()) {
            DPoint curr = Queue.get(0);
            Queue.remove(0);
            DT_Points.add(curr);
            curr.InDT = true;

            for (DPoint near_point : curr.F1Node) {
                if (near_point.InDT == false && !Queue.contains(near_point) && !DT_Points.contains(near_point)) {
                    Queue.add(near_point);
                }
            }
        }
        return DT_Points;
    }

    //#</editor-fold>
    //<editor-fold desc="Remove constraint">
    public void GlobalMean() // Def 8: Global mean
    {
        double ans = 0;
        for (String key : DelaunayEdge.keySet()) {
            ans += DelaunayEdge.get(key).length;
        }
        Global_mean = ans / (double) DelaunayEdge.size();
    }

    public void GlobalSD() // Def 9: Global SD
    {
        double ans = 0;
        for (String key : DelaunayEdge.keySet()) {
            ans += (DelaunayEdge.get(key).length - Global_mean) * (DelaunayEdge.get(key).length - Global_mean);
        }

        double tyso = 1 / ((double) DelaunayEdge.size() - 1);
        ans *= tyso;
        ans = Math.sqrt(ans);
        Global_SD = ans;
    }

    public void Remove_Global() {
        Set<String> delKey = new HashSet();
        for (String key : DelaunayEdge.keySet()) {
            Edge edge = DelaunayEdge.get(key);
            DPoint start = edge.Start;
            DPoint end = edge.End;

            if (edge.length > start.constant_global || edge.length > end.constant_global) {
                start.F1Node.remove(end);
                start.Local_Length -= edge.length;
                start.CountEdge--;
                end.F1Node.remove(start);
                end.Local_Length -= edge.length;
                end.CountEdge--;
                delKey.add(key);
            }
        }
        for (String key : delKey) {
            DelaunayEdge.remove(key);
        }
    }

    public void Conduct_LocalSD() {
        for (String key : DelaunayEdge.keySet()) {
            Edge e = DelaunayEdge.get(key);
            DPoint start = e.Start, end = e.End;
            start.SumLocalSD += (e.length - start.local_mean) * (e.length - start.local_mean);
            end.SumLocalSD += (e.length - end.local_mean) * (e.length - end.local_mean);
        }
        for (DPoint p : points) {
            p.local_SD = Math.sqrt(p.SumLocalSD / (double) (p.CountEdge - 1));
        }
    }

    public void Remove_Lobal() {
        Set<String> delKey = new HashSet();
        for (String key : DelaunayEdge.keySet()) {
            Edge edge = DelaunayEdge.get(key);
            DPoint start = edge.Start;
            DPoint end = edge.End;

            if (edge.length > start.constant_local || edge.length > end.constant_local) {
                start.F1Node.remove(end);
                start.Local_Length -= edge.length;
                start.CountEdge--;
                end.F1Node.remove(start);
                end.Local_Length -= edge.length;
                end.CountEdge--;
                delKey.add(key);
            }
        }
        for (String key : delKey) {
            DelaunayEdge.remove(key);
        }
    }
    //</editor-fold>

    public Map<DPoint, List<List<DPoint>>> GenStarDelaunayTriangle(List<DPoint> S) // Step 5:
    {
        Table2_ = new HashMap();

        Map<DPoint, List<List<DPoint>>> star_DT = new HashMap();
        for (DPoint p : S) {
            List<List<DPoint>> DT = p.StarDelaunayTriangle();
            star_DT.put(p, DT);
        }
        Get_MaxMemory();
        return star_DT;
    }

    //#<editor-fold desc="Merge">
    public void MergeDelaunay(List<DPoint> S) {
        //List<List<DPoint>> R_ = new ArrayList();
        for (DPoint p : S) {
            List<List<DPoint>> polygons = p.Star_DT;
            int t = polygons.size();
            while (t >= 1) {
                if (t == 1) {
                    //R_.addAll(combination_row_instances(polygons));
                    combination_row_instances(polygons);
                    break;
                } else {
                    polygons = merger_polygons(polygons);
                    if (polygons.size() == t) {
                        //R_.addAll(combination_row_instances(polygons));
                        combination_row_instances(polygons);
                        break;
                    } else {
                        t = polygons.size();
                    }
                }
            }
        }
        Get_MaxMemory();
        //return R_;
    }

    private void combination_row_instances(List<List<DPoint>> polygons) {
        List<List<DPoint>> row_instances = new ArrayList();
        for (List<DPoint> polygon : polygons) {
            row_instances.addAll(Instances(polygon));
        }
        row_instances = Sort_instance(row_instances);
        for(List<DPoint> instance : row_instances)
        {
            Add_InstanceTable(instance);
        }
        Get_MaxMemory();
        //return row_instances;
    }

    public List<List<DPoint>> Instances(List<DPoint> polygon) {
        List<List<DPoint>> instances = new ArrayList();
        int size = polygon.size();
        int tong = (int) Math.pow(2, size) - 1;
        List<DPoint> instance;
        int chiso = 0;
        String dem = "";
        for (int i = 0; i < polygon.size(); i++) {
            dem += "0";
        }

        for (int i = tong; i >= 1; i--) {
            instance = new ArrayList();
            String patterm = Integer.toBinaryString(i);
            patterm = dem.substring(patterm.length(), size) + patterm;
            chiso = 0;
            for (char c : patterm.toCharArray()) {
                if (c == '1') {
                    instance.add(polygon.get(chiso));
                }
                chiso++;
            }
            if (instance.size() > 1) {
                count_instance++;
                instances.add(instance);
            }
        }
        
        Get_MaxMemory();
        return instances;
    }

    private List<List<DPoint>> merger_polygons(List<List<DPoint>> polygons) {
        List<List<DPoint>> POLYGONS = new ArrayList();
        Set<String> checkInstance = new HashSet();
        for (int i = 0; i < polygons.size() - 1; i++) {
            for (int j = i + 1; j < polygons.size(); j++) {
                Set<DPoint> new_pol = Merger(polygons.get(i), polygons.get(j));
                if (new_pol != null) {
                    String name_new_pol = "";
                    for (DPoint p : new_pol) {
                        name_new_pol += p.Feature + p.Instance;
                    }
                    if (!checkInstance.contains(name_new_pol)) {
                        List<DPoint> t = new ArrayList();
                        for (DPoint p : new_pol) {
                            t.add(p);
                        }
                        POLYGONS.add(t);
                        checkInstance.add(name_new_pol);
                    }
                    Get_MaxMemory();
                }
            }
        }
        if (POLYGONS.size() < 1) {
            POLYGONS = polygons;
        }
        return POLYGONS;
    }

    private Set<DPoint> Merger(List<DPoint> pol1, List<DPoint> pol2) {
        Set<DPoint> PointCheck = new HashSet();
        Set<String> FeatureCheck = new HashSet();
        int size = pol1.size() + 1;
        for (DPoint p : pol1) {
            PointCheck.add(p);
            FeatureCheck.add(p.Feature);
        }
        for (DPoint p : pol2) {
            PointCheck.add(p);
            FeatureCheck.add(p.Feature);
        }
        if (PointCheck.size() == size && FeatureCheck.size() == size) {
            Set<DPoint> polygon = new HashSet();
            polygon.addAll(PointCheck);
            Get_MaxMemory();
            return polygon;
        }
        Get_MaxMemory();
        return null;
    }
    //#</editor-fold>

    //#<editor-fold desc="Step: 24 25 26">
    public long count_instance = 0;
    public Map<String, Map<String, Set<DPoint>>> Table2_; // table instances
    public Map<String, Integer> Features;// count instance each Feature

    public Map<String, Integer> CountFeature(List<DPoint> S) {
        Map<String, Integer> F = new HashMap();
        Map<String, List<DPoint>> CountF = new HashMap();

        for (DPoint p : S) {
            if (!CountF.containsKey(p.Feature)) {
                CountF.put(p.Feature, new ArrayList());
            }
            if (!CountF.get(p.Feature).contains(p)) {
                CountF.get(p.Feature).add(p);
            }
        }
        for (String key : CountF.keySet()) {
            if (!F.containsKey(key)) {
                F.put(key, CountF.get(key).size());
            }
        }
        return F;
    }

    public List<List<DPoint>> Sort_instance(List<List<DPoint>> R_s) {
        for (List<DPoint> instance : R_s) {
            Collections.sort(instance, new Comparator<DPoint>() {
                public int compare(DPoint a, DPoint b) {
                    return a.Feature.compareTo(b.Feature);
                }
            });
        }
        return R_s;
    }
    
    public void Table_Instances2(List<List<DPoint>> R_s) // Step 24
    {
        for (List<DPoint> row_instance : R_s) {
            String Col = "";
            for (DPoint p : row_instance) {
                Col += p.Feature;
            }
            if (!Table2_.containsKey(Col)) {
                Table2_.put(Col, new HashMap());
            }

            for (DPoint p : row_instance) {
                if (!Table2_.get(Col).containsKey(p.Feature)) {
                    Table2_.get(Col).put(p.Feature, new HashSet());
                }
                Table2_.get(Col).get(p.Feature).add(p);
            }
        }
    }

    private void Add_InstanceTable(List<DPoint> row_instance) {
        String Col = "";
        for (DPoint p : row_instance) {
            Col += p.Feature;
        }
        if (!Table2_.containsKey(Col)) {
            Table2_.put(Col, new HashMap());
        }

        for (DPoint p : row_instance) {
            if (!Table2_.get(Col).containsKey(p.Feature)) {
                Table2_.get(Col).put(p.Feature, new HashSet());
            }
            Table2_.get(Col).get(p.Feature).add(p);
        }
    }

    public Map<String, Double> Filter_prevalent_colocation(double min_prev, List<DPoint> S) {
        Features = CountFeature(S);
        Map<String, Double> PIs = new HashMap();
        for (String colocation : Table2_.keySet()) {
            Double PI = CaculatePI(colocation, Table2_.get(colocation));
            if (min_prev < PI) {
                PIs.put(colocation, PI);
            }
        }
        return PIs;
    }

    private Double CaculatePI(String currCandidate, Map<String, Set<DPoint>> Table) {
        double minPRs = 1;
        int count = 0;

        for (int i = 0; i < currCandidate.length(); i++) {
            String F = "" + currCandidate.charAt(i);
            int count_f = Table.get(F).size();
            if (minPRs >= (double) count_f / (double) Features.get(F)) {
                count++;
                minPRs = (double) count_f / (double) Features.get(F);
            }
        }
        if (count == 0) {
            return 0.0;
        }

        return minPRs;
    }
    //#</editor-fold>
}
