package kr.ac.snu.selab.soot.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kr.ac.snu.selab.soot.util.XMLWriter;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public abstract class AbstractGraphTest {
	protected StringGraph graph;

	protected void setUp() {
		Logger.getLogger("kr.ac.snu.selab").setLevel(Level.OFF);
		graph = new StringGraph();
		initializeGraph();
	}

	protected void sortPaths(ArrayList<Path<StringNode>> paths) {
		Collections.sort(paths, new Comparator<Path<StringNode>>() {
			@Override
			public int compare(Path<StringNode> path1, Path<StringNode> path2) {
				List<StringNode> list1 = path1.getNodeList();
				List<StringNode> list2 = path2.getNodeList();
				int length1 = path1.length();
				int length2 = path1.length();
				int min = Math.min(length1, length2);

				for (int i = 0; i < min; i++) {
					StringNode node1 = list1.get(i);
					StringNode node2 = list2.get(i);

					String element1 = (String) node1.getElement();
					String element2 = (String) node2.getElement();

					if (element1.compareTo(element2) < 0)
						return -1;
					if (element1.compareTo(element2) > 0)
						return 1;
				}

				return 0;
			}
		});
	}

	protected static String pathString(Path<StringNode> path) {
		StringBuffer buffer = new StringBuffer();
		for (StringNode node : path.getNodeList()) {
			buffer.append(node.getElement());
			buffer.append(";");
		}
		return buffer.toString();
	}

	protected abstract void initializeGraph();

	protected static class StringGraph extends DefaultHashMapGraph<StringNode> {
	}

	protected static class StringNode extends Node {
		public StringNode(String text) {
			super(text);
		}

		@Override
		public void writeXML(XMLWriter writer) {
		}

		@Override
		public String toXML() {
			return "";
		}
	}
}
