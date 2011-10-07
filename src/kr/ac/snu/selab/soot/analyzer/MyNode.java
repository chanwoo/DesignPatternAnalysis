package kr.ac.snu.selab.soot.analyzer;

public abstract class MyNode {
	public abstract String toString();
	public abstract String toXML();
	public abstract boolean isCreator();
	public abstract boolean isCaller();
}
