package com.namal.reing;

public abstract class AbstractOutput{

	protected long id;
	protected long nbIf;
	protected long nbElse;

	protected String fileName;
	protected String className;


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

	public AbstractOutput setNbIf(long nbIf){
		this.nbIf=nbIf;
		return this;
	}

	public AbstractOutput setNbElse(long nbElse){
		this.nbIf=nbElse;
		return this;
	}

	public abstract void dumpData();
}
