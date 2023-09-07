package Delaunay;

import org.poly2tri.geometry.primitives.DPoint;

public class Edge {
    public double length;
    public DPoint Start, End;
    
    public Edge(){}
    public Edge(DPoint start, DPoint end)
    {
        this.Start = start;
        this.End = end;
        KhoiTaoEdege();
    }
    private void KhoiTaoEdege()
    {
        this.length = Math.sqrt((Start.CorX-End.CorX)*(Start.CorX-End.CorX)+(Start.CorY-End.CorY)*(Start.CorY-End.CorY));
        this.Start.Local_Length +=length; this.Start.CountEdge++;
        this.End.Local_Length += length; this.End.CountEdge++;
    }
    public double Length()
    {
        return length = Math.sqrt((Start.CorX-End.CorX)*(Start.CorX-End.CorX)+(Start.CorY-End.CorY)*(Start.CorY-End.CorY));
    }
    public void Show()
    {
        System.out.print(Start.Feature+Start.Instance+" "+End.Feature+End.Instance);
    }
}
