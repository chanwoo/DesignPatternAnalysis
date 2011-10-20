package kr.ac.snu.selab.soot.graph;

import java.io.CharArrayWriter;

import kr.ac.snu.selab.soot.util.XMLWriter;

public abstract class MyNode extends Node {
	public MyNode(Object element) {
		super(element);
	}

	@Override
	public String toString() {
		return element.toString();
	}

	@Override
	public String toXML() {
		CharArrayWriter writer = new CharArrayWriter();
		XMLWriter w = new XMLWriter(writer);
		writeXML(w);
		w.close();
		return writer.toString();
	}

	public abstract boolean isCreator();

	public abstract boolean isCaller();

	public abstract boolean isStore();

	public abstract void setIsCreator(boolean value);

	public abstract void setIsCaller(boolean value);

	public abstract void setIsStore(boolean value);

	public void writeXML(XMLWriter writer) {
	}
}
