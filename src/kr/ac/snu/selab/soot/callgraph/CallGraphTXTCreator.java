package kr.ac.snu.selab.soot.callgraph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.snu.selab.soot.analyzer.AbstractAnalyzer;
import kr.ac.snu.selab.soot.core.AbstractProject;
import kr.ac.snu.selab.soot.util.MyUtil;
import soot.Body;
import soot.Hierarchy;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInvokeStmt;

public class CallGraphTXTCreator extends AbstractAnalyzer {

	private String callGraphPath;
	public static final String CALL_GRAPH_TXT_FILE_NAME = "callgraph.txt";

	public CallGraphTXTCreator(AbstractProject project) {
		super(project);
	}

	private List<Unit> getUnits(SootMethod aMethod) {
		List<Unit> unitList = new ArrayList<Unit>();
		if (aMethod.hasActiveBody()) {
			Body body = aMethod.getActiveBody();
			unitList.addAll(body.getUnits());
		}
		return unitList;
	}

	public List<SootClass> getSubTypeClassOf(SootClass aType,
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

	@Override
	protected void preAnalysis() {
		String fileName2 = CALL_GRAPH_TXT_FILE_NAME;
		callGraphPath = MyUtil.getPath(project.getOutputDirectory(), fileName2);
	}

	@Override
	protected void analyze(List<SootClass> classList, Hierarchy hierarchy) {
		// This is a map that has keys of (class, subsignature of method) pair.
		Map<Map<SootClass, String>, SootMethod> methodMapBySubSignature = new HashMap<Map<SootClass, String>, SootMethod>();

		for (SootClass aClass : classList) {
			for (SootMethod aMethod : aClass.getMethods()) {
				Map<SootClass, String> key = new HashMap<SootClass, String>();
				key.put(aClass, aMethod.getSubSignature());
				methodMapBySubSignature.put(key, aMethod);
			}
		}

		PrintWriter writer = null;
		try {
			File outputFile = new File(callGraphPath);
			File dir = outputFile.getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			writer = new PrintWriter(new FileWriter(callGraphPath));
			for (SootClass aClass : classList) {
				for (SootMethod aMethod : aClass.getMethods()) {
					for (Unit aUnit : getUnits(aMethod)) {
						if (aUnit instanceof JInvokeStmt) {
							JInvokeStmt jInvokeStatement = (JInvokeStmt) aUnit;
							SootMethod invokedMethod = jInvokeStatement
									.getInvokeExpr().getMethod();
							SootClass receiverClass = invokedMethod
									.getDeclaringClass();
							if (classList.contains(receiverClass)) {
								writer.print(aMethod.toString());
								writer.print("\t");
								writer.println(invokedMethod.toString());

								// If there is a call to abstract type class,
								// its subclass' methods should be added to a
								// call graph.
								if (!invokedMethod.getName().equals("<init>")) {
									List<SootMethod> overrideMethodList = getOverrideMethodsOf(
											invokedMethod, hierarchy,
											methodMapBySubSignature);
									for (SootMethod overrideMethod : overrideMethodList) {
										writer.print(aMethod.toString());
										writer.print("\t");
										writer.println(overrideMethod
												.toString());
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
								if (classList.contains(receiverClass)) {
									writer.print(aMethod.toString());
									writer.print("\t");
									writer.println(invokedMethod.toString());

									// If there is a call to abstract type
									// class, its subclass' methods should be
									// added to a call graph.
									if (!invokedMethod.getName().equals(
											"<init>")) {
										List<SootMethod> overrideMethodList = getOverrideMethodsOf(
												invokedMethod, hierarchy,
												methodMapBySubSignature);
										for (SootMethod overrideMethod : overrideMethodList) {
											writer.print(aMethod.toString());
											writer.print("\t");
											writer.println(overrideMethod
													.toString());
										}
									}
								}
							}
						}
					}
				}
			}
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null)
				writer.close();
		}
	}
}
