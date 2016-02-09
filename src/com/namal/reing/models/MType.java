package com.namal.reing.models;

import com.namal.reing.visitors.AbstractVisitor;
import com.namal.reing.visitors.ReingVisitor;

public class MType{
    private boolean isBasic;
    private String fullName;
    private boolean hasEllipsis=false;

    public MType(String n,boolean iB){
        fullName=n;
        isBasic=iB;
    }

    public void setFullName(String fN){
        fullName=fN;
    }
    
    public boolean isBasic(){
        return isBasic;
    }

    public String getFullName(){
        return fullName;
    }

    public String getName(){
        int index = fullName.lastIndexOf(".");
        String res;
        if(index <0)
            res= fullName;
        else
            res= fullName.substring(index+1);
        if(hasEllipsis)
            res+="...";
        return res;
    }

    public void hasEllipsis(){
        hasEllipsis =true;
    }

    public String toString(){
		return AbstractVisitor.getPrinter().print(this);
    }
}
