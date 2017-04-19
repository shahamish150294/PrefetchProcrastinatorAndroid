package Prefetch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * Track intent calls
 */
public class TraceIntentCalls {
	static String prefetchInitiator;
	static String prefetchReceiver;
	static List<String> networkresources = new ArrayList<String>();
	static String filename;
	static String srcDir;
	static List<String> methodDeclarationForDetection=  new ArrayList<String>();
	static List<String> methodDeclarationForNewActvityCall=  new ArrayList<String>();
	/**
	 * If PutExtra variable is found in a function, then find an assignment to
	 * that variable which is a network call. Assumption is that PutExtra
	 * variable is a response and needs to be read using InputStream using a
	 * custom function. Hence, all the arguments of function calls that return
	 * values to these PutExtra variables are stored in candidateParameterList
	 * The variables will then be checked, if they are objects of InputStreams
	 */
	static List<String> candidateParametersList;
	/**
	 * We need to do above process for each putExtra variables and there can be
	 * many such putExtra variable
	 * 
	 */
	static Map<Integer, List<String>> candidateParametersMap = new HashMap<Integer, List<String>>();
	static int intentResultIterator = -1;
	
	static String prefetchURL;
	static String templateFilename;
	static String prefetchBgCallVariable;
	static String prefetchBgCallClass= "new AsyncCallPrefetch(";
	public static void main(String[] args) throws IOException {
		
		// TODO Auto-generated method stub
		filename = args[1];//"MainActivity";
		templateFilename = args[2];//"Template";
		srcDir = args[0];//"C:/Users/shaha/Desktop/";
		FileInputStream in = new FileInputStream(srcDir + filename + ".java");
		// parse it
		CompilationUnit cu = JavaParser.parse(in);
		new IntentTracker().visit(cu, null);
		int countIntentresults = 0;
		//
		for (String transferVariables : networkresources) {
			candidateParametersList = new ArrayList<String>();
			new TraceIntentTransferData().visit(cu, transferVariables);
			candidateParametersMap.put(countIntentresults, candidateParametersList);
			countIntentresults++;
		}

		// Iterate over map to find whether any parameter/variable is calls
		// InputStream constructor, copy its key
		for (int key : candidateParametersMap.keySet()) {
			List<String> testParameterList = candidateParametersMap.get(key);
			for (String eachParameter : testParameterList) {
				InputStreamMatcher a = new InputStreamMatcher();
				KeyParameterPair currentPair = new KeyParameterPair(key, eachParameter, false);
				a.visit(cu, currentPair);
				if (currentPair.found)
					{
					System.out.println(networkresources.get(key));
					
					MethodFinder mf = new MethodFinder();
					mf.visit(cu, networkresources.get(key));
					}
			}
		}

		//Detect the prefetch bg variable name
		new PrefetchVariableFinder().visit(cu,prefetchBgCallClass); 
		
		//Cancel connections
		/*DetectConnections d = new DetectConnections();
		d.executeDetections();*/
		DetectConnections dr = new DetectConnections();
		dr.readFile();
		
		
		
		//Inject Template
		TemplateInjection t = new TemplateInjection();
		t.Injection(srcDir, templateFilename);
	}

	private static class IntentTracker extends VoidVisitorAdapter<Void> {
		@Override
		public void visit(MethodDeclaration n, Void arg) {
			// Check whether a method contains instantiation of Intent
			if (n.toString().contains("new Intent")) {
				Node methodBlock = (n.getChildNodes().get(n.getChildNodes().size() - 1));
				int count = 0;
				while (count < methodBlock.getChildNodes().size()) {
					Node statement = methodBlock.getChildNodes().get(count);
					fetchValues(statement);
					count++;
				}

			}
		}

