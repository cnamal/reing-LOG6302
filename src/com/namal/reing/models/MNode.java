package com.namal.reing.models;

import java.util.*;

/**
 * Created by namalgac on 2/25/16.
 */
public class MNode {

    private List<MNode> children = new ArrayList<>();
    private List<MNode> predecessors = new ArrayList<>();
    private MNode endNode = null;
    //private List<MNode> successors = new
    private MNode domTreeParent;
    private List<MNode> cdgTreeParent;
    private List<MNode> cdgTreeChildren;
    private List<MNode> dataDependance;
    private List<MNode> reverseDataDependance;
    private String id;
    private String label;
    private String labelId;
    private Set<MVariable> gen= new HashSet<>();
    private Map<MVariable,Set<MNode>> in = null;
    private Map<MVariable,Set<MNode>> out = null;
    private Set<MVariable> data = new HashSet<>();
    private Map<String,MVariable> variables = null;
    private int lineNumber;

    public String getMethodName() {
        return methodName;
    }

    public int getLineNumber(){ return lineNumber;}

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    private String methodName = null;

    public Map<String,MVariable> getVariables() {
        return variables;
    }

    public void setVariables(Map<String,MVariable> variables) {
        this.variables = variables;
    }


    public void addNode(MNode node) {
        children.add(node);
    }
    public void addData(MVariable var){ data.add(var);}
    public void setEndNode(MNode node) {endNode = node;}

    public void addGen(MVariable var){
        gen.add(var);
    }



    public Set<MVariable> getGen(){
        return gen;
    }
    public Set<MVariable> getData() {return data;}
    /*public void addOut(Pair var){
        out.add(var);
    }*/

    public Map<MVariable,Set<MNode>> getIn(){ return in;}

    public Map<MVariable,Set<MNode>> getOut(){ return out;}

    public void initOut(){ out = new HashMap<>();}

    public List<MNode> getChildren() {
        return children;
    }

    public MNode(String name, int id,int line) {
        label = name;
        this.labelId = id + "";
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        out = new HashMap<>();
        lineNumber = line;
    }


