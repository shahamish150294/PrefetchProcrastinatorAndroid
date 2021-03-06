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
		String file = TraceIntentCalls.srcDir+TraceIntentCalls.filename+".java";
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
						writeLines.add(methodDecl+("{\nif(!Procastinator.getInstance( this.getApplicationContext()).requiresProcrastination(false))\n{Intent intent = new Intent(this, "+TraceIntentCalls.prefetchReceiver+".class);\nintent.putExtra(EXTRA_MESSAGE, "+TraceIntentCalls.networkresources.get(0)+");\nstartActivity(intent);\n}\nelse\n{\nLog.d(this.getClass().getSimpleName(), \"Now, fetching next activity data\");\nTemplate t = new Template(getApplicationContext());\nt.execute();\n}}"));
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
		//Add import statements - import com.mcomputing.procrastinate.Procastinator;		
		for (int i=0;i<writeLines.size();i++){
			if (writeLines.get(i).contains("package"))
			{
				String packageSt = writeLines.get(i);
				packageSt+=" import com.mcomputing.procrastinate.Procastinator;";
				writeLines.set(i, packageSt);
			}
			else if (writeLines.get(i).contains(TraceIntentCalls.prefetchBgCallClass)&&writeLines.get(i).contains(TraceIntentCalls.prefetchBgCallVariable)){
				String instantiation = writeLines.get(i);
				instantiation = "";
				instantiation = "if(!Procastinator.getInstance(context).requiresProcrastination(true))\n{\nLog.d(this.getClass().getSimpleName(), \"Fetching next activity data!\");\nAsyncCallPrefetch asyncCallPrefetch = new AsyncCallPrefetch();\nasyncCallPrefetch.execute();\n}\nelse{\nLog.d(this.getClass().getSimpleName(), \"Procrastinated Prefetch Call\");\n}";
				writeLines.set(i, instantiation);
			}
			else if (writeLines.get(i).contains(TraceIntentCalls.prefetchBgCallVariable) && writeLines.get(i).contains("execute(")){
				writeLines.remove(i);
			}
		}
		
		FileWriter writer = new FileWriter("MainActivity2.java"); 
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
			//Following code makes the connection of prefetch to null. Since we dont want this after design changes. Code commented
			/*if (strExpr.contains("url.openConnection();")){
				// find index of =
				int indexEq = strExpr.indexOf("=");
				// find index of ;
				int indexSemiColon = strExpr.indexOf(";");
				// replace the text between above indices with ""
				String toBeReplaced = strExpr.substring(indexEq + 1, indexSemiColon);
				return strExpr.replace(toBeReplaced, "null");
			}*/
			//Fetch the prefetch URL. By making use of the prefetchMethodFound variable
			if (strExpr.contains("URL(")){
				// find index of =
				int indexEq = strExpr.indexOf("(");
				// find index of ;
				int indexSemiColon = strExpr.indexOf(")");
				// replace the text between above indices with ""
				//String toBeReplaced = strExpr.substring(indexEq + 1, indexSemiColon);
				TraceIntentCalls.prefetchURL = strExpr.substring(indexEq+1, indexSemiColon);
				System.out.println(TraceIntentCalls.prefetchURL);
				//return strExpr.replace(toBeReplaced, "null");
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
