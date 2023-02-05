package jwhile.antlr4.cfg.entities;

import java.util.HashSet;
import java.util.Set;

public abstract class NonTrivialBExp extends BExp{
	
	private String operation;
	private Exp leftExp;
	private Exp rightExp;
	private String text;
	
	protected NonTrivialBExp(String operation, Exp left, Exp right, String text) {
		this.operation = operation;
		this.leftExp = left;
		this.rightExp = right;
		this.text = text;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Exp getLeftExp() {
		return leftExp;
	}

	public void setLeftExp(Exp leftExp) {
		this.leftExp = leftExp;
	}

	public Exp getRightExp() {
		return rightExp;
	}

	public void setRightExp(Exp rightExp) {
		this.rightExp = rightExp;
	}

	public String getText() {
		return text;
	}
	
	@Override
	public String toString() {
		return getName() + " " + leftExp + " " + operation + " " + rightExp;
	}
	
	@Override
	public Set<UsageEdge> getUsages() {
		Set<UsageEdge> usages = new HashSet<UsageEdge>();
		usages.add(this.getProgram().getEntityFactory().getUsageEdge(this, leftExp, "left", ExpressionUsageEdge.class));
		usages.add(this.getProgram().getEntityFactory().getUsageEdge(this, rightExp, "righ", ExpressionUsageEdge.class));
		usages.addAll(this.leftExp.getUsages());
		usages.addAll(this.rightExp.getUsages());
		return usages;
	}
}
