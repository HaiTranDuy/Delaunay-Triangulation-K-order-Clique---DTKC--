package Main;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {
    
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static double Max_memory =0;
    
    static String dataName = "LasVegas_x_y_alphabet_version_02.csv";
    static String paths = "C:\\Users\\PC\\Desktop\\Do an\\data\\file\\";
    //static String paths = "D:\\DO AN\\Data\\";
    
    public static double min_prev = 0.2;
    public static double min_dist = 50;
    
    public static void main(String[] args) throws IOException
    {
        paths += dataName;
        System.out.println(dataName);
        System.out.println("Min distance: "+min_dist+" min_prev: "+min_prev); // cau hinh test
        System.out.println("");
        
        List<Point> S; ReadData readdata = new ReadData(); S = readdata.SetPoint(paths);
        Condensed Cd = new Condensed();
        
        System.out.println("-----------------Test : PIs ------------------");
        Cd.Condensed(S, min_dist, min_prev);
        System.out.println("");
        System.out.println("Max Memory : "+Max_memory);
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
    public static String SortString(String string)
    {
        char[] chars = string.toCharArray();
        Arrays.sort(chars);
        String sorted = new String(chars);
        return sorted;
    }
}
