import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class FunctionDeclarationDetector {

	public static void main(String[] args) {
		
		Map<String,Integer> functionNames = new HashMap<>();
		Map<String,Integer> unDeclaredFunctions = new HashMap<>();
		
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
				
				if(line.contains("function")){
					String[] funDeclaration = line.split("\\(");
					String funName = funDeclaration[0];
					funName= funName.replace("function ", "");
					functionNames.put(funName,lineCount);
					if(unDeclaredFunctions.containsKey(funName)){
						unDeclaredFunctions.remove(funName);
					}
					
				}
				
				if(line.contains(");")){
					String[] functionLine = line.split("\\(");
					String functionName = functionLine[0];
					if(functionName.contains("=")){
						String[] funcs = functionName.split("=");
						functionName= funcs[1];
					}
					
					if(!functionNames.containsKey(functionName)){
						unDeclaredFunctions.put(functionName,lineCount);
					}
					
				}				
			}
			
			
			Set<String> keys = unDeclaredFunctions.keySet();
			for(String key: keys){
				if(key.contains("if") || key.contains("for") || key.contains("catch") || key.contains("while")
						|| key.contains("switch") || key.contains("console.log")){
					//Removal functionality in Map is not included to avoid concurrent modification exception.
				}else{
					System.out.println("The function : "+key+ " in line : "+ unDeclaredFunctions.get(key)+  " never declared");
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
