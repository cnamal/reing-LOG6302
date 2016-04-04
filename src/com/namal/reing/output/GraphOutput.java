package com.namal.reing.output;

import com.namal.reing.models.*;
import com.namal.reing.utils.Configuration;
import com.namal.reing.utils.EConfiguration;

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

    private String printIn(Map<MVariable,Set<MNode>> in, MVariable gen){
        String res ="";
        Set<MNode> list = in.get(gen);
        if(list!=null)
            for(MNode label:list)
                res+=label.getLabelId()+ " ";
        return res;
    }

    private String printOut(Map<MVariable,Set<MNode>> out){
        String res="";
        for (Map.Entry<MVariable, Set<MNode>> entry : out.entrySet()) {
            res+=entry.getKey().getName()+ "-";
            Set<MNode> list=entry.getValue();
            for(MNode label:list)
                res+= label.getLabelId() +" ";
        }
        return res;
    }
    private void printGraph(MNode node,PrintWriter writer){
        if(!set.contains(node.getId())) {
            set.add(node.getId());
            writer.print("\"" + node.getId() + "\"" + "[ label = \"" + node.getLabelId()+ " "+node.getLabel());
            Set<EConfiguration> configurations = Configuration.getTP5Config();
            if(configurations.contains(EConfiguration.INOUT)){
                writer.print(" \\n ");
                if(node.getGen().size()>0){
                    for(MVariable v : node.getGen()) {
                        writer.print("GEN : " + v.getName() + " \\n ");
                        writer.print("KILL : " + printKills(v.getKills()) + " \\n ");
                        writer.print("IN : " + printIn(node.getIn(), v) + " \\n ");
                    }
                }
                writer.print("OUT : " + printOut(node.getOut()));
            }
            writer.println( "\"];");
            if(configurations.contains(EConfiguration.DDG)||configurations.contains(EConfiguration.PDG))
                if(node.getDataDependencies()!=null)
                    for (MNode data : node.getDataDependencies())
                        writer.println("\"" + node.getId() + "\"" + "->" + "\"" + data.getId() + "\"  [color=red]" + ";");


            if(configurations.contains(EConfiguration.CFG)) {
                for (MNode child : node.getChildren()) {
                    writer.println("\"" + node.getId() + "\"" + "->" + "\"" + child.getId() + "\"" + ";");
                    printGraph(child, writer);
                }
            }
            if(configurations.contains(EConfiguration.DOM)||configurations.contains(EConfiguration.PDOM)) {
                MNode parent = node.getDomTreeParent();
                if (parent != null) {
                    writer.println("\"" + parent.getId() + "\"" + "->" + "\"" + node.getId() + "\"" + ";");
                }
                for (MNode child : node.getChildren()) {
                    printGraph(child, writer);
                }
            }
            if(configurations.contains(EConfiguration.CDG)|| configurations.contains(EConfiguration.PDG)) {
                List<MNode> parents = node.getCdgTreeParent();
                if (parents != null) {
                    for (MNode parent : parents)
                        writer.println("\"" + parent.getId() + "\"" + "->" + "\"" + node.getId() + "\"" + ";");
                }
                for (MNode child : node.getChildren()) {
                    printGraph(child, writer);
                }
            }
        }
    }

    private String printGen(List<MVariable> gen) {
        String res="";
        for(MVariable g:gen){
            res+=g + " ";
        }
        return res;
    }


    @Override
    public void dumpData() {
        try {
            PrintWriter writer;
            File file = new File(fileName);
            writer = new PrintWriter("graphs/" +file.getName()+ ".dot", "UTF-8");
            writer.println("digraph G {");
            for (MNode graph : nodes)
                printGraph(graph, writer);
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
