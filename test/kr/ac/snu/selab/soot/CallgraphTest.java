package kr.ac.snu.selab.soot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kr.ac.snu.selab.soot.analyzer.AbstractAnalyzer;
import kr.ac.snu.selab.soot.core.AbstractProject;
import kr.ac.snu.selab.soot.core.ProjectManager;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import soot.Body;
import soot.Hierarchy;
import soot.Local;
import soot.PackManager;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JReturnVoidStmt;
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
		assertEquals(8, targetClassCount);
		logger.debug("hello");
	}

	private class TestRunner extends AbstractAnalyzer {
		public TestRunner(AbstractProject project) {
			super(project);
		}

		@Override
		protected void analyze(List<SootClass> classList, Hierarchy hierarchy, CallGraph cg) {
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
					
					logger.debug("--------------------------");
					
					if (aMethod.hasActiveBody()) {
						Body body = aMethod.getActiveBody();
						Iterator<Local> localIter = body.getLocals().iterator();
						
						logger.debug("----Locals----");
						while (localIter.hasNext()) {
							logger.debug("local: " + localIter.next().toString());
						}
						
						logger.debug("----Params----");
						int numOfParam = aMethod.getParameterCount();
						for (int i = 0; i < numOfParam; i++) {
							logger.debug(body.getParameterLocal(i).toString());
						}
						
						logger.debug("----Units----");
						
						List<Unit> units = new ArrayList<Unit>();
						units.addAll(body.getUnits());
						
						for (Unit aUnit : units) {
							if (aUnit instanceof JReturnStmt) {
								logger.debug("<JReturnStmt>");
								logger.debug(aUnit);
								logger.debug("Value JReturnStmt.getOp() => " + ((JReturnStmt)aUnit).getOp());
							}
							
							if (aUnit instanceof JReturnVoidStmt) {
								logger.debug("<JReturnVoidStmt>");
								logger.debug(aUnit);
							}
							
							if (aUnit instanceof JInvokeStmt) {
								logger.debug("<JInvokeStmt>");
								logger.debug(aUnit);
								JInvokeStmt stmt = (JInvokeStmt)aUnit;
								if (stmt.containsInvokeExpr()) {
									logger.debug("InvokeExpr JInvokeStmt.getInvokeExpr() =>" + stmt.getInvokeExpr());
									logger.debug("ValueBox JInvokeStmt.getInvokeExprBox() =>" + stmt.getInvokeExprBox());
									logger.debug("Type InvokeExpr.getType() =>" + stmt.getInvokeExpr().getType());
									logger.debug("SootMethod InvokeExpr.getMethod() =>" + stmt.getInvokeExpr().getMethod());
									logger.debug("SootMethodRef InvokeExpr.getMethodRef() =>" + stmt.getInvokeExpr().getMethodRef());
									
									List<Local> args = new ArrayList<Local>();
									args = stmt.getInvokeExpr().getArgs();
									
									logger.debug("List InvokeExpr.getArgs() =>");
									for (Local arg : args) {
										logger.debug(arg.toString());
									}
								}
							}
							
							if (aUnit instanceof JAssignStmt) {
								logger.debug("<JAssignStmt>");
								logger.debug(aUnit);
								JAssignStmt stmt = (JAssignStmt)aUnit;
								
								
							}
						}
						
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
