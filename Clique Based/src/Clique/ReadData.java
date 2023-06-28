package Clique;

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
        try{
            BufferedReader br = new BufferedReader(new FileReader(path));  
            while((line = br.readLine())!= null)
            {
                count++;
                if(count==1){continue;}
                String[] infor = line.split(","); 
                S.add(new Point(infor[0],Integer.parseInt(infor[1]), Double.parseDouble(infor[2]), Double.parseDouble(infor[3])));
            }
        }catch(IOException e){System.out.println("Read CSV faile");}
        
        return S;
    }
}
