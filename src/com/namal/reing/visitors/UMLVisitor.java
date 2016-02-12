package com.namal.reing.visitors;

import com.namal.reing.*;
import com.namal.reing.Package;
import com.namal.reing.models.*;
import com.namal.reing.output.AbstractOutput;

import java.util.*;

public class UMLVisitor extends AbstractVisitor
{


    private MCIE currentCIE;
    private Stack<MClass> stackClasses=new Stack<>();
    private List<MClass> classes=new ArrayList<>();
    private List<MInterface> interfaces = new ArrayList<>();
    private MMethod currentMethod;
    private Stack<MMethod> stackMethods=new Stack<>();
    private static final String PACKAGE = "package";
    private static final String IMPORT = "import";
    private String pack="";
    private List<String> imports=new ArrayList<>();
    private static HashMap<String,MType> unfoundClasses = new HashMap<>();
    private static HashSet<String> allCIE = new HashSet<>();
    private boolean inCIE =false;

    public Object visit(ClassBodyDeclaration node, Object data){
        if(node.jjtGetNumChildren()>0){
            Node n = node.jjtGetChild(0);
            if(n instanceof ModifierList){
                MModifier l = (MModifier) node.jjtGetChild(0).jjtAccept(this,data);
                return node.jjtGetChild(1).jjtAccept(this,l);
            }else
                return super.visit(node,data);
        }
        return data;
    }

    public Object visit(ModifierList node, Object data){
        MModifier m=new MModifier();

        for(int i =0 ; i<node.jjtGetNumChildren();i++){
            Node n = node.jjtGetChild(i).jjtGetChild(0);
            if( n instanceof KeywordModifier)
                m.addModifier(((KeywordModifier)n).jjtGetFirstToken().image);
        }
        return m;
    }

    public Object visit(QualifiedIdentifier node,Object data){
        String s ="";
        for (int i =0; i< node.jjtGetNumChildren()-1;i++){
            s+=((Identifier)node.jjtGetChild(i)).jjtGetFirstToken().image+".";
        }
        s+=((Identifier)node.jjtGetChild(node.jjtGetNumChildren()-1)).jjtGetFirstToken().image;
        return s;
    }

    public Object visit(Package node, Object data){
        pack=(String)node.jjtGetChild(node.jjtGetNumChildren()-1).jjtAccept(this,data);
        return data;
    }

    public Object visit(ImportDeclaration node,Object data){
        Node  n = node.jjtGetChild(0).jjtGetChild(0);
        if(!(n instanceof HasStaticModifier)){
            n = node.jjtGetChild(node.jjtGetNumChildren()-1).jjtGetChild(0);

            if(!(n instanceof HasEllipsis)){
                String s="";
                for (int i =1; i< node.jjtGetNumChildren()-2;i++){
                    s+=((Identifier)node.jjtGetChild(i)).jjtGetFirstToken().image+".";
                }
                s+=((Identifier)node.jjtGetChild(node.jjtGetNumChildren()-2)).jjtGetFirstToken().image;
                imports.add(s);
            }
        }
        return data;
    }

    public Object visit(CompilationUnit node, Object data){
        data = new HashMap<String,Object>();
        propagate(node,data);
        return data;
    }

    public Object visit(ExtendOne node, Object data){
        if(node.jjtGetNumChildren()>0){
            return node.jjtGetChild(0).jjtAccept(this,data);
        }
        else
            return new MType("",true);
    }

    public Object visit(Implement node, Object data){
        if(node.jjtGetNumChildren()>0)
            return node.jjtGetChild(0).jjtAccept(this,data);
        else
            return new ArrayList<MType>();
    }

    public Object visit(ExtendMultiple node, Object data){
        if(node.jjtGetNumChildren()>0)
            return node.jjtGetChild(0).jjtAccept(this,data);
        else
            return new ArrayList<MType>();
    }

    public Object visit(BasicType node,Object data){
        return node.jjtGetFirstToken().image;
    }

    public Object visit(ReferenceType node,Object data){
        return node.jjtGetFirstToken().image;
    }


    public Object visit(Type node, Object data){
        String s=(String)node.jjtGetChild(0).jjtAccept(this,data);
        for(int i =1;i<node.jjtGetNumChildren();i++){
            s+="[]";
        }
        return new MType(s,node.jjtGetChild(0) instanceof BasicType);
    }

    public Object visit(TypeList node, Object data){
        List<MType> l = new ArrayList<>();
        for(int i=0;i<node.jjtGetNumChildren();i++){
            String s= (String)node.jjtGetChild(0).jjtAccept(this,data);
            l.add(new MType(s,false));
        }
        return l;
    }

