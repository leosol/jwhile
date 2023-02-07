package jwhile.antlr4.analysis;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.javatuples.Pair;
import org.neo4j.driver.types.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jwhile.antlr4.analysis.algorithms.Algorithm;
import jwhile.antlr4.analysis.algorithms.AnalysisInformation;
import jwhile.antlr4.analysis.algorithms.ConcernType;
import jwhile.antlr4.analysis.algorithms.FlowType;
import jwhile.antlr4.analysis.algorithms.MeetOperator;
import jwhile.antlr4.cfg.persistence.ProgramPersistenceStore;
import jwhile.antlr4.interpreter.JWhileVisitor;

public class Framework<T extends AnalysisInformation<?>> {

	private static Logger logger = LoggerFactory.getLogger(JWhileVisitor.class);
	private String programId;
	private Algorithm<T> algorithm;
	private ProgramPersistenceStore store;
	private AnalysisTable<T> table = new AnalysisTable<T>();

	public Framework(String programId, Algorithm<T> algorithm, ProgramPersistenceStore store) {
		this.programId = programId;
		this.algorithm = algorithm;
		this.store = store;
	}

	public void analyzeProgram() {
		initGenKill();
		table.printTable();
		List<Node> nodes = store.findControlFlowNodes(this.programId);
		List<Pair<Node, Node>> cfgForAnalysis = getFlowForAlgorithm();
		do {
			table.checkPoint();
			for (Node node : nodes) {
				
				Set<T> focusOfConcern = getFocusOfConcern(node);
				Set<T> contraFocus = getContraFocusOfConcern(node);
				logger.debug("Current node: "+ node.get("text")+" "+ node.labels()+" focus "+focusOfConcern+" contraFocus "+contraFocus);

				if (isInExtremeEdges(node)) {
					focusOfConcern.clear();
					focusOfConcern.addAll(algorithm.getInitSet());
					logger.debug("Current node in START|END: "+ node.get("text")+" "+ node.labels()+" focus "+focusOfConcern+" contraFocus "+contraFocus);
				} else {
					List<Pair<Node, Node>> nodesOfInterest = filterFlow(cfgForAnalysis, node);
					for (Pair<Node, Node> pair : nodesOfInterest) {
						Set<T> incomingContrafocus = getContraFocusOfConcern(pair.getValue0());
						logger.debug("Referenced by: "+ pair.getValue0().get("text")+" "+ pair.getValue0().labels()+" incomingContrafocus "+incomingContrafocus);
						doMeetOperation(focusOfConcern, incomingContrafocus);
						logger.debug("After MEET Operator: "+ node.get("text")+" "+ node.labels()+" focus "+focusOfConcern);
					}
				}
				logger.debug("Before gen|kill: "+ node.get("text")+" "+ node.labels()+" contraFocus "+contraFocus);
				doGenKillOperation(contraFocus, focusOfConcern, table.getKill(node), table.getGen(node));
				logger.debug("After gen|kill: "+ node.get("text")+" "+ node.labels()+" contraFocus "+contraFocus);
				table.printTable();
			}
			table.printTable();
		} while (!table.isStable());
		table.printTable();
	}
	
	private void initGenKill() {
		List<Node> nodes = store.findControlFlowNodes(this.programId);
		for (Node node : nodes) {
			AnalysisTableEntry<T> entry = table.getEntry(node);
			entry.setGen(this.algorithm.gen(node));
			entry.setKill(this.algorithm.kill(node));
//			Set<T> initEntry = new HashSet<T>();
//			initEntry.addAll(this.algorithm.findCachedSuperset());
//			entry.setEntry(initEntry);	
		}
//		List<Node> extremeEdges = this.algorithm.getExtremeEdges();
//		for (Node node : extremeEdges) {
//			AnalysisTableEntry<T> entry = table.getEntry(node);
//			Set<T> initEntry = new HashSet<T>();
//			Set<T> initExit = new HashSet<T>();
//			initExit.addAll(this.algorithm.findCachedSuperset());
//			initEntry.addAll(this.algorithm.findCachedSuperset());
//			entry.setExit(initExit);			
//			entry.setEntry(initEntry);
//		}
	}

	private void doGenKillOperation(Set<T> contraFocus, Set<T> focusOfConcern, Set<T> kill, Set<T> gen) {
		contraFocus.clear();
		contraFocus.addAll(focusOfConcern);
		contraFocus.removeAll(kill);
		contraFocus.addAll(gen);
	}

	private void doMeetOperation(Set<T> focusOfConcern, Set<T> incomingContrafocus) {
		focusOfConcern.addAll(this.algorithm.findCachedSuperset());
		if (algorithm.getMeetOperator().equals(MeetOperator.MAY)) {
			focusOfConcern.addAll(incomingContrafocus);
		} else {
			focusOfConcern.retainAll(incomingContrafocus);
		}
	}

	public Set<T> getFocusOfConcern(Node node) {
		Set<T> focusOfConcern = null;
		AnalysisTableEntry<T> nodeAnalysis = table.getEntry(node);
		if (this.algorithm.getConcernType().equals(ConcernType.CONCERNS_ENTRY_CONDITIONS)) {
			focusOfConcern = nodeAnalysis.getEntry();
		} else {
			focusOfConcern = nodeAnalysis.getExit();
		}
		return focusOfConcern;
	}

	public Set<T> getContraFocusOfConcern(Node node) {
		Set<T> contraFocus = null;
		AnalysisTableEntry<T> nodeAnalysis = table.getEntry(node);
		if (this.algorithm.getConcernType().equals(ConcernType.CONCERNS_ENTRY_CONDITIONS)) {
			contraFocus = nodeAnalysis.getExit();
		} else {
			contraFocus = nodeAnalysis.getEntry();
		}
		return contraFocus;
	}

	private boolean isInExtremeEdges(Node node) {
		List<Node> extremes = this.algorithm.getExtremeEdges();
		for (Node extremeNode : extremes) {
			if (extremeNode.get("id").asLong() == node.get("id").asLong()) {
				return true;
			}
		}
		return false;
	}

	private List<Pair<Node, Node>> getFlowForAlgorithm() {
		List<Pair<Node, Node>> flow;
		if (algorithm.getFlowType().equals(FlowType.BACKWARD)) {
			flow = store.findFlowR(programId);
		} else {
			flow = store.findFlow(programId);
		}
		return flow;
	}

	private List<Pair<Node, Node>> filterFlow(List<Pair<Node, Node>> flow, Node node) {
		List<Pair<Node, Node>> subFlow = new LinkedList<Pair<Node, Node>>();
		for (Pair<Node, Node> pair : flow) {
			Node l = pair.getValue1();
			if (l.equals(node)) {
				subFlow.add(pair);
			}
		}
		return subFlow;
	}

}
