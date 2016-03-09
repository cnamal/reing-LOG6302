package com.namal.reing.output;
import java.util.*;
import com.namal.reing.models.*;

public abstract class AbstractOutput{

	protected long id;

	protected String fileName;
	protected List<MClass> classes;
	protected List<MInterface> interfaces;
	protected List<MNode> nodes;

	public AbstractOutput(){

	}

	public AbstractOutput setId(long id){
		this.id=id;
		return this;
	}

	public AbstractOutput setFileName(String fileName){
		this.fileName=fileName;
		return this;
	}


	public AbstractOutput setClasses(List<MClass> classes){
		this.classes=classes;
		return this;
	}

	public AbstractOutput setInterfaces(List<MInterface> interfaces){
		this.interfaces=interfaces;
		return this;
	}

	public AbstractOutput setGraphs(List<MNode> nodes){
		this.nodes=nodes;
		return this;
	}

	public abstract void dumpData();

	public abstract IPrint getPrinter();
}
