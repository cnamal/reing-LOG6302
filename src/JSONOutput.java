package com.namal.reing;

import java.util.*;

public class JSONOutput extends AbstractOutput implements IPrint {
	private Indent indentType;
	private int indentLevel;

	public enum Indent{
		TAB,SPACE
	}

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
		for(int i=0 ;i< l.size()-1;i++){
			s.append(l.get(i));
			print(",",s);
		}
		if(!l.isEmpty())
			s.append(l.get(l.size()-1));
		indentLevel--;
	}

	public String print (Method m){
		StringBuilder s = new StringBuilder();
		print("{",s);
		indentLevel++;
		print("\"methodName\": \""+m.getName()+"\",",s);
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

	public String print (Field f){
		StringBuilder s = new StringBuilder();
		print("{",s);
		indentLevel++;
		print("\"fieldName\": \""+f.getName()+"\"",s);
		indentLevel--;
		print("}",s);
		return s.toString();
	}

	public String print(Class c){
		StringBuilder s = new StringBuilder();
		print("{",s);
		indentLevel++;
		print("\"className\": \""+c.getName()+"\",",s);
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
