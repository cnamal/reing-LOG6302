package com.namal.reing.models;

import com.namal.reing.visitors.AbstractVisitor;

public class MField{

	private String fieldName;
    private MModifier mod;
    private MType type;

	public MField(String fieldName){
		this.fieldName=fieldName;
	}
	
	public String getName(){
		return fieldName;
	}

	public String toString(){
		return AbstractVisitor.getPrinter().print(this);
	}

    public void setType(MType type){
        this.type=type;
    }

    public void setMod(MModifier mod){
        this.mod=mod;
    }

    public MType getType(){
        return type;
    }

    public MModifier getMod(){
        return mod;
    }
}
