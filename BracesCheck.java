import java.io.*;
import java.util.*;
class BracesCheck{
	public static void main(String[] args) throws Exception {
		int lineno=0;
		Stack<LineAndNumber> st = new Stack<LineAndNumber>();
		FileReader fr = new FileReader("input.txt");
		BufferedReader br = new BufferedReader(fr);
		try{
		String line="";
		String previous_line="";
		while((line = br.readLine())!=null){
			lineno++;
			line = line.trim();
			if(line.contains("){")){
				line = line.replace("{","");
				LineAndNumber ln = new LineAndNumber();
				ln.setData(line);
				ln.setNum(lineno);
				st.push(ln);
			}
			else if(line.contains("{")){
				LineAndNumber ln = new LineAndNumber();
				ln.setData(previous_line);
				ln.setNum(lineno-1);
				st.push(ln);
			}
			if(line.contains("}")){
				if(st.empty()){
					System.out.println("Extra Brace at line no: "+lineno);
				}
				else{
					st.pop();
				}
			}
			previous_line = line;
		}
		while(!st.empty()){
			System.out.println("Missing brace at "+st.pop());
		}
		br.close();
	      }catch(Exception ex){
		ex.printStrackTrace();
		}
	}
}