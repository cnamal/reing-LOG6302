package com.namal.reing.visitors;

import com.namal.reing.*;
import com.namal.reing.models.MNode;
import com.namal.reing.models.MVariable;
import com.namal.reing.output.AbstractOutput;
import com.namal.reing.utils.Configuration;
import com.namal.reing.utils.EConfiguration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
    private int id=0;
    private int nestedExpr =0;

    private Map<String,MVariable> variables;
    private String filename ;

    public GraphVisitor(String name){
        filename = name;
    }

    private void updateNested(){
        nested = nestedCond>0|nestedLoops;
    }

    private Object methodOrConstr(SimpleNode node, Object data, String name){
        //System.err.println(name);
        id=0;
        variables = new HashMap<>();
        MNode currGraph = currNode = new MNode("Entry " + name,id++,node.jjtGetFirstToken().beginLine);
        currGraph.initOut();
        currGraph.setMethodName(name);
        endMethod = new MNode("End " + name,node.jjtGetLastToken().beginLine);
        currGraph.setEndNode(endMethod);
        propagate(node,data);
        if(currNode!=null)
            currNode.addNode(endMethod);
        graphs.add(currGraph);
        currNode=null;
        endMethod.setLabelId(id);
        currGraph.setVariables(variables);
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

    private MNode[] initLoop(String beg,String end,int line){
        nestedLoops = true;
        updateNested();
        MNode begin = new MNode(beg,id++,line);
        beginNode.push(begin);
        MNode cond = new MNode("Condition",id++,line);
        MNode en = new MNode(end,line);
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
        MNode[] init=initLoop("WhileBegin","WhileEnd",node.jjtGetFirstToken().beginLine);
        nestedExpr++;
        node.jjtGetChild(0).jjtAccept(this,data);
        nestedExpr--;
        node.jjtGetChild(1).jjtAccept(this,data);
        endLoop(init);
        return data;
    }

    private void forData(int i,ForStatement node,Object data){
        Node n=node.jjtGetChild(0);
        if(i==0){
            n=n.jjtGetChild(0);
            if(n instanceof ForInit)
                n.jjtAccept(this,data);
            else if(n instanceof ForVarControl){
                Node tmp = n.jjtGetChild(n.jjtGetNumChildren()-2);
                tmp.jjtAccept(this,data);
                tmp = n.jjtGetChild(n.jjtGetNumChildren()-1).jjtGetChild(0);
                tmp.jjtAccept(this,data);
            }else
                System.err.println("forData "+ i + " n instance of "+ n);
        }else if(i==1){
            if(n.jjtGetNumChildren()>1){
                n = n.jjtGetChild(1);
            }else{
                n = n.jjtGetChild(0);
                n= n.jjtGetChild(n.jjtGetNumChildren()-1);
                n=n.jjtGetChild(1);
            }
            if(!(n instanceof ForTest)){
                System.err.println("forData expecting ForTest");
            }
            n.jjtAccept(this,data);
        }else if(i==2){
            if(n.jjtGetNumChildren()>1){
                n = n.jjtGetChild(2);
            }else{
                n = n.jjtGetChild(0).jjtGetChild(n.jjtGetNumChildren()-1).jjtGetChild(2);
            }
            if(!(n instanceof ForTest)){
                System.err.println("forData expecting ForTest");
            }
            n.jjtAccept(this,data);
        }else
            System.err.println("forData with "+i);
    }
    @Override
    public Object visit(ForStatement node,Object data){
        MNode varInit = new MNode("ForInit",id++,node.jjtGetFirstToken().beginLine);
        currNode.addNode(varInit);
        currNode = varInit;
        nestedExpr++;
        forData(0,node,data);
        nestedExpr--;
        MNode[] init=initLoop("ForBegin","ForEnd",node.jjtGetFirstToken().beginLine);
        nestedExpr++;
        forData(1,node,data);
        nestedExpr--;
        node.jjtGetChild(node.jjtGetNumChildren()-1).jjtAccept(this,data);
        MNode update = new MNode("ForUpdate",id++,node.jjtGetFirstToken().beginLine);
        currNode.addNode(update);
        currNode = update;
        endLoop(init);
        return data;
    }

    @Override
    public Object visit(ForVarControlRest node, Object data){
        System.out.println("rest");
        return data;
    }


    @Override
    public Object visit(BreakStatement node, Object data){
        MNode breakNode = new MNode("BreakStmt",id++,node.jjtGetFirstToken().beginLine);
        currNode.addNode(breakNode);
        breakNode.addNode(endNode.peek());
        if(!nested)
            currNode=null;
        return data;
    }

    @Override
    public Object visit(ReturnStatement node, Object data){
        MNode returnStmt = new MNode("ReturnStmt",id++,node.jjtGetFirstToken().beginLine);
        currNode.addNode(returnStmt);
        returnStmt.addNode(endMethod);
        MNode tmp = currNode;
        currNode = returnStmt;
        nestedExpr++;
        propagate(node,data);
        nestedExpr--;
        currNode = tmp;
        if(!nested) {
            currNode =null;
        }
        return data;
    }

    @Override
    public Object visit(ContinueStatement node, Object data){
        MNode continueStmt = new MNode("ContinueStmt",id++,node.jjtGetFirstToken().beginLine);
        currNode.addNode(continueStmt);
        continueStmt.addNode(beginNode.peek());
        if(!nested)
            currNode=null;
        return data;
    }

    @Override
    public Object visit(VariableDeclaratorId node, Object data){
        if(currNode==null)
            return data;
        MVariable var = new MVariable(((Identifier)node.jjtGetChild(0)).jjtGetFirstToken().image);
        variables.put(var.getName(),var);
        currNode.addGen(var);
        var.addKill(currNode);
        return data;
    }

    @Override
    public Object visit(IfStatement node,Object data){
        nestedCond++;
        updateNested();
        MNode ifNode = new MNode("IfBegin",id++,node.jjtGetFirstToken().beginLine);
        MNode cond = new MNode("Condition",id++,node.jjtGetFirstToken().beginLine);
        MNode end = new MNode("IfEnd",node.jjtGetFirstToken().beginLine);
        ifNode.addNode(cond);
        currNode.addNode(ifNode);
        currNode = cond;
        nestedExpr++;
        node.jjtGetChild(0).jjtAccept(this,data);
        nestedExpr--;
        for(int i=1;i<node.jjtGetNumChildren()-1;i++)
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
        MNode[] init=initLoop("SwitchBegin","SwitchEnd",node.jjtGetFirstToken().beginLine);
        propagate(node,data);
        endLoop(init);
        return data;
    }

    @Override
    public Object visit(DoStatement node,Object data){
        //TODO might not work
        nestedLoops = true;
        updateNested();
        MNode begin = new MNode("DoBegin",id++);
        MNode beginWhile = new MNode("DoWhileBegin",node.jjtGetFirstToken().beginLine);
        MNode cond = new MNode("Condition",node.jjtGetFirstToken().beginLine);
        MNode end = new MNode("DoEnd",node.jjtGetFirstToken().beginLine);
        MNode endWhile = new MNode("DoWhileEnd",node.jjtGetFirstToken().beginLine);

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
        MVariable variable = new MVariable(((Identifier) node.jjtGetChild(0)).jjtGetFirstToken().image);
        variables.put(variable.getName(), variable);
        if(nestedExpr==0) {
            //VariableDeclaratorRest child = (VariableDeclaratorRest) node.jjtGetChild(1);
            //if(child.jjtGetChild(child.jjtGetNumChildren()-1) instanceof VariableInitializer) {
            MNode init = new MNode("Variable init", id++,node.jjtGetFirstToken().beginLine);
            currNode.addNode(init);
            currNode = init;

            nestedExpr++;
            propagate(node, data);
            nestedExpr--;

            kill(variable);
        }else {
            currNode.addData(variable);
            variable.addRead(currNode);
        }
        return data;
    }

    public Object visit (Expression node, Object data){
        if(currNode==null)
            return data;
        /*if(node.jjtGetNumChildren()>1) {
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
                    currNode.addGen(variable);
                    Map<MVariable,Set<String>> in = assign.getIn();
                    Map<MVariable,Set<String>> out = assign.getOut();
                    if(in!=null)
                        out.putAll(in);
                    Set<String> list = new HashSet<>();
                    list.add(currNode.getLabelId());
                    out.put(variable,list);
                }
            }
        }*/
        if(nestedExpr++==0){
            MNode tmp = new MNode("Expression",id++,node.jjtGetFirstToken().beginLine);
            currNode.addNode(tmp);
            currNode = tmp;
        }
        if(node.jjtGetNumChildren()>1) {
            currNode.initOut();
            Node first = node.jjtGetChild(0);
            first.jjtAccept(this, 1);
            node.jjtGetChild(1).jjtAccept(this,2);
        }else
            node.jjtGetChild(0).jjtAccept(this, 2);

        nestedExpr--;
        return data;
    }

    public Object visit (Expression1 node, Object data){
        propagate(node,data);
        return data;
    }

    public Object visit (Expression2 node, Object data){
        propagate(node,data);
        return data;
    }

    public Object visit (Expression3 node, Object data){
        Node tmp = node.jjtGetChild(0);
        int choice = 2;
        if(data instanceof Integer)
            choice = (Integer)data;

        if(tmp instanceof PrefixOp){
            String prefix =((PrefixOp) tmp).jjtGetFirstToken().image;
            if(prefix.equals("++")|| prefix.equals("--"))
                choice|=1;
            node.jjtGetChild(1).jjtAccept(this,choice);
        }else{
            if(node.jjtGetNumChildren()>1){
                if(node.jjtGetChild(node.jjtGetNumChildren()-1)instanceof PostfixOp)
                    choice|=1;
            }
            node.jjtGetChild(0).jjtAccept(this,choice);
        }
        return data;
        //return node.jjtGetChild(0).jjtAccept(this,data);
    }

    public Object visit (Primary node,Object data){
        if(currNode==null)
            return data;
        if(node.jjtGetNumChildren()>0) {
            Node first = node.jjtGetChild(0);
            if (first instanceof Identifier) {
                MVariable variable = variables.get(((Identifier) first).jjtGetFirstToken().image);
                if(variable!=null){
                    currNode.initOut();
                    if(!(data instanceof Integer))
                        throw new RuntimeException("Expected integer");
                    int choice = (int)data;
                    if(choice == 3 || choice ==1) {
                        kill(variable);
                    }
                    if(choice==3 || choice==2) {
                        currNode.addData(variable);
                        variable.addRead(currNode);
                    }
                }
                propagate(node,2);
                return ((Identifier) first).jjtGetFirstToken().image;
            }
        }
        propagate(node,2);
        return null;
    }

    private void kill(MVariable variable){
        variable.addKill(currNode);
        currNode.addGen(variable);
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
        if(preDump()) {
            absOutput.setGraphs(graphs);
            ip = absOutput.getPrinter();
            absOutput.dumpData();
        }
    }

    private boolean dom=false;
    private boolean inOut = false;

    private boolean preDump(){
        Set<EConfiguration> configurations = Configuration.getTP5Config();
        if(configurations.contains(EConfiguration.INOUT))
            inOut();
        if(configurations.contains(EConfiguration.DOM))
            dom(true);
        else if(configurations.contains(EConfiguration.PDOM))
            dom(false);
        if(configurations.contains(EConfiguration.CDG))
            cdg();
        else if(configurations.contains(EConfiguration.DDG))
            ddg();
        else if(configurations.contains(EConfiguration.PDG))
            pdg();
        else if(configurations.contains(EConfiguration.SLICE)) {
            slice();
            return false;
        }
        return true;
    }

    private void dom(boolean pred){
        if(dom)
            return;
        dom = true;
        for(int i=0; i<graphs.size();i++){
            MNode graph = graphs.get(i);
            HashSet<MNode> visited = new HashSet<>();
            MNode start = new MNode("Entry",-1,graph.getLineNumber());
            start.setVariables(graph.getVariables());
            start.setMethodName(graph.getMethodName());
            start.setLabelId(-1);
            start.setEndNode(graph.getEnd());
            start.addNode(graph.getEnd());
            start.addNode(graph);
            start.reverseCFG(visited);
            start.getDFSTree(pred);
            do {
                visited = new HashSet<>();
            }while(start.makeDomTree(visited,pred));
            graphs.set(i,start);
            /*graph.reverseCFG(visited);
            graph.getDFSTree(pred);
            do {
                visited = new HashSet<>();
            }while(graph.makeDomTree(visited,pred));*/
        }
    }

    private void inOut(){
        if(inOut)
            return;
        inOut=true;
        for(MNode graph:graphs){
            graph.firstOut();
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

    private void cdg(){
        dom(false);
        for(MNode graph:graphs){
            graph.makeCDG();
        }
    }

    private void ddg(){
        dom(false);
        inOut();
        for(MNode graph :graphs){
            graph.makeDDG();
        }
    }

    private void pdg(){
        inOut();
        cdg();
        ddg();
    }

    public void test(){
        //graphs.add(makeWordCount());
        graphs.add(makeFibo());
    }

    //String[] testFileNames = new String[]{"test/wordcount.c","examples/Fibo.java"};
    String[] testFileNames = new String[]{"examples/Fibo.java"};

    private void slice(){
        pdg();
        char cont;
        Scanner sc = new Scanner(System.in);
        System.out.println("Do you wish to slice? "+filename+" (y/Y for yes, anything else for no)");
        cont=sc.next().charAt(0);
        if(cont!='y'&& cont!='Y')
            return;
        if(graphs.size()==0)
            return;
        do {
            MNode n ;
            int answer=0;
            if(graphs.size()==1) {
                n = graphs.get(0);
            }
            else{
                if(!Configuration.getTP5Config().contains(EConfiguration.TEST))
                    executeCommand("cat "+filename);
                else
                    for(String name : testFileNames)
                        executeCommand("cat "+name);
                do {
                    System.out.println("Please choose a method ");
                    int i = 0;
                    for (MNode node : graphs) {
                        System.out.println(i++ + " : " + node.getMethodName());
                    }
                    answer=sc.nextInt();
                }while (answer<0||answer>=graphs.size());
                n = graphs.get(answer);
            }
            String fileName=Configuration.getTP5Config().contains(EConfiguration.TEST)?testFileNames[answer]:filename;
            executeCommand("python slice.py -f "+fileName+" -b "+n.getLineNumber()+ " -e "+n.getEnd().getLineNumber());
            Map<String, MVariable> map = n.getVariables();
            List<String> list = new ArrayList<>(map.keySet());
            do{
                System.out.println("Please choose a variable to slice");
                int i=0;
                for(String var : list){
                    System.out.println(i++ + " : "+var);
                }
                try {
                    answer = sc.nextInt();
                }catch (Exception e) {
                    answer = -1;
                    sc.nextLine();
                }
            }while (answer<0||answer>=list.size());
            MVariable var = map.get(list.get(answer));
            List<MNode> listN = new ArrayList<>(var.getUse());
            do {
                System.out.println("Please choose a line number from which to slice");
                int i=0;
                listN.sort((n1,n2)-> n1.getLineNumber()-n2.getLineNumber());
                for (MNode c : listN) {
                    System.out.println(i++ + " : " + c.getLineNumber());
                }
                answer = sc.nextInt();
            }while(answer<0 || answer>=listN.size());
            MNode node = listN.get(answer);
            printSlice(node,var,fileName);
            System.out.println("Do you wish to continue? (y/Y for yes, anything else for no)");
            cont=sc.next().charAt(0);
        } while (cont=='y'||cont=='Y');
    }

    private void printSlice(MNode node,MVariable var,String filename){
        String list="";
        for(MNode n : node.bSlice(var))
            list+=n.getLineNumber()+",";
        executeCommand("python slice.py -f "+filename+ " -l "+list);
    }
    private MNode makeWordCount(){
        /*MNode entry = new MNode("Entry");
        entry.setLabelId(-1);*/
        MVariable inword = new MVariable("inword");
        MVariable nl = new MVariable("nl");
        MVariable nw = new MVariable("nw");
        MVariable nc = new MVariable("nc");
        MVariable c = new MVariable("c");

        Map<String, MVariable> map = new HashMap<>();
        map.put("inword",inword);
        map.put("nl",nl);
        map.put("nw",nw);
        map.put("nc",nc);
        map.put("c",c);

        MNode start = new MNode("Start",3);
        start.setMethodName("main");
        start.setLabelId(-1);
        start.setVariables(map);

        MNode stop = new MNode("Stop",26);
        stop.setLabelId(-1);
        start.setEndNode(stop);

        MNode _1 = new MNode("",6);
        _1.setLabelId(1);
        _1.addGen(inword);
        inword.addKill(_1);
        start.addNode(_1);

        MNode _2 = new MNode("",7);
        _2.setLabelId(2);
        _2.addGen(nl);
        nl.addKill(_2);

        MNode _3 = new MNode("",8);
        _3.setLabelId(3);
        _3.addGen(nw);
        nw.addKill(_3);

        MNode _4 = new MNode("",9);
        _4.setLabelId(4);
        _4.addGen(nc);
        nc.addKill(_4);

        MNode _5 = new MNode("",10);
        _5.setLabelId(5);
        _5.addGen(c);
        c.addKill(_5);

        MNode _6 = new MNode("",11);
        _6.setLabelId(6);
        _6.addData(c);
        c.addRead(_6);

        MNode _7 = new MNode("",12);
        _7.setLabelId(7);
        _7.addGen(nc);
        _7.addData(nc);
        nc.addKill(_7);
        nc.addRead(_7);

        MNode _8 = new MNode("",13);
        _8.setLabelId(8);
        _8.addData(c);
        c.addRead(_8);

        MNode _9 = new MNode("",14);
        _9.setLabelId(9);
        _9.addData(nl);
        _9.addGen(nl);
        nl.addKill(_9);
        nl.addRead(_9);

        MNode _10 = new MNode("",15);
        _10.setLabelId(10);
        _10.addData(c);
        c.addRead(_10);

        MNode _11 = new MNode("",16);
        _11.setLabelId(11);
        _11.addGen(inword);
        inword.addKill(_11);

        MNode _12 = new MNode("",17);
        _12.setLabelId(12);
        _12.addData(inword);
        inword.addRead(_12);

        MNode _13 = new MNode("",18);
        _13.setLabelId(13);
        _13.addGen(inword);
        inword.addKill(_13);

        MNode _14 = new MNode("",19);
        _14.setLabelId(14);
        _14.addData(nw);
        _14.addGen(nw);
        nw.addKill(_14);
        nw.addRead(_14);

        MNode _15 = new MNode("",21);
        _15.setLabelId(15);
        _15.addGen(c);
        c.addKill(_15);

        MNode _16 = new MNode("",23);
        _16.setLabelId(16);
        _16.addData(nl);
        nl.addRead(_16);

        MNode _17 = new MNode("",24);
        _17.setLabelId(17);
        _17.addData(nw);
        nw.addRead(_17);

        MNode _18 = new MNode("",25);
        _18.setLabelId(18);
        _18.addData(nc);
        nc.addRead(_18);

        _1.addNode(_2);
        _2.addNode(_3);
        _3.addNode(_4);
        _4.addNode(_5);
        _5.addNode(_6);
        _6.addNode(_7);
        _6.addNode(_16);
        _7.addNode(_8);
        _8.addNode(_9);
        _8.addNode(_10);
        _9.addNode(_10);
        _10.addNode(_11);
        _10.addNode(_12);
        _11.addNode(_15);
        _12.addNode(_13);
        _12.addNode(_15);
        _13.addNode(_14);
        _14.addNode(_15);
        _15.addNode(_6);
        _16.addNode(_17);
        _17.addNode(_18);
        _18.addNode(stop);
        return start;
    }

    private MNode makeFibo(){
        /*MNode entry = new MNode("Entry");
        entry.setLabelId(-1);*/
        MVariable i = new MVariable("i");
        MVariable n = new MVariable("n");
        MVariable a = new MVariable("a");
        MVariable b = new MVariable("b");
        MVariable c = new MVariable("c");
        MVariable d = new MVariable("d");
        MVariable t = new MVariable("t");

        Map<String, MVariable> map = new HashMap<>();
        map.put("i",i);
        map.put("n",n);
        map.put("a",a);
        map.put("b",b);
        map.put("c",c);
        map.put("d",d);
        map.put("t",t);

        MNode start = new MNode("Start",3);
        start.setMethodName("fib");
        start.addGen(n);
        n.addKill(start);
        start.setLabelId(-1);
        start.setVariables(map);

        MNode stop = new MNode("Stop",26);
        stop.setLabelId(-1);
        start.setEndNode(stop);

        MNode _1 = new MNode("",4);
        _1.setLabelId(1);
        _1.addGen(i);
        _1.addData(n);
        i.addKill(_1);
        n.addRead(_1);
        start.addNode(_1);

        MNode _2 = new MNode("",5);
        _2.setLabelId(2);
        _2.addGen(a);
        a.addKill(_2);

        MNode _3 = new MNode("",6);
        _3.setLabelId(3);
        _3.addGen(b);
        b.addKill(_3);

        MNode _4 = new MNode("",7);
        _4.setLabelId(4);
        _4.addGen(c);
        c.addKill(_4);

        MNode _5 = new MNode("",8);
        _5.setLabelId(5);
        _5.addGen(d);
        d.addKill(_5);

        MNode _6 = new MNode("",9);
        _6.setLabelId(6);
        _6.addGen(t);
        t.addKill(_6);

        MNode _7 = new MNode("",10);
        _7.setLabelId(7);
        _7.addData(n);
        n.addRead(_7);

        MNode _8 = new MNode("",11);
        _8.setLabelId(8);

        MNode _9 = new MNode("",12);
        _9.setLabelId(9);
        _9.addData(i);
        i.addRead(_9);

        MNode _10 = new MNode("",13);
        _10.setLabelId(10);
        _10.addData(i);
        i.addRead(_10);

        MNode _11 = new MNode("",14);
        _11.setLabelId(11);
        _11.addGen(t);
        _11.addData(d);
        _11.addData(c);
        t.addKill(_11);
        c.addRead(_11);
        d.addRead(_11);

        MNode _12 = new MNode("",15);
        _12.setLabelId(12);
        _12.addData(c);
        _12.addData(d);
        _12.addGen(c);
        c.addRead(_12);
        c.addKill(_12);
        d.addRead(_12);

        MNode _13 = new MNode("",16);
        _13.setLabelId(13);
        _13.addGen(d);
        _13.addData(t);
        d.addKill(_13);
        t.addRead(_13);

        MNode _14 = new MNode("",17);
        _14.setLabelId(14);
        _14.addData(i);
        _14.addGen(i);
        i.addKill(_14);
        i.addRead(_14);

        MNode _15 = new MNode("",19);
        _15.setLabelId(15);
        _15.addGen(t);
        _15.addData(d);
        _15.addData(b);
        _15.addData(a);
        _15.addData(c);
        t.addKill(_15);
        a.addRead(_15);
        b.addRead(_15);
        c.addRead(_15);
        d.addRead(_15);

        MNode _16 = new MNode("",20);
        _16.setLabelId(16);
        _16.addData(d);
        _16.addData(a);
        _16.addGen(a);
        _16.addData(b);
        _16.addData(c);
        a.addRead(_16);
        a.addKill(_16);
        b.addRead(_16);
        c.addRead(_16);
        d.addRead(_16);

        MNode _17 = new MNode("",21);
        _17.setLabelId(17);
        _17.addData(t);
        _17.addGen(b);
        t.addRead(_17);
        b.addKill(_17);

        MNode _18 = new MNode("",22);
        _18.setLabelId(18);
        _18.addData(i);
        _18.addGen(i);
        i.addRead(_18);
        i.addKill(_18);

        MNode _19 = new MNode("",24);
        _19.setLabelId(19);
        _19.addData(a);
        _19.addData(b);
        a.addRead(_19);
        b.addRead(_19);

        _1.addNode(_2);
        _2.addNode(_3);
        _3.addNode(_4);
        _4.addNode(_5);
        _5.addNode(_6);
        _6.addNode(_7);
        _7.addNode(_8);
        _8.addNode(stop);
        _19.addNode(stop);
        _7.addNode(_9);
        _9.addNode(_19);
        _9.addNode(_10);
        _10.addNode(_11);
        _11.addNode(_12);
        _12.addNode(_13);
        _13.addNode(_14);
        _14.addNode(_10);
        _10.addNode(_15);
        _15.addNode(_16);
        _16.addNode(_17);
        _17.addNode(_18);
        _18.addNode(_9);
        return start;
    }


    public void executeCommand(String command){
        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // process procs standard output here
                System.out.println(line);
            }
            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = stderrReader.readLine()) != null) {
                // process procs standard error here
                System.err.println(" .. stderr: "+line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(output.toString());
    }
}
