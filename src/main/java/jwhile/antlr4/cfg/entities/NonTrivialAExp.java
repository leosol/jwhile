package jwhile.antlr4.cfg.entities;

import java.util.HashSet;
import java.util.Set;

public class NonTrivialAExp extends AExp {

	private String operation;
	private AExp leftSide;
	private AExp rightSide;
	private String text;

	protected NonTrivialAExp(String operation, AExp leftSide, AExp rightSide, String text) {
		this.operation = operation;
		this.leftSide = leftSide;
		this.rightSide = rightSide;
		this.text = text;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public AExp getLeftSide() {
		return leftSide;
	}

	public void setLeftSide(AExp leftSide) {
		this.leftSide = leftSide;
	}

	public AExp getRightSide() {
		return rightSide;
	}

	public void setRightSide(AExp rightSide) {
		this.rightSide = rightSide;
	}

	public String getText() {
		return text;
	}

	@Override
	public Set<UsageEdge> getUsages() {
		Set<UsageEdge> usages = new HashSet<UsageEdge>();
		usages.add(this.getProgram().getEntityFactory().getUsageEdge(this, leftSide, "left", ExpressionUsageEdge.class));
		usages.add(this.getProgram().getEntityFactory().getUsageEdge(this, rightSide, "righ", ExpressionUsageEdge.class));
		usages.addAll(this.leftSide.getUsages());
		usages.addAll(this.rightSide.getUsages());
		return usages;
	}

	@Override
	public String toString() {
		return getName() + " " + leftSide + " " + operation + " " + rightSide;
	}
}
