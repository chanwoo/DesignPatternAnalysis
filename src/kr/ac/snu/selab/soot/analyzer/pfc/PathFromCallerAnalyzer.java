package kr.ac.snu.selab.soot.analyzer.pfc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.ac.snu.selab.soot.analyzer.AbstractAnalyzer;
import kr.ac.snu.selab.soot.analyzer.AnalysisResult;
import kr.ac.snu.selab.soot.core.AbstractProject;
import kr.ac.snu.selab.soot.util.MyUtil;
import kr.ac.snu.selab.soot.util.XMLWriter;
import soot.Hierarchy;
import soot.SootClass;

public class PathFromCallerAnalyzer extends AbstractAnalyzer {
	public PathFromCallerAnalyzer(AbstractProject project) {
		super(project);
	}

	@Override
	protected void analyze(List<SootClass> classList, Hierarchy hierarchy) {
		PathFromCallerAnalysis analysis = new PathFromCallerAnalysis(classList,
				hierarchy, project.isUseSimpleCallGraph());

		Map<String, Integer> analysisFileNameMap = new HashMap<String, Integer>();

		for (AnalysisResult anAnalysisResult : analysis
				.analyzeOverAllAbstractTypes()) {
			String fileName = "PathFromCallerAnalysis_"
					+ anAnalysisResult.getAbstractTypeName();
			if (!analysisFileNameMap.containsKey(fileName)) {
				analysisFileNameMap.put(fileName, 0);
			} else {
				int number = analysisFileNameMap.get(fileName) + 1;
				fileName = fileName + String.format("%d", number);
			}

			String outputPath = MyUtil.getPath(outputDirectory, fileName
					+ ".xml");
			XMLWriter writer = new XMLWriter(outputPath);
			anAnalysisResult.writeXML(writer);
			writer.close();
		}
	}

}
