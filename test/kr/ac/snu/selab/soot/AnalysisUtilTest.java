package kr.ac.snu.selab.soot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	static List<Unit> units;
	static Map<String, LocalInfo> localsOfMethodParam;
	static Map<String, LocalInfo> localsLeftOfField;
	static Map<String, Local> locals;
	static int numOfFieldInRightStmt;
	static int numOfFieldInLeftStmt;
	static int numOfInvokeInRightStmt;
	static Map<String, LocalInfo> localsLeftOfInvoke;
	static boolean touch = false;
	
	@Before
	public void prepare() throws Throwable {
		if(touch) return;
		
		touch = true;
		
		units = new ArrayList<Unit>();
		locals = new HashMap<String, Local>();
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
	public void test() {
		assertEquals(17, locals.size());
		assertEquals(2, localsOfMethodParam.size());
		assertEquals(5, numOfFieldInRightStmt);
		assertEquals(5, localsLeftOfField.size());
		
		List<String> localsLeftOfFieldStrs = new ArrayList<String>();
		for (String localStr : localsLeftOfField.keySet()) {
			localsLeftOfFieldStrs.add(localStr);
		}
		
		assertTrue(localsLeftOfFieldStrs.contains("temp$0"));
		assertTrue(localsLeftOfFieldStrs.contains("temp$3"));
		assertTrue(localsLeftOfFieldStrs.contains("temp$7"));
		assertTrue(localsLeftOfFieldStrs.contains("temp$8"));
		assertTrue(localsLeftOfFieldStrs.contains("temp$10"));
		
		assertEquals(5, numOfInvokeInRightStmt);
		
		assertEquals(5, localsLeftOfInvoke.size());
		
		List<String> localsLeftOfInvokeStrs = new ArrayList<String>();
		for (String localStr : localsLeftOfInvoke.keySet()) {
			localsLeftOfInvokeStrs.add(localStr);
		}
		
		// temp$2, 4, 5, 6, 9
		assertTrue(localsLeftOfInvokeStrs.contains("temp$2"));
		assertTrue(localsLeftOfInvokeStrs.contains("temp$4"));
		assertTrue(localsLeftOfInvokeStrs.contains("temp$5"));
		assertTrue(localsLeftOfInvokeStrs.contains("temp$6"));
		assertTrue(localsLeftOfInvokeStrs.contains("temp$9"));
	}
	
	@Test
	public void test2() {
		assertEquals(27, units.size());
	}

	private class TestRunner extends AbstractAnalyzer {
		public TestRunner(AbstractProject project) {
			super(project);
		}

		@Override
		protected void analyze(List<SootClass> classList, Hierarchy hierarchy) {
			CallGraph cg = Scene.v().getCallGraph();
			AnalysisUtil au = new AnalysisUtil();
			assertNotNull("Target classes not found", classList);
			Map<String, SootClass> classMap = new HashMap<String, SootClass>();
			
			for (SootClass aClass : classList) {
				classMap.put(aClass.toString(), aClass);
			}
			

			for (SootClass aClass : classList) {
				for (SootMethod aMethod : aClass.getMethods()) {
					if (aMethod.getName().equals("inout")) {
						
						units = au.units(aMethod);
						
						locals = au.locals(aMethod);
					
						localsOfMethodParam = au.localsOfMethodParam(aMethod);
						
						// temp$0, temp$3, temp$7, temp$8, temp$10
						localsLeftOfField = au.localsLeftOfField(aMethod);
						
						for (Unit unit : units) {
							if (au.isFieldInRightSide(unit)) {
								numOfFieldInRightStmt++;
							}
							
							if (au.isInvokeInRightSide(unit)) {
								numOfInvokeInRightStmt++;
							}
							
							if (au.isFieldInLeftSide(unit)) {
								numOfFieldInLeftStmt++;
							}
						}
						
						localsLeftOfInvoke = au.localsLeftOfInvoke(aMethod);
						
						String typeStr1 = locals.get("temp$0").getType().toString();
						String typeStr2 = locals.get("temp$1").getType().toString();
						SootClass i = classMap.get(typeStr1);
						SootClass c = classMap.get(typeStr2);						
						
						assertNotNull("SootClass.toString() != Type.toString()", i);
						assertNotNull("SootClass.toString() != Type.toString()", c);
						
						SootClass d = classMap.get("D");
						SootClass subD = classMap.get("SubD");
						SootClass subI = classMap.get("SubI");
						
						assertNotNull("SootClass.toString() != Type.toString()", d);
						assertNotNull("SootClass.toString() != Type.toString()", subD);
						assertNotNull("SootClass.toString() != Type.toString()", subI);
						
						assertTrue(au.isSubtypeIncluding(i, i, hierarchy));
						assertTrue(au.isSubtypeIncluding(c, c, hierarchy));
						assertTrue(au.isSubtypeIncluding(c, i, hierarchy));
						assertTrue(au.isSubtypeIncluding(subD, d, hierarchy));
						assertTrue(au.isSubtypeIncluding(subI, i, hierarchy));
						assertFalse(au.isSubtypeIncluding(c, d, hierarchy));
						
						assertEquals("temp$10", au.localOfReturn(aMethod).values().iterator().next().local().toString());
						
						Map<String, LocalInfo> localsOfInvokeParam = au.localsOfInvokeParam(aMethod);
						assertEquals(3, localsOfInvokeParam.size());
						assertTrue(localsOfInvokeParam.containsKey("arg1"));
						assertTrue(localsOfInvokeParam.containsKey("temp$0"));
						assertTrue(localsOfInvokeParam.containsKey("dummy"));
						
						assertEquals(5, numOfFieldInLeftStmt);
						
						Set<String> localsRightOfFieldStrs = au.localsRightOfField(aMethod).keySet();
						assertTrue(localsRightOfFieldStrs.contains("newB"));
						assertTrue(localsRightOfFieldStrs.contains("temp$3"));
						assertTrue(localsRightOfFieldStrs.contains("temp$7"));
						assertTrue(localsRightOfFieldStrs.contains("temp$8"));
						assertTrue(localsRightOfFieldStrs.contains("temp$9"));
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
