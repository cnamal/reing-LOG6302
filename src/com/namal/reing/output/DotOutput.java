package com.namal.reing.output;



import com.namal.reing.models.*;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by namalgac on 2/8/16.
 */
public class DotOutput extends AbstractOutput implements IPrint {

    private String target="graphs/";

    public DotOutput(String targetFolder){
        if(targetFolder.charAt(targetFolder.length()-1)!='/')
            targetFolder+="/";
        target=targetFolder;
    }

    private void initDump(PrintWriter writer){
        writer.println("digraph G {");
        writer.println("fontname = \"Bitstream Vera Sans\"");
        writer.println("fontsize = 8");
        writer.println("node [");
        writer.println("fontname = \"Bitstream Vera Sans\"");
        writer.println("fontsize = 8");
        writer.println("shape = \"record\"");
        writer.println("]");
        writer.println("edge [");
        writer.println("fontname = \"Bitstream Vera Sans\"");
        writer.println("fontsize = 8");
        writer.println("]");
    }

    private void printAN(Accessibility a, String n, PrintWriter writer){
        String mod="";
        //for some reason, the switch doesn't work
        if(a==Accessibility.PUBLIC)
            mod="+";
        else if(a==Accessibility.PRIVATE)
            mod="-";
        else if(a==Accessibility.PROTECTED)
            mod="#";
        else if(a==Accessibility.PACKAGE)
            mod="~";
        writer.print(mod+" ");
        writer.print(n);
    }

    @Override
    public void dumpData() {
        for(MCIE cie:classes){
            PrintWriter writer;
            try {
                writer = new PrintWriter(target+cie.getFullName()+".dot", "UTF-8");
                initDump(writer);
                writer.println(cie.getFullName()+ " [");
                writer.print("label = \"{"+cie.getName()+"|");

                for(MField f:cie.getFields()){
                   printAN(f.getMod().getAccessibility(),f.getName(),writer);
                    writer.print(" : ");
                   writer.print(f.getType().getName() + "\n");
                }

                for(MMethod m : cie.getMethods()){
                    printAN(m.getMod().getAccessibility(),m.getName(),writer);
                    writer.print("(");
                    for(MField t:m.getParams())
                        writer.print(t.getType().getName()+ ",");
                    writer.print(") :");
                    writer.print((m.getRet()!=null?m.getRet().getName():"" )+ "\\l");
                }
                writer.println("}\"");
                writer.println("]");
                writer.println("}");
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
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
