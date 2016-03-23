package com.namal.reing.visitors;

import com.namal.reing.*;
import com.namal.reing.models.MNode;
import com.namal.reing.models.MVariable;
import com.namal.reing.output.AbstractOutput;

import java.util.*;

/**
 * Created by namalgac on 2/25/16.
 */
public class GraphVisitor extends AbstractVisitor {

    private List<MNode> graphs = new ArrayList<>();
    private MNode currNode =null;
    private Stack<MNode> beginNode = new Stack<>();
    private Stack<MNode> endNode = new Stack<>();
    private MNode endMethod = null;
    private boolean nestedLoops = false;
    private int nestedCond = 0;
    private boolean nested = false;
    private int ifNb=0;
    private int id=0;

    private Map<String,MVariable> variables;

    private void updateNested(){
        nested = nestedCond>0|nestedLoops;
    }

    private Object methodOrConstr(Node node, Object data, String name){
        //System.err.println(name);
        id=0;
        variables = new HashMap<>();
        MNode currGraph = currNode = new MNode("Entry " + name,id++);
        currGraph.initOut();
        endMethod = new MNode("End " + name);
        currGraph.setEndNode(endMethod);
        propagate(node,data);
        if(currNode!=null)
            currNode.addNode(endMethod);
        graphs.add(currGraph);
        currNode=null;
        endMethod.setLabelId(id);
        return data;
    }

    @Override
    public Object visit(VoidMethodDecl node, Object data){
        String methodName= ((Identifier)node.jjtGetChild(0)).jjtGetFirstToken().image;
        return methodOrConstr(node,data,methodName);
    }

    @Override
    public Object visit(MethodDeclaratorRest node, Object data){
        if(!(data instanceof String))
            throw new RuntimeException("The data was supposed to be a String");
        return methodOrConstr(node,data,(String)data);
    }

    @Override
    public Object visit(ConstructorDeclaratorRest node, Object data){
        if(!(data instanceof String))
            throw new RuntimeException("The data was supposed to be a String");
        return methodOrConstr(node,data,(String)data);
    }

    @Override
    public Object visit(ConstructorDecl node,Object data){
        propagate(node,((Identifier)node.jjtGetChild(0)).jjtGetFirstToken().image);
        return data;
    }

    @Override
    public Object visit(GenericMethodOrConstructorRest node,Object data){
        Identifier id = node.jjtGetNumChildren()==2?((Identifier)node.jjtGetChild(0)):((Identifier)node.jjtGetChild(1));
        propagate(node,id.jjtGetFirstToken().image);
        return data;
    }

    @Override
    public Object visit(MethodOrFieldDecl node,Object data){
        propagate(node,((Identifier)node.jjtGetChild(1)).jjtGetFirstToken().image);
        return data;
    }

    @Override
    public Object visit(NormalClassDeclaration node,Object data){
        if(currNode==null)
            propagate(node,data);
        return data;
    }

    private MNode[] initLoop(String beg,String end){
        nestedLoops = true;
        updateNested();
        MNode begin = new MNode(beg,id++,currNode,false);
        beginNode.push(begin);
        MNode cond = new MNode("Condition",id++,begin,false);
        MNode en = new MNode(end);
        cond.addNode(en);
        begin.addNode(cond);
        currNode.addNode(begin);
        currNode = cond;
        endNode.push(en);
        return new MNode[]{begin,cond};

    }

    private void endLoop(MNode[] init){
        if(currNode!=init[1]) {
            if(currNode!=null)
                currNode.addNode(init[0]);
        }
        currNode=endNode.pop();
        currNode.setLabelId(id++);
        beginNode.pop();
        if(endNode.isEmpty()) {
            nestedLoops = false;
            updateNested();
        }
    }

    @Override
    public Object visit(WhileStatement node, Object data){
        MNode[] init=initLoop("WhileBegin","WhileEnd");
        propagate(node,data);
        endLoop(init);
        return data;
    }

    @Override
    public Object visit(ForStatement node,Object data){
        MNode[] init=initLoop("ForBegin","ForEnd");
        propagate(node,data);
        endLoop(init);
        return data;
    }

    @Override
    public Object visit(BreakStatement node, Object data){
        MNode breakNode = new MNode("BreakStmt",id++);
        currNode.addNode(breakNode);
        breakNode.addNode(endNode.peek());
        if(!nested)
            currNode=null;
        return data;
    }

    @Override
    public Object visit(ReturnStatement node, Object data){
        MNode returnStmt = new MNode("ReturnStmt",id++,currNode,false);
        currNode.addNode(returnStmt);
        returnStmt.addNode(endMethod);
        if(!nested)
            currNode =null;
        return data;
    }

    @Override
    public Object visit(ContinueStatement node, Object data){
        MNode continueStmt = new MNode("ContinueStmt",id++,currNode,false);
        currNode.addNode(continueStmt);
        continueStmt.addNode(beginNode.peek());
        if(!nested)
            currNode=null;
        return data;
    }

