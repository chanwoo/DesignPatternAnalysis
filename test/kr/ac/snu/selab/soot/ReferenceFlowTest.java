package kr.ac.snu.selab.soot;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kr.ac.snu.selab.soot.analyzer.AbstractAnalyzer;
import kr.ac.snu.selab.soot.analyzer.AnalysisUtil;
import kr.ac.snu.selab.soot.analyzer.Creator;
import kr.ac.snu.selab.soot.analyzer.LocalInfo;
import kr.ac.snu.selab.soot.analyzer.MethodInfo;
import kr.ac.snu.selab.soot.analyzer.MethodInternalPath;
import kr.ac.snu.selab.soot.analyzer.Pair;
import kr.ac.snu.selab.soot.analyzer.RoleRepository;
import kr.ac.snu.selab.soot.core.AbstractProject;
import kr.ac.snu.selab.soot.core.ProjectManager;
import kr.ac.snu.selab.soot.graph.MetaInfo;
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

public class ReferenceFlowTest {

	private static Logger logger = Logger.getLogger(ReferenceFlowTest.class);

	private static final String PROJECTS_NAME = "StatePatternExample";
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

	static List<Pair<LocalInfo, LocalInfo>> internalEdges;
	static MethodInfo methodInfo;
	static Map<SootMethod, MethodInfo> methodInfoMap;
	static SootMethod inout;

	static ReferenceFlowGraph referenceFlowGraph;
	static Map<LocalInfoNode, List<Path<LocalInfoNode>>> referenceFlows;
	static Set<Path<MetaInfo>> abstractReferenceFlows;

	
	// for paramter number consistency test
	static Map<String, LocalInfo> localsOfMethodParam_methodForAnalyzeMethodParamToReturnTest;
	static Map<String, LocalInfo> localsOfMethodParam_methodForAnalyzeMethodParamToReturnTest2;
	static Map<String, LocalInfo> localsOfInvokeParam_main;
	
	// for interMethodConnectionTest
	static SootMethod main;
	
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
	public void abstractReferenceFlowsTest() {

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
			writer.startElement("abstractReferenceFlows");
			for (Path<MetaInfo> path : abstractReferenceFlows) {
				path.writeXML(writer);
			}
			writer.endElement();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		writer.close();
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

			i = classMap.get("State");
			
			// preparation for abstractReferenceFlowsTest
			Map<String, MetaInfo> metaInfoMap = au.metaInfoMap(classList);
			RoleRepository roles = new RoleRepository();
			abstractReferenceFlows = au.abstractReferenceFlows(i, classMap, hierarchy, cg, metaInfoMap, roles);

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
