package com.namal.reing.output;

import com.namal.reing.models.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by namalgac on 2/25/16.
 */
public class GraphOutput extends AbstractOutput implements IPrint {

    private Set<String> set = new HashSet<>();

    private boolean out= true;

    private String printKills(List<MNode> list){
        String res = "";
        for(MNode pair: list){
            res+=pair.getLabelId()+ " ";
        }
        return res;
    }

    private String printIn(Map<MVariable,Set<String>> in, MVariable gen){
        String res ="";
        if(in==null)
            return res;
        Set<String> list = in.get(gen);
        if(list!=null)
            for(String label:list)
                res+=label+ " ";
        return res;
    }

    private String printOut(Map<MVariable,Set<String>> out){
        String res="";
        //FIXME this shouldn't be necessary
        if(out==null)
            return res;
        for (Map.Entry<MVariable, Set<String>> entry : out.entrySet()) {
            res+=entry.getKey().getName()+ "-";
            Set<String> list=entry.getValue();
            for(String label:list)
                res+= label +" ";
        }
        return res;
    }
    private void printGraph(MNode node,PrintWriter writer){
        if(!set.contains(node.getId())) {
            set.add(node.getId());
            writer.print("\"" + node.getId() + "\"" + "[ label = \"" + node.getLabelId()+ " "+node.getLabel());
            //System.out.println(node.getLabel() + " "+node.getIn()+ " "+ node.getOut());
            if(out){
                writer.print(" \\n ");
                if(node.getGen()!=null){
                    writer.print("GEN : "+ node.getGen().getName() + " \\n ");
                    writer.print("KILL : " + printKills(node.getGen().getKills())+" \\n ");
                    writer.print("IN : " + printIn(node.getIn(),node.getGen())+" \\n ");
                }
                writer.print("OUT : " + printOut(node.getOut()));
            }
            writer.println( "\"];");
            for (MNode child : node.getChildren()) {
                writer.println("\"" + node.getId() + "\"" + "->" + "\"" + child.getId() + "\"" + ";");
                printGraph(child, writer);
            }
            /*MNode parent =node.getDomTreeParent();
            if(parent != null){
                writer.println("\"" + parent.getId() + "\"" + "->" + "\"" + node.getId() + "\"" + ";");
            }
            for (MNode child : node.getChildren()) {
                printGraph(child, writer);
            }*/
        }
    }

    @Override
    public void dumpData() {
        try {
            PrintWriter writer;
            File file = new File(fileName);
            writer = new PrintWriter("graphs/" +file.getName()+ ".dot", "UTF-8");
            writer.println("digraph G {");
            for (MNode graph : nodes) {
                printGraph(graph, writer);
            }
            writer.println("}");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public IPrint getPrinter() {
        return this;
    }

    @Override
    public String print(MMethod m) {
        return null;
    }

    @Override
    public String print(MClass c) {
        return null;
    }

    @Override
    public String print(MField f) {
        return null;
    }

    @Override
    public String print(MType t) {
        return null;
    }

    @Override
    public String print(MInterface i) {
        return null;
    }

    @Override
    public String print(MModifier m) {
        return null;
    }
}
