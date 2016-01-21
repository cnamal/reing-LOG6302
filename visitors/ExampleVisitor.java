package javaparser;

public class ExampleVisitor extends AbstractVisitor
{

  public Object visit(Identifier node, Object data){
        System.out.println(node.jjtGetFirstToken().image);

		propagate(node, data);
		return data;
	}

}
