import java.io.BufferedReader;
import java.io.FileReader;
import java.security.KeyStore.Entry;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeclaredUnusedDetector {
	
	static Map<String,Variable> variableCounter = new HashMap<String,Variable>();
	
	
	public static void main(String[] args) {
		//Read JS File.
		readFile();
		
	}
	
	private static void readFile(){
		// File Reader to read the JS File.
		int lineCount = 0;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("input.txt"));
			
			//Read Each Line.
			String line = "";
			while ((line = reader.readLine()) != null) {
				lineCount = lineCount + 1;
				if(line.contains("var")){
					int count = checkMultipleOccurancesOfVar(line);
					
					if(count > 1){
						
						String[] variables = line.split(";");
						for(String var: variables){	
							var= var.replace("var ", "");
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
			}
			
			System.out.println(variableCounter.toString());
			
			for(java.util.Map.Entry<String, Variable> entry : variableCounter.entrySet()){
				String var = entry.getKey();
				Variable variable = entry.getValue();
				if(variable.getCount()==1){
					System.out.println("Variable Unused: "+ var + " declared at Line Number: "+variable.getLineNumber());
				}
			}
			
			
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
		
		Pattern p = Pattern.compile("var");
		Matcher m = p.matcher(line);
		
		while (m.find()){
		    count +=1;
		}
		
		
		return count;
	
	}
}
