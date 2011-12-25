package kr.ac.snu.selab.soot.analyzer;

public class Pair<A, B> {
	private A first;
	private B second;
	
	public Pair() {
		
	}
	
	public Pair(A fst, B sec) {
		setFirst(fst);
		setSecond(sec);
	}
	
	public A first() {
		return first;
	}
	
	public void setFirst(A item) {
		first = item;
	}
	
	public B second() {
		return second;
	}
	
	public void setSecond(B item) {
		second = item;
	}
	
	public String toString() {
		String str = "<" + first.toString() + ", " + second.toString() + ">";
		return str;
	}
}
