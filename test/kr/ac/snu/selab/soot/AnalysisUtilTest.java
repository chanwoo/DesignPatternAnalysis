package kr.ac.snu.selab.soot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.snu.selab.soot.analyzer.AbstractAnalyzer;
import kr.ac.snu.selab.soot.analyzer.AnalysisUtil;
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
import soot.SootField;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.toolkits.callgraph.CallGraph;

public class AnalysisUtilTest {

	private static Logger logger = Logger.getLogger(AnalysisUtilTest.class);

	private static final String PROJECTS_NAME = "SparkTest";
	private static final String PROJECTS_FILE_NAME = "projects.xml";

	List<Unit> units;
	Map<String, Local> paramLocals;
	Map<Local, SootField> fieldLocals;
	Map<String, Local> locals;
	int numOfFieldInRightStmt;
	int numOfInvokeInRightStmt;
	Map<Local, SootMethod> methodLocals;
	

	@Before
	public void prepare() throws Throwable {
		units = new ArrayList<Unit>();
		paramLocals = new HashMap<String, Local>();
		fieldLocals = new HashMap<Local, SootField>();
		locals = new HashMap<String, Local>();
		numOfFieldInRightStmt = 0;
		numOfInvokeInRightStmt = 0;
		methodLocals = new HashMap<Local, SootMethod>();

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
	public void Test() {
		assertEquals(27, units.size());
		assertEquals(17, locals.size());
		assertEquals(2, paramLocals.size());
		assertEquals(5, numOfFieldInRightStmt);
		assertEquals(5, fieldLocals.size());
		
		List<String> fieldLocalStrs = new ArrayList<String>();
		for (Local local : fieldLocals.keySet()) {
			fieldLocalStrs.add(local.toString());
		}
		
		assertTrue(fieldLocalStrs.contains("temp$0"));
		assertTrue(fieldLocalStrs.contains("temp$3"));
		assertTrue(fieldLocalStrs.contains("temp$7"));
		assertTrue(fieldLocalStrs.contains("temp$8"));
		assertTrue(fieldLocalStrs.contains("temp$10"));
		
		assertEquals(5, numOfInvokeInRightStmt);
		
		assertEquals(5, methodLocals.size());
		
		List<String> methodLocalStrs = new ArrayList<String>();
		for (Local local : methodLocals.keySet()) {
			methodLocalStrs.add(local.toString());
		}
		
		// temp$2, 4, 5, 6, 9
		assertTrue(methodLocalStrs.contains("temp$2"));
		assertTrue(methodLocalStrs.contains("temp$4"));
		assertTrue(methodLocalStrs.contains("temp$5"));
		assertTrue(methodLocalStrs.contains("temp$6"));
		assertTrue(methodLocalStrs.contains("temp$9"));
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

			for (SootClass aClass : classList) {
				for (SootMethod aMethod : aClass.getMethods()) {
					if (aMethod.getName().equals("inout")) {
						
						units = au.units(aMethod);
						
						locals = au.locals(aMethod);
					
						paramLocals = au.paramLocals(aMethod);
						
						// temp$0, temp$3, temp$7, temp$8, temp$10
						fieldLocals = au.fieldLocals(aMethod);
						
						for (Unit unit : units) {
							if (au.isFieldInRightStmt(unit)) {
								numOfFieldInRightStmt++;
							}
						}
						
						for (Unit unit : units) {
							if (au.isInvokeInRightStmt(unit)) {
								numOfInvokeInRightStmt++;
							}
						}
						
						methodLocals = au.methodLocals(aMethod);
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
