package kr.ac.snu.selab.soot.callgraph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.ac.snu.selab.soot.core.AbstractProject;
import kr.ac.snu.selab.soot.util.MyUtil;
import soot.Body;
import soot.BodyTransformer;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.internal.JInvokeStmt;

public class CallGraphTXTCreator extends BodyTransformer {

	private static boolean touch = false;
	private String callGraphPath;
	public static final String CALL_GRAPH_FILE_NAME = "call_graph.txt";

	public CallGraphTXTCreator(AbstractProject project) {
		String fileName2 = CALL_GRAPH_FILE_NAME;
		this.callGraphPath = MyUtil.getPath(project.getOutputDirectory(),
				fileName2);
	}

	private List<Unit> getUnits(SootMethod aMethod) {
		List<Unit> unitList = new ArrayList<Unit>();
		if (aMethod.hasActiveBody()) {
			Body body = aMethod.getActiveBody();
			unitList.addAll(body.getUnits());
		}
		return unitList;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void internalTransform(Body arg0, String arg1, Map arg2) {
		if (touch) {
			return;
		}
		touch = true;
		List<SootClass> classList = new ArrayList<SootClass>();
		classList.addAll(Scene.v().getApplicationClasses());

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
							writer.print(aMethod.toString());
							writer.print("\t");
							writer.println(jInvokeStatement.getInvokeExpr()
									.getMethod().toString());
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