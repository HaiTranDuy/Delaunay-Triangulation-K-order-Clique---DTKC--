package Main;
import static Demo.demo.number_of_features;
import static Demo.demo.number_of_instances;

import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.poly2tri.geometry.primitives.DPoint;

public class ReadData {
    public List<DPoint> SetPoint(String path) throws Exception
    {
        List<DPoint> S = new ArrayList<DPoint>();
        String line;
        int count=0;
        
        double MinX = Double.MAX_VALUE, MaxX = Double.MIN_VALUE, MinY=Double.MAX_VALUE, MaxY=Double.MIN_VALUE;
        try{
            BufferedReader br = new BufferedReader(new FileReader(path));  
            while((line = br.readLine())!= null)
            {
                count++;
                if(count==1){continue;}
                String[] infor = line.split(","); 
                S.add(new DPoint(infor[0],Integer.parseInt(infor[1]), Double.parseDouble(infor[2]), Double.parseDouble(infor[3])));
                
                if(MinX >Double.parseDouble(infor[2]) ) MinX = Double.parseDouble(infor[2]);
                if(MaxX <Double.parseDouble(infor[2])) MaxX = Double.parseDouble(infor[2]);
                if(MinY >Double.parseDouble(infor[3]) ) MinY = Double.parseDouble(infor[3]);
                if(MaxY <Double.parseDouble(infor[3])) MaxY = Double.parseDouble(infor[3]);
            }
            System.out.println("MinX: "+ MinX +" MaxX: " + MaxX + " MinY: "+MinY+" MaxY: "+MaxY);
        }catch(IOException e){System.out.println("Read CSV faile");}
        
        Collections.sort(S, new Comparator<DPoint>(){
            public int compare(DPoint a, DPoint b)
            {
                if(a.CorX == b.CorX)
                {
                    if(a.CorY > b.CorY) return 1;
                    else return -1;
                }
                else
                {
                    if(a.CorX > b.CorX) return 1;
                    else return -1;
                }
            }
        });
        
        
        int i=0;
        while(i<S.size()-1)
        {
            if(Objects.equals(S.get(i).X(), S.get(i+1).X()) && Objects.equals(S.get(i).Y(), S.get(i+1).Y()))
            {
//                S.get(i).Show(0); S.get(i).Show(1); System.out.print("         ");S.get(i+1).Show(0); S.get(i+1).Show(1);
//                System.out.println("");
                S.remove(i);
            }
            else i++;
        }
//        
//        for(Point p : S)
//        {
//            p.Show(0);
//            p.Show(1);
//            System.out.println("");
//        }
        return S;
    }
    public List<DPoint> SetPoint_demo(String path) throws Exception
    {
        List<DPoint> S = new ArrayList<DPoint>();
        Set<String> Features = new HashSet();
        String line;
        int count=0;
        try{
            BufferedReader br = new BufferedReader(new FileReader(path));  
            while((line = br.readLine())!= null)
            {
                count++;
                if(count==1){continue;}
                String[] infor = line.split(","); 
                S.add(new DPoint(infor[0],Integer.parseInt(infor[1]), Double.parseDouble(infor[2]), Double.parseDouble(infor[3])));
                Features.add(infor[0]);
            }
        }
        catch(Exception e){System.out.println("Read Data Set CSV faile");}
        
        if(count>0) number_of_instances = count-1;
        number_of_features = Features.size(); Features = null;
        Collections.sort(S, new Comparator<DPoint>(){
            public int compare(DPoint a, DPoint b)
            {
                if(a.CorX == b.CorX)
                {
                    if(a.CorY > b.CorY) return 1;
                    else return -1;
                }
                else
                {
                    if(a.CorX > b.CorX) return 1;
                    else return -1;
                }
            }
        });
        
        int i=0;
        while(i<S.size()-1)
        {
            if(Objects.equals(S.get(i).X(), S.get(i+1).X()) && Objects.equals(S.get(i).Y(), S.get(i+1).Y()))
            {
                S.remove(i);
            }
            else i++;
        }
        return S;
    }
    public Map<String, String> Set_Feature(String path) throws Exception
    {
        Map<String, String> Feature_map = new HashMap();
        String line;
        int count=0;
        try{
            BufferedReader br = new BufferedReader(new FileReader(path));  
            while((line = br.readLine())!= null)
            {
                count++;
                if(count==1){continue;}
                String[] infor = line.split(","); 
                Feature_map.put(infor[1], infor[0]);
            }
        }
        catch(Exception e){System.out.println("Read Feature name CSV faile");}
        return Feature_map;
    }
}
