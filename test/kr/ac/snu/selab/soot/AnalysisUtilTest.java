package kr.ac.snu.selab.soot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import kr.ac.snu.selab.soot.analyzer.AbstractAnalyzer;
import kr.ac.snu.selab.soot.analyzer.AnalysisUtil;
import kr.ac.snu.selab.soot.analyzer.LocalInfo;
import kr.ac.snu.selab.soot.core.AbstractProject;
import kr.ac.snu.selab.soot.core.ProjectManager;

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
	
	static int numOfFieldInRightStmt;
	static int numOfFieldInLeftStmt;
	static int numOfInvokeInRightStmt;
	
	static SootClass c;
	static SootClass d;
	static SootClass i;
	static SootClass subD;
	static SootClass subI;
	
	static boolean touch = false;
	
	@Before
	public void prepare() throws Throwable {
		if(touch) return;
		
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

		
		final String[] arguments = { "-cp", project.getClassPath(), "-f", "J", "-w", "-p", "cg.spark", "verbose:true,on-fly-cg:true",
				"-d", project.getJimpleDirectory(), "--process-dir", project.getSourceDirectory()};

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
		assertEquals("temp$10", localOfReturn.values().iterator().next().local().toString());
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
		assertEquals(1, au.typeFilterOfLocalMap(allLocalInfos, c, hierarchy, classMap).size());
		assertEquals(16, au.typeFilterOfLocalMap(allLocalInfos, i, hierarchy, classMap).size());
		SootClass t = classMap.get("T");
		assertEquals(1, au.typeFilterOfLocalMap(allLocalInfos, t, hierarchy, classMap).size());
		assertEquals(0, au.typeFilterOfLocalMap(allLocalInfos, subD, hierarchy, classMap).size());
		assertEquals(0, au.typeFilterOfLocalMap(allLocalInfos, subI, hierarchy, classMap).size());
	}
	
	private class TestRunner extends AbstractAnalyzer {
		public TestRunner(AbstractProject project) {
			super(project);
		}

		@Override
		protected void analyze(List<SootClass> classList, Hierarchy aHierarchy) {
			CallGraph cg = Scene.v().getCallGraph();
			assertNotNull("Target classes not found", classList);
			hierarchy = aHierarchy;
			classMap = new HashMap<String, SootClass>();
			
			for (SootClass aClass : classList) {
				classMap.put(aClass.toString(), aClass);
			}
			

			for (SootClass aClass : classList) {
				for (SootMethod aMethod : aClass.getMethods()) {
					if (aMethod.getName().equals("inout")) {
						
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
						
						// preparation for isSubtypeIncludingTest
						String typeStr1 = locals.get("temp$0").getType().toString();
						String typeStr2 = locals.get("temp$1").getType().toString();
						i = classMap.get(typeStr1);
						c = classMap.get(typeStr2);
						d = classMap.get("D");
						subD = classMap.get("SubD");
						subI = classMap.get("SubI");
						
						// preparation for localOfReturnTest
						// Out
						localOfReturn = au.localOfReturn(aMethod);
						
						// preparation for localsOfInvokeParamTest
						// Out
						localsOfInvokeParam = au.localsOfInvokeParam(aMethod);
						
						// preparation for localsRightOfFieldTest
						// Out
						localsRightOfField = au.localsRightOfField(aMethod);
						
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
