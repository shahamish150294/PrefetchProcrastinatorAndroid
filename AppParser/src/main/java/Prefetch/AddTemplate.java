package Prefetch;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class AddTemplate {
	static String filename;
	static String srcDir;
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		filename = "MainActivity";
		srcDir = "C:/Users/shaha/Documents/PrefetchProcrastinatorAndroid/VolleyProcrastrinate/app/src/main/java/com/example/tau/volleyprocrastrinate/";
		FileInputStream in = new FileInputStream(srcDir + filename + ".java");
		// parse it
		CompilationUnit cu = JavaParser.parse(in);
		
	}

}
