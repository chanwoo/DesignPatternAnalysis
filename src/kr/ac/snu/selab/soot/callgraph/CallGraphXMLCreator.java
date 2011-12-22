package kr.ac.snu.selab.soot.callgraph;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import kr.ac.snu.selab.soot.analyzer.AbstractAnalyzer;
import kr.ac.snu.selab.soot.core.AbstractProject;
import kr.ac.snu.selab.soot.util.MyUtil;
import soot.Hierarchy;
import soot.SootClass;
import soot.SootMethod;

public class CallGraphXMLCreator extends AbstractAnalyzer {
	private String outputPath;
	private String callGraphTXTPath;

	public CallGraphXMLCreator(AbstractProject project) {
		super(project);
	}

	@Override
	protected void preAnalysis() {
		String fileName = CallGraphTXTCreator.CALL_GRAPH_TXT_FILE_NAME;
		callGraphTXTPath = MyUtil.getPath(project.getOutputDirectory(),
				fileName);

		File parent = project.getOutputDirectory();
		parent.mkdirs();
		outputPath = MyUtil.getPath(parent, project.getProjectName()
				+ "_callgraph.xml");
	}

	@Override
	protected void analyze(List<SootClass> classList, Hierarchy hierarchy) {
		HashMap<String, SootMethod> methodMap = new HashMap<String, SootMethod>();

		for (SootClass aClass : classList) {
			for (SootMethod aMethod : aClass.getMethods()) {
				methodMap.put(aMethod.toString(), aMethod);
			}
		}

		SimpleCallGraph cg = new SimpleCallGraph();
		cg = cg.load(callGraphTXTPath, methodMap);
		MyUtil.stringToFile(cg.toXML(), outputPath);
	}

}
