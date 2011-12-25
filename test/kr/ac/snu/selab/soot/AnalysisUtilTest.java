package kr.ac.snu.selab.soot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import kr.ac.snu.selab.soot.analyzer.AbstractAnalyzer;
import kr.ac.snu.selab.soot.analyzer.AnalysisUtil;
import kr.ac.snu.selab.soot.analyzer.Creator;
import kr.ac.snu.selab.soot.analyzer.LocalInfo;
import kr.ac.snu.selab.soot.analyzer.MethodInfo;
import kr.ac.snu.selab.soot.analyzer.MethodInternalPath;
import kr.ac.snu.selab.soot.core.AbstractProject;
import kr.ac.snu.selab.soot.core.ProjectManager;
import kr.ac.snu.selab.soot.graph.Path;
import kr.ac.snu.selab.soot.graph.refgraph.LocalInfoNode;
import kr.ac.snu.selab.soot.graph.refgraph.ReferenceFlowGraph;
import kr.ac.snu.selab.soot.util.XMLWriter;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import soot.Hierarchy;
import soot.Local;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.toolkits.callgraph.CallGraph;

public class AnalysisUtilTest {

	private static Logger logger = Logger.getLogger(AnalysisUtilTest.class);

	private static final String PROJECTS_NAME = "SparkTest";
	private static final String PROJECTS_FILE_NAME = "projects.xml";

	static AnalysisUtil au;
	static Map<String, SootClass> classMap;
	static Hierarchy hierarchy;
	static CallGraph cg;

	static List<Unit> units;
	static Map<String, Local> locals;
	static Map<String, LocalInfo> allLocalInfos;

	// In
	static Map<String, LocalInfo> localsOfMethodParam;
	static Map<String, LocalInfo> localsLeftOfField;
	static Map<String, LocalInfo> localsLeftOfInvoke;

	// Out
	static Map<String, LocalInfo> localsOfInvokeParam;
	static Map<String, LocalInfo> localsRightOfField;
	static Map<String, LocalInfo> localOfReturn;

	// Creation
	static Map<String, LocalInfo> creations;

	static int numOfFieldInRightStmt;
	static int numOfFieldInLeftStmt;
	static int numOfInvokeInRightStmt;

	static SootClass a;
	static SootClass b;
	static SootClass c;
	static SootClass d;
	static SootClass i;
	static SootClass subD;
	static SootClass subI;

	static Map<SootClass, List<Creator>> creatorsOfI;
	static Map<SootClass, List<Creator>> creatorsOfA;
	static Map<SootClass, List<Creator>> creatorsOfB;
	static Map<SootClass, List<Creator>> creatorsOfC;

	static MethodInternalPath mip_methodParamToReturn1;
	static MethodInternalPath mip_methodParamToReturn2;
	static MethodInternalPath mip_methodParamToReturn3;

	static Map<LocalInfo, LocalInfo> internalEdges;
	static MethodInfo methodInfo;
	static Map<SootMethod, MethodInfo> methodInfoMap;
	static SootMethod inout;

	static ReferenceFlowGraph referenceFlowGraph;
	static Map<LocalInfoNode, List<Path<LocalInfoNode>>> referenceFlows;

	
	// for paramter number consistency test
	static Map<String, LocalInfo> localsOfMethodParam_methodForAnalyzeMethodParamToReturnTest;
	static Map<String, LocalInfo> localsOfMethodParam_methodForAnalyzeMethodParamToReturnTest2;
	static Map<String, LocalInfo> localsOfInvokeParam_main;
	
	static boolean touch = false;

	@Before
	public void prepare() throws Throwable {
		if (touch)
			return;

		touch = true;

		au = new AnalysisUtil();

		numOfFieldInRightStmt = 0;
		numOfFieldInLeftStmt = 0;
		numOfInvokeInRightStmt = 0;

		ProjectManager projects = ProjectManager.getInstance();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(findProjectsFile());
			projects.loadProjects(fis);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}

		AbstractProject project = projects.getProject(PROJECTS_NAME);
		assertNotNull("Cannot find project!!!", project);

		AbstractAnalyzer analyzer = new TestRunner(project);

