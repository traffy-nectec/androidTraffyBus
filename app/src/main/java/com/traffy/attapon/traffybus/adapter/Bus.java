package com.traffy.attapon.traffybus.adapter;

/**
 * Created by chanpc on 7/26/2016.
 */
public class Bus {
    String name,line,start,end;
    public Bus(String BName, String BLine, String s , String n)
    {
        name = BName;
        line = BLine;
        start = s;
        end = n;
    }
    public String getName()
    {
        return name;
    }
    public String getLine()
    {
        return line;
    }
    public String getStart()
    {
        return start;
    }
    public String getEnd()
    {
        return end;
    }


}
