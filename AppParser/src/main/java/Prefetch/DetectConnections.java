package Prefetch;

import com.github.javaparser.ast.expr.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class DetectConnections {
	static String url;
	public static void main(String[] args) throws FileNotFoundException {
		FileInputStream in = new FileInputStream("C:/Users/shaha/Documents/PrefetchProcrastinatorAndroid/VolleyProcrastrinate/app/src/main/java/com/example/tau/volleyprocrastrinate/MainActivity.java");
        // parse it
        CompilationUnit cu = JavaParser.parse(in);

        // visit and print the methods names
        new MyVisitor().visit(cu, null);
        
        //Read template
		FileInputStream in2 = new FileInputStream("C:/Users/shaha/Documents/PrefetchProcrastinatorAndroid/VolleyProcrastrinate/app/src/main/java/com/example/tau/volleyprocrastrinate/Template.java");
        // parse it
        CompilationUnit cu2 = JavaParser.parse(in2);
        //Replace string url
        new MyVisitor2().visit(cu2, null);
        System.out.println(cu2);
	}

	
}
class MyVisitor extends ModifierVisitor<Void> {
    @Override
    
    public Node visit(VariableDeclarator declarator, Void args) {
    	//To make conn null
    	if (declarator.getNameAsString().equals("conn")){
                // the initializer is optional, first check if there is one
                if( declarator.getInitializer().isPresent()) {
            Expression expression = declarator.getInitializer().get();
            if (expression.toString().contains("HttpURLConnection") && 
            		expression.toString().contains("openConnection()")) {
          
            	return null;
            }
                }
        }
    	if (declarator.getNameAsString().equals("a")){
        	if( declarator.getInitializer().isPresent()) {
        		Expression expression = declarator.getInitializer().get();
        		if (expression.toString().contains("AsyncCalls()")) {
        			
        			return null;
        		}
        		}
        	}
    	if (declarator.getNameAsString().equals("url")){
        	if( declarator.getInitializer().isPresent()) {
        		Expression expression = declarator.getInitializer().get();
        		if (expression.toString().contains("new URL")) {
        			Pattern p = Pattern.compile(".*\\\"(.*)\\\".*");
        			Matcher m = p.matcher(expression.toString());
        			if(m.find()) {
        				DetectConnections.url = (m.group(1));
        	        }
        			
        			return null;
        		}
        		}
        	}
        
        
        return declarator;
    }
}
class MyVisitor2 extends ModifierVisitor<Void> {
    @Override
    public Node visit(VariableDeclarator declarator, Void args) {
        if (declarator.getNameAsString().equals("stringurl")
                // the initializer is optional, first check if there is one
                && declarator.getInitializer().isPresent()) {
            Expression expression = declarator.getInitializer().get();
            // We're looking for a literal integer.
            if (expression instanceof StringLiteralExpr) {                //
                if (((StringLiteralExpr) expression).getValue().equals("http://www.bing.com")) {
                    // Returning null means "remove this VariableDeclarator"
                     ((StringLiteralExpr) expression).setValue(DetectConnections.url);
                     return declarator;
                }
            }
        }
        return declarator;
    }
}
