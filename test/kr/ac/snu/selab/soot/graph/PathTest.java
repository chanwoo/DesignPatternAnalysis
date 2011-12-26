package kr.ac.snu.selab.soot.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import kr.ac.snu.selab.soot.util.XMLWriter;

import org.junit.Test;

public class PathTest {

	@Test
	public void testEquals() {
		Path<StringNode> path1 = new Path<StringNode>();
		path1.add(new StringNode("A"));
		path1.add(new StringNode("B"));
		path1.add(new StringNode("C"));

		Path<StringNode> path2 = new Path<StringNode>();
		path2.add(new StringNode("A"));
		path2.add(new StringNode("B"));
		path2.add(new StringNode("C"));

		assertEquals(path1, path2);
	}

	@Test
	public void testInEquals() {
		Path<StringNode> path1 = new Path<StringNode>();
		path1.add(new StringNode("A"));
		path1.add(new StringNode("B"));
		path1.add(new StringNode("C"));

		Path<StringNode> path2 = new Path<StringNode>();
		path2.add(new StringNode("A"));
		path2.add(new StringNode("B"));
		path2.add(new StringNode("C"));
		path2.add(new StringNode("D"));

		assertFalse(path1.equals(path2));
	}

	@Test
	public void testHashCodeWithHashCodeBuilder() {
		Path<SampleNode> path1 = new Path<SampleNode>();
		path1.add(new SampleNode(new NodeElement("A")));
		path1.add(new SampleNode(new NodeElement("B")));
		path1.add(new SampleNode(new NodeElement("C")));

		Path<SampleNode> path2 = new Path<SampleNode>();
		path2.add(new SampleNode(new NodeElement("A")));
		path2.add(new SampleNode(new NodeElement("B")));
		path2.add(new SampleNode(new NodeElement("C")));

		assertTrue(path1.hashCode() == path2.hashCode());
	}

	@Test
	public void testHashCodeWithHashCodeBuilder2() {
		Path<SampleNode> path1 = new Path<SampleNode>();
		path1.add(new SampleNode(new NodeElement("A")));
		path1.add(new SampleNode(new NodeElement("B")));
		path1.add(new SampleNode(new NodeElement("C")));

		Path<SampleNode> path2 = new Path<SampleNode>();
		path2.add(new SampleNode(new NodeElement("A")));
		path2.add(new SampleNode(new NodeElement("B")));
		path2.add(new SampleNode(new NodeElement("C")));
		path2.add(new SampleNode(new NodeElement("D")));

		assertFalse(path1.hashCode() == path2.hashCode());
	}

	@Test
	public void testHashCodeWithHashCodeBuilder3() {
		Path<SampleNode> path1 = new Path<SampleNode>();
		path1.add(new SampleNode(new NoHashCodeNodeElement("A")));
		path1.add(new SampleNode(new NoHashCodeNodeElement("B")));
		path1.add(new SampleNode(new NoHashCodeNodeElement("C")));

		Path<SampleNode> path2 = new Path<SampleNode>();
		path2.add(new SampleNode(new NoHashCodeNodeElement("A")));
		path2.add(new SampleNode(new NoHashCodeNodeElement("B")));
		path2.add(new SampleNode(new NoHashCodeNodeElement("C")));

		assertFalse(path1.hashCode() == path2.hashCode());
	}

	private static class StringNode extends Node {
		public StringNode(String element) {
			super(element);
		}

		@Override
		public String toXML() {
			return null;
		}

		@Override
		public void writeXML(XMLWriter writer) {
		}
	}

	private static class NodeElement {
		String value;

		NodeElement(String v) {
			this.value = v;
		}

		@Override
		public int hashCode() {
			return value.hashCode();
		}
	}

	private static class NoHashCodeNodeElement {
		@SuppressWarnings("unused")
		String value;

		NoHashCodeNodeElement(String v) {
			this.value = v;
		}
	}

	private static class SampleNode extends Node {
		public SampleNode(Object element) {
			super(element);
		}

		@Override
		public String toXML() {
			return null;
		}

		@Override
		public void writeXML(XMLWriter writer) {
		}
	}
}
