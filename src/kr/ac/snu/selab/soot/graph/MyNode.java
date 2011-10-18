package kr.ac.snu.selab.soot.graph;

import kr.ac.snu.selab.soot.graphx.Node;

public abstract class MyNode extends Node {
	public MyNode(Object element) {
		super(element);
	}

	@Override
	public String toString() {
		return element.toString();
	}

	public abstract String toXML();

	public abstract boolean isCreator();

	public abstract boolean isCaller();

	public abstract boolean isStore();

	public abstract void setIsCreator(boolean value);

	public abstract void setIsCaller(boolean value);

	public abstract void setIsStore(boolean value);
}
