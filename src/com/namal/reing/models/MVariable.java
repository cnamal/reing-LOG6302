package com.namal.reing.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by namalgac on 3/19/16.
 */
public class MVariable {

    String name;
    List<MNode> kills = new ArrayList<>();

    public void addKill(MNode node){ kills.add(node); }

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
