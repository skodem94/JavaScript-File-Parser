import java.util.HashMap;
import java.util.Map;

public class Variable {
	
	String name;
	int count;
	int lineNumber;
	
	Map<String,Integer> operatorDictionary = new HashMap<>();
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	@Override
	public String toString() {
		return "Variable [name=" + name + ", count=" + count + ", lineNumber=" + lineNumber + "]";
	}
	

}
