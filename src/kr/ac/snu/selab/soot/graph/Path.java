package kr.ac.snu.selab.soot.graph;

import java.io.IOException;
import java.util.ArrayList;

import kr.ac.snu.selab.soot.util.XMLWriter;

import org.apache.log4j.Logger;

public class Path<N extends Node> {

	private static Logger log = Logger.getLogger(Path.class);

	public ArrayList<N> nodeList;

	public Path() {
		nodeList = new ArrayList<N>();
	}

	public Path<N> copy() {
		Path<N> p = new Path<N>();
		p.nodeList.addAll(nodeList);
		return p;
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

	public String toXML() {
		String result = "<Path>";
		for (N aNode : nodeList) {
			result = result + aNode.toXML();
		}
		result = result + "</Path>";
		return result;
	}

	public int length() {
		return nodeList.size();
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