    private boolean initCIE(SimpleNode node,Object data){
        /*if(currentCIE!=null){
            return false;
        }*/
        inCIE=true;
        currentCIE.setName(((Identifier)node.jjtGetChild(0)).jjtGetFirstToken().image);
        currentCIE.setPackage(pack);
        String tmp = pack.equals("")?"":pack+".";
        tmp+=currentCIE.getName();
        MType type=unfoundClasses.get(tmp);
        if(type!=null)
            type.setFullName(tmp);
        allCIE.add(tmp);
        propagate(node,data);
        return true;
    }


    private String inImport(String name){
        if(name.lastIndexOf(".")>=0)//needs to be tested
            return name;

        for(String s:imports){
            if(s.equals(name))
                return name;
            int index = s.lastIndexOf(".");
            if(index>=0){
                String sub = s.substring(index+1);
                if(sub.equals(name))
                    return s;
            }
        }
        return null;
    }

    public Object visit(NormalInterfaceDeclaration node,Object data){
        if(inCIE)
            return data;
        MInterface tmp=new MInterface();
        currentCIE=tmp;
        initCIE(node,data);
        List<MType> l = (List)node.jjtGetChild(node.jjtGetNumChildren()-2).jjtAccept(this,data);
        for(int i=0;i<l.size();i++)
            tmp.addExtend(processType(l.get(i)));
        interfaces.add(tmp);
        inCIE=false;
        return data;
    }


    private MType processType(MType t){
        if(!t.isBasic()){
            String fullName=inImport(t.getName());
            if(fullName!=null){
                t.setFullName(fullName);
            }
            else{
                String unfound="";
                if(pack!=null)
                    unfound = pack+".";
                unfound+=t.getName();
                if(allCIE.contains(unfound))
                    t.setFullName(unfound);
                else{
                    MType tmp=unfoundClasses.get(unfound);
                    if(tmp==null)
                        unfoundClasses.put(unfound,t);
                    else
                        t=tmp;
                }
            }
        }
        return t;
    }

    public Object visit(NormalClassDeclaration node, Object data){
        if(inCIE)
            return data;
        MClass tmp= new MClass();
        currentCIE=tmp;
        initCIE(node,data);
        MType t = (MType)node.jjtGetChild(node.jjtGetNumChildren()-3).jjtAccept(this,data);
        t=processType(t);
        tmp.setExtend(t);
        List<MType> l = (List)node.jjtGetChild(node.jjtGetNumChildren()-2).jjtAccept(this,data);
        for(int i=0;i<l.size();i++)
            tmp.addImplement(processType(l.get(i)));
        classes.add(tmp);
        inCIE=false;
        return data;
    }

    public Object visit(EnumDeclaration node, Object data){
        if(inCIE)
            return data;
        MClass tmp=new MClass();
        currentCIE=tmp;
        initCIE(node,data);
        tmp.setExtend(new MType("",true));
        List<MType> l = (List)node.jjtGetChild(node.jjtGetNumChildren()-2).jjtAccept(this,data);
        for(int i=0;i<l.size();i++)
            tmp.addImplement(processType(l.get(i)));
        classes.add(tmp);
        inCIE=false;
        return data;
    }

    private Object methodOrField(Node node,Object data){
        if(!(data instanceof MModifier))
            throw new RuntimeException("The data was supposed to be a MModifier");
        MModifier mod = (MModifier)data;
        List<Object> l = new ArrayList<>();
        l.add(mod);
        MType t = (MType)node.jjtGetChild(0).jjtAccept(this,data);
        t=processType(t);
        l.add(t);
        l.add(((Identifier)node.jjtGetChild(1)).jjtGetFirstToken().image);
        propagate(node,l);
        return data;
    }

    public Object visit(MethodOrFieldDecl node,Object data){
        return methodOrField(node,data);
    }

    public Object visit(InterfaceMethodOrFieldDecl node,Object data){
        return methodOrField(node,data);
    }

    public Object visit(GenericType node,Object data){
        if(node.jjtGetNumChildren()>0)
            return node.jjtGetChild(0).jjtAccept(this,data);
        else
            return new MType("void",true);
    }

    public Object visit(ConstructorDeclaratorRest node, Object data){
        if(!(data instanceof List<?>))
            throw new RuntimeException("The data was supposed to be a List<Object>");
        List<Object> l = (List)data;
        currentMethod=new MMethod((String)l.get(1));
        currentMethod.setMod((MModifier) l.get(0));
        propagate(node,data);
        currentCIE.addMethod(currentMethod);
        currentMethod=null;
        return data;
    }