		public void fetchValues(Node statement) {
			/*
			 * Conveys that intent constructor was found Hence, we find the
			 * classes need for procrastinator
			 */
			if (statement.toString().contains("new Intent")) {
				Pattern p = Pattern.compile("\\((.*?)\\)");
				Matcher m = p.matcher(statement.toString());
				if (m.find()) {
					String[] classes = new String[2];
					classes = m.group(1).split(",");
					// Context of current class can be passed as "this" or
					// getApplicationContext
					if ((classes[0].contains("this") && !classes[0].contains(".this"))
							|| classes[0].contains("getApplicationContext")) {
						prefetchInitiator = filename;
					} else {
						prefetchInitiator = classes[0].replaceAll("\\s", "").substring(0, classes[0].indexOf("."));
					}
					prefetchReceiver = classes[1].substring(0, classes[1].indexOf("."));
				}
			}

			// Find all the variables passed in intent
			if (statement.toString().contains("putExtra")) {
				Pattern p = Pattern.compile("\\((.*?)\\)");
				Matcher m = p.matcher(statement.toString());
				if (m.find()) {
					networkresources.add(m.group(1).split(",")[1].replaceAll("\\s", ""));// Store
																							// the
																							// second
																							// variable
																							// passed

				}

			}

		}
	}

	/**
	 * Track the contents of Intent to check whether they are a response from a
	 * network fetch
	 */
	private static class TraceIntentTransferData extends VoidVisitorAdapter<String> {
		@Override
		public void visit(MethodDeclaration declarator, String content) {

			
				boolean results[] = new boolean[2];
				processMethod(declarator, results, content);

			
		}

		public void processMethod(Node n, boolean[] results, String var) {
			List<Node> childrenNodes = n.getChildNodes();
			if (n.toString().equals(var) && !results[0]) {
				// Variable matched
				results[0] = true;
			}
			if (results[0] && n instanceof MethodCallExpr) {
				// Check if the nodes is an instance of inputStream
				// First we get the parameter
				int childNodesCount = n.getChildNodes().size();
				int count = 0;
				while (count < childNodesCount) {

					if (n.getChildNodes().get(count) instanceof NameExpr) {
						candidateParametersList.add(n.getChildNodes().get(count).toString());
						results[0] = false;
					}
					count++;
				}
			}

			for (Node child : childrenNodes) {
				processMethod(child, results, var);
			}

		}
	}

	private static class InputStreamMatcher extends ModifierVisitor<KeyParameterPair> {
		@Override
		public Node visit(VariableDeclarator declarator, KeyParameterPair pair) {
			if (declarator.getName().toString().contains(pair.parameter) && declarator.getInitializer().isPresent()) {
				Expression expression = declarator.getInitializer().get();
				if (expression.toString().contains("new BufferedInputStream("))
				{
					pair.found = true;
				}
			}

			return declarator;
		}
	}

	private static class MethodFinder extends VoidVisitorAdapter<String> {
		@Override
		public void visit(MethodDeclaration declarator, String content) {
			
			//Find method that has the network response fetched
			if (declarator.toString().contains(content) && !declarator.toString().contains(prefetchInitiator)
					&& !declarator.toString().contains(prefetchReceiver)){
				methodDeclarationForDetection.add(declarator.getDeclarationAsString());
			}
			//Find method that has intent to include AsyncTemplate Calls
			if (declarator.toString().contains("new Intent(") && declarator.toString().contains(prefetchReceiver)){
				methodDeclarationForNewActvityCall.add(declarator.getDeclarationAsString());
			}
		}
	}
	
	private static class PrefetchVariableFinder extends ModifierVisitor<String> {
	    @Override
	    public Node visit(VariableDeclarator declarator, String AsnycClassName) {
	        if (declarator.getInitializer().isPresent()) {
	            Expression expression = declarator.getInitializer().get();
	            if (expression.toString().contains(AsnycClassName)) {
	                prefetchBgCallVariable = declarator.getNameAsString();
	            }
	        }
	        return declarator;
	    }
	}
}
