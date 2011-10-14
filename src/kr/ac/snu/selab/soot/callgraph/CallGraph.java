package kr.ac.snu.selab.soot.callgraph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kr.ac.snu.selab.soot.analyzer.MyMethod;
import kr.ac.snu.selab.soot.graph.MyGraph;
import kr.ac.snu.selab.soot.graph.MyNode;
import kr.ac.snu.selab.soot.util.MyUtil;
import soot.Body;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInvokeStmt;

public class CallGraph extends MyGraph {
	public CallGraph() {
		super();
	}

	public CallGraph(List<SootClass> aClassList,
			HashMap<String, SootMethod> methodMap) {
		targetMap = new HashMap<String, HashSet<MyNode>>();
		sourceMap = new HashMap<String, HashSet<MyNode>>();

		for (SootClass aClass : aClassList) {
			for (SootMethod aMethod : aClass.getMethods()) {
				List<Unit> unitList = new ArrayList<Unit>();
				if (aMethod.hasActiveBody()) {
					Body body = aMethod.getActiveBody();
					unitList.addAll(body.getUnits());
				}
				for (Unit aUnit : unitList) {
					if (aUnit instanceof JInvokeStmt) {
						JInvokeStmt jInvokeStatement = (JInvokeStmt) aUnit;
						addEdge(aMethod.toString(), jInvokeStatement
								.getInvokeExpr().getMethod().toString(),
								methodMap);
					}
					if (aUnit instanceof JAssignStmt) {
						JAssignStmt jAssignStatement = (JAssignStmt) aUnit;
						if (jAssignStatement.containsInvokeExpr()) {
							addEdge(aMethod.toString(), jAssignStatement
									.getInvokeExpr().getMethod().toString(),
									methodMap);
						}
					}
				}
			}
		}
	}

	// public void addEdge(String source, String target,
	// Map<String, SootMethod> methodMap) {
	// if (!methodMap.containsKey(source) || !methodMap.containsKey(target))
	// return;
	//
	// if (!sourceMap.containsKey(target)) {
	// HashSet<MyNode> sourceSet = new HashSet<MyNode>();
	// sourceMap.put(target, sourceSet);
	// }
	//
	// HashSet<MyNode> sourceSet = sourceMap.get(target);
	// sourceSet.add(new MyMethod(methodMap.get(source)));
	//
	// if (!targetMap.containsKey(source)) {
	// HashSet<MyNode> targetSet = new HashSet<MyNode>();
	// targetMap.put(source, targetSet);
	// }
	//
	// HashSet<MyNode> targetSet = targetMap.get(source);
	// targetSet.add(new MyMethod(methodMap.get(target)));
	// }

	public void addEdge(String source, String target,
			Map<String, SootMethod> methodMap) {
		if (!methodMap.containsKey(source) || !methodMap.containsKey(target))
			return;

		if (!sourceMap.containsKey(target)) {
			HashSet<MyNode> sourceSet = new HashSet<MyNode>();
			sourceMap.put(target, sourceSet);
		}

		HashSet<MyNode> sourceSet = sourceMap.get(target);
		sourceSet.add(new MyMethod(methodMap.get(source)));

		if (!targetMap.containsKey(source)) {
			HashSet<MyNode> targetSet = new HashSet<MyNode>();
			targetMap.put(source, targetSet);
		}

		HashSet<MyNode> targetSet = targetMap.get(source);
		targetSet.add(new MyMethod(methodMap.get(target)));
	}

	public CallGraph load(String filePath, Map<String, SootMethod> methodMap) {
		CallGraph g = new CallGraph();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(filePath));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split("\t");
				if (tokens == null || tokens.length != 2)
					continue;

				g.addEdge(tokens[0], tokens[1], methodMap);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
				}
		}
		return g;
	}

	public String toXML() {
		String result = "";
		result = result + "<CallGraph>";
		result = result + "<SourceToTargetSetList>";
		for (Entry<String, HashSet<MyNode>> anEntry : targetMap.entrySet()) {
			result = result + "<SourceToTargetSet>";
			result = result + "<Source>"
					+ MyUtil.removeBracket(anEntry.getKey()) + "</Source>";
			result = result + "<TargetSet>";
			for (MyNode aNode : anEntry.getValue()) {
				result = result + "<Target>"
						+ MyUtil.removeBracket(aNode.toString()) + "</Target>";
			}
			result = result + "</TargetSet>";
			result = result + "</SourceToTargetSet>";
		}
		result = result + "</SourceToTargetSetList>";
		result = result + "<TargetToSourceSetList>";
		for (Entry<String, HashSet<MyNode>> anEntry : sourceMap.entrySet()) {
			result = result + "<TargetToSourceSet>";
			result = result + "<Target>"
					+ MyUtil.removeBracket(anEntry.getKey()) + "</Target>";
			result = result + "<SourceSet>";
			for (MyNode aNode : anEntry.getValue()) {
				result = result + "<Source>"
						+ MyUtil.removeBracket(aNode.toString()) + "</Source>";
			}
			result = result + "</SourceSet>";
			result = result + "</TargetToSourceSet>";
		}
		result = result + "</TargetToSourceSetList>";
		result = result + "</CallGraph>";
		return result;
	}

}