		PackManager.v().getPack("jtp")
				.add(new Transform("jtp.Experiment", analyzer));

		final String[] arguments = { "-cp", project.getClassPath(), "-f", "J",
				"-w", "-p", "cg.spark", "verbose:true,on-fly-cg:true", "-d",
				project.getJimpleDirectory(), "--process-dir",
				project.getSourceDirectory() };

		soot.Main.main(arguments);
	}

	@Test
	public void unitsTest() {
		assertEquals(27, units.size());
	}

	@Test
	public void localsTest() {
		assertEquals(17, locals.size());
	}

	@Test
	public void isFieldInRightSideTest() {
		assertEquals(5, numOfFieldInRightStmt);
	}

	@Test
	public void isFieldInLeftSideTest() {
		assertEquals(5, numOfFieldInLeftStmt);
	}

	@Test
	public void isInvokeInRightSideTest() {
		assertEquals(5, numOfInvokeInRightStmt);
	}

	@Test
	public void localsOfMethodParamTest() {
		assertEquals(2, localsOfMethodParam.size());

		Set<String> localsOfMethodParamStrs = localsOfMethodParam.keySet();
		assertTrue(localsOfMethodParamStrs.contains("arg1"));
		assertTrue(localsOfMethodParamStrs.contains("arg2"));
	}

	@Test
	public void localsLeftOfFieldTest() {
		assertEquals(5, localsLeftOfField.size());

		Set<String> localsLeftOfFieldStrs = localsLeftOfField.keySet();

		assertTrue(localsLeftOfFieldStrs.contains("temp$0"));
		assertTrue(localsLeftOfFieldStrs.contains("temp$3"));
		assertTrue(localsLeftOfFieldStrs.contains("temp$7"));
		assertTrue(localsLeftOfFieldStrs.contains("temp$8"));
		assertTrue(localsLeftOfFieldStrs.contains("temp$10"));
	}

	@Test
	public void localsLeftOfInvokeTest() {
		assertEquals(5, localsLeftOfInvoke.size());

		Set<String> localsLeftOfInvokeStrs = localsLeftOfInvoke.keySet();

		// temp$2, 4, 5, 6, 9
		assertTrue(localsLeftOfInvokeStrs.contains("temp$2"));
		assertTrue(localsLeftOfInvokeStrs.contains("temp$4"));
		assertTrue(localsLeftOfInvokeStrs.contains("temp$5"));
		assertTrue(localsLeftOfInvokeStrs.contains("temp$6"));
		assertTrue(localsLeftOfInvokeStrs.contains("temp$9"));
	}

	@Test
	public void isSubtypeIncludingTest() {
		assertNotNull("SootClass.toString() != Type.toString()", i);
		assertNotNull("SootClass.toString() != Type.toString()", c);
		assertNotNull("SootClass.toString() != Type.toString()", d);
		assertNotNull("SootClass.toString() != Type.toString()", subD);
		assertNotNull("SootClass.toString() != Type.toString()", subI);

		assertTrue(au.isSubtypeIncluding(i, i, hierarchy));
		assertTrue(au.isSubtypeIncluding(c, c, hierarchy));
		assertTrue(au.isSubtypeIncluding(c, i, hierarchy));
		assertTrue(au.isSubtypeIncluding(subD, d, hierarchy));
		assertTrue(au.isSubtypeIncluding(subI, i, hierarchy));
		assertFalse(au.isSubtypeIncluding(c, d, hierarchy));
	}

	// Out
	@Test
	public void localOfReturnTest() {
		assertEquals("temp$10", localOfReturn.values().iterator().next()
				.local().toString());
	}

	// Out
	@Test
	public void localsOfInvokeParamTest() {
		assertEquals(3, localsOfInvokeParam.size());
		assertTrue(localsOfInvokeParam.containsKey("arg1"));
		assertTrue(localsOfInvokeParam.containsKey("temp$0"));
		assertTrue(localsOfInvokeParam.containsKey("dummy"));
	}

	// Out
	@Test
	public void localsRightOfFieldTest() {
		assertTrue(localsRightOfField.containsKey("newB"));
		assertTrue(localsRightOfField.containsKey("temp$3"));
		assertTrue(localsRightOfField.containsKey("temp$7"));
		assertTrue(localsRightOfField.containsKey("temp$8"));
		assertTrue(localsRightOfField.containsKey("temp$9"));
	}

	@Test
	public void typeFilterOfLocalMapTest() {
		assertEquals(1,
				au.typeFilterOfLocalMap(allLocalInfos, c, hierarchy, classMap)
						.size());
		assertEquals(16,
				au.typeFilterOfLocalMap(allLocalInfos, i, hierarchy, classMap)
						.size());
		SootClass t = classMap.get("T");
		assertEquals(1,
				au.typeFilterOfLocalMap(allLocalInfos, t, hierarchy, classMap)
						.size());
		assertEquals(
				0,
				au.typeFilterOfLocalMap(allLocalInfos, subD, hierarchy,
						classMap).size());
		assertEquals(
				0,
				au.typeFilterOfLocalMap(allLocalInfos, subI, hierarchy,
						classMap).size());
	}

	@Test
	public void creatorsTest() {
		assertEquals(2, creatorsOfI.get(i).size());
		assertEquals(1, creatorsOfA.get(a).size());
		assertEquals(1, creatorsOfB.get(b).size());
		assertTrue(creatorsOfC.isEmpty());
	}

	@Test
	public void analyzeMethodParamToReturnTest() {
		assertEquals(2, mip_methodParamToReturn1.methodParamToReturn());
		assertEquals(3, mip_methodParamToReturn2.methodParamToReturn());
		assertEquals(3, mip_methodParamToReturn3.methodParamToReturn());
	}

	
