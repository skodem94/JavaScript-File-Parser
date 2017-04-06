import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* 	This class is responsible for parsing the Javascript file to automatically detect any unused variables, any conditional statements which doesn't use the braces,
* 	and function calls which are not being declared.
*	@Author: Sree Gouri Varshini Kodem.
*/
public class JSParser {

	// map to store variable declared and number of times they have been used
	static Map<String, Variable> variableCounter = new HashMap<String, Variable>();
	// map to store function declarations
	static Map<String, Integer> functionNames = new HashMap<>();
	// map to store functions calls
	static Map<String, Integer> unDeclaredFunctions = new HashMap<>();
	// set to store the if or else blocks without braces
	static Set<Variable> braceDetector = new HashSet<Variable>();
	// stack to detect extra or missing curly brackets
	static Stack<LineAndNumber> st = new Stack<LineAndNumber>();
	static boolean checkBrace = false;
	static String conditionType = "";
	static String previous_line = "";

	public static void main(String[] args) {
		// Read JS File.
		int lineCount = 0;
		int singleLineFlag = 0, multiLineFlag = 0;
		BufferedReader reader = null;
		// Input file taken from command line arguments
		String filename = args[0];
		File jsFile = new File(filename);
		// check if the input file is a javascript file.
		if (!filename.contains(".js")) {
			System.out.println("Invalid File");
			return;
		}

		// check if file exists
		if (!jsFile.exists()) {
			System.out.println("File doesn't exist.");
			return;
		}

		// Ensure file is not empty
		// Exit if file is empty
		if (jsFile.length() == 0) {
			System.out.println("File is empty.");
			return;
		}
		try {
			String line = "";
			reader = new BufferedReader(new FileReader(filename));
			// Read file line by line
			while ((line = reader.readLine()) != null) {
				lineCount += 1;
				singleLineFlag = 0;
				// check if the line contains single line comment
				if (line.contains("//")) {
					String lines[] = line.split("//");
					// if there is a statement before comment consider it
					if (lines.length > 1) {
						line = lines[0];

					} else {
						singleLineFlag = 1;
					}
				}
				// check if line has a multi line comment
				if (line.contains("/*")) {
					String lines[] = line.split(Pattern.quote("/*"));
					if (lines.length > 1) {
						line = lines[0];

					}
					multiLineFlag = 1;
				}
				// check for end of multi line comment
				if (line.contains("*/")) {

					String lines[] = line.split(Pattern.quote("*/"));
					if (lines.length > 1) {
						line = lines[1];
						multiLineFlag = 0;
					} else {
						line = "";
						multiLineFlag = 0;
					}
				}
				// if there are no comments proceed
				if (singleLineFlag == 0 && multiLineFlag == 0) {
					// function call to check declared variables that are not
					// used.
					checkVariables(line, lineCount);
					// function call to check function calls that have not been
					// declared.
					checkFunction(line, lineCount);
					// function call to check if/else statements that don’t have
					// curly brackets.
					checkIfElse(line, lineCount);
					// function call to find any missing/extra curly brackets.
					checkBraces(line, lineCount);
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// print the variables that are declared but not used
		for (java.util.Map.Entry<String, Variable> entry : variableCounter.entrySet()) {
			String var = entry.getKey();
			Variable variable = entry.getValue();
			if (variable.getCount() == 1) {
				System.out.println("Variable Unused: " + var + " declared at Line Number: " + variable.getLineNumber());
			}
		}

		// print the functions that are called but not declared.
		Set<String> keys = unDeclaredFunctions.keySet();
		for (String key : keys) {
			if (key.contains("if") || key.contains("for") || key.contains("catch") || key.contains("while")
					|| key.contains("switch") || key.contains("console.log")) {
				// Removal functionality in Map is not included to avoid
				// concurrent modification exception.
			} else {
				System.out.println(
						"The function : " + key + " in line : " + unDeclaredFunctions.get(key) + " never declared");
			}
		}

		// print if else blocks with no braces
		for (Variable var : braceDetector) {
			System.out.println(
					"Missing Paranthesis for : " + var.getName() + " block at line number: " + var.getLineNumber());
		}

		// print missing braces

		while (!st.empty()) {
			System.out.println("Missing brace at " + st.pop());
		}
	}

	/*
	 * This method is the actual implementation to check any unused variables.
	 * 
	 * @param: line: Each line of the java script file to be parsed.
	 * 
	 * @param: lineCount: the Line count in the Javascript file given.
	 */
	private static void checkVariables(String line, int lineCount) {

		try {
			// condition to check for var and let keywords in each line
			if (line.contains("var") || line.contains("let")) {
				int count = checkMultipleOccurancesOfVar(line);
				if (count > 1) {
					// Variables separated by semicolon.
					String[] variables = line.split(";");
					for (String var : variables) {
						if (var.contains("var"))
							// replace var keyword with empty string.
							var = var.replace("var ", "");
						else
							// replace let keyword with empty string.
							var = var.replaceAll("let", "");
						if (var.contains(",")) {
							// Variables seperated by comma.
							String[] varComma = var.split(",");
							for (String str : varComma) {

								updateVariableCount(str, lineCount);
							}
						} else {
							updateVariableCount(var, lineCount);
						}
					}
				} else {
					// if only single declaration is present
					line = line.replace(";", "");
					line = line.replace("var ", "");
					if (line.contains(",")) {
						// Variables seperated by comma.
						String[] varComma = line.split(",");
						for (String str : varComma) {

							updateVariableCount(str, lineCount);
						}
					} else {
						updateVariableCount(line, lineCount);

					}
				}

			} else {
				// to remove spaces in line.
				line = line.trim();

				// split operators and non operators separately
				String[] ops = line.split("\\s*[a-zA-Z]+\\s*");
				String[] notops = line.split("\\s*[^a-zA-Z]+\\s*");
				String[] res = new String[ops.length + notops.length - 1];

				for (int i = 0; i < res.length; i++) {
					// store all the variables/alphanumeric strings into res
					res[i] = i % 2 == 0 ? notops[i / 2] : ops[i / 2 + 1];
					if (variableCounter.containsKey(res[i])) {
						// if the variable is declared update its count value to
						// indicate that it has been used.
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

	/*
	 * This method is the actual implementation to check any function calls that
	 * are not declared.
	 * 
	 * @param: line: Each line of the java script file to be parsed.
	 * 
	 * @param: lineCount: the Line count in the Javascript file given.
	 */
	private static void checkFunction(String line, int lineCount) {

		try {
			// check for function keyword in line
			if (line.contains("function")) {
				// split line by (
				String[] funDeclaration = line.split("\\(");
				String funName = funDeclaration[0];
				// replace function keywords with empty string
				funName = funName.replace("function ", "");
				functionNames.put(funName, lineCount);
				// if the function name is present in undeclared functions map
				// remove it.
				if (unDeclaredFunctions.containsKey(funName)) {
					unDeclaredFunctions.remove(funName);
				}

			}
			// check if it is a function call
			if (line.contains(");")) {
				String[] functionLine = line.split("\\(");
				String functionName = functionLine[0];
				if (functionName.contains("=")) {
					String[] funcs = functionName.split("=");
					functionName = funcs[1];
				}

				if (!functionNames.containsKey(functionName)) {
					// add the function name to the undeclared functions map
					unDeclaredFunctions.put(functionName, lineCount);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * This method is the actual implementation to check if/else statements that
	 * do not have curly brackets.
	 * @param: String: line: Each line of the java script file to be parsed.
	 * @param: int : lineCount: the Line count in the Javascript file given.
	 */
	private static void checkIfElse(String line, int lineCount) {

		try {
			if (checkBrace) {
				/*
				 * if there is if/else on the previous line and there is no
				 * brace in the current line add it to the set of if and else
				 * with no braces
				 */
				if (!line.contains("{")) {
					Variable var = new Variable();
					var.setName(conditionType);
					var.setLineNumber(lineCount - 1);
					braceDetector.add(var);
				}
				// Resetting the flag Variables.
				checkBrace = false;
				conditionType = "";
			}
			// if the line contains if/else and doesnt end with { check the next
			// line for { before adding it to the set
			if (line.contains("if") || line.contains("else")) {
				if (!line.endsWith("{")) {
					checkBrace = true;

				}

				if (line.contains("if")) {
					conditionType = "if";
				} else {
					conditionType = "else";
				}
			}

		} catch (Exception e) {

		}

	}

	/*
	 * This method is the actual implementation to check missing/extra curly
	 * brackets .
	 * 
	 * @param: String: line: Each line of the java script file to be parsed.
	 * 
	 * @param: int: lineno: the Line count in the Javascript file given.
	 */
	private static void checkBraces(String line, int lineno) {

		try {

			line = line.trim();
			// if line contain { push to stack
			if (line.contains("){")) {
				previous_line = line;
				line = line.replace("{", "");
				LineAndNumber ln = new LineAndNumber();
				ln.setData(line);
				ln.setNum(lineno);
				st.push(ln);
			} else if (line.contains("{")) {
				previous_line = line;
				LineAndNumber ln = new LineAndNumber();
				if (!previous_line.contains("{")) {
					ln.setData(previous_line);
					ln.setNum(lineno);

				} else {
					ln.setData("");
					ln.setNum(lineno);
				}
				st.push(ln);
			}
			// if line contains } pop
			if (line.contains("}")) {
				int closing_bracks = closingCount(line);
				// if a line has multiple }
				for (int i = 1; i <= closing_bracks; i++) {
					if (st.empty()) {
						System.out.println("Extra Brace at line no: " + lineno);
					} else {
						st.pop();
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// count the number of } present in a line
	static int closingCount(String current) {
		int count = 0;
		for (int i = 0; i < current.length(); i++) {
			if (current.charAt(i) == '}') {
				count++;
			}
		}
		return count;
	}

	// Update the number of a times a variable is encountered
	private static void updateVariableCount(String str, int lineCount) {
		if (str.contains("=")) {
			// Variables seperated by equal to.
			String[] varEqual = str.split("=");
			String targetVariable = varEqual[0];
			Variable variable = new Variable();
			variable.setCount(variable.getCount() + 1);
			variable.setName(targetVariable.trim());
			variable.setLineNumber(lineCount);
			variableCounter.put(targetVariable.trim(), variable);
			// remove spaces
			varEqual[1] = varEqual[1].trim();
			// if the variable is present in the map update the count
			if (variableCounter.containsKey(varEqual[1])) {
				Variable var1 = variableCounter.get(varEqual[1]);
				var1.setCount(var1.getCount() + 1);
				variableCounter.put(varEqual[1], var1);
			}
			// split operators and non operators separately

			String[] ops = varEqual[1].split("\\s*[a-zA-Z]+\\s*");
			String[] notops = varEqual[1].split("\\s*[^a-zA-Z]+\\s*");
			String[] res = new String[ops.length + notops.length - 1];
			for (int i = 0; i < res.length; i++) {
				// store all the variables/alphanumeric strings into res

				res[i] = i % 2 == 0 ? notops[i / 2] : ops[i / 2 + 1];
				// if the variable is declared update its count value to
				// indicate that it has been used.

				if (variableCounter.containsKey(res[i])) {
					Variable var = variableCounter.get(res[i]);
					var.setCount(var.getCount() + 1);
					variableCounter.put(res[i], var);
				}

			}

		} else {
			// if there is no assignment operator directly update the map
			Variable variable = new Variable();
			variable.setCount(variable.getCount() + 1);
			variable.setName(str.trim());
			variable.setLineNumber(lineCount);
			variableCounter.put(str.trim(), variable);
		}
	}

	private static int checkMultipleOccurancesOfVar(String line) {
		int count = 0;
		// check for multiple occurances of var string in a line.
		Pattern p = Pattern.compile("var");
		Pattern p1 = Pattern.compile("let");
		Matcher m = p.matcher(line);
		Matcher m1 = p1.matcher(line);

		while (m.find() || m1.find()) {
			count += 1;
		}

		return count;

	}
}
