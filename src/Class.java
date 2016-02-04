package com.namal.reing;
import java.util.*;

public class Class{

	private List<Method> methods= new ArrayList<>();
	private List<Field> fields = new ArrayList<>();
	private List<Class> classes= new ArrayList<>();
	private String name;
	private IPrint ip;

	
	public void addMethod(Method method){
		methods.add(method);
	}

	public void addField(Field field){
		fields.add(field);
	}
	
	public void addClass(Class c){
		classes.add(c);
	}

	public List<Method> getMethods(){
		return methods;
	}

	public List<Field> getFields(){
		return fields;
	}

	public List<Class> getClasses(){
		return classes;
	}

	public void setName(String className){
		this.name=className;
	}

	public String getName(){
		return name;
	}

	public String toString(){
		return ReingVisitor.getPrinter().print(this);
	}
}
