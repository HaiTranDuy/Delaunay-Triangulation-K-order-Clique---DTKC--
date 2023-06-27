package Main;

import Clique.CLIQUE;
import Delaunay.*;
import java.io.IOException;
import java.util.List;
import org.poly2tri.geometry.primitives.DPoint;

public class Main {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static double Max_memory =0;
    
    public static String data_name = "Toronto_x_y_alphabet_version_02.csv";
    //public static String path = "D:\\DO AN\\Thi nghiem\\data\\";
    public static String path = "C:\\Users\\PC\\Desktop\\Do an\\data\\file\\";
    
    public static int k = 2;
    public static double min_prev = 0.2;
    public static int Step = 4;
    public static int loaiIDS = 1;
    
    public static void main(String[] args) throws IOException {
        
        path += data_name;
        System.out.println(data_name);
        System.out.println("k: "+k+" min_prev: "+min_prev+" Step: "+ Step+" IDS: "+loaiIDS);
        System.out.println("");
        
        //#<editor-fold desc="Read data from CSV  ">
        CLIQUE Clique = new CLIQUE();
        Hai K_neighbor = new Hai();
        ReadData Data = new ReadData(); List<DPoint> S = Data.SetPoint(path);
        System.out.println("Point Number: "+S.size());
        //#</editor-fold> 
        //#<editor-fold desc="Delaunay triangle Algorithm">
        Delaunay delaunay = new Delaunay(S);
        delaunay.CreateDelaunay();
        //#</editor-fold>
        //#<editor-fold desc="Filter Global positive edge ">
        for(DPoint p: S) p.Global_Positive_Edge(delaunay);
        delaunay.Remove_Global();
        //for(Point p: S) p.Remove_Constraint(p.constant_global);
        //#</editor-fold>
        //#<editor-fold desc="Filter Local positive edge ">
        //DT2.Get_Edge_From_Point();
        List<DTriangle> DTSet = delaunay.DivideDT(S);
        delaunay.Conduct_LocalSD();
        for(DTriangle Set : DTSet)
        {
            for(DPoint p : Set.S)
            {
                p.GetF2Node();
                //p.Local_Positive_Edge(Set);
            }
            Set.MEanLocal();
        }
        int sum=0;
        for(DTriangle Set : DTSet)
        {
            for(DPoint p : Set.S)
            {
                p.local_pps(Set);
                sum+=p.F1Node.size();
            }
        }
        delaunay.Remove_Lobal();
        //#</editor-fold>
        System.out.println("");
        System.out.println(ANSI_GREEN+"Done Constraint Delaunay. ");  memory();
        
        K_neighbor.K_Order_Neighbor(S, delaunay.DelaunayEdge.keySet(), k);
        delaunay = null; K_neighbor = null;
        
        Clique.Clique_travel(S, min_prev, Step, loaiIDS, k);
        
        System.out.println("");
        System.out.println("Max Memory: "+Max_memory);
    }
    public static void memory(){
        double currentMemory = ( (double)((double)(Runtime.getRuntime().totalMemory()/1024)/1024))- ((double)((double)(Runtime.getRuntime().freeMemory()/1024)/1024));
        System.out.println(ANSI_RED+"                Memory use: "+ currentMemory);
    }
    public static void Get_MaxMemory(){
        double currentMemory = ( (double)((double)(Runtime.getRuntime().totalMemory()/1024)/1024))- ((double)((double)(Runtime.getRuntime().freeMemory()/1024)/1024));
        if(currentMemory>Max_memory) Max_memory = currentMemory;
    }
}
