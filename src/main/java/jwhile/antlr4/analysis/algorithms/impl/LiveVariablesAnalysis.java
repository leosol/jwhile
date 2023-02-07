package jwhile.antlr4.analysis.algorithms.impl;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.neo4j.driver.types.Node;

import jwhile.antlr4.analysis.algorithms.Algorithm;
import jwhile.antlr4.analysis.algorithms.AnalysisInformation;
import jwhile.antlr4.analysis.algorithms.ConcernType;
import jwhile.antlr4.analysis.algorithms.FlowType;
import jwhile.antlr4.analysis.algorithms.MeetOperator;
import jwhile.antlr4.cfg.entities.Program;
import jwhile.antlr4.cfg.persistence.ProgramPersistenceStore;
import jwhile.antlr4.utils.NodeUtils;

public class LiveVariablesAnalysis extends Algorithm<AnalysisInformation<String>> {

	public LiveVariablesAnalysis(Program program, ProgramPersistenceStore store) {
		super(program, store);
	}

	@Override
	public FlowType getFlowType() {
		return FlowType.BACKWARD;
	}

	@Override
	public ConcernType getConcernType() {
		return ConcernType.CONCERNS_EXIT_CONDITIONS;
	}

	@Override
	public MeetOperator getMeetOperator() {
		return MeetOperator.MAY;
	}

	@Override
	public List<Node> getExtremeEdges() {
		List<Node> extremes = new LinkedList<Node>();
		extremes.add(this.getStore().findProgramEnd(this.getProgram().getProgramId()));
		return extremes;
	}

	@Override
	public Set<AnalysisInformation<String>> getInitSet() {
		return new TreeSet<AnalysisInformation<String>>();
	}

	@Override
	public Set<AnalysisInformation<String>> gen(Node n) {
		Set<AnalysisInformation<String>> res = new LinkedHashSet<AnalysisInformation<String>>();
		List<Node> nodes = this.getStore().findExpressionsIdentifiers(this.getProgram().getProgramId(),
				n.get("id").asLong());
		for (Node node : nodes) {
			res.add(new AnalysisInformation<String>(node.get("text").asString()));
		}
		return res;
	}

	@Override
	public Set<AnalysisInformation<String>> kill(Node n) {
		Set<AnalysisInformation<String>> res = new LinkedHashSet<AnalysisInformation<String>>();
		if (n.hasLabel("AssignmentStmt")) {
			List<String> identifiers = NodeUtils.nodesToTextList(
					this.getStore().findAssignmentsIdentifiers(this.getProgram().getProgramId(), n.get("id").asLong()));
			for (String identifier : identifiers) {
				res.add(new AnalysisInformation<String>(identifier));
			}
		}
		return res;
	}

	@Override
	public Set<AnalysisInformation<String>> findSuperset() {
		return new HashSet<AnalysisInformation<String>>();
	}

}
