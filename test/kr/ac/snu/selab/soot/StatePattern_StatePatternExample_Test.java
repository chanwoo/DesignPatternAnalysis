package kr.ac.snu.selab.soot;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.snu.selab.soot.analyzer.AbstractAnalyzer;
import kr.ac.snu.selab.soot.analyzer.AnalysisUtil;
import kr.ac.snu.selab.soot.analyzer.PatternAnalysis;
import kr.ac.snu.selab.soot.analyzer.PatternAnalysisResult;
import kr.ac.snu.selab.soot.analyzer.StatePattern;
import kr.ac.snu.selab.soot.core.AbstractProject;
import kr.ac.snu.selab.soot.core.ProjectManager;
import kr.ac.snu.selab.soot.util.XMLWriter;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import soot.Hierarchy;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.Transform;
import soot.jimple.toolkits.callgraph.CallGraph;

public class StatePattern_StatePatternExample_Test {

	private static Logger logger = Logger.getLogger(StatePattern_StatePatternExample_Test.class);

	private static final String PROJECTS_NAME = "jhd6";
	private static final String PROJECTS_FILE_NAME = "projects.xml";

	static AnalysisUtil au;
	static Map<String, SootClass> classMap;
	static Hierarchy hierarchy;
	static CallGraph cg;

	static PatternAnalysisResult result;
	
	static AbstractProject project;
	
	static boolean touch = false;

	@Before
	public void prepare() throws Throwable {
		if (touch)
			return;

		touch = true;

		au = new AnalysisUtil();

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

		project = projects.getProject(PROJECTS_NAME);
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
	public void writePatternAnalysisResult() {
		File outputDir = new File(project.getOutputDirectory().getAbsolutePath());
		File output = new File(outputDir, "StatePatternAnalysisResult.xml");
		
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
		
		result.writeXML(writer);
		
		writer.close();
	}
	
	@Test
	public void statePatternTest() {
		assertTrue(result.patternExistence());
		
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

			PatternAnalysis analysis = new StatePattern();
			result = au.analyzePattern(analysis, classMap, aHierarchy, cg);
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
