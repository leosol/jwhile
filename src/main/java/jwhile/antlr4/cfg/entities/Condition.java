package jwhile.antlr4.cfg.entities;

import java.util.HashSet;
import java.util.Set;

public class Condition extends Entity implements LabledEntity {

	private Label label;
	private String text;
	private Exp exp;

	protected Condition(Label label, Exp exp, String text) {
		this.label = label;
		this.exp = exp;
		this.text = text;
	}

	@Override
	public Label getLabel() {
		return this.label;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	public Exp getExp() {
		return exp;
	}

	public void setExp(Exp exp) {
		this.exp = exp;
	}

	@Override
	public String toString() {
		return getName() + " label: " + label + " text " + text;
	}

	@Override
	public Set<UsageEdge> getUsages() {
		Set<UsageEdge> usages = new HashSet<UsageEdge>();
		usages.add(this.getProgram().getEntityFactory().getUsageEdge(this, exp, ExpressionUsageEdge.class));
		usages.addAll(exp.getUsages());
		return usages;
	}

}
