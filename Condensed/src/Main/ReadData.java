package Main;


import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

public class ReadData {
    public List<Point> SetPoint(String path) throws IOException
    {
        List<Point> S = new ArrayList<Point>();
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
                String namePoint = infor[0]+Integer.parseInt(infor[1]);
                S.add(new Point(infor[0],Integer.parseInt(infor[1]), Double.parseDouble(infor[2]), Double.parseDouble(infor[3])));
                
                if(MinX >Double.parseDouble(infor[2]) ) MinX = Double.parseDouble(infor[2]);
                if(MaxX <Double.parseDouble(infor[2])) MaxX = Double.parseDouble(infor[2]);
                if(MinY >Double.parseDouble(infor[3]) ) MinY = Double.parseDouble(infor[3]);
                if(MaxY <Double.parseDouble(infor[3])) MaxY = Double.parseDouble(infor[3]);
            }
            System.out.println("MinX: "+ MinX +" MaxX: " + MaxX + " MinY: "+MinY+" MaxY: "+MaxY);
            
        }catch(IOException e){System.out.println("Read CSV faile");}
        
        return S;
    }
}
