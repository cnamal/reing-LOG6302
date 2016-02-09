package com.namal.reing.models;

import com.namal.reing.visitors.AbstractVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by namalgac on 2/8/16.
 */
public class MModifier {

    private List<String> mod = new ArrayList<>();

    public void addModifier(String s){
        mod.add(s);
    }

    public String toString(){
        return AbstractVisitor.getPrinter().print(this);
    }

    public Accessibility getAccessibility(){
        if(mod.contains("public"))
            return Accessibility.PUBLIC;
        if(mod.contains("private"))
            return Accessibility.PRIVATE;
        if(mod.contains("protected"))
            return Accessibility.PROTECTED;
        return Accessibility.PACKAGE;
    }

    public List getList(){
        return mod;
    }
}

