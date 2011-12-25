package kr.ac.snu.selab.soot.graph;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kr.ac.snu.selab.soot.util.XMLWriter;

import org.apache.log4j.Logger;

public class Path<N extends Node> {

	private static Logger log = Logger.getLogger(Path.class);

	private ArrayList<N> nodeList;

	public Path() {
		nodeList = new ArrayList<N>();
	}

	public Path<N> copy() {
		Path<N> p = new Path<N>();
		p.nodeList.addAll(nodeList);
		return p;
	}
	
	public int hashCode() {
		return nodeList.hashCode();
	}
	
	public boolean equals(Object anObject) {
		if (anObject.getClass() != getClass())
			return false;
		
		Path compare = (Path)anObject;
		
		if (compare.length() != length()) {
			return false;
		}
		
		if (compare.last() != last()) {
			return false;
		}
		
		boolean result = true;
		int length = length();
		for (int i = 0 ; i < length ; i++) {
			if (!compare.getNodeList().get(i).equals(getNodeList().get(i))) {
				result = false;
				break;
			}
		}
		return result;
	}

	public void add(N aNode) {
		nodeList.add(aNode);
	}

	public void addTop(N aNode) {
		nodeList.add(0, aNode);
	}

	public boolean contains(N aNode) {
		return nodeList.contains(aNode);
	}

	public N last() {
		return nodeList.get(nodeList.size() - 1);
	}

	public boolean isEmpty() {
		return nodeList.isEmpty();
	}

	public List<N> getNodeList() {
		return nodeList;
	}

	public int length() {
		return nodeList.size();
	}

	public String toXML() {
		CharArrayWriter writer = new CharArrayWriter();
		XMLWriter w = new XMLWriter(writer);
		writeXML(w);
		w.close();
		return writer.toString();
	}

	public void writeXML(XMLWriter writer) {
		try {
			writer.startElement("Path");
			for (N aNode : nodeList) {
				aNode.writeXML(writer);
			}
			writer.endElement();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
