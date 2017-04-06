class LineAndNumber{
	String data;
	int num;
	public void setData(String s){
		this.data = s;
	}

	public String getData(){
		return this.data;
	}

	public void setNum(int n){
		this.num = n;
	}

	public int getNum(){
		return this.num;
	}

	@Override
	public String toString(){
		return " "+this.data+" line number: "+this.num;
	}
}