//	@Test
//	public void key_LocalInfo_Test() {
//		List<String> inMethodParamStr = new ArrayList<String>();
//		
//		for (LocalInfo localInfo : localsOfMethodParam.values()) {
//			inMethodParamStr.add(localInfo.key());
//		}
//		
//		assertTrue(inMethodParamStr.contains("in_methodParam_<T: I inout(I,I)>_arg1_0"));
//		assertTrue(inMethodParamStr.contains("in_methodParam_<T: I inout(I,I)>_arg2_1"));
//		
//		List<String> inFieldStr = new ArrayList<String>();
//		
//		for (LocalInfo localInfo : localsLeftOfField.values()) {
//			inFieldStr.add(localInfo.key());
//		}
//		
//		assertTrue(inFieldStr.contains("in_field_<T: I inout(I,I)>_temp$0_-1"));
//		assertTrue(inFieldStr.contains("in_field_<T: I inout(I,I)>_temp$3_-1"));
//		assertTrue(inFieldStr.contains("in_field_<T: I inout(I,I)>_temp$7_-1"));
//		assertTrue(inFieldStr.contains("in_field_<T: I inout(I,I)>_temp$8_-1"));
//		assertTrue(inFieldStr.contains("in_field_<T: I inout(I,I)>_temp$10_-1"));
//		
//		List<String> inInvokeStr = new ArrayList<String>();
//		
//		for (LocalInfo localInfo : localsLeftOfInvoke.values()) {
//			inInvokeStr.add(localInfo.key());
//		}
//		
//		assertTrue(inInvokeStr.contains("in_invoke_<T: I inout(I,I)>_temp$2_-1"));
//		assertTrue(inInvokeStr.contains("in_invoke_<T: I inout(I,I)>_temp$4_-1"));
//		assertTrue(inInvokeStr.contains("in_invoke_<T: I inout(I,I)>_temp$5_-1"));
//		assertTrue(inInvokeStr.contains("in_invoke_<T: I inout(I,I)>_temp$6_-1"));
//		assertTrue(inInvokeStr.contains("in_invoke_<T: I inout(I,I)>_temp$9_-1"));
//
//		List<String> outInvokeParamStr = new ArrayList<String>();
//		
//		for (LocalInfo localInfo : localsOfInvokeParam.values()) {
//			outInvokeParamStr.add(localInfo.key());
//		}
//		
//		assertTrue(outInvokeParamStr.contains("out_invokeParam_<T: I inout(I,I)>_arg1_0"));
//		assertTrue(outInvokeParamStr.contains("out_invokeParam_<T: I inout(I,I)>_temp$0_0"));
//		assertTrue(outInvokeParamStr.contains("out_invokeParam_<T: I inout(I,I)>_dummy_0"));
//		
//		List<String> outFieldStr = new ArrayList<String>();
//		
//		for (LocalInfo localInfo : localsRightOfField.values()) {
//			outFieldStr.add(localInfo.key());
//		}
//		
//		assertTrue(outFieldStr.contains("out_field_<T: I inout(I,I)>_newB_-1"));
//		assertTrue(outFieldStr.contains("out_field_<T: I inout(I,I)>_temp$3_-1"));
//		assertTrue(outFieldStr.contains("out_field_<T: I inout(I,I)>_temp$7_-1"));
//		assertTrue(outFieldStr.contains("out_field_<T: I inout(I,I)>_temp$8_-1"));
//		assertTrue(outFieldStr.contains("out_field_<T: I inout(I,I)>_temp$9_-1"));
//		
//		List<String> outReturnStr = new ArrayList<String>();
//		
//		for (LocalInfo localInfo : localOfReturn.values()) {
//			outReturnStr.add(localInfo.key());
//		}
//		
//		assertTrue(outReturnStr.contains("out_return_<T: I inout(I,I)>_temp$10_-1"));
//	}
	


	@Test
	public void key_LocalInfo_Test() {
		List<String> inMethodParamStr = new ArrayList<String>();

		for (LocalInfo localInfo : localsOfMethodParam.values()) {
			inMethodParamStr.add(localInfo.key());
		}

		assertTrue(inMethodParamStr
				.contains("in_methodParam_<T: I inout(I,I)>_arg1_0"));
		assertTrue(inMethodParamStr
				.contains("in_methodParam_<T: I inout(I,I)>_arg2_1"));

		List<String> inFieldStr = new ArrayList<String>();

		for (LocalInfo localInfo : localsLeftOfField.values()) {
			inFieldStr.add(localInfo.key());
		}

		assertTrue(inFieldStr.contains("in_field_<T: I inout(I,I)>_temp$0_-1"));
		assertTrue(inFieldStr.contains("in_field_<T: I inout(I,I)>_temp$3_-1"));
		assertTrue(inFieldStr.contains("in_field_<T: I inout(I,I)>_temp$7_-1"));
		assertTrue(inFieldStr.contains("in_field_<T: I inout(I,I)>_temp$8_-1"));
		assertTrue(inFieldStr.contains("in_field_<T: I inout(I,I)>_temp$10_-1"));

		List<String> inInvokeStr = new ArrayList<String>();

		for (LocalInfo localInfo : localsLeftOfInvoke.values()) {
			inInvokeStr.add(localInfo.key());
		}

		assertTrue(inInvokeStr
				.contains("in_invoke_<T: I inout(I,I)>_temp$2_-1"));
		assertTrue(inInvokeStr
				.contains("in_invoke_<T: I inout(I,I)>_temp$4_-1"));
		assertTrue(inInvokeStr
				.contains("in_invoke_<T: I inout(I,I)>_temp$5_-1"));
		assertTrue(inInvokeStr
				.contains("in_invoke_<T: I inout(I,I)>_temp$6_-1"));
		assertTrue(inInvokeStr
				.contains("in_invoke_<T: I inout(I,I)>_temp$9_-1"));

		List<String> outInvokeParamStr = new ArrayList<String>();

		for (LocalInfo localInfo : localsOfInvokeParam.values()) {
			outInvokeParamStr.add(localInfo.key());
		}

		assertTrue(outInvokeParamStr
				.contains("out_invokeParam_<T: I inout(I,I)>_arg1_0"));
		assertTrue(outInvokeParamStr
				.contains("out_invokeParam_<T: I inout(I,I)>_temp$0_0"));
		assertTrue(outInvokeParamStr
				.contains("out_invokeParam_<T: I inout(I,I)>_dummy_0"));

		List<String> outFieldStr = new ArrayList<String>();

		for (LocalInfo localInfo : localsRightOfField.values()) {
			outFieldStr.add(localInfo.key());
		}

		assertTrue(outFieldStr.contains("out_field_<T: I inout(I,I)>_newB_-1"));
		assertTrue(outFieldStr
				.contains("out_field_<T: I inout(I,I)>_temp$3_-1"));
		assertTrue(outFieldStr
				.contains("out_field_<T: I inout(I,I)>_temp$7_-1"));
		assertTrue(outFieldStr
				.contains("out_field_<T: I inout(I,I)>_temp$8_-1"));
		assertTrue(outFieldStr
				.contains("out_field_<T: I inout(I,I)>_temp$9_-1"));

		List<String> outReturnStr = new ArrayList<String>();

		for (LocalInfo localInfo : localOfReturn.values()) {
			outReturnStr.add(localInfo.key());
		}

		assertTrue(outReturnStr
				.contains("out_return_<T: I inout(I,I)>_temp$10_-1"));
	}

	@Test
	public void isConnectedTest() {
		assertTrue(au.isConnected(localsOfMethodParam.get("arg1"),
				localsOfInvokeParam.get("arg1")));
		assertTrue(au.isConnected(localsOfMethodParam.get("arg2"),
				localsOfInvokeParam.get("dummy")));
		assertTrue(au.isConnected(localsLeftOfField.get("temp$0"),
				localsOfInvokeParam.get("temp$0")));
		assertTrue(au.isConnected(creations.get("temp$1"),
				localsRightOfField.get("newB")));
		assertFalse(au.isConnected(localsLeftOfInvoke.get("temp$2"),
				localsRightOfField.get("temp$3")));
		assertFalse(au.isConnected(localsLeftOfInvoke.get("temp$4"),
				localsLeftOfInvoke.get("temp$9")));
		// assertFalse(au.isConnected(localsLeftOfInvoke.get("temp$5"),
		// localsLeftOfInvoke.get("temp$6"))); because of context insensitive
		// analysis
		assertTrue(au.isConnected(localsLeftOfField.get("temp$7"),
				localsLeftOfField.get("temp$8")));

		assertTrue(au.isConnected(localsLeftOfField.get("temp$10"),
				localOfReturn.get("temp$10")));
		assertFalse(au.isConnected(localsOfInvokeParam.get("dummy"),
				localOfReturn.get("temp$10")));
		assertTrue(au.isConnected(localsOfMethodParam.get("arg2"),
				localsOfInvokeParam.get("dummy")));
		assertFalse(au.isConnected(localsOfMethodParam.get("arg1"),
				localsOfInvokeParam.get("dummy")));
	}

	@Test
	public void creationsTest() {
		assertEquals(creations.size(), 1);
		assertTrue(creations.containsKey("temp$1"));
	}

	// number of edges of T.inout method should be >= 9
	// arg1 -> setA(arg1)
	// b -> setA(b)
	// arg2 -> dummy -> callI(dummy)
	// new C() -> b
	// b -> a
	// T.publicStaticFieldOfT -> a
	// publicStaticFieldOfT -> a
	// T.staticMethodA() -> b
	// publicStaticFieldOfT -> a -> return
	@Test
	public void internalEdgesTest() {
		assertTrue(9 <= internalEdges.size());
		for (LocalInfo start : internalEdges.keySet()) {
			logger.debug(start.key() + " ===> "
					+ internalEdges.get(start).key());
		}
	}

	@Test
	public void analyzeMethodTest() {
		assertEquals(methodInfo.creation().size(), 1);
		assertEquals(methodInfo.methodParamIn().size(), 2);
		assertEquals(methodInfo.fieldIn().size(), 5);
		assertEquals(methodInfo.invokeIn().size(), 5);
		assertEquals(methodInfo.invokeParamOut().size(), 3);
		assertEquals(methodInfo.fieldOut().size(), 5);
		assertEquals(methodInfo.returnOut().size(), 1);
		assertEquals(methodInfo.internalEdges().size(), 11);
	}

	@Test
	public void methodInfoMapTest() {
		assertTrue(methodInfoMap.size() >= 22);
		MethodInfo inoutInfo = methodInfoMap.get(inout);
		for (LocalInfo localInfo : inoutInfo.creation().values()) {
			logger.debug("inoutInfo.creation().values() => " + localInfo.key());
		}
		assertEquals(inoutInfo.creation().size(), 1);
		assertEquals(inoutInfo.methodParamIn().size(), 2);
		assertEquals(inoutInfo.fieldIn().size(), 5);
		assertEquals(inoutInfo.invokeIn().size(), 5);
		assertEquals(inoutInfo.invokeParamOut().size(), 3);
		assertEquals(inoutInfo.fieldOut().size(), 5);
		assertEquals(inoutInfo.returnOut().size(), 1);
		assertEquals(inoutInfo.internalEdges().size(), 11);
	}

	@Test
	public void referenceFlowGraphTest() {
		logger.debug("referenceFlowGraph.numOfNodes() => "
				+ referenceFlowGraph.numOfNodes());
		logger.debug("referenceFlowGraph.startNodes().size() => "
				+ referenceFlowGraph.startNodes().size());
	}

	@Test
	public void referenceFlowsTest() {
		logger.debug("referenceFlows.size() => " + referenceFlows.size());

		// FIXME: Fix these codes!!!
		File outputDir = new File("/Users/chanwoo/Downloads");
		if (!outputDir.exists() || !outputDir.isDirectory()) {
			outputDir = new File("/Users/dolicoli/Downloads");
		}
		File output = new File(outputDir, "paths.xml");
		XMLWriter writer = null;
		try {
			writer = new XMLWriter(new FileWriter(output));
		} catch (IOException e) {
			logger.error(e);
		}

		if (writer == null) {
			fail("Can not open output file.");
			return;
		}

		try {
			writer.startElement("ReferenceFlows");
			for (LocalInfoNode key : referenceFlows.keySet()) {
				List<Path<LocalInfoNode>> pathList = referenceFlows.get(key);
				for (Path<LocalInfoNode> path : pathList) {
					logger.debug("path size => " + path.length());
					path.writeXML(writer);
				}
			}
			writer.endElement();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		writer.close();
	}

	
	@Test
	public void paramNumConsistencyTest() {
		Map<String, LocalInfo> mp2 = localsOfMethodParam_methodForAnalyzeMethodParamToReturnTest2;
		Map<String, LocalInfo> mp1 = localsOfMethodParam_methodForAnalyzeMethodParamToReturnTest;
		Map<String, LocalInfo> ip = localsOfInvokeParam_main;
		
		logger.debug("localsOfInvokeParam_main");
		for (LocalInfo localInfo : ip.values()) {
			logger.debug("LocalInfo => " + localInfo.toString());
		}
		
		logger.debug("localsOfMethodParam_methodForAnalyzeMethodParamToReturnTest");
		for (LocalInfo localInfo : mp1.values()) {
			logger.debug("LocalInfo => " + localInfo.toString());
		}
		
		logger.debug("localsOfMethodParam_methodForAnalyzeMethodParamToReturnTest2");
		for (LocalInfo localInfo : mp2.values()) {
			logger.debug("LocalInfo => " + localInfo.toString());
		}
	}
	

	private class TestRunner extends AbstractAnalyzer {
		public TestRunner(AbstractProject project) {
			super(project);
		}

		@Override
		protected void analyze(List<SootClass> classList, Hierarchy aHierarchy) {
			cg = Scene.v().getCallGraph();
			assertNotNull("Target classes not found", classList);
			hierarchy = aHierarchy;
			classMap = new HashMap<String, SootClass>();

			for (SootClass aClass : classList) {
				classMap.put(aClass.toString(), aClass);
			}

			i = classMap.get("I");
			a = classMap.get("A");
			b = classMap.get("B");
			c = classMap.get("C");
			d = classMap.get("D");
			subD = classMap.get("SubD");
			subI = classMap.get("SubI");

			// preparation for methodInfoMapTest
			methodInfoMap = au.methodInfoMap(i, classMap, hierarchy);

			// preparation for referenceFlowGraphTest
			referenceFlowGraph = au.referenceFlowGraph(i, classMap, hierarchy,
					cg);

			// preparation for referenceFlowsTest
			referenceFlows = au.referenceFlows(i, classMap, hierarchy, cg);

			for (SootClass aClass : classList) {
				for (SootMethod aMethod : aClass.getMethods()) {
					if (aMethod.getName().equals("main")) {
						localsOfInvokeParam_main = au.localsOfInvokeParam(aMethod);
					}
					
					if (aMethod.getName().equals("callInout")) {

						// preparation for creatorsTest
						creatorsOfI = au.creators(aMethod, i, classMap,
								hierarchy);
						creatorsOfA = au.creators(aMethod, a, classMap,
								hierarchy);
						creatorsOfB = au.creators(aMethod, b, classMap,
								hierarchy);
						creatorsOfC = au.creators(aMethod, c, classMap,
								hierarchy);
					}

					if (aMethod.getName().equals(
							"methodForAnalyzeMethodParamToReturnTest")) {
						// preparation for analyzeMethodParamToReturnTest

						mip_methodParamToReturn1 = au.analyzeMethodParamToReturn(aMethod, i, hierarchy, classMap);
						
						localsOfMethodParam_methodForAnalyzeMethodParamToReturnTest = au.localsOfMethodParam(aMethod);

					}

					if (aMethod.getName().equals(
							"methodForAnalyzeMethodParamToReturnTest2")) {
						// preparation for analyzeMethodParamToReturnTest

						mip_methodParamToReturn2 = au.analyzeMethodParamToReturn(aMethod, i, hierarchy, classMap);
						mip_methodParamToReturn3 = au.analyzeMethodParamToReturn(aMethod, d, hierarchy, classMap);
						
						localsOfMethodParam_methodForAnalyzeMethodParamToReturnTest2 = au.localsOfMethodParam(aMethod);

					}

					if (aMethod.getName().equals("inout")) {

						// preparation for methodInfoMapTest
						inout = aMethod;

						// preparation for unitsTest
						units = au.units(aMethod);

						// preparation for localsTest
						locals = au.locals(aMethod);

						// preparation for typeFileterOfLocalMapTest
						allLocalInfos = new HashMap<String, LocalInfo>();
						for (Entry<String, Local> entry : locals.entrySet()) {
							LocalInfo localInfo = new LocalInfo();
							localInfo.setLocal(entry.getValue());
							allLocalInfos.put(entry.getKey(), localInfo);
						}

						for (Unit unit : units) {
							// preparation for isFieldInRightSideTest
							if (au.isFieldInRightSide(unit)) {
								numOfFieldInRightStmt++;
							}
							// preparation for isInvokeInRightSideTest
							if (au.isInvokeInRightSide(unit)) {
								numOfInvokeInRightStmt++;
							}
							// preparation for isFieldInLeftSideTest
							if (au.isFieldInLeftSide(unit)) {
								numOfFieldInLeftStmt++;
							}
						}

						// preparation for localsOfMethodParamTest
						// In
						localsOfMethodParam = au.localsOfMethodParam(aMethod);

						// preparation for localsLeftOfFieldTest
						// In
						// temp$0, temp$3, temp$7, temp$8, temp$10
						localsLeftOfField = au.localsLeftOfField(aMethod);

						// preparation for localsLeftOfInvokeTest
						// In
						localsLeftOfInvoke = au.localsLeftOfInvoke(aMethod);

						// preparation for localOfReturnTest
						// Out
						localOfReturn = au.localOfReturn(aMethod);

						// preparation for localsOfInvokeParamTest
						// Out
						localsOfInvokeParam = au.localsOfInvokeParam(aMethod);

						// preparation for localsRightOfFieldTest
						// Out
						localsRightOfField = au.localsRightOfField(aMethod);

						// preparation for creationsTest
						// Creation
						creations = au.creations(aMethod);

						// preparation for internalEdgeTest
						internalEdges = au.internalEdges(aMethod, i, hierarchy,
								classMap);

						// preparation for analyzeMethodTest
						methodInfo = au.analyzeMethod(aMethod, i, hierarchy,
								classMap);
					}
				}
			}
		}
	}

	private static File findProjectsFile() {
		URL url = ClassLoader.getSystemResource(PROJECTS_FILE_NAME);
		if (url == null) {
			return null;
		}

		File file = new File(url.getFile());
		if (file == null || !file.exists() || !file.isFile()) {
			return null;
		}
		return file;
	}
}
