package com.namal.reing.output;
import java.util.*;
import com.namal.reing.models.*;

public abstract class AbstractOutput{

	protected long id;

	protected String fileName;
	protected List<MCIE> classes;

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


	public AbstractOutput setClasses(List<MCIE> classes){
		this.classes=classes;
		return this;
	}

	public abstract void dumpData();

	public abstract IPrint getPrinter();
}
