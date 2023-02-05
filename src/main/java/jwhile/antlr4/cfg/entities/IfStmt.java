package jwhile.antlr4.cfg.entities;

import java.util.LinkedList;
import java.util.List;

public class IfStmt extends Stmt {

	private Condition c;
	private Stmt trueBranch;
	private Stmt falseBranch;

	protected IfStmt(Condition c, Stmt trueBranch, Stmt falseBranch) {
		this.c = c;
		this.trueBranch = trueBranch;
		this.falseBranch = falseBranch;
	}

	public Condition getC() {
		return c;
	}

	public Stmt getTrueBranch() {
		return trueBranch;
	}

	public Stmt getFalseBranch() {
		return falseBranch;
	}

	@Override
	public Label getInitLabel() {
		return c.getLabel();
	}

	@Override
	public List<Label> getFinalLabels() {
		List<Label> finals = new LinkedList<Label>();
		finals.addAll(this.trueBranch.getFinalLabels());
		if (falseBranch != null)
			finals.addAll(this.falseBranch.getFinalLabels());
		else
			finals.add(this.c.getLabel());
		return finals;
	}

	@Override
	public List<FlowEdge> flow() {
		LinkedList<FlowEdge> edges = new LinkedList<FlowEdge>();
		edges.addAll(trueBranch.flow());
		if (falseBranch != null)
			edges.addAll(falseBranch.flow());
		Label l = c.getLabel();
		edges.add(new FlowEdge(l, trueBranch.getInitLabel()));
		if (falseBranch != null)
			edges.add(new FlowEdge(l, falseBranch.getInitLabel()));
		return edges;
	}

	@Override
	public String toString() {
		return getName() + " c: " + c + " s1 " + trueBranch + " s2 " + falseBranch;
	}
}
