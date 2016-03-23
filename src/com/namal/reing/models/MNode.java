package com.namal.reing.models;

import java.util.*;

/**
 * Created by namalgac on 2/25/16.
 */
public class MNode {

    private List<MNode> children = new ArrayList<>();
    private List<MNode> predecessors = new ArrayList<>();
    private MNode endNode = null;
    private boolean reversed = false;
    //private List<MNode> successors = new
    private MNode domTreeParent;
    private String id;
    private String label;
    private String labelId;
    private MVariable gen=null;
    private Map<MVariable,Set<String>> in = null;
    private Map<MVariable,Set<String>> out = null;

    public void addNode(MNode MNode) {
        children.add(MNode);
    }
    public void setEndNode(MNode node) {endNode = node;}

    public void setGen(MVariable var){
        gen = var;
    }

    public MVariable getGen(){
        return gen;
    }

    /*public void addOut(Pair var){
        out.add(var);
    }*/

    public Map<MVariable,Set<String>> getIn(){ return in;}

    public Map<MVariable,Set<String>> getOut(){ return out;}

    public void initOut(){ out = new HashMap<>();}

    public List<MNode> getChildren() {
        return children;
    }

    public MNode(String name, int id) {
        label = name;
        this.labelId = id + "";
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
    }

    public MNode(String name, int id,MNode prev,boolean inB) {
        label = name;
        this.labelId = id + "";
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        if(inB) {
            in = prev.out;
            out = new HashMap<>();
        }else
            out = prev.out;
    }

    public MNode(String name) {
        label = name;
        this.labelId = Integer.MAX_VALUE + "";
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
    }

    public String getLabel() {
        return label;
    }

    public String getId() {
        return id;
    }

    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(int id) {
        this.labelId = id + "";
    }

    public void reverseCFG(HashSet<MNode> visited) {
        if(reversed)
            return;
        reversed=true;
        if(!visited.contains(this)) {
            visited.add(this);
            for (MNode n : children) {
                n.predecessors.add(this);
                n.reverseCFG(visited);
            }
        }
    }

    public void getDFSTree(boolean pred){
        Stack<MNode> s = new Stack<>();
        HashSet<MNode> visited = new HashSet<>();
        if(pred)
            s.push(this);
        else
            s.push(endNode);
        while(!s.empty()){
            MNode top = s.pop();
            if(visited.contains(top)){
                continue;
            }

            List<MNode> list = pred ? top.children:top.predecessors;
            for(MNode v : list){
                if(!visited.contains(v) && v != top){
                    s.push(v);
                    v.domTreeParent = top;
                }
            }
            visited.add(top);
        }
    }

    public boolean makeInOut(HashSet<MNode> visited){
        boolean changed = false;
        MNode c = this;
        if(!visited.contains(c)) {
            visited.add(c);
            //System.out.println(getLabel()+ " "+ c.predecessors.size()+ " " + in + " "+ out);
            //List<MNode> list = pred ? c.predecessors:c.children;

            if(c.predecessors.size()==1){
                if(c.getGen()==null){
                    if(c.getOut()==null && c.predecessors.get(0).getOut()!=null) {
                        c.out = c.predecessors.get(0).getOut();
                        changed=true;
                        //System.out.println("CHANGED 1 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    }
                }else{
                    if(in==null && c.predecessors.get(0).getOut()!=null) {
                        in = c.predecessors.get(0).getOut();
                        changed=true;
                        //System.out.println("CHANGED 2 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        out.clear();
                        out.putAll(in);
                        Set<String> list = new HashSet<>();
                        list.add(getLabelId());
                        out.put(getGen(),list);
                    }
                }
            }else if(c.predecessors.size()>1){
                if(c.getGen()!=null){
                    throw new UnsupportedOperationException();
                }

                if(out == null)
                    out = new HashMap<>();
                /*for(MNode pred : c.predecessors){
                    Map<MVariable,Set<String>> outPrev = pred.getOut();
                    if(outPrev==null)
                        continue;
                    for (Map.Entry<MVariable, Set<String>> entry : outPrev.entrySet()) {
                        MVariable variable=entry.getKey();
                        if(!out.containsKey(variable)) {
                            changed = true;
                            //System.out.println("CHANGED 3 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                            Set<String> newList = new HashSet<>();
                            newList.addAll(entry.getValue());
                            out.put(variable,newList);
                            //out.put(variable,entry.getValue());
                            continue;
                        }
                        Set<String> list=entry.getValue();
                        Set<String> prev = out.get(variable);
                        for(String label:list) {
                            if (!prev.contains(label)){
                                changed=true;
                                //System.out.println("CHANGED 4 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " + variable+ " "+label);
                                //Set<String> newList = new HashSet<>();
                                //newList.addAll(list);
                                //out.put(variable,newList);
                                //break;
                                prev.add(label);
                            }
                        }
                    }
                }*/
            }
            for (MNode pred : c.predecessors) {
                Map<MVariable, Set<String>> outPrev = pred.getOut();
                if (outPrev == null)
                    continue;
                for (Map.Entry<MVariable, Set<String>> entry : outPrev.entrySet()) {
                    MVariable variable = entry.getKey();
                    if(!out.containsKey(variable)) {
                        changed = true;
                        //System.out.println("CHANGED 3 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                        Set<String> newList = new HashSet<>();
                        newList.addAll(entry.getValue());
                        out.put(variable,newList);
                        //out.put(variable,entry.getValue());
                        continue;
                    }
                    Set<String> list = entry.getValue();
                    Set<String> prev = out.get(variable);
                    MVariable gen = getGen();
                    if(gen!=null && gen==variable)
                        continue;
                    for (String label : list) {
                        if (!prev.contains(label)) {
                            changed = true;
                            //System.out.println("CHANGED 4 !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! " + variable + " " + label);
                                /*Set<String> newList = new HashSet<>();
                                newList.addAll(list);
                                out.put(variable,newList);
                                break;*/
                            prev.add(label);
                        }
                    }
                }
            }
            /*if (list.size() > 0) {
                Iterator<MNode> predItr = list.iterator();
                MNode parent = predItr.next();
                while (predItr.hasNext()) {
                    MNode p = predItr.next();
                    parent = nca(parent, p);
                }
                if (c.domTreeParent != parent) {
                    c.domTreeParent = parent;
                    changed = true;
                }
            }*/

            for (MNode n : this.children) {
                changed =  n.makeInOut(visited) || changed;
            }

        }
        return changed;
    }

    public boolean makeDomTree(HashSet<MNode> visited,boolean pred){
        /*getDFSTree();
        reverseCFG();*/
        boolean changed = false;
        MNode c = this;
        if(!visited.contains(c)) {
            visited.add(c);
            List<MNode> list = pred ? c.predecessors:c.children;


            if (list.size() > 0) {
                Iterator<MNode> predItr = list.iterator();
                MNode parent = predItr.next();
                while (predItr.hasNext()) {
                    MNode p = predItr.next();
                    parent = nca(parent, p);
                }
                if (c.domTreeParent != parent) {
                    c.domTreeParent = parent;
                    changed = true;
                }
            }

            for (MNode n : this.children) {
                changed =  n.makeDomTree(visited,pred) || changed;
            }

        }
        return changed;
    }

    private MNode nca(MNode n1, MNode n2){
        HashSet<MNode> path = new HashSet<>();
        MNode c = n1;
        while(c != null){
            path.add(c);
            c = c.domTreeParent;
        }

        c = n2;
        while(c != null && !path.contains(c)){
            c = c.domTreeParent;
        }

        return c;
    }

    public MNode getDomTreeParent(){
        return domTreeParent;
    }

}
