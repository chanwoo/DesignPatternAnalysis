package kr.ac.snu.selab.soot.callgraph;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.snu.selab.soot.core.AbstractProject;
import kr.ac.snu.selab.soot.util.MyUtil;
import soot.Body;
import soot.BodyTransformer;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

public class CallGraphXMLCreator extends BodyTransformer {
	private static boolean touch = false;
	private String outputPath;
	private String callGraphTXTPath;

	public CallGraphXMLCreator(AbstractProject project) {
		String fileName = CallGraphTXTCreator.CALL_GRAPH_TXT_FILE_NAME;
		this.callGraphTXTPath = MyUtil.getPath(project.getOutputDirectory(),
				fileName);

		File parent = project.getOutputDirectory();
		parent.mkdirs();
		this.outputPath = MyUtil.getPath(parent, project.getProjectName() + "_callgraph.xml");
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void internalTransform(Body b, String phaseName, Map options) {
		if (touch)
			return;
		touch = true;

		List<SootClass> classList = new ArrayList<SootClass>();
		classList.addAll(Scene.v().getApplicationClasses());
		HashMap<String, SootMethod> methodMap = new HashMap<String, SootMethod>();

		for (SootClass aClass : classList) {
			for (SootMethod aMethod : aClass.getMethods()) {
				methodMap.put(aMethod.toString(), aMethod);
			}
		}

		CallGraph cg = new CallGraph();
		cg = cg.load(callGraphTXTPath, methodMap);
		MyUtil.stringToFile(cg.toXML(), outputPath);
	}

}
