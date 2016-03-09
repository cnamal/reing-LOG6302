package com.namal.reing.output;

import com.namal.reing.models.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by namalgac on 2/25/16.
 */
public class GraphOutput extends AbstractOutput implements IPrint {

    private Set<String> set = new HashSet<>();

    private void printGraph(MNode node,PrintWriter writer){
        if(!set.contains(node.getId())) {
            set.add(node.getId());
            writer.println("\"" + node.getId() + "\"" + "[ label = \"" + node.getLabel() + "\"];");
            for (MNode child : node.getChildren()) {
                writer.println("\"" + node.getId() + "\"" + "->" + "\"" + child.getId() + "\"" + ";");
                printGraph(child, writer);
            }
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
