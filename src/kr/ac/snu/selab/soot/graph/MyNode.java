package kr.ac.snu.selab.soot.graph;

import kr.ac.snu.selab.soot.graphx.Node;

public abstract class MyNode<T> extends Node<T> {
	public MyNode(T element) {
		super(element);
	}

	public abstract String toXML();

	public abstract boolean isCreator();

	public abstract boolean isCaller();

	public abstract boolean isStore();

	public abstract void setIsCreator(boolean value);

	public abstract void setIsCaller(boolean value);

	public abstract void setIsStore(boolean value);

	public boolean equals(Object anObject) {
		if (anObject.getClass() != getClass())
			return false;
		MyNode compare = (MyNode) anObject;
		return element.equals(compare.element);
	}

	public int hashcode() {
		return element.hashCode();
	}

	@Override
	public String toString() {
		return element.toString();
	}
}
