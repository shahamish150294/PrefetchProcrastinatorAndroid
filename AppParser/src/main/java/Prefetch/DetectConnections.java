package Prefetch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class DetectConnections {
	boolean prefetchMethodFound;
	boolean newActivityMethodFound;
	int indexForMethodCall;
	Stack<Character> bracketsStack; 
	public DetectConnections() {
		this.prefetchMethodFound = false;
		this.newActivityMethodFound = false;
		indexForMethodCall = 0;
		bracketsStack = new Stack<Character>();
	}
	public void readFile() throws IOException{
		String file = "C:/Users/shaha/Documents/PrefetchProcrastinatorAndroid/VolleyProcrastrinate/app/src/main/java/com/example/tau/volleyprocrastrinate//MainActivity.java";
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String line = null;
		String inputLine = null;
		String tempLine = null;
		ArrayList<String> writeLines = new ArrayList<String>(); 
		while ((line = br.readLine()) != null)  
		{  
			inputLine = makeConnectionNull(line);
			if (inputLine!=null)
			{
				writeLines.add(inputLine);
				continue;
			}
			
			if (newActivityMethodFound){
				tempLine = modifySendMessage(line);
				if (tempLine==""){
					writeLines.add("");
					
				}
				
					if (!newActivityMethodFound && bracketsStack.isEmpty()){
						String methodDecl = TraceIntentCalls.methodDeclarationForNewActvityCall.get(indexForMethodCall);
						writeLines.add(methodDecl+("{\nTemplate t = new Template();\nt.execute(getApplicationContext());\n}"));
						continue;
					}
				
			}
			else{
				if (checkNewActvityCallMethod(line)){
					writeLines.add("");
				}
				else{
					writeLines.add(line);
				}
			}
			
		} 
		
		FileWriter writer = new FileWriter("MainActivity.java"); 
		for(String str: writeLines) {
		  writer.write(str+"\n");
		}
		writer.close();
		br.close();
	}


	public  String makeConnectionNull(String strExpr) {
		
		for (String element:TraceIntentCalls.methodDeclarationForDetection){
			if (strExpr.contains(element)) {
				prefetchMethodFound = true;
				break;
			}
		}
		if( prefetchMethodFound){
			if (strExpr.contains("url.openConnection();")){
				// find index of =
				int indexEq = strExpr.indexOf("=");
				// find index of ;
				int indexSemiColon = strExpr.indexOf(";");
				// replace the text between above indices with ""
				String toBeReplaced = strExpr.substring(indexEq + 1, indexSemiColon);
				return strExpr.replace(toBeReplaced, "null");
			}
		}
		return null;
	}

	public  String modifySendMessage(String strExpr) {
		
			/*if (strExpr.contains("new Intent(")){
				String methodDecl = TraceIntentCalls.methodDeclarationForNewActvityCall.get(indexForMethodCall).replace("(", "3(");
				strExpr +=("{\nTemplate t = new Template();\nt.execute();\n}");
				newActivityMethodFound=false;
				return strExpr;
			}
			*/
		if (strExpr.contains("{")){
			bracketsStack.push('}');
		}
		if (strExpr.contains("}")){
			bracketsStack.pop();
		}
		
		if (bracketsStack.isEmpty()){
			newActivityMethodFound = false;
			/**/
		}
		return "";
	}
	public Boolean checkNewActvityCallMethod(String strExpr){
		if (newActivityMethodFound)
			return false;
		for (int i =0;i<TraceIntentCalls.methodDeclarationForNewActvityCall.size();i++){
			if (strExpr.contains(TraceIntentCalls.methodDeclarationForNewActvityCall.get(i))) {
				newActivityMethodFound = true;
				indexForMethodCall = i;
				if (strExpr.contains("{")){
					bracketsStack.push('}');
				}
				return true;
				
			}
		}
		return false;
	}
}
