package Prefetch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DetectConnections {
	boolean prefetchMethodFound;
	boolean newActivityMethodFound;
	public DetectConnections() {
		this.prefetchMethodFound = false;
		this.newActivityMethodFound = false;
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
				tempLine = makeSendMessage(line);
				if (tempLine!=null){
					writeLines.add(tempLine);
					continue;
				}
			}
			else{
				checkNewActvityCallMethod(line);
			}
			writeLines.add(line);
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

	public  String makeSendMessage(String strExpr) {
		
			if (strExpr.contains("new Intent(")){
				strExpr +=("\nTemplate t = new Template();\nt.execute();\n");
				newActivityMethodFound=false;
				return strExpr;
			}
		
		return null;
	}
	public void checkNewActvityCallMethod(String strExpr){
		if (newActivityMethodFound)
			return;
		for (String element:TraceIntentCalls.methodDeclarationForNewActvityCall){
			if (strExpr.contains(element)) {
				newActivityMethodFound = true;
				break;
			}
		}
		
	}
}
