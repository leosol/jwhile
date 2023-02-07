package jwhile.antlr4.analysis.algorithms.impl;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.javatuples.Pair;
import org.neo4j.driver.types.Node;

import jwhile.antlr4.analysis.algorithms.Algorithm;
import jwhile.antlr4.analysis.algorithms.AnalysisInformation;
import jwhile.antlr4.analysis.algorithms.ConcernType;
import jwhile.antlr4.analysis.algorithms.FlowType;
import jwhile.antlr4.analysis.algorithms.MeetOperator;
import jwhile.antlr4.cfg.entities.Program;
import jwhile.antlr4.cfg.persistence.ProgramPersistenceStore;
import jwhile.antlr4.utils.NodeUtils;

public class VeryBusyExpressions extends Algorithm<AnalysisInformation<String>> {

	public VeryBusyExpressions(Program program, ProgramPersistenceStore store) {
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
		return MeetOperator.MUST;
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
	public Set<AnalysisInformation<String>> findSuperset() {
		Set<AnalysisInformation<String>> superset = new LinkedHashSet<AnalysisInformation<String>>();
		List<Pair<Node, Node>> allNonTrivials = this.getStore()
				.findNonTrivialExpressions(this.getProgram().getProgramId());
		for (Pair<Node, Node> pair : allNonTrivials) {
			Node trgt = pair.getValue1();
			superset.add(new AnalysisInformation<String>(trgt.get("text").asString()));
		}
		return superset;
	}

	@Override
	public Set<AnalysisInformation<String>> gen(Node n) {
		Set<AnalysisInformation<String>> res = new LinkedHashSet<AnalysisInformation<String>>();
		if (n.hasLabel("Condition")) {
			return res;
		}
		List<Node> nonTrivials = this.getStore().findNonTrivalExpressions(this.getProgram().getProgramId(),
				n.get("id").asLong());
		for (Node nt : nonTrivials) {
			res.add(new AnalysisInformation<String>(nt.get("text").asString()));
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
				List<Node> nonTrivialExps = this.getStore().findNonTrivalExpressionsWithIdentifier(
						this.getProgram().getProgramId(), n.get("id").asLong(), identifier);
				for (Node nt : nonTrivialExps) {
					res.add(new AnalysisInformation<String>(nt.get("text").asString()));
				}
			}
		}
		return res;
	}

}
