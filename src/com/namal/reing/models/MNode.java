package com.namal.reing.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by namalgac on 2/25/16.
 */
public class MNode {

    private List<MNode> children = new ArrayList<>();
    private String id;
    private String label;

    public void addNode(MNode MNode){
        children.add(MNode);
    }

    public List<MNode> getChildren(){
        return children;
    }

    public MNode(String name){
        label = name;
        id =  UUID.randomUUID().toString().replaceAll("-", "");
    }

    public String getLabel(){
        return label;
    }

    public String getId(){
        return id;
    }
}
