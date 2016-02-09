package com.namal.reing.models;

import com.namal.reing.visitors.AbstractVisitor;

import java.util.ArrayList;
import java.util.List;

public class MInterface extends MCIE{

    private List<MType> extend = new ArrayList<>();


    public void addExtend(MType implement){
        this.extend.add(implement);
    }

    public List<MType> getExtend(){
        return extend;
    }


	public String toString(){
		return AbstractVisitor.getPrinter().print(this);
	}
}
