package com.namal.reing.output;

import com.namal.reing.models.*;
import java.util.*;


public class JSONOutput extends AbstractOutput implements IPrint {
	private Indent indentType;
	private int indentLevel;


	public JSONOutput(Indent indent){
		indentType=indent;
	}

	public JSONOutput(){
        indentType=Indent.SPACE;
	}

	private void spaceIndent(StringBuilder s){
		for(int i=0;i<indentLevel*2;i++)
			s.append(" ");
	}

	private void tabIndent(StringBuilder s){
		for(int i=0;i<indentLevel;i++)
			s.append("\t");
	}

	private void indent(StringBuilder s){
		if(indentType==Indent.SPACE)
			spaceIndent(s);
		else
			tabIndent(s);
	}

	private void print(String string,StringBuilder s){
		indent(s);
		s.append(string+"\n");
	}

	private void print(List<?> l,StringBuilder s){
		indentLevel++;
        boolean isString=false;
        if(!l.isEmpty()){
            isString = l.get(0).getClass()=="".getClass();
            if(!isString)
                s.append(l.get(0));
            else
                print("\""+l.get(0)+"\"",s);
        }
        for(int i=1 ;i< l.size();i++){
            print(",",s);
            if(!isString)
                s.append(l.get(i));
            else
                print("\""+l.get(i)+"\"",s);
        }
        indentLevel--;
    }


    @Override
    public String print (MMethod m){
        StringBuilder s = new StringBuilder();
        print("{",s);
        indentLevel++;
        print("\"methodName\": \""+m.getName()+"\",",s);
        print("\"returnType\": \""+((m.getRet()!=null)?m.getRet().getFullName():"")+"\",",s);
        print("\"modifiers\": [",s);
        print(m.getMod().getList(),s);
        print("],",s);
        print("\"params\": [",s);
        print(m.getParams(),s);
        print("],",s);
        print("\"nbVariable\": "+m.getVariable()+",",s);
        print("\"nbIf\": "+m.getIf()+",",s);
        print("\"nbElse\": "+m.getElse()+",",s);
        print("\"nbSwitch\": "+m.getSwitch()+",",s);
        print("\"nbFor\": "+m.getFor()+",",s);
        print("\"nbWhile\": "+m.getWhile()+",",s);
        print("\"nbBreak\": "+m.getBreak()+",",s);
        print("\"nbContinue\": "+m.getContinue()+",",s);
        print("\"nbTry\": "+m.getTry()+",",s);
        print("\"nbThrow\": "+m.getThrow()+",",s);
        print("\"nbReturn\": "+m.getReturn()+",",s);
        print("\"nbSynchronized\": "+m.getSynchronized()+",",s);
        print("\"classes\" : [",s);
        print(m.getClasses(),s);
        print("]",s);
        indentLevel--;
        print("}",s);
        return s.toString();
    }

    @Override
    public String print (MField f){
        StringBuilder s = new StringBuilder();
        print("{",s);
        indentLevel++;
        print("\"name\": \""+f.getName()+"\",",s);
        print("\"type\": "+f.getType(),s);
        indentLevel--;
        print("}",s);
        return s.toString();
    }

    @Override
    public String print (MType t){
        /*StringBuilder s = new StringBuilder();
        print("\""+t.getFullName()+"\"",s);*/
        return "\"" + t.getFullName() + "\"";
    }

    @Override
    public String print(MInterface i) {
        StringBuilder s = new StringBuilder();
        print("{",s);
        indentLevel++;
        print("\"className\": \""+i.getName()+"\",",s);
        print("\"package\": \""+i.getPackage()+"\",",s);
        print("\"extends\": [",s);
        print(i.getExtend(),s);
        print("],",s);
        print("\"classes\" : [",s);
        print(i.getClasses(),s);
        print("],",s);
        print("\"fields\" : [",s);
        print(i.getFields(),s);
        print("],",s);
        print("\"methods\" : [",s);
        print(i.getMethods(),s);
        print("]",s);
        indentLevel--;
        print("}",s);
        return s.toString();
    }

    @Override
    public String print(MModifier m) {
        return null;
    }

    @Override
    public String print(MClass c){
        StringBuilder s = new StringBuilder();
        print("{",s);
        indentLevel++;
        print("\"className\": \""+c.getName()+"\",",s);
        print("\"package\": \""+c.getPackage()+"\",",s);
        print("\"extends\": "+c.getExtend(),s);
        print("\"implements\": [",s);
        print(c.getImplement(),s);
        print("],",s);
        print("\"classes\" : [",s);
        print(c.getClasses(),s);
        print("],",s);
        print("\"fields\" : [",s);
        print(c.getFields(),s);
        print("],",s);
        print("\"methods\" : [",s);
        print(c.getMethods(),s);
        print("]",s);
        indentLevel--;
        print("}",s);
        return s.toString();
    }

    @Override
    public void dumpData(){
        StringBuilder s=new StringBuilder();
        print("{",s);
        indentLevel++;
        print("\"id\":"+id+",",s);
        print("\"fileName\": \""+fileName+"\",",s);
        print("\"classes\": [",s);
        print(classes,s);
        print("]",s);
        indentLevel--;
        print("}",s);
        System.out.println(s);
    }

    @Override
    public IPrint getPrinter(){
        return this;
    }
}