    @Override
    public Object visit(IfStatement node,Object data){
        nestedCond++;
        updateNested();
        MNode ifNode = new MNode("IfBegin",id++,currNode,false);
        MNode cond = new MNode("Condition",id++,ifNode,false);
        MNode end = new MNode("IfEnd");
        ifNode.addNode(cond);
        currNode.addNode(ifNode);
        currNode = cond;

        for(int i=0;i<node.jjtGetNumChildren()-1;i++)
            node.jjtGetChild(i).jjtAccept(this,data);


        if(currNode!=cond)
            currNode.addNode(end);

        if(node.jjtGetChild(node.jjtGetNumChildren()-1) instanceof ElseStatement){
            currNode = cond;
            node.jjtGetChild(node.jjtGetNumChildren()-1).jjtAccept(this,data);
            if(currNode!=cond)
                currNode.addNode(end);
        }else
            cond.addNode(end);
        end.setLabelId(id++);
        currNode = end;
        nestedCond--;
        updateNested();
        return data;
    }

    @Override
    public Object visit(SwitchStatement node,Object data){
        MNode[] init=initLoop("SwitchBegin","SwitchEnd");
        propagate(node,data);
        endLoop(init);
        return data;
    }

    @Override
    public Object visit(DoStatement node,Object data){
        nestedLoops = true;
        updateNested();
        MNode begin = new MNode("DoBegin",id++,currNode,false);
        MNode beginWhile = new MNode("DoWhileBegin");
        MNode cond = new MNode("Condition");
        MNode end = new MNode("DoEnd");
        MNode endWhile = new MNode("DoWhileEnd");

        cond.addNode(begin);
        cond.addNode(endWhile);

        beginNode.push(beginWhile);

        beginWhile.addNode(cond);
        endWhile.addNode(end);

        endNode.push(end);
        currNode.addNode(begin);
        currNode = begin;
        node.jjtGetChild(0).jjtAccept(this,data);
        if(currNode!=begin)
            currNode.addNode(beginWhile);
        currNode = endNode.pop();
        beginNode.pop();
        beginWhile.setLabelId(id++);
        cond.setLabelId(id++);
        endWhile.setLabelId(id++);
        end.setLabelId(id++);
        if(endNode.isEmpty()) {
            nestedLoops = false;
            updateNested();
        }
        return data;
    }

    public Object visit (VariableDeclarator node, Object data){
        MVariable variable = new MVariable(((Identifier)node.jjtGetChild(0)).jjtGetFirstToken().image);
        variables.put(variable.getName(),variable);
        //VariableDeclaratorRest child = (VariableDeclaratorRest) node.jjtGetChild(1);
        //if(child.jjtGetChild(child.jjtGetNumChildren()-1) instanceof VariableInitializer) {
        MNode init = new MNode("Variable init",id++,currNode,true);
        currNode.addNode(init);
        currNode = init;
        variable.addKill(currNode);
        currNode.setGen(variable);
        Map<MVariable,Set<String>> in = init.getIn();
        Map<MVariable,Set<String>> out = init.getOut();
        out.putAll(in);
        Set<String> list = new HashSet<>();
        list.add(currNode.getLabelId());
        out.put(variable,list);
        /*if(in.h(variable)){

        }*/
        //}
        return data;
    }

    public Object visit (Expression node, Object data){
        if(node.jjtGetNumChildren()>1) {
            Node first = node.jjtGetChild(0);
            Object ret = first.jjtAccept(this, data);
            if (ret != null) {
                String var = (String) ret;
                MVariable variable = variables.get(var);
                if(variable!=null){
                    MNode assign = new MNode("Assignment",id++,currNode,true);
                    currNode.addNode(assign);
                    currNode = assign;
                    variable.addKill(currNode);
                    currNode.setGen(variable);
                    Map<MVariable,Set<String>> in = assign.getIn();
                    Map<MVariable,Set<String>> out = assign.getOut();
                    if(in!=null)
                        out.putAll(in);
                    Set<String> list = new HashSet<>();
                    list.add(currNode.getLabelId());
                    out.put(variable,list);
                }
            }
        }
        return data;
    }

    public Object visit (Expression1 node, Object data){
        return node.jjtGetChild(0).jjtAccept(this,data);
    }

    public Object visit (Expression2 node, Object data){
        return node.jjtGetChild(0).jjtAccept(this,data);
    }

    public Object visit (Expression3 node, Object data){
        return node.jjtGetChild(0).jjtAccept(this,data);
    }
    public Object visit (Primary node,Object data){
        Node first = node.jjtGetChild(0);
        if(first instanceof Identifier)
            return ((Identifier) first).jjtGetFirstToken().image;
        return null;
    }
    public Object visit (Creator node,Object data){
        return data;
    }

    public Object visit (TryStatement node, Object data){return data;}

    public Object visit (CatchClause node, Object data){return data;}

    public Object visit (StaticInitBlock node, Object data){return data;}

    public Object visit(FieldDeclaratorsRest node, Object data){return data;}

    public Object visit(GenericMethodOrConstructorDecl node,Object data){return data;}

    @Override
    public void dumpData(AbstractOutput absOutput) {
        absOutput.setGraphs(graphs);
        ip=absOutput.getPrinter();
        absOutput.dumpData();
    }

    public void dom(){
        boolean pred = false;
        for(MNode graph : graphs){
            HashSet<MNode> visited = new HashSet<>();
            graph.reverseCFG(visited);
            graph.getDFSTree(pred);
            do {
                visited = new HashSet<>();
            }while(graph.makeDomTree(visited,pred));
        }
    }

    public void inOut(){
        for(MNode graph:graphs){
            HashSet<MNode> visited = new HashSet<>();
            graph.reverseCFG(visited);
            int i=0;
            do {
                i++;
                //System.out.println();
                visited = new HashSet<>();
                //System.out.println();
            }while(graph.makeInOut(visited));
            //System.out.println();
        }
    }
}
