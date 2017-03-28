package Prefetch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class getMethod {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		FileInputStream in = new FileInputStream("C:/Users/shaha/Documents/PrefetchProcrastinatorAndroid/VolleyProcrastrinate/app/src/main/java/com/example/tau/volleyprocrastrinate/MainActivity.java");
        // parse it
        CompilationUnit cu = JavaParser.parse(in);
        new MethodChangerVisitor().visit(cu, null);
        System.out.println(cu);
	}
	private static class MethodChangerVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            // change the name of the method to upper case
            if (n.getNameAsString().equals("execute"))
        	{
            n.addParameter("int", "value");
            NodeList a = n.getParameters();
            a.getParentNode();
        	}
        }
    }
}
