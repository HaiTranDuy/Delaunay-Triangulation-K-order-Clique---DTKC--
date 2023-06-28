package Clique;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Point {
    public String Feature;
    public int Instance;
    public double CorX, CorY;
    public Point(){}
    public Point(String Feature, int Instance, double CorX, double CorY)
    {
        this.Feature = Feature;
        this.Instance = Instance;
        this.CorX = CorX;
        this.CorY = CorY;
    }
    
    public boolean ClearEdge = false;
    
    public List<Point> BNs = new ArrayList();
    //public List<Point> SNs = new ArrayList();
    
    public void Show()
    {
        System.out.print(Feature+Instance+" ");
    }
    
    public double Length(Point p) // length from this poin to other point
    {
        double ans = (this.CorX-p.CorX)*(this.CorX-p.CorX)+(this.CorY-p.CorY)*(this.CorY-p.CorY);
        return (double) Math.sqrt(ans);
    }
    
    public void SortNeighbor()
    {
        Collections.sort(BNs, new Comparator<Point>()
        {
            public int compare(Point a, Point b)
            {
                if(a.Feature.equals(b.Feature))
                {
                    return a.Instance -b.Instance;
                }
                else return a.Feature.compareTo(b.Feature);
            }
        }
        );
//        Collections.sort(SNs, new Comparator<Point>()
//        {
//            public int compare(Point a, Point b)
//            {
//                if(a.Feature.equals(b.Feature))
//                {
//                    return a.Instance -b.Instance;
//                }
//                else return a.Feature.compareTo(b.Feature);
//            }
//        }
//        );
    }
}
