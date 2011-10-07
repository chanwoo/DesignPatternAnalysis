package kr.ac.snu.selab.soot.analyzer;

import java.util.Map;

public class ReferenceFlowPathCollector extends MyGraphPathCollector {
	
	public ReferenceFlowPathCollector (MyNode aStartNode, MyGraph aGraph) {
		super(aStartNode, aGraph);
	}

	@Override
	protected boolean isGoal(MyNode aNode) {
		// TODO Auto-generated method stub
		return aNode.isCreator();
	}

}
