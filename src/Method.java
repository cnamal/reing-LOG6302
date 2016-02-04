package com.namal.reing;

import java.util.*;

public class Method{
	private int nbVariable=0;
	private int nbIf=0;
	private int nbElse=0;
	private int nbWhile=0;
	private int nbFor=0;
	private int nbSwitch=0;
	private int nbBreak=0;
	private int nbContinue=0;
	private int nbReturn=0;
	private int nbThrow=0;
	private int nbSynchronized=0;
	private int nbTry=0;
	
	private List<Class> classes= new ArrayList<>();

	private String methodName;

	public void incrVariable(){
		nbVariable++;
	}

	public void incrIf(){
		nbIf++;
	}

	public void incrElse(){
		nbElse++;
	}
	
	public void incrWhile(){
		nbWhile++;
	}
	
	public void incrFor(){
		nbFor++;
	}
	
	public void incrBreak(){
		nbBreak++;
	}
	
	public void incrContinue(){
		nbContinue++;
	}
	
	public void incrSwitch(){
		nbSwitch++;
	}
	
	public void incrReturn(){
		nbReturn++;
	}
	
	public void incrThrow(){
		nbThrow++;
	}
	
	public void incrSynchronized(){
		nbSynchronized++;
	}
	
	public void incrTry(){
		nbTry++;
	}
	
	public void addClass(Class c){
		classes.add(c);
	}

	public Method(String methodName){
		this.methodName=methodName;
	}

	public int getIf(){
		return nbIf;
	}
	
	public int getElse(){
		return nbElse;
	}

	public int getSwitch(){
		return nbSwitch;
	}

	public int getFor(){
		return nbFor;
	}

	public int getWhile(){
		return nbWhile;
	}

	public int getBreak(){
		return nbBreak;
	}

	public int getContinue(){
		return nbContinue;
	}

	public String getName(){
		return methodName;
	}

	public int getReturn(){
		return nbReturn;
	}

	public int getTry(){
		return nbTry;
	}

	public int getThrow(){
		return nbThrow;
	}

	public int getVariable(){
		return nbVariable;
	}

	public int getSynchronized(){
		return nbSynchronized;
	}

	public List<Class> getClasses(){
		return classes;
	}

	public String toString(){
		return ReingVisitor.getPrinter().print(this);
	}
}
