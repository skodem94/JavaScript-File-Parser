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
			
		}
		while(!st.empty()){
			System.out.println("Missing brace at "+st.pop());
		  }
		br.close();
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
}