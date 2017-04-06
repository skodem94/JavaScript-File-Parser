import java.io.BufferedReader;
import java.io.FileReader;
import java.security.KeyStore.Entry;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeclaredUnusedDetector {
	
	static Map<String,Variable> variableCounter = new HashMap<String,Variable>();
	static Map<String,Integer> functionNames = new HashMap<>();
	static Map<String,Integer> unDeclaredFunctions = new HashMap<>();
	static Set<Variable> braceDetector = new HashSet<Variable>();
	static Stack<LineAndNumber> st = new Stack<LineAndNumber>();
	static boolean checkBrace = false;
	static String conditionType= "";
	static 	String previous_line="";

	//Map to store declared variables 
	
	public static void main(String[] args) {
		//Read JS File.
		int lineCount = 0;
		BufferedReader reader = null;
		try {
			String line ="";
			reader = new BufferedReader(new FileReader("input.txt"));
			while ((line = reader.readLine()) != null) {
				lineCount += 1;
		checkVariables(line,lineCount);
		checkFunction(line,lineCount);
		checkIfElse(line,lineCount);
		checkBraces(line,lineCount);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	//	
	//	
	//	
		//print variables
		for(java.util.Map.Entry<String, Variable> entry : variableCounter.entrySet()){
			String var = entry.getKey();
			Variable variable = entry.getValue();
			if(variable.getCount()==1){
				System.out.println("Variable Unused: "+ var + " declared at Line Number: "+variable.getLineNumber());
			}
		}
		
		//print function
		Set<String> keys = unDeclaredFunctions.keySet();
		for(String key: keys){
			if(key.contains("if") || key.contains("for") || key.contains("catch") || key.contains("while")
					|| key.contains("switch") || key.contains("console.log")){
				//Removal functionality in Map is not included to avoid concurrent modification exception.
			}else{
				System.out.println("The function : "+key+ " in line : "+ unDeclaredFunctions.get(key)+  " never declared");
			}
		}
		
		//print if else
		for(Variable var: braceDetector){
			System.out.println("Missing Paranthesis for : "+ var.getName()+" block at line number: "+var.getLineNumber());
		}
		
		
		//print missing braces
		
		while(!st.empty()){
			System.out.println("Missing brace at "+st.pop());
		  }
		
	}
	
	private static void checkVariables(String line, int lineCount){
		// File Reader to read the JS File.
		
		try {
			
				
			
				if(line.contains("var")||line.contains("let")){
					int count = checkMultipleOccurancesOfVar(line);
					
					if(count > 1){
						//Variables separated by semicolon.
						String[] variables = line.split(";");
						for(String var: variables){	
							if(var.contains("var"))
							var= var.replace("var ", "");
							else
								var=var.replaceAll("let", "");
							if(var.contains(",")){
								//Variables seperated by comma.
								String[] varComma = var.split(",");
								for(String str: varComma){

									updateVariableCount(str,lineCount);
								}
							}else{
								updateVariableCount(var,lineCount);
							}
						}
					}else{
							line = line.replace(";", "");
							line= line.replace("var ", "");
							if(line.contains(",")){
								//Variables seperated by comma.
								String[] varComma = line.split(",");
								for(String str: varComma){
									
									updateVariableCount(str,lineCount);
								}
							}else{
								updateVariableCount(line,lineCount);
								
							}
					}
					
				}else{
					line = line.trim();
					String[] ops = line.split("\\s*[a-zA-Z]+\\s*");
					String[] notops = line.split("\\s*[^a-zA-Z]+\\s*");
					String[] res = new String[ops.length + notops.length -1 ];
					for(int i=0; i < res.length; i++){
						res[i]= i%2==0 ? notops[i/2] :ops[i/2+1];
						if(variableCounter.containsKey(res[i])){
							Variable var = variableCounter.get(res[i]);
							var.setCount(var.getCount() + 1);
							variableCounter.put(res[i], var);
						}
						
					}
					
				}
					
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}
	
	private static void checkFunction(String line, int lineCount){
		
		try {

				
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
			
			
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
private static void checkIfElse(String line,int lineCount){
			
		try {
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
				
		
			
			
			
			
			
		}catch(Exception e){
			
		}

	}


private static void checkBraces(String line,int lineno){
	
	
	try{
	
	
	
		line = line.trim();
		if(line.contains("){")){
			previous_line = line;
			line = line.replace("{","");
			LineAndNumber ln = new LineAndNumber();
			ln.setData(line);
			ln.setNum(lineno);
			st.push(ln);
		}
		else if(line.contains("{")){
			previous_line = line;
			LineAndNumber ln = new LineAndNumber();
			 if(!previous_line.contains("{")){
			ln.setData(previous_line);
			ln.setNum(lineno);
			
			 }else{
				 ln.setData("No statement started the curly bracket");
				 ln.setNum(lineno);
			  }
			 st.push(ln);
		}
		if(line.contains("}")){
			int closing_bracks = closingCount(line);
			for(int i=1;i<=closing_bracks;i++){
			if(st.empty()){
				System.out.println("Extra Brace at line no: "+lineno);
			}
			else{
				st.pop();
			}
		}
		}
		

	
	}catch(Exception ex){
		ex.printStackTrace();
	}
}

static int closingCount(String current){
	int count = 0;
	for(int i=0;i<current.length();i++){
		if(current.charAt(i)=='}'){
			count++;
		}
	}
	return count;
}
	

	private static void updateVariableCount(String str,int lineCount) {
		if(str.contains("=")){
			//Variables seperated by equal to.
			String[] varEqual = str.split("=");
			String targetVariable = varEqual[0];
			Variable variable = new Variable();
			variable.setCount(variable.getCount() + 1);
			variable.setName(targetVariable.trim());
			variable.setLineNumber(lineCount);
			variableCounter.put(targetVariable.trim(), variable);
			
			varEqual[1] = varEqual[1].trim();
			if(variableCounter.containsKey(varEqual[1])){
				Variable var1 = variableCounter.get(varEqual[1]);
				var1.setCount(var1.getCount() + 1);
				variableCounter.put(varEqual[1], var1);
			}
						
			String[] ops = varEqual[1].split("\\s*[a-zA-Z]+\\s*");
			String[] notops = varEqual[1].split("\\s*[^a-zA-Z]+\\s*");
			String[] res = new String[ops.length + notops.length -1 ];
			for(int i=0; i < res.length; i++){
				res[i]= i%2==0 ? notops[i/2] :ops[i/2+1];
				if(variableCounter.containsKey(res[i])){
					Variable var = variableCounter.get(res[i]);
					var.setCount(var.getCount() + 1);
					variableCounter.put(res[i], var);
				}
				
			}
			
			
		}else{
			Variable variable = new Variable();
			variable.setCount(variable.getCount() + 1);
			variable.setName(str.trim());
			variable.setLineNumber(lineCount);
			variableCounter.put(str.trim(), variable);
		}
	}

	
	private static int checkMultipleOccurancesOfVar(String line){
		int count = 0;
		//check for multiple occurances of var string in a line.
		Pattern p = Pattern.compile("var");
		Pattern p1 = Pattern.compile("let");
		Matcher m = p.matcher(line);
		Matcher m1 = p1.matcher(line);
		
		while (m.find()||m1.find()){
		    count +=1;
		}
		
		
		return count;
	
	}
}
