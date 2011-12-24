package kr.ac.snu.selab.soot.graph.refgraph;

import kr.ac.snu.selab.soot.analyzer.LocalInfo;
import kr.ac.snu.selab.soot.graph.Node;
import kr.ac.snu.selab.soot.util.XMLWriter;

public class LocalInfoNode extends Node {

	private LocalInfoNode[] sourceNodes, targetNodes;

	public LocalInfoNode(LocalInfo element) {
		super(element);
	}

	void collectConnectedNodes() {
		collectSources();
		collectTargets();
	}

	private void collectSources() {
		LocalInfo info = (LocalInfo) element;

		// find source nodes
		LocalInfo[] srcs = info.getSources();

		int length = srcs.length;
		sourceNodes = new LocalInfoNode[length];
		for (int i = 0; i < length; i++) {
			sourceNodes[i] = new LocalInfoNode(srcs[i]);
		}
	}

	private void collectTargets() {
		LocalInfo info = (LocalInfo) element;

		// find source nodes
		LocalInfo[] targets = info.getTargets();

		int length = targets.length;
		targetNodes = new LocalInfoNode[length];
		for (int i = 0; i < length; i++) {
			targetNodes[i] = new LocalInfoNode(targets[i]);
		}
	}

	LocalInfoNode[] getSources() {
		return sourceNodes;
	}

	LocalInfoNode[] getTargets() {
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

}
