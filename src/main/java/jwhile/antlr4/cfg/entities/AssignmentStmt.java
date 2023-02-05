package jwhile.antlr4.cfg.entities;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class AssignmentStmt extends Stmt implements LabledEntity {

	private Label label;
	private Identifier i;
	private Exp exp;

	protected AssignmentStmt(Label label, Identifier i, Exp exp) {
		this.label = label;
		this.i = i;
		this.exp = exp;
	}

	public Identifier getI() {
		return i;
	}

	public Exp getExp() {
		return exp;
	}

	@Override
	public Label getLabel() {
		return this.label;
	}

	@Override
	public Label getInitLabel() {
		return this.label;
	}

	@Override
	public List<Label> getFinalLabels() {
		List<Label> finals = new LinkedList<Label>();
		finals.add(this.label);
		return finals;
	}

	@Override
	public List<FlowEdge> flow() {
		return new LinkedList<FlowEdge>();
	}

	@Override
	public Set<UsageEdge> getUsages() {
		Set<UsageEdge> usages = new HashSet<UsageEdge>();
		usages.add(this.getProgram().getEntityFactory().getUsageEdge(this, i, AssignmentEdge.class));
		usages.add(this.getProgram().getEntityFactory().getUsageEdge(this, exp, ExpressionUsageEdge.class));
		usages.addAll(i.getUsages());
		usages.addAll(exp.getUsages());
		return usages;
	}


}
