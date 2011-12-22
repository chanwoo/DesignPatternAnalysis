package kr.ac.snu.selab.soot.callgraph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import kr.ac.snu.selab.soot.analyzer.MyMethod;
import kr.ac.snu.selab.soot.graph.MyNode;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.Edge;

public class SootCallGraph extends CallGraph {

	private soot.jimple.toolkits.callgraph.CallGraph graph;

	public SootCallGraph() {
		super();
	}

	public SootCallGraph(List<SootClass> aClassList) {
		super();

		graph = Scene.v().getCallGraph();
		
		for (SootClass aClass : aClassList) {
			for (SootMethod aMethod : aClass.getMethods()) {
				fillEdges(aMethod);
			}
		}
	}

	private void fillEdges(SootMethod aMethod) {
		MyMethod node = new MyMethod(aMethod);
		String key = node.key();

		if (!sourceMap.containsKey(key)) {
			// FIXME: Is it right?
			Iterator<Edge> sourceEdges = graph.edgesInto(aMethod);
			if (sourceEdges != null) {
				HashSet<MyNode> sourceSet = new HashSet<MyNode>();
				while (sourceEdges.hasNext()) {
					Edge edge = sourceEdges.next();
					// FIXME: Is it right?
					MethodOrMethodContext src = edge.getSrc();
					SootMethod srcMethod = src.method();

					sourceSet.add(new MyMethod(srcMethod));
				}
				sourceMap.put(key, sourceSet);
			}
		}

		if (!targetMap.containsKey(key)) {
			// FIXME: Is it right?
			Iterator<Edge> targetEdges = graph.edgesOutOf(aMethod);
			if (targetEdges != null) {
				HashSet<MyNode> targetSet = new HashSet<MyNode>();
				while (targetEdges.hasNext()) {
					Edge edge = targetEdges.next();
					// FIXME: Is it right?
					MethodOrMethodContext target = edge.getTgt();
					SootMethod targetMethod = target.method();

					targetSet.add(new MyMethod(targetMethod));
				}
				targetMap.put(key, targetSet);
			}
		}
	}
}
