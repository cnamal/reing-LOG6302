package com.namal.reing;

public class JSONOutput extends AbstractOutput{
	public JSONOutput(){};

	public void dumpData(){
		System.out.println("{");
		System.out.println("\tid:"+id+",");
		System.out.println("\tfileName:"+fileName+",");
		System.out.println("}");
	}
}
