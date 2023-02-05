package jwhile.antlr4.cfg.entities;

import java.util.LinkedList;
import java.util.List;

public class SeqStmt extends Stmt {

	private Stmt s1;
	private Stmt s2;

	protected SeqStmt(Stmt s1, Stmt s2) {
		this.s1 = s1;
		this.s2 = s2;
	}

	public Stmt getS1() {
		return s1;
	}

	public Stmt getS2() {
		return s2;
	}

	@Override
	public Label getInitLabel() {
		return s1.getInitLabel();
	}

	@Override
	public List<Label> getFinalLabels() {
		return s2.getFinalLabels();
	}

	@Override
	public String toString() {
		return "(S1: " + this.s1.toString() + ";S2: " + this.s2.toString() + ")";
	}

	@Override
	public List<FlowEdge> flow() {
		LinkedList<FlowEdge> edges = new LinkedList<FlowEdge>();
		edges.addAll(s1.flow());
		edges.addAll(s2.flow());
		List<Label> finals = s1.getFinalLabels();
		Label s2Init = s2.getInitLabel();
		for (Label label : finals) {
			FlowEdge fe = new FlowEdge(label, s2Init);
			edges.add(fe);
		}
		return edges;
	}

}
