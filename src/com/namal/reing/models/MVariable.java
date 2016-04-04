package com.namal.reing.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by namalgac on 3/19/16.
 */
public class MVariable {

    String name;
    List<MNode> kills = new ArrayList<>();
    List<MNode> read = new ArrayList<>();
    Set<MNode> use = null;

    public void addKill(MNode node){ kills.add(node); }
    public void addRead(MNode node) { read.add(node);}

    public Set<MNode> getUse(){
        if(use==null) {
            use = new HashSet<>();
            use.addAll(kills);
            use.addAll(read);
        }
        return use;
    }

    public MVariable(String n){
        name = n;
    }

    public List<MNode> getKills(){
        return kills;
    }

    public String getName(){
        return name;
    }


    @Override
    public int hashCode(){
        return name.hashCode();
    }

}
