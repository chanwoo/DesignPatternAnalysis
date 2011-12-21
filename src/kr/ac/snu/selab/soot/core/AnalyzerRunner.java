package kr.ac.snu.selab.soot.core;

import kr.ac.snu.selab.soot.analyzer.AbstractAnalyzer;
import kr.ac.snu.selab.soot.analyzer.code.CodeAnalyzer;
import kr.ac.snu.selab.soot.analyzer.decNcor.DecNCorAnalyzer;
import kr.ac.snu.selab.soot.analyzer.pfc.PathFromCallerAnalyzer;
import kr.ac.snu.selab.soot.analyzer.role.RoleAnalyzer;
import kr.ac.snu.selab.soot.analyzer.sta.StatePatternAnalyzer;
import kr.ac.snu.selab.soot.callgraph.CallGraphTXTCreator;
import kr.ac.snu.selab.soot.callgraph.CallGraphXMLCreator;
import soot.PackManager;
import soot.Transform;

public class AnalyzerRunner {

	public static void run(AbstractProject project, String analyzerName,
			boolean noJimpleOutput) throws InvalidAnalyzerException {

		AbstractAnalyzer analyzer = null;

		if (analyzerName.equalsIgnoreCase("r")
				|| analyzerName.equalsIgnoreCase("Role")
				|| analyzerName.equalsIgnoreCase("RoleAnalyzer")) {
			analyzer = new RoleAnalyzer(project);
		} else if (analyzerName.equalsIgnoreCase("ct")
				|| analyzerName.equalsIgnoreCase("CallGraphTXTCreater")) {
			analyzer = new CallGraphTXTCreator(project);
		} else if (analyzerName.equalsIgnoreCase("cx")
				|| analyzerName.equalsIgnoreCase("CallGraphXMLCreater")) {
			analyzer = new CallGraphXMLCreator(project);
		} else if (analyzerName.equalsIgnoreCase("c")
				|| analyzerName.equalsIgnoreCase("Code")
				|| analyzerName.equalsIgnoreCase("CodeAnalyzer")) {
			analyzer = new CodeAnalyzer(project);
		} else if (analyzerName.equalsIgnoreCase("pfc")
				|| analyzerName.equalsIgnoreCase("PathFromCaller")) {
			analyzer = new PathFromCallerAnalyzer(project);
		} else if (analyzerName.equalsIgnoreCase("sta")
				|| analyzerName.equalsIgnoreCase("State")) {
			analyzer = new StatePatternAnalyzer(project);
		} else if (analyzerName.equalsIgnoreCase("decncor")
				|| analyzerName.equalsIgnoreCase("DecoratorAndCor")) {
			analyzer = new DecNCorAnalyzer(project);
		} else {
			throw new InvalidAnalyzerException("Can not find proper analyzer: "
					+ analyzerName);
		}

		PackManager.v().getPack("jtp")
				.add(new Transform("jtp.Experiment", analyzer));

		String includePackage = project.getIncludePackage();
		if (includePackage == null || includePackage.equals("")) {
			final String[] arguments = { "-cp", project.getClassPath(), "-f",
					(noJimpleOutput) ? "n" : "J", "-d",
					project.getJimpleDirectory(), "--process-dir",
					project.getSourceDirectory(), "-w", "-p", "cg.spark",
					"verbose:true,on-fly-cg:true", "-p", "jb", "use-original-names:true"};
			soot.Main.main(arguments);
		} else {
			final String[] arguments = { "-cp", project.getClassPath(), "-f",
					(noJimpleOutput) ? "n" : "J", "-d",
					project.getJimpleDirectory(), "--process-dir",
					project.getSourceDirectory(), "-i", includePackage };
			soot.Main.main(arguments);
		}
	}
}
