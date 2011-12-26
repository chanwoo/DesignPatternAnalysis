package kr.ac.snu.selab.soot;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import kr.ac.snu.selab.soot.analyzer.AbstractAnalyzer;
import kr.ac.snu.selab.soot.core.AbstractProject;
import kr.ac.snu.selab.soot.core.ProjectManager;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import soot.Hierarchy;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

public class CallgraphTest {

	private static Logger logger = Logger.getLogger(CallgraphTest.class);

	private static final String PROJECTS_NAME = "SparkTest";
	private static final String PROJECTS_FILE_NAME = "projects.xml";

	int targetClassCount = 0;

	@Before
	public void prepare() throws Throwable {
		targetClassCount = 0;

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
	public void classNumber() {
		//assertEquals(8, targetClassCount);
		logger.debug("hello");
	}

	private class TestRunner extends AbstractAnalyzer {
		public TestRunner(AbstractProject project) {
			super(project);
		}

		@Override
		protected void analyze(List<SootClass> classList, Hierarchy hierarchy) {
			CallGraph cg = Scene.v().getCallGraph();
			assertNotNull("Target classes not found", classList);
			// assertEquals(7, classList.size());
			targetClassCount = classList.size();

			
			logger.debug("###############################");
			logger.debug("Call Graph after applying Spark");
			logger.debug("###############################");
			

			logger.debug("cg size: " + cg.size());
			logger.debug("class number: " + classList.size());

			for (SootClass aClass : classList) {
				logger.debug("get in the loop");
				for (SootMethod aMethod : aClass.getMethods()) {
					logger.debug("**************************");

					logger.debug("Callgraph of Method: " + aMethod.getSignature());
					
					Iterator<Edge> edgesInto = cg.edgesInto(aMethod);
					while (edgesInto.hasNext()) {
						SootMethod srcMethod = edgesInto.next().src();
						logger.debug(srcMethod.getSignature() + "=====>");
					}
					
					Iterator<Edge> edgesOutOf = cg.edgesOutOf(aMethod);
					while (edgesOutOf.hasNext()) {
						SootMethod tgtMethod = edgesOutOf.next().tgt();
						logger.debug("=====>" + tgtMethod.getSignature());
					}

					logger.debug("**************************");
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
