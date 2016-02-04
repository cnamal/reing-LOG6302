package com.namal.reing;

public class Field{

	private String fieldName;

	public Field(String fieldName){
		this.fieldName=fieldName;
	}
	
	public String getName(){
		return fieldName;
	}

	public String toString(){
		return ReingVisitor.getPrinter().print(this);
	}
}
