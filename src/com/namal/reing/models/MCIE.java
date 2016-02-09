package com.namal.reing.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by namalgac on 2/8/16.
 */
public abstract class MCIE {

    private String name;
    private String pack="";
    private List<MMethod> methods= new ArrayList<>();
    private List<MField> fields = new ArrayList<>();
    private List<MCIE> classes= new ArrayList<>();

    public void setPackage(String pack){
        this.pack=pack;
    }

    public String getPackage(){
        return pack;
    }

    public String getName(){
        return name;
    }

    public String getFullName(){
        String res = pack;
        if(!pack.equals(""))
            res+=".";
        return res+name;
    }


    public void setName(String name){
        this.name=name;
    }

    public void addMethod(MMethod method){
        methods.add(method);
    }

    public void addField(MField field){
        fields.add(field);
    }

    public void addClass(MCIE c){
        classes.add(c);
    }

    public List<MMethod> getMethods(){
        return methods;
    }

    public List<MField> getFields(){
        return fields;
    }

    public List<MCIE> getClasses(){
        return classes;
    }
}
