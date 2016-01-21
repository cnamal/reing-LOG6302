package com.namal.reing;

public class ReingVisitor extends AbstractVisitor
{

		private int nbIf=0;
		private int nbElse=0;
		private String className;


		public Object visit(IfStatement node, Object data){
			nbIf++;
			propagate(node, data);
			return data;
		}
		
		public Object visit(ElseStatement node,Object data){
			nbElse++;
			propagate(node,data);
			return data;
		}
		
		public void setData(AbstractOutput absOutput){
			absOutput.setNbIf(nbIf);
			absOutput.setNbElse(nbElse);
		}
}