    public MNode(String name,int line) {
        label = name;
        this.labelId = Integer.MAX_VALUE + "";
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        out = new HashMap<>();
        lineNumber = line;
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

    public void firstOut(){
        Stack<MNode> stack = new Stack<>();
        Set<MNode> visited = new HashSet<>();
        stack.add(this);
        if(data.size()!=0) {
            for(MVariable d : data){
                Set<MNode> list = new HashSet<>();
                list.add(this);
                out.put(d,list);
            }
        }
        while (!stack.empty()){
            MNode top = stack.pop();
            if(visited.contains(top))
                continue;
            visited.add(top);
            for(MVariable variable:top.getGen()){
                Set<MNode> list = new HashSet<>();
                list.add(top);
                top.out.put(variable, list);
            }
            for(MNode child :top.children){
                stack.add(child);
            }
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
                if(c.getGen().size()==0){
                    if(c.getOut()==null && c.predecessors.get(0).getOut()!=null) {
                        c.out = c.predecessors.get(0).getOut();
                        changed=true;
                    }
                }else{
                    if(in==null && c.predecessors.get(0).getOut()!=null) {

                        in = c.predecessors.get(0).getOut();
                        changed=true;
                        out.clear();
                        out.putAll(in);
                        for(MVariable gen :c.getGen()) {
                            Set<MNode> list = new HashSet<>();
                            list.add(c);
                            out.put(gen, list);
                        }
                    }
                }
            }else if(c.predecessors.size()>1){

                if(c.getGen().size()!=0 && in==null){
                    in = new HashMap<>();
                    changed=true;
                }

                if(c.getGen().size()!=0){
                    for(MNode pred: c.predecessors){
                        Map<MVariable, Set<MNode>> outPrev = pred.getOut();
                        if (outPrev == null)
                            continue;
                        for (Map.Entry<MVariable, Set<MNode>> entry : outPrev.entrySet()) {
                            MVariable variable = entry.getKey();
                            if(!in.containsKey(variable)) {
                                changed = true;
                                Set<MNode> newList = new HashSet<>();
                                newList.addAll(entry.getValue());
                                in.put(variable,newList);
                                continue;
                            }
                            Set<MNode> list = entry.getValue();
                            Set<MNode> prev = in.get(variable);

                            for (MNode label : list) {
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
                }
            }
            /*if(c.getLabelId().equals("1"))
                System.out.println(c.out);*/
            for (MNode pred : c.predecessors) {
                Map<MVariable, Set<MNode>> outPrev = pred.getOut();
                if (outPrev == null)
                    continue;
                for (Map.Entry<MVariable, Set<MNode>> entry : outPrev.entrySet()) {
                    MVariable variable = entry.getKey();
                    if(!out.containsKey(variable)) {
                        changed = true;
                        Set<MNode> newList = new HashSet<>();
                        newList.addAll(entry.getValue());
                        out.put(variable,newList);
                        continue;
                    }
                    Set<MNode> list = entry.getValue();
                    Set<MNode> prev = out.get(variable);
                    Set<MVariable> gen = getGen();
                    if(gen.size()>0){
                        boolean cont= false;
                        for(MVariable g:gen)
                            if(g==variable)
                                cont=true;
                        if (cont)
                            continue;
                    }

                    for (MNode label : list) {
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

    public void makeCDG(){
        List<Pair<MNode,MNode>> s = new ArrayList<>();
        Stack<MNode> stack = new Stack<>();
        Set<MNode> visited = new HashSet<>();
        stack.add(this);
        while (!stack.empty()){
            MNode top = stack.pop();
            if(visited.contains(top))
                continue;
            visited.add(top);

            //if(top.labelId.equals("6"))
            //System.out.println(top.getLabelId() + " "+ children.size());
            for(MNode child: top.children){
                stack.add(child);
                if(!postDominates(top,child))
                    s.add(new Pair<>(top,child));
            }
        }
        for(Pair<MNode,MNode> pair : s){
            ncaCdg(pair);
        }
    }

    public void makeDDG(){
        Stack<MNode> stack = new Stack<>();
        Set<MNode> visited = new HashSet<>();
        stack.add(this);
        while (!stack.empty()){
            MNode top = stack.pop();
            if(visited.contains(top))
                continue;
            visited.add(top);

            for(MVariable var : top.data){
                Set<MNode> nodes;
                if(top.in==null){
                    nodes = top.out.get(var);
                }else
                    nodes = top.in.get(var);
                if(nodes==null)
                    System.out.println(var.getName()+ " " +top.getLabelId()+" " + top.getLabel());
                for(MNode node : nodes){
                    if(node.dataDependance==null)
                        node.dataDependance = new ArrayList<>();
                    if(top.reverseDataDependance==null)
                        top.reverseDataDependance = new ArrayList<>();
                    node.dataDependance.add(top);
                    top.reverseDataDependance.add(node);
                }
            }
            //if(top.labelId.equals("6"))
            //System.out.println(top.getLabelId() + " "+ children.size());
            for(MNode child: top.children){
                stack.add(child);
            }
        }
    }

    public Set<MNode> bSlice(MVariable variable){
        if (!data.contains(variable) && !getGen().contains(variable)) {
            System.err.println(getLabelId() + " " + getLabel() + " doesn't have variable " + variable.getName());
            return null;
        }

        Stack<MNode> stack  = new Stack<>();
        Set<MNode> visited = new HashSet<>();
        stack.add(this);
        while (!stack.empty()){
            MNode top = stack.pop();
            if(visited.contains(top))
                continue;
            visited.add(top);
            if(top.reverseDataDependance!=null)
                for(MNode d : top.reverseDataDependance)
                    stack.add(d);

            if(top.cdgTreeParent!=null)
                for(MNode c : top.cdgTreeParent)
                    stack.add(c);

        }
        return visited;
    }
    @Override
    public boolean equals(Object o){
        if(o instanceof MNode) {
            return ((MNode) o).id.equals(id);
        }
        return false;
    }

    @Override
    public int hashCode(){
        return labelId.hashCode();
    }


    private boolean postDominates(MNode first, MNode second){
        MNode c = first;
        while(c != null){
            c = c.domTreeParent;
            if(c==second)
                return true;
        }
        return false;
    }

    private void ncaCdg(Pair<MNode,MNode> pair){
        HashSet<MNode> path = new HashSet<>();
        MNode c = pair.first;
        while(c != null){
            path.add(c);
            c = c.domTreeParent;
        }

        c = pair.second;
        while(c != null && !path.contains(c)){
            if(c.cdgTreeParent==null)
                c.cdgTreeParent = new ArrayList<>();
            if(pair.first.cdgTreeChildren==null)
                pair.first.cdgTreeChildren = new ArrayList<>();
            c.cdgTreeParent.add(pair.first);
            pair.first.cdgTreeChildren.add(c);
            c = c.domTreeParent;
        }
        if(c== pair.first) {
            c.cdgTreeParent.add(c);
            pair.first.cdgTreeChildren.add(c);
        }
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
    public List<MNode> getCdgTreeParent() { return cdgTreeParent;}

    public void clearDomTreeParent() {
        domTreeParent=null;
    }

    public MNode getEnd(){
        return endNode;
    }


    public List<MNode> getDataDependencies() {
        return dataDependance;
    }
}
