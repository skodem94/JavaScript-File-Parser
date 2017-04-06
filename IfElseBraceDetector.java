import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class IfElseBraceDetector {

	public static void main(String[] args) {
		
		Set<Variable> braceDetector = new HashSet<Variable>();
		int lineCount = 0;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("input.txt"));
			
			//Read Each Line.
			String line = "";
			boolean checkBrace = false;
			String conditionType= "";
			while ((line = reader.readLine()) != null) {
				lineCount = lineCount + 1;
				
				if(checkBrace){
					if(!line.contains("{")){
						Variable var = new Variable();
						var.setName(conditionType);
						var.setLineNumber(lineCount-1);
						braceDetector.add(var);
					}
					//Resetting the flag Variables.
					checkBrace = false;
					conditionType = "";
				}
				
				if(line.contains("if") || line.contains("else")){
					if(!line.endsWith("{")){
						checkBrace=true;	
						
					}
					if(line.contains("if")){
						conditionType="if";
					}else{
						conditionType = "else";
					}
				}
				
		
			
			}
			
			for(Variable var: braceDetector){
				System.out.println("Missing Paranthesis for : "+ var.getName()+" block at line number: "+var.getLineNumber());
			}
			
		}catch(Exception e){
			
		}

	}
}
