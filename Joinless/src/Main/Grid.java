package Main;

import java.util.ArrayList;
import java.util.List;

public class Grid {
    public int CorX, CorY;
    
    public List<Point> Instances = new ArrayList();
    
    public Grid(){}
    public Grid(int CorX, int CorY)
    {
        this.CorX = CorX;
        this.CorY = CorY;
    }
    public Grid(Point p)
    {
        Instances.add(p);
    }
    public void Clear()
    {
        Instances = new ArrayList();
    }
    public void Remove(Point p)
    {
        Instances.remove(p);
    }
    public void Add(Point p)
    {
        Instances.add(p);
    }
}
