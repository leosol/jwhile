package jwhile.antlr4.analysis.algorithms.impl;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.javatuples.Pair;
import org.neo4j.driver.types.Node;

import jwhile.antlr4.analysis.algorithms.Algorithm;
import jwhile.antlr4.analysis.algorithms.AnalysisInformation;
import jwhile.antlr4.analysis.algorithms.ConcernType;
import jwhile.antlr4.analysis.algorithms.FlowType;
import jwhile.antlr4.analysis.algorithms.MeetOperator;
import jwhile.antlr4.cfg.entities.Label;
import jwhile.antlr4.cfg.entities.Program;
import jwhile.antlr4.cfg.persistence.ProgramPersistenceStore;
import jwhile.antlr4.utils.NodeUtils;

public class ReachingDefinitions extends Algorithm<AnalysisInformation<Pair<Label, String>>> {

	//There should be an adjustment for where this is referenced to work correctly
	private boolean solvelater = false;
	public ReachingDefinitions(Program program, ProgramPersistenceStore store) {
		super(program, store);
	}

	@Override
	public FlowType getFlowType() {
		return FlowType.FORWARD;
	}

	@Override
	public ConcernType getConcernType() {
		return ConcernType.CONCERNS_ENTRY_CONDITIONS;
	}

	@Override
	public MeetOperator getMeetOperator() {
		return MeetOperator.MAY;
	}

	@Override
	public List<Node> getExtremeEdges() {
		List<Node> extremes = new LinkedList<Node>();
		extremes.add(this.getStore().findProgramStart(this.getProgram().getProgramId()));
		return extremes;
	}

	@Override
	public Set<AnalysisInformation<Pair<Label, String>>> getInitSet() {
		Set<AnalysisInformation<Pair<Label, String>>> res = new LinkedHashSet<AnalysisInformation<Pair<Label, String>>>();
		//TODO think about this later
		if (solvelater) {
			List<Pair<Node, Node>> assignments = getStore().findAssignments(this.getProgram().getProgramId());
			Set<String> knownVariables = new HashSet<String>();
			for (Pair<Node, Node> pair : assignments) {
				Node identifier = pair.getValue1();
				Label label = new Label(-1, "?");
				String identifierStr = identifier.get("text").asString();
				if (!knownVariables.contains(identifierStr)) {
					res.add(new AnalysisInformation<Pair<Label, String>>(
							new Pair<Label, String>(label, identifierStr)));
					knownVariables.add(identifierStr);
				}
			}
		}
		return res;
	}

	private Label getLabel(Node n) {
		Label label = new Label(n.get("intLabel").asLong(), n.get("contextLabel").asString());
		return label;
	}

	@Override
	public Set<AnalysisInformation<Pair<Label, String>>> gen(Node n) {
		Set<AnalysisInformation<Pair<Label, String>>> res = new LinkedHashSet<AnalysisInformation<Pair<Label, String>>>();
		if (n.hasLabel("AssignmentStmt")) {
			List<String> identifiers = NodeUtils.nodesToTextList(
					this.getStore().findAssignmentsIdentifiers(this.getProgram().getProgramId(), n.get("id").asLong()));
			for (String identifier : identifiers) {
				Label label = getLabel(n);
				Pair<Label, String> pair = new Pair<Label, String>(label, identifier);
				res.add(new AnalysisInformation<Pair<Label, String>>(pair));

			}
		}
		return res;
	}

	@Override
	public Set<AnalysisInformation<Pair<Label, String>>> kill(Node n) {
		Set<AnalysisInformation<Pair<Label, String>>> res = new LinkedHashSet<AnalysisInformation<Pair<Label, String>>>();
		if (n.hasLabel("AssignmentStmt")) {
			List<String> identifiers = NodeUtils.nodesToTextList(
					this.getStore().findAssignmentsIdentifiers(this.getProgram().getProgramId(), n.get("id").asLong()));
			List<Pair<Node, Node>> programAssignments = this.getStore()
					.findAssignments(this.getProgram().getProgramId());
			for (Pair<Node, Node> pair : programAssignments) {
				Node assignment = pair.getValue0();
				Node identifier = pair.getValue1();
				if (identifiers.contains(identifier.get("text").asString())) {
					Label other = getLabel(assignment);
					Pair<Label, String> resPair = new Pair<Label, String>(other, identifier.get("text").asString());
					res.add(new AnalysisInformation<Pair<Label, String>>(resPair));
				}
			}
		}
		return res;
	}

	@Override
	public Set<AnalysisInformation<Pair<Label, String>>> findSuperset() {
		return new HashSet<AnalysisInformation<Pair<Label,String>>>();
	}

}
