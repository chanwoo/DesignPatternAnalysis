package kr.ac.snu.selab.soot.graph.refgraph;

import java.util.ArrayList;
import java.util.List;

import kr.ac.snu.selab.soot.analyzer.LocalInfo;
import kr.ac.snu.selab.soot.graph.Node;
import kr.ac.snu.selab.soot.util.XMLWriter;

public class LocalInfoNode extends Node {

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
		return null;
	}

	@Override
	public void writeXML(XMLWriter writer) {
	}

	void addSource(LocalInfoNode node) {
		sourceNodes.add(node);
	}

	void addTarget(LocalInfoNode node) {
		targetNodes.add(node);
	}

}
