package com.namal.reing;
import java.util.*;

public abstract class AbstractOutput{

	protected long id;

	protected String fileName;
	protected String className;
	protected List<Class> classes;


	public AbstractOutput setId(long id){
		this.id=id;
		return this;
	}

	public AbstractOutput setFileName(String fileName){
		this.fileName=fileName;
		return this;
	}
	
	public AbstractOutput setClassName(String className){
		this.className=className;
		return this;
	}

	public AbstractOutput setClasses(List<Class> classes){
		this.classes=classes;
		return this;
	}

	public abstract void dumpData();

	public abstract IPrint getPrinter();
}
