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
import kr.ac.snu.selab.soot.graph.Graph;
import kr.ac.snu.selab.soot.graph.MyNode;
import kr.ac.snu.selab.soot.util.MyUtil;
import soot.Body;
import soot.Hierarchy;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInvokeStmt;

public class CallGraph extends Graph<MyNode> {
	public CallGraph() {
		super();
	}

	public CallGraph(List<SootClass> aClassList,
			HashMap<String, SootMethod> methodMap, Hierarchy aHierarchy) {
		targetMap = new HashMap<String, HashSet<MyNode>>();
		sourceMap = new HashMap<String, HashSet<MyNode>>();

		Map<String, MyNode> nodeMap = new HashMap<String, MyNode>();
		for (Entry<String, SootMethod> anEntry : methodMap.entrySet()) {
			nodeMap.put(anEntry.getKey(), new MyMethod(anEntry.getValue()));
		}

		// This is a map that has keys of (class, subsignature of method) pair.
		Map<Map<SootClass, String>, SootMethod> methodMapBySubSignature = new HashMap<Map<SootClass, String>, SootMethod>();

		for (SootClass aClass : aClassList) {
			for (SootMethod aMethod : aClass.getMethods()) {
				Map<SootClass, String> key = new HashMap<SootClass, String>();
				key.put(aClass, aMethod.getSubSignature());
				methodMapBySubSignature.put(key, aMethod);
			}
		}

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
						SootMethod invokedMethod = jInvokeStatement
								.getInvokeExpr().getMethod();
						SootClass receiverClass = invokedMethod
								.getDeclaringClass();
						if (aClassList.contains(receiverClass)) {
							addEdge(aMethod.toString(),
									invokedMethod.toString(), nodeMap);
							// If there is a call to abstract type class, its
							// subclass' methods should be added to a call
							// graph.
							if (!invokedMethod.getName().equals("<init>")) {
								List<SootMethod> overrideMethodList = getOverrideMethodsOf(
										invokedMethod, aHierarchy,
										methodMapBySubSignature);
								for (SootMethod overrideMethod : overrideMethodList) {
									addEdge(aMethod.toString(),
											overrideMethod.toString(), nodeMap);
								}
							}
						}
					}
					if (aUnit instanceof JAssignStmt) {
						JAssignStmt jAssignStatement = (JAssignStmt) aUnit;
						if (jAssignStatement.containsInvokeExpr()) {
							SootMethod invokedMethod = jAssignStatement
									.getInvokeExpr().getMethod();
							SootClass receiverClass = invokedMethod
									.getDeclaringClass();
							if (aClassList.contains(receiverClass)) {
								addEdge(aMethod.toString(),
										invokedMethod.toString(), nodeMap);

								// If there is a call to abstract type class,
								// its
								// subclass' methods should be added to a call
								// graph.
								if (!invokedMethod.getName().equals("<init>")) {
									List<SootMethod> overrideMethodList = getOverrideMethodsOf(
											invokedMethod, aHierarchy,
											methodMapBySubSignature);
									for (SootMethod overrideMethod : overrideMethodList) {
										addEdge(aMethod.toString(),
												overrideMethod.toString(),
												nodeMap);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private List<SootClass> getSubTypeClassOf(SootClass aType,
			Hierarchy aHierarchy) {
		if (aType.isInterface()) {
			return aHierarchy.getImplementersOf(aType);
		} else {
			return aHierarchy.getSubclassesOf(aType);
		}
	}

	public List<SootMethod> getOverrideMethodsOf(SootMethod aMethod,
			Hierarchy aHierarchy,
			Map<Map<SootClass, String>, SootMethod> aMethodMapBySubSignature) {
		List<SootMethod> result = new ArrayList<SootMethod>();
		SootClass receiverType = aMethod.getDeclaringClass();
		List<SootClass> subTypeClassList = getSubTypeClassOf(receiverType,
				aHierarchy);
		if (!subTypeClassList.isEmpty()) {
			for (SootClass aClass : subTypeClassList) {
				Map<SootClass, String> key = new HashMap<SootClass, String>();
				key.put(aClass, aMethod.getSubSignature());
				SootMethod overrideMethod = aMethodMapBySubSignature.get(key);
				if (overrideMethod != null) {
					result.add(overrideMethod);
				}
			}
		}
		return result;
	}

	public void addEdge(String source, String target,
			Map<String, MyNode> nodeMap) {
		if (!nodeMap.containsKey(source) || !nodeMap.containsKey(target))
			return;

		if (!sourceMap.containsKey(target)) {
			HashSet<MyNode> sourceSet = new HashSet<MyNode>();
			sourceMap.put(target, sourceSet);
		}

		HashSet<MyNode> sourceSet = sourceMap.get(target);
		sourceSet.add(nodeMap.get(source));

		if (!targetMap.containsKey(source)) {
			HashSet<MyNode> targetSet = new HashSet<MyNode>();
			targetMap.put(source, targetSet);
		}

		HashSet<MyNode> targetSet = targetMap.get(source);
		targetSet.add(nodeMap.get(target));
	}

	public CallGraph load(String filePath, Map<String, SootMethod> methodMap) {
		CallGraph g = new CallGraph();
		BufferedReader reader = null;
		Map<String, MyNode> nodeMap = new HashMap<String, MyNode>();
		for (Entry<String, SootMethod> anEntry : methodMap.entrySet()) {
			nodeMap.put(anEntry.getKey(), new MyMethod(anEntry.getValue()));
		}
		try {
			reader = new BufferedReader(new FileReader(filePath));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split("\t");
				if (tokens == null || tokens.length != 2)
					continue;

				g.addEdge(tokens[0], tokens[1], nodeMap);
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
