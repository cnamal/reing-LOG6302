package com.namal.reing;
import java.util.*;

public class ReingVisitor extends AbstractVisitor
{

    private int nbElse=0;

    private Class currentClass;
    private Stack<Class> stackClasses=new Stack<>();
    private List<Class> classes=new ArrayList<>();
    private Method currentMethod;
    private Stack<Method> stackMethods=new Stack<>();
    private static IPrint ip;
    
   
    public Object visit(IfStatement node, Object data){
        currentMethod.incrIf();
        propagate(node, data);
        return data;
    }

    public Object visit (Creator node,Object data){
        return data;
    }

    public Object visit(ElseStatement node,Object data){
        currentMethod.incrElse();
        propagate(node,data);
        return data;
    }

    public Object visit(ForStatement node, Object data){
        currentMethod.incrFor();
        propagate(node, data);
        return data;
    }

    public Object visit(WhileStatement node, Object data){
        currentMethod.incrWhile();
        propagate(node, data);
        return data;
    }

    public Object visit(SwitchStatement node, Object data){
        currentMethod.incrSwitch();
        propagate(node, data);
        return data;
    }

    public Object visit(BreakStatement node, Object data){
        currentMethod.incrBreak();
        propagate(node, data);
        return data;
    }

    public Object visit(ContinueStatement node, Object data){
        currentMethod.incrContinue();
        propagate(node, data);
        return data;
    }

    public Object visit(ReturnStatement node, Object data){
        currentMethod.incrReturn();
        propagate(node, data);
        return data;
    }

    public Object visit(TryStatement node, Object data){
        currentMethod.incrTry();
        propagate(node, data);
        return data;
    }

    public Object visit(ThrowStatement node, Object data){
        currentMethod.incrThrow();
        propagate(node, data);
        return data;
    }

    public Object visit(SynchronizedStatement node, Object data){
        currentMethod.incrSynchronized();
        propagate(node, data);
        return data;
    }

    public Object visit(VariableDeclarator node, Object data){
        currentMethod.incrVariable();
        //propagate(node, data);
        return data;
    }

    private Object classOrInterface(SimpleNode node,Object data){
        if(currentClass!=null){
            if(currentMethod!=null)
                stackMethods.push(currentMethod);
            stackClasses.push(currentClass);
        }
        currentClass=new Class();
        currentClass.setName(((Identifier)node.jjtGetChild(0)).jjtGetFirstToken().image);
        propagate(node,data);
        if(!stackClasses.empty()){
            if(!stackMethods.empty()){
                currentMethod=stackMethods.pop();
                currentMethod.addClass(currentClass);
                currentClass=stackClasses.pop();
            }else{
                Class tmp=currentClass;
                currentClass=stackClasses.pop();
                currentClass.addClass(tmp);
            }
        }
        else{
            classes.add(currentClass);
            currentClass=null;
        }
        return data;

    }

    public Object visit(NormalInterfaceDeclaration node,Object data){
        return classOrInterface(node,data);	
    }

    public Object visit(NormalClassDeclaration node, Object data){
        return classOrInterface(node,data);	
    }

    public Object visit(EnumDeclaration node, Object data){
        return classOrInterface(node,data);	
    }

    public Object visit(MethodOrFieldDecl node,Object data){
        propagate(node,((Identifier)node.jjtGetChild(1)).jjtGetFirstToken().image);
        return data;
    }

    public Object visit(ConstructorDeclaratorRest node, Object data){
        if(!(data instanceof String))
            throw new RuntimeException("The data was supposed to be a String");
        currentMethod=new Method((String)data);
        propagate(node,data);
        currentClass.addMethod(currentMethod);
        currentMethod=null;
        return data;
    }

    public Object visit(MethodDeclaratorRest node, Object data){
        if(!(data instanceof String))
            throw new RuntimeException("The data was supposed to be a String");
        currentMethod=new Method((String)data);
        propagate(node,data);
        currentClass.addMethod(currentMethod);
        currentMethod=null;
        return data;

    }

    public Object visit(StaticInitBlock node, Object data){
        return data;
    }

    public Object visit(FieldDeclaratorsRest node, Object data){
        if(!(data instanceof String))
            throw new RuntimeException("The data was supposed to be a String");
        currentClass.addField(new Field((String)data));
        //propagate(node,data);
        return data;
    }

    public Object visit(GenericMethodOrConstructorDecl node,Object data){
        System.out.println("ENTERED GenericMethodOrContstrDecl");
        for(int i=0;i<node.jjtGetNumChildren();i++)
            System.out.println(node.jjtGetChild(i));
        propagate(node,data);
        return data;
    }

    public Object visit(VoidMethodDecl node,Object data){
        currentMethod=new Method(((Identifier)node.jjtGetChild(0)).jjtGetFirstToken().image);
        propagate(node,data);
        currentClass.addMethod(currentMethod);
        currentMethod=null;
        return data;
    }

    public Object visit(ConstructorDecl node,Object data){
        propagate(node,((Identifier)node.jjtGetChild(0)).jjtGetFirstToken().image);
        return data;
    }

    public Object visit(GenericMethodOrConstructorRest node,Object data){
        Identifier id = node.jjtGetNumChildren()==2?((Identifier)node.jjtGetChild(0)):((Identifier)node.jjtGetChild(1));

        propagate(node,id.jjtGetFirstToken().image);
        return data;
    }

    public void dumpData(AbstractOutput absOutput){
        /*
           absOutput.setNbIf(nbIf);
           absOutput.setNbElse(nbElse);*/
        absOutput.setClasses(classes);
        ip=absOutput.getPrinter();
        absOutput.dumpData();
    }

    public static IPrint getPrinter(){
        return ip;
    }
}
