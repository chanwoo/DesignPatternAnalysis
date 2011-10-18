package kr.ac.snu.selab.soot.graph;

public abstract class MyNode {
	public abstract String toString();
	public abstract String toXML();
	public abstract boolean isCreator();
	public abstract boolean isCaller();
	public abstract boolean isStore();
	public abstract void setIsCreator(boolean value);
	public abstract void setIsCaller(boolean value);
	public abstract void setIsStore(boolean value);
	public abstract boolean equals(Object anObject);
	public abstract int hashcode();
}
