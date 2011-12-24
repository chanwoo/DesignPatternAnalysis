package kr.ac.snu.selab.soot;

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
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JReturnVoidStmt;
import soot.jimple.toolkits.callgraph.CallGraph;

public class SootAPITest {

	private static Logger logger = Logger.getLogger(SootAPITest.class);

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

			logger.debug("cg size: " + cg.size());
			logger.debug("class number: " + classList.size());

			for (SootClass aClass : classList) {
				logger.debug("###############################");
				logger.debug("<Class> => " + aClass.toString());
				logger.debug("###############################");
				for (SootMethod aMethod : aClass.getMethods()) {
					logger.debug("<Method> => " + aMethod.getSignature());
					logger.debug("SootMethod.getName() => " + aMethod.getName());
					logger.debug("**************************");
					
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
									logger.debug("ValueBox JInvokeStmt.getInvokeExprBox() => " + stmt.getInvokeExprBox());
									logger.debug("Type InvokeExpr.getType() => " + stmt.getInvokeExpr().getType());
									logger.debug("SootMethod InvokeExpr.getMethod() => " + stmt.getInvokeExpr().getMethod());
									logger.debug("SootMethodRef InvokeExpr.getMethodRef() => " + stmt.getInvokeExpr().getMethodRef());
									
									logger.debug("List<ValueBox> InvokeExpr.getUseBoxes() =>");
									List<ValueBox> useBoxes = stmt.getInvokeExpr().getUseBoxes();
									for (ValueBox box : useBoxes) {
										logger.debug(box.getValue().toString());
									}
									
									List<Local> args = new ArrayList<Local>();
									args = stmt.getInvokeExpr().getArgs();
									
									logger.debug("List InvokeExpr.getArgs() => ");
									for (Local arg : args) {
										logger.debug(arg.toString());
									}
								}
							}
							
							if (aUnit instanceof JAssignStmt) {
								logger.debug("<JAssignStmt>");
								logger.debug(aUnit);
								JAssignStmt stmt = (JAssignStmt)aUnit;
								if (stmt.containsFieldRef()) {
									logger.debug("JAssignStmt.getFieldRef() => " + stmt.getFieldRef().toString());
									logger.debug("JAssignStmt.getFieldRefBox() => " + stmt.getFieldRefBox().toString());
									logger.debug("JAssignStmt.getFieldRef().getField() => " + stmt.getFieldRef().getField().toString());
								}
								logger.debug("JAssingStmt.getRightOp() => " + stmt.getRightOp().toString());
								logger.debug("JAssignStmt.getRightOp().getType() => " + stmt.getRightOp().getType().toString());
								logger.debug("JAssingStmt.getRightOp().getClass() => " + stmt.getRightOp().getClass().toString());
								
								logger.debug("--------RightOp--------");
								List<ValueBox> valueboxes = stmt.getRightOp().getUseBoxes();
								List<Value> values = new ArrayList<Value>();
								for (ValueBox valuebox : valueboxes) {
									logger.debug("valuebox => " + valuebox);
									logger.debug("valuebox.getClass() => " + valuebox.getClass());
									values.add(valuebox.getValue());
								}
								
								for (Value value : values) {
									logger.debug("value => " + value);
									logger.debug("value.getClass() => " + value.getClass());
								}
								
								if (stmt.containsInvokeExpr()) {
									logger.debug("List<ValueBox> InvokeExpr.getUseBoxes() =>");
									List<ValueBox> useBoxes = stmt.getInvokeExpr().getUseBoxes();
									for (ValueBox box : useBoxes) {
										logger.debug(box.getValue().toString());
									}
								}
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
