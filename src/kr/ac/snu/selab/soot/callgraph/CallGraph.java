package kr.ac.snu.selab.soot.callgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kr.ac.snu.selab.soot.graph.Graph;
import kr.ac.snu.selab.soot.graph.MyNode;
import kr.ac.snu.selab.soot.util.MyUtil;
import soot.Hierarchy;
import soot.SootClass;
import soot.SootMethod;

public abstract class CallGraph extends Graph<MyNode> {
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
