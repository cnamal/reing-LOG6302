package com.namal.reing.visitors;

import com.namal.reing.*;
import com.namal.reing.models.MMethod;
import com.namal.reing.models.MNode;
import com.namal.reing.output.AbstractOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

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

    private void updateNested(){
        nested = nestedCond>0|nestedLoops;
    }

    private Object methodOrConstr(Node node, Object data, String name){
        //System.err.println(name);
        MNode currGraph = currNode = new MNode("Entry " + name);
        endMethod = new MNode("End " + name);
        propagate(node,data);
        if(currNode!=null)
            currNode.addNode(endMethod);
        graphs.add(currGraph);
        currNode=null;
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
        MNode begin = new MNode(beg);
        beginNode.push(begin);
        MNode cond = new MNode("Condition");
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
        MNode breakNode = new MNode("BreakStmt");
        currNode.addNode(breakNode);
        breakNode.addNode(endNode.peek());
        if(!nested)
            currNode=null;
        return data;
    }

    @Override
    public Object visit(ReturnStatement node, Object data){
        MNode returnStmt = new MNode("ReturnStmt");
        currNode.addNode(returnStmt);
        returnStmt.addNode(endMethod);
        if(!nested)
            currNode =null;
        return data;
    }

    @Override
    public Object visit(ContinueStatement node, Object data){
        MNode continueStmt = new MNode("ContinueStmt");
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
        MNode ifNode = new MNode("IfBegin"+ ++ifNb);
        MNode cond = new MNode("Condition");
        MNode end = new MNode("IfEnd" + ifNb);
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
        MNode begin = new MNode("DoBegin");
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
        if(endNode.isEmpty()) {
            nestedLoops = false;
            updateNested();
        }
        return data;
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
}
