package Prefetch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Try to get onPostExecute method
 */
public class TraceIntentCalls {
	static String prefetchInitiator;
	static String prefetchReceiver;
	static List<String> networkresources = new ArrayList<String>();

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		FileInputStream in = new FileInputStream(
				"C:/Users/shaha/Documents/PrefetchProcrastinatorAndroid/VolleyProcrastrinate/app/src/main/java/com/example/tau/volleyprocrastrinate/MainActivity.java");
		// parse it
		CompilationUnit cu = JavaParser.parse(in);
		new MethodChangerVisitor().visit(cu, null);
		// System.out.println(cu);
	}

	private static class MethodChangerVisitor extends VoidVisitorAdapter<Void> {
		@Override
		public void visit(MethodDeclaration n, Void arg) {
			// change the name of the method to upper case
			if (n.getNameAsString().equals("onPostExecute")) {
				/*
				 * n.addParameter("int", "value"); NodeList a =
				 * n.getParameters(); a.getParentNode();
				 */
				Node methodBlock = (n.getChildNodes().get(n.getChildNodes().size() - 1));
				if (methodBlock.getChildNodes().get(0) instanceof IfStmt) {
					Node ifStatement = methodBlock.getChildNodes().get(0);
					Node ifStatementBlock = ifStatement.getChildNodes().get(ifStatement.getChildNodes().size() - 1);

					int count = 0;
					while (count < ifStatementBlock.getChildNodes().size()) {
						Node statement = ifStatementBlock.getChildNodes().get(count);
						fetchValues(statement);
						count++;

					}

				} else {
					methodBlock = (n.getChildNodes().get(n.getChildNodes().size() - 1));
					int count = 0;
					while (count < methodBlock.getChildNodes().size()) {
						Node statement = methodBlock.getChildNodes().get(count);
						fetchValues(statement);
						count++;
					}

				}
			}
		}
		public void fetchValues(Node statement){
			/*
			 * Conveys that intent constructor was found Hence, we
			 * find the classes need for procrastinator
			 */
			if (statement.toString().contains("new Intent")) {
				Pattern p = Pattern.compile("\\((.*?)\\)");
				Matcher m = p.matcher(statement.toString());
				if (m.find()) {
					String[] classes = new String[2];
					classes = m.group(1).split(",");
					classes[0] = classes[0].replaceAll("\\s", "").substring(0, classes[0].indexOf("."));
					classes[1] = classes[1].replaceAll("\\s", "").substring(0, classes[1].indexOf("."));
					prefetchInitiator = classes[0];
					prefetchReceiver = classes[1];
				}
			}
			
			// Find all the variables passed in intent
			if (statement.toString().contains("putExtra")) {
				Pattern p = Pattern.compile("\\((.*?)\\)");
				Matcher m = p.matcher(statement.toString());
				if (m.find()) {
					networkresources.add(m.group(1).split(",")[1].replaceAll("\\s", ""));// Store the second result
					System.out.println(networkresources);
				}
			
			}
			
		}
	}
}
