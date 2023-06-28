package Main;



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
    public double Length(Point p) // length from this poin to other point
    {
        double ans = (this.CorX-p.CorX)*(this.CorX-p.CorX)+(this.CorY-p.CorY)*(this.CorY-p.CorY);
        return (double) Math.sqrt(ans);
    }
    
    List<Point> SN = new ArrayList();
    List<String> FN = new ArrayList();
    
    public void SortNeighbor()
    {
        Collections.sort(SN, new Comparator<Point>()
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
        Collections.sort(FN, new Comparator<String>()
        {
            public int compare(String a, String b)
            {
               return a.compareTo(b);
            }
        }
        );
    }
}
