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

public class CallGraphXMLCreater extends BodyTransformer {
	private static boolean touch = false;
	private String outputPath;
	private String callGraphPath;

	public CallGraphXMLCreater(AbstractProject project) {
		String fileName = CallGraphTXTCreater.CALL_GRAPH_FILE_NAME;
		this.callGraphPath = MyUtil.getPath(project.getOutputDirectory(),
				fileName);

		File parent = new File(project.getOutputDirectory(), "call_graph_xml");
		parent.mkdirs();
		this.outputPath = MyUtil.getPath(parent, "call_graph.xml");
	}

	@Override
	protected void internalTransform(Body b, String phaseName, Map options) {
		// TODO Auto-generated method stub
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

		MyCallGraph cg = new MyCallGraph();
		cg = cg.load(callGraphPath, methodMap);
		MyUtil.stringToFile(cg.toXML(), outputPath);
	}

}
