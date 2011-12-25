package kr.ac.snu.selab.soot.graph;

import java.util.Collection;

public interface Graph<N extends Node> {

	Collection<N> sourceNodes(N aNode);

	Collection<N> targetNodes(N aNode);

	String toXML();

}
