package com.namal.reing.models;

import com.namal.reing.visitors.AbstractVisitor;

import java.util.ArrayList;
import java.util.List;

public class MClass extends MCIE{


    private MType inherits;
    private List<MType> implement = new ArrayList<>();

    public void setExtend(MType l) {
        inherits=l;
    }

    public void addImplement(MType implement){
        this.implement.add(implement);
    }

    public MType getExtend(){
        return inherits;
    }

    public List<MType> getImplement(){
        return implement;
    }

	public String toString(){
		return AbstractVisitor.getPrinter().print(this);
	}
}
