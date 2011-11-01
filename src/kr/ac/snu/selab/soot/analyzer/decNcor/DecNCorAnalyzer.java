package kr.ac.snu.selab.soot.analyzer.decNcor;

import java.util.List;

import kr.ac.snu.selab.soot.analyzer.AbstractAnalyzer;
import kr.ac.snu.selab.soot.core.AbstractProject;
import soot.Hierarchy;
import soot.SootClass;

public class DecNCorAnalyzer extends AbstractAnalyzer {
	
	public DecNCorAnalyzer(AbstractProject project) {
		super(project);
	}

	@Override
	protected void analyze(List<SootClass> classList, Hierarchy hierarchy) {
		DecNCorAnalysis analysis = new DecNCorAnalysis(classList,
				hierarchy);
		analysis.writeAnalysisResultOverAllAbstractTypes(outputDirectory);
	}

}
