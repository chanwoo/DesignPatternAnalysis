package kr.ac.snu.selab.soot.graph;

import kr.ac.snu.selab.soot.util.XMLWriter;

public abstract class Node {
	protected Object element;

	public Node(Object element) {
		this.element = element;
	}

	public Object getElement() {
		return element;
	}

	public String key() {
		return element.toString();
	}

	@Override
	public int hashCode() {
		return element.hashCode();
	}

	@Override
	public boolean equals(Object anObject) {
		if (anObject.getClass() != getClass())
			return false;

		Node compare = (Node) anObject;
		return (element.equals(compare.element));
	}
	
	public abstract String toXML();
	
	public abstract void writeXML(XMLWriter writer);
}
