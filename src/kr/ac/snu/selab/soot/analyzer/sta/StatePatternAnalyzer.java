package kr.ac.snu.selab.soot.analyzer.sta;

import java.util.List;

import kr.ac.snu.selab.soot.analyzer.AbstractAnalyzer;
import kr.ac.snu.selab.soot.core.AbstractProject;
import soot.Hierarchy;
import soot.SootClass;

public class StatePatternAnalyzer extends AbstractAnalyzer {

	public StatePatternAnalyzer(AbstractProject project) {
		super(project);
	}

	@Override
	protected void analyze(List<SootClass> classList, Hierarchy hierarchy) {
		StatePatternAnalysis analysis = new StatePatternAnalysis(classList,
				hierarchy);
		analysis.writeAnalysisResultOverAllAbstractTypes(outputDirectory);
	}

}