    public Object visit (Creator node,Object data){
        return data;
    }

    private Object method(Node node, Object data){
        if(!(data instanceof List<?>))
            throw new RuntimeException("The data was supposed to be a List<Object>");
        List<Object> l = (List)data;
        currentMethod=new MMethod((String)l.get(2));
        currentMethod.setRet((MType)l.get(1));
        currentMethod.setMod((MModifier) l.get(0));
        //propagate(node,data);
        currentCIE.addMethod(currentMethod);
        currentMethod=null;
        return data;
    }

    public Object visit(MethodDeclaratorRest node, Object data){
        return method(node,data);
    }

    public Object visit(InterfaceMethodDeclaratorRest node, Object data){
        return method(node,data);
    }

    public Object visit(VariableDeclaratorId node,Object data){
        String s=((Identifier)node.jjtGetChild(0)).jjtGetFirstToken().image;
        for(int i =1;i<node.jjtGetNumChildren();i++){
            s+="[]";
        }
        return new MField(s);
    }

    public Object visit(FieldDeclaratorsRest node, Object data){
        if(!(data instanceof List<?>))
            throw new RuntimeException("The data was supposed to be a List<Object>");
        List<Object> l = (List)data;
        MField f = new MField((String)l.get(2));
        f.setType((MType)l.get(1));
        f.setMod((MModifier)l.get(0));
        currentCIE.addField(f);
        //propagate(node,data);
        return data;
    }


    private Object voidMethod(Node node,Object data){
        if(!(data instanceof MModifier))
            throw new RuntimeException("The data was supposed to be a MModifier");
        currentMethod=new MMethod(((Identifier)node.jjtGetChild(0)).jjtGetFirstToken().image);
        currentMethod.setRet(new MType("void",true));
        currentMethod.setMod((MModifier) data);
        propagate(node,data);
        currentCIE.addMethod(currentMethod);
        currentMethod=null;
        return data;
    }

    public Object visit(VoidMethodDecl node,Object data){
        return voidMethod(node,data);
    }

    public Object visit(VoidInterfaceMethodDecl node,Object data){
        return voidMethod(node,data);
    }

    public Object visit(NonEmptyInterfaceDeclaration node, Object data){
        MModifier l = (MModifier) node.jjtGetChild(0).jjtAccept(this,data);
        return node.jjtGetChild(1).jjtAccept(this,l);
    }

    public Object visit(ConstructorDecl node,Object data){
        if(!(data instanceof MModifier))
            throw new RuntimeException("The data was supposed to be a List<Object>");
        MModifier mod=(MModifier)data;
        List<Object> l = new ArrayList<>();
        l.add(mod);
        l.add(((Identifier)node.jjtGetChild(0)).jjtGetFirstToken().image);
        propagate(node,l);
        return data;
    }

    public Object visit(FormalParameterDecls node, Object data){
        int children= node.jjtGetNumChildren();
        MType t = (MType)node.jjtGetChild(children-2).jjtAccept(this,data);
        t=processType(t);
        Node n= node.jjtGetChild(children-1);
        if(n instanceof EllipsisParam){
            t.hasEllipsis();
        }
        MField f = (MField)n.jjtGetChild(0).jjtAccept(this,data);
        f.setType(t);
        currentMethod.addParam(f);
        n.jjtAccept(this,data);
        return data;
    }

    public Object visit(EllipsisParam node,Object data){
        return node.jjtGetChild(0).jjtAccept(this,data);
    }

    private Object genericMethodOrConstr(Node node,Object data){
        Identifier id = (Identifier)node.jjtGetChild(node.jjtGetNumChildren()-2);
        if(!(data instanceof MModifier))
            throw new RuntimeException("The data was supposed to be a List<Object>");
        MModifier mod = (MModifier) data;
        List<Object> l = new ArrayList<>();
        l.add(mod);
        if(node.jjtGetNumChildren()>2){
            MType t = (MType)node.jjtGetChild(node.jjtGetNumChildren()-3).jjtAccept(this,data);
            t=processType(t);
            l.add(t);
        }
        l.add(id.jjtGetFirstToken().image);
        propagate(node,l);
        return data;
    }

    public Object visit(GenericMethodOrConstructorRest node,Object data){
        return genericMethodOrConstr(node,data);
    }

    public Object visit(InterfaceGenericMethodDecl node,Object data){
        return genericMethodOrConstr(node,data);
    }

    public void dumpData(AbstractOutput absOutput){
        absOutput.setClasses(classes).setInterfaces(interfaces);
        ip=absOutput.getPrinter();
        absOutput.dumpData();
    }

}
