package Clique;

import java.io.IOException;
import java.util.List;

public class Main {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static double Max_memory =0;
    
    static String dataName = "beijing_POI_18f.csv";
    static String paths = "C:\\Users\\PC\\Desktop\\Do an\\data\\file\\";
    //static String paths = "D:\\DO AN\\Data\\";
    
    public static double min_prev = 0.2;
    public static double min_dist = 100;
    public static int Step = 4; 
    public static int loaiIDS = 0;
    
    public static void main(String[] args) throws IOException
    {
        paths += dataName;
        System.out.println(dataName);
        System.out.println("Min distance: "+min_dist+" min_prev: "+min_prev+" Step: "+ Step+" IDS: "+loaiIDS); System.out.println("");
        
        CLIQUE Clique = new CLIQUE();
        List<Point> S;  ReadData readdata = new ReadData(); S = readdata.SetPoint(paths);
        
        Clique.Clique_travel(min_dist, S, min_prev, Step, loaiIDS);
        
        System.out.println("");
        System.out.println("Max Memory used: "+Max_memory);
    }
    
    public static void memory()
    {
        double currentMemory = ( (double)((double)(Runtime.getRuntime().totalMemory()/1024)/1024))- ((double)((double)(Runtime.getRuntime().freeMemory()/1024)/1024));
        System.out.println(ANSI_RED + "                 Memory use: "+ currentMemory);
    }
    public static void Get_MaxMemory()
    {
        double currentMemory = ( (double)((double)(Runtime.getRuntime().totalMemory()/1024)/1024))- ((double)((double)(Runtime.getRuntime().freeMemory()/1024)/1024));
        if(currentMemory>Max_memory) Max_memory = currentMemory;
    }
}
