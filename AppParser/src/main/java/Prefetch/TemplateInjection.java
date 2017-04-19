package Prefetch;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TemplateInjection {
	
	public void Injection(String srcDir, String templateFile) throws IOException{
		
		String file = srcDir+templateFile+".java";
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String line = null;
		ArrayList<String> writeLines = new ArrayList<String>(); 
		while ((line = br.readLine()) != null)  
		{  
			//Replace the URL
			if (line.contains("String stringurl =\"\"")){
				line = "String stringurl ="+TraceIntentCalls.prefetchURL+";";
			}
			else if (line.contains("id12345")){
				line = "Intent intent = new Intent(context, "+TraceIntentCalls.prefetchReceiver+".class);";
			}
			else if (line.contains("putExtra(")){
				line = "intent.putExtra(EXTRA_MESSAGE, "+TraceIntentCalls.networkresources.get(0)+");";
			}
			writeLines.add(line);
		}
		FileWriter writer = new FileWriter("Template2.java"); 
		for(String str: writeLines) {
			writer.write(str+"\n");
		}
		writer.close();
		br.close();
		

		
	}
}
