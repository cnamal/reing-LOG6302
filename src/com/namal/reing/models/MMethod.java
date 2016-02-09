package com.namal.reing.models;

import com.namal.reing.visitors.AbstractVisitor;
import com.namal.reing.visitors.ReingVisitor;

import java.util.ArrayList;
import java.util.List;

public class MMethod{
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
    private MType ret;
    private MModifier mod;
	
	private List<MCIE> classes= new ArrayList<>();
	private List<MField> params = new ArrayList<>();

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
	
	public void addClass(MCIE c){
		classes.add(c);
	}

    public void setRet(MType ret){
        this.ret=ret;
    }

    public void setMod(MModifier mod){
        this.mod=mod;
    }

	public MMethod(String methodName){
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

	public MType getRet(){
		return ret;
	}

	public MModifier getMod(){
		return mod;
	}

	public int getSynchronized(){
		return nbSynchronized;
	}

	public List<MCIE> getClasses(){
		return classes;
	}

    public void addParam(MField f){
        params.add(f);
    }

    public List<MField> getParams(){
        return params;
    }

	public String toString(){
		return AbstractVisitor.getPrinter().print(this);
	}
}
