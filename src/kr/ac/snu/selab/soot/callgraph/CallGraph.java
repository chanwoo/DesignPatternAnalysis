package kr.ac.snu.selab.soot.callgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.snu.selab.soot.graph.DefaultHashMapGraph;
import kr.ac.snu.selab.soot.graph.MyNode;
import soot.Hierarchy;
import soot.SootClass;
import soot.SootMethod;

public abstract class CallGraph extends DefaultHashMapGraph<MyNode> {
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
}
