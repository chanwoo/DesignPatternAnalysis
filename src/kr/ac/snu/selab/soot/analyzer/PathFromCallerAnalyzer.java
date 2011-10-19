package kr.ac.snu.selab.soot.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.snu.selab.soot.core.AbstractProject;
import kr.ac.snu.selab.soot.util.MyUtil;
import soot.Body;
import soot.BodyTransformer;
import soot.Hierarchy;
import soot.Scene;
import soot.SootClass;

public class PathFromCallerAnalyzer extends BodyTransformer {
	private static boolean touch = false;
	private String outputDirectory;

	public PathFromCallerAnalyzer(AbstractProject project) {
		outputDirectory = project.getOutputDirectory().getAbsolutePath();
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void internalTransform(Body b, String phaseName, Map options) {
		if (touch)
			return;

		touch = true;

		Hierarchy hierarchy = Scene.v().getActiveHierarchy();
		List<SootClass> classList = new ArrayList<SootClass>();
		classList.addAll(Scene.v().getApplicationClasses());
		PathFromCallerAnalysis analysis = new PathFromCallerAnalysis(classList, hierarchy);

		Map<String, Integer> analysisFileNameMap = new HashMap<String, Integer>();

		for (AnalysisResult anAnalysisResult : analysis
				.analyzeOverAllAbstractTypes()) {
			String fileName = "PathFromCallerAnalysis_" + anAnalysisResult.getAbstractTypeName();
			if (!analysisFileNameMap.containsKey(fileName)) {
				analysisFileNameMap.put(fileName, 0);
			} else {
				int number = analysisFileNameMap.get(fileName) + 1;
				fileName = fileName + String.format("%d", number);
			}

			String outputPath = MyUtil.getPath(outputDirectory, fileName
					+ ".xml");
			MyUtil.stringToFile(anAnalysisResult.toXML(), outputPath);
		}
	}

}
