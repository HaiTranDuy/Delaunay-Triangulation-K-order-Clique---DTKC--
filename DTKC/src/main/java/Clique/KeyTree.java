package Clique;

import java.util.HashSet;
import java.util.Set;

public class KeyTree {
    public ColNote Root;
    public KeyTree (){Root = new ColNote();}
    
    public void AddCol(String colocation) // ABCD
    {
        for(int i=0; i<colocation.length()-1; i++)
        {
            String sub_col = colocation.substring(i);
            AddBranch(sub_col, colocation);
        }
    }
        private void AddBranch(String branch, String colocation)
        {
            String Core = ""+branch.charAt(0);
            Set<String> branchs = GenBranch(branch.substring(1));
            
            if(!Root.NextF.containsKey(Core)) Root.NextF.put(Core, new ColNote(Core));
            ColNote curNote = Root.NextF.get(Core);
            for(String nhanh : branchs)
            {
                Add(curNote, nhanh, colocation);
            }
        }
            private Set<String> GenBranch(String branch) // A|BCD -> branch = BCD
            {
                Set<String> branchs = new HashSet();
                for(int i=1; i<Math.pow(2, branch.length()); i++)
                {
                    String bitMap = Integer.toBinaryString(i);
                    
                    String nhanh = "";
                    for(int j=0; j<bitMap.length(); j++)
                    {
                        if(bitMap.charAt(j) == '1')
                        {
                            nhanh+=branch.charAt(j+branch.length()-bitMap.length());
                        }
                    }
                    branchs.add(nhanh);
                }
                return branchs;
            }
            private void Add(ColNote curNote, String nhanh, String colocation)
            {
                if(nhanh.length()>0)
                {
                    String Core = ""+nhanh.charAt(0);
                    if(!curNote.NextF.containsKey(Core)) curNote.NextF.put(Core, new ColNote(Core));
                    curNote = curNote.NextF.get(Core); curNote.SuperSet.add(colocation);
                    Add(curNote, nhanh.substring(1), colocation);
                }
            }
            
    public Set<String> GetSuperSet(String candidate)
    {
        ColNote curNote = Root;
        for(int i=0; i<candidate.length(); i++)
        {
            String Core = ""+candidate.charAt(i);
            curNote = curNote.NextF.get(Core);
        }
        return curNote.SuperSet;
    }
}
