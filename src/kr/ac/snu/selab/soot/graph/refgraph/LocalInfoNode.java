package kr.ac.snu.selab.soot.graph.refgraph;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import kr.ac.snu.selab.soot.analyzer.LocalInfo;
import kr.ac.snu.selab.soot.analyzer.MyMethod;
import kr.ac.snu.selab.soot.graph.Node;
import kr.ac.snu.selab.soot.util.XMLWriter;

public class LocalInfoNode extends Node {
	
	private static Logger log = Logger.getLogger(LocalInfoNode.class);
	
	private ArrayList<LocalInfoNode> sourceNodes, targetNodes;

	public LocalInfoNode(LocalInfo element) {
		super(element);
		sourceNodes = new ArrayList<LocalInfoNode>();
		targetNodes = new ArrayList<LocalInfoNode>();
	}

	List<LocalInfoNode> getSources() {
		return sourceNodes;
	}

	List<LocalInfoNode> getTargets() {
		return targetNodes;
	}

	@Override
	public boolean equals(Object o) {
		if (o.getClass() != getClass())
			return false;

		LocalInfoNode compare = (LocalInfoNode) o;
		return element.equals(compare.element);
	}

	@Override
	public String toXML() {
		CharArrayWriter writer = new CharArrayWriter();
		XMLWriter w = new XMLWriter(writer);
		writeXML(w);
		w.close();
		return writer.toString();
	}

	@Override
	public void writeXML(XMLWriter writer) {
		try {
			writer.simpleElement("LocalInfo", element.toString());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	void addSource(LocalInfoNode node) {
		sourceNodes.add(node);
	}

	void addTarget(LocalInfoNode node) {
		targetNodes.add(node);
	}

}
