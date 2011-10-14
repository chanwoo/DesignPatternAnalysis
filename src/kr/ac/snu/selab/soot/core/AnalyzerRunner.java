package kr.ac.snu.selab.soot.core;

import kr.ac.snu.selab.soot.analyzer.CodeAnalyzer;
import kr.ac.snu.selab.soot.analyzer.RoleAnalyzer;
import kr.ac.snu.selab.soot.callgraph.CallGraphTXTCreator;
import kr.ac.snu.selab.soot.callgraph.CallGraphXMLCreator;
import soot.BodyTransformer;
import soot.PackManager;
import soot.Transform;

public class AnalyzerRunner {

	public static void run(AbstractProject project, String analyzer)
			throws InvalidAnalyzerException {

		BodyTransformer bodyTransformer = null;

		if (analyzer.equalsIgnoreCase("r") || analyzer.equalsIgnoreCase("Role")
				|| analyzer.equalsIgnoreCase("RoleAnalyzer")) {
			bodyTransformer = new RoleAnalyzer(project);
		} else if (analyzer.equalsIgnoreCase("ct")
				|| analyzer.equalsIgnoreCase("CallGraphTXTCreater")) {
			bodyTransformer = new CallGraphTXTCreator(project);
		} else if (analyzer.equalsIgnoreCase("cx")
				|| analyzer.equalsIgnoreCase("CallGraphXMLCreater")) {
			bodyTransformer = new CallGraphXMLCreator(project);
		} else if (analyzer.equalsIgnoreCase("c")
				|| analyzer.equalsIgnoreCase("Code")
				|| analyzer.equalsIgnoreCase("CodeAnalyzer")) {
			bodyTransformer = new CodeAnalyzer(project);
		} else {
			throw new InvalidAnalyzerException("Can not find proper analyzer: "
					+ analyzer);
		}

		PackManager.v().getPack("jtp")
				.add(new Transform("jtp.Experiment", bodyTransformer));

		final String[] arguments = { "-cp", project.getClassPath(), "-f", "J",
				"-d", project.getJimpleDirectory(), "--process-dir",
				project.getSourceDirectory() };
		soot.Main.main(arguments);
	}
}